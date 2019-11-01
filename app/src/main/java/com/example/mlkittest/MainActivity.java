package com.example.mlkittest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mlkittest.Model.Label;
import com.example.mlkittest.Model.TimeGroupAlgorithm;
import com.example.mlkittest.Model.UnitImageFile;
import com.example.mlkittest.Model.UnitImageFileGroup;
import com.example.mlkittest.Util.ImageFileLabeler;
import com.google.android.gms.vision.label.ImageLabeler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static int PICK_IMAGE_MULTIPLE = 1;
    TextView textView1;
    TextView textView2;
    Bitmap bitmap;

    private int finishedTaskCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat_dog);

        //  파일 쓰기 권한 확인
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1001);
            }
        }

        //  이미지 -> 단위 이미지
        List<String> filePaths = getListOfFile();
        List<UnitImageFile> selectedImages = new ArrayList<>();
        for (String path: filePaths) {
            selectedImages.add(new UnitImageFile(path));
        }


        //  시간 단위 클러스터링
        TimeGroupAlgorithm algorithm = new TimeGroupAlgorithm();
        List<UnitImageFileGroup> processedGroups = algorithm.processImages(selectedImages);
        for (int groupIdx = 0; groupIdx < processedGroups.size(); groupIdx++) {
            textView1.append("idx: " + groupIdx + "\n");

            List<UnitImageFile> images = processedGroups.get(groupIdx).getImages();
            textView1.append("length: " + images.size() + "\n");

            for (int imageIdx = 0; imageIdx < images.size(); imageIdx++) {
                UnitImageFile image = images.get(imageIdx);
                textView1.append("filename: " + image.getFilename() + "\n");
            }
            textView1.append("\n\n\n");
        }


        //  시간 단위 그룹 레이블링
        for (int groupIdx = 0; groupIdx < processedGroups.size(); groupIdx++) {
            List<UnitImageFile> images = processedGroups.get(groupIdx).getImages();

            new AsyncLabelingTask(() -> {
                finishedTaskCount++;
                if (finishedTaskCount % 20 == 0) {
                    Toast.makeText(this, "그룹 " + finishedTaskCount + " 완료. ", Toast.LENGTH_SHORT).show();
                }
            }).execute(images);
        }
    }

    private static List<String> getListOfFile(){
        List<String> list = new ArrayList<>();
        File file = null;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            Toast.makeText(getContext(), "Error! No SDCARD Found!", Toast.LENGTH_LONG).show();
        } else {
            // Locate the image folder in your SD Card
            file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "DCIM/Camera");
            // Create a new folder if no folder named SDImageTutorial exist
            file.mkdirs();
        }

        List<File> files = null;
        if (file.isDirectory()) {
            files = Arrays.asList(file.listFiles((File dir, String name) -> name.toLowerCase().endsWith(".jpg")));
            Log.e("LENGTH", String.valueOf(files.size()));

            for (File imageFile: files) {
                list.add(0, imageFile.getAbsolutePath());
            }
        }

        return list;
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }


    /**
     * Thread for Async MLKit Image Labeler
     */
    private class AsyncLabelingTask extends AsyncTask<List<UnitImageFile>, Integer, List<List<Label>>> {
        List<List<Label>> result = new ArrayList<>();
        List<String> filenames = new ArrayList<>();

        // 쓰레드 종료 될 때 Callback listener
        private OnTaskCompleted listener;

        public AsyncLabelingTask(OnTaskCompleted listener){
            this.listener = listener;
        }

        @Override
        protected List<List<Label>> doInBackground(List<UnitImageFile>... files) {
            List<List<Label>> fileLabels = processImagesWithMlkit(files[0]);
            return fileLabels;
        }

        @Override
        protected void onPostExecute(List<List<Label>> labels) {
            super.onPostExecute(labels);

            for (int lIdx = 0; lIdx < labels.size(); lIdx++) {
                textView2.append("{\n" +
                        "  \"filename\": \"" + filenames.get(lIdx) + "\"," +
                        "  \"labels\": [\n" +
                        "    ");
                for (Label l: labels.get(lIdx)) {
                    textView2.append(l.toString() + ",\n");
                }
                textView2.append("  ]\n" +
                        "}\n\n\n");
            }
            textView2.append("/*------------------------------------------*/");
            listener.onTaskCompleted();
        }

        private List<List<Label>> processImagesWithMlkit(List<UnitImageFile> imageFiles) {
            final CountDownLatch latch = new CountDownLatch(imageFiles.size());
            for (UnitImageFile imageFile: imageFiles) {
                File file = imageFile.getFile();

                ImageFileLabeler imageFileLabeler = new ImageFileLabeler(file, new ImageFileLabeler.ImageFileLabelerListener() {
                    @Override
                    public void onSuccess(File file, List<Label> labels) {
                        result.add(labels);
                        filenames.add(file.getName());

                        latch.countDown();
                    }

                    @Override
                    public void onFailure(File file) {
                        Log.e("ImageFileLabeler", file.getName());
                        latch.countDown();
                    }
                });

                imageFileLabeler.process();
            }

            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Log.e("MLKIT_ASYNC_TASK", e.toString());
            }

            return result;
        }
    }
}
