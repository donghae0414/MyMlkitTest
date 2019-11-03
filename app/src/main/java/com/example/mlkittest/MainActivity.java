package com.example.mlkittest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Room.Database.AppDatabase;
import com.example.Room.Database.AppExecutors;
import com.example.Room.Entity.DbImage;
import com.example.Room.Entity.DbLabel;
import com.example.mlkittest.Model.Label;
import com.example.mlkittest.Model.SimGroupAlgorithm;
import com.example.mlkittest.Model.TimeGroupAlgorithm;
import com.example.mlkittest.Model.UnitImageFile;
import com.example.mlkittest.Model.UnitImageFileGroup;
import com.example.mlkittest.Model.imagehash.AverageHash;
import com.example.mlkittest.Model.imagehash.Hamming;
import com.example.mlkittest.Model.imagehash.ImageResult;
import com.example.mlkittest.Util.ImageFileLabeler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String BASE_DIRECTORY_PATH = "DCIM/Camera";
    public static final String EXTENSION_TYPE = ".jpg";

    long ahashRunningTime = 0L;

    TextView textView1;
    TextView textView2;
    Bitmap bitmap;

    private AppDatabase mDb;

    private int finishedTaskCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat_dog);


        // 데이터베이스 테스트
//        mDb = AppDatabase.getInstance(getApplicationContext());
//        AppExecutors.getInstance().diskIO().execute(() -> {
//            mDb.populateInitialData();
//
//            List<DbImage> dbImages = mDb.imageDao().getAll();
//            for (DbImage dbImage : dbImages) {
//                textView1.append(dbImage.path + "\n");
//            }
//
//            List<DbLabel> labels = mDb.mlkitLabelDao().getAll();
//            for (DbLabel label: labels) {
//                textView2.append(label.text + "\n");
//            }
//        });


        //  파일 쓰기 권한 확인
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // do nothing;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1001);
            }
        }

        //  이미지 -> 단위 이미지
        List<File> files = getListOfFile();
        List<UnitImageFile> selectedImages = new ArrayList<>();

        for (File file: files.subList(0, 30)) {
            selectedImages.add(new UnitImageFile(file));
//            Log.e("MainActivity", file.toString() + " UnitImageFile 생성");
        }

//        // Average hash test
//        new SearchImage()
//                .execute(files.toArray(new File[files.size()]));

        SimGroupAlgorithm algorithm = new SimGroupAlgorithm();
        List<UnitImageFileGroup> processedGroups = algorithm.processImages(selectedImages.subList(0, 30));

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


        //  시간 단위 클러스터링
//        TimeGroupAlgorithm algorithm = new TimeGroupAlgorithm();
//        List<UnitImageFileGroup> processedGroups = algorithm.processImages(selectedImages);
//        for (int groupIdx = 0; groupIdx < processedGroups.size(); groupIdx++) {
//            textView1.append("idx: " + groupIdx + "\n");
//
//            List<UnitImageFile> images = processedGroups.get(groupIdx).getImages();
//            textView1.append("length: " + images.size() + "\n");
//
//            for (int imageIdx = 0; imageIdx < images.size(); imageIdx++) {
//                UnitImageFile image = images.get(imageIdx);
//                textView1.append("filename: " + image.getFilename() + "\n");
//            }
//            textView1.append("\n\n\n");
//        }
//
//
//        //  시간 단위 그룹 레이블링
//        for (int groupIdx = 0; groupIdx < processedGroups.size(); groupIdx++) {
//            List<UnitImageFile> images = processedGroups.get(groupIdx).getImages();
//
//            new AsyncLabelingTask(() -> {
//                finishedTaskCount++;
//                if (finishedTaskCount % 20 == 0) {
//                    Toast.makeText(this, "그룹 " + finishedTaskCount + " 완료. ", Toast.LENGTH_SHORT).show();
//                }
//            }).execute(images);
//        }
    }

    private List<File> getListOfFile(){
        File baseDirectory = null;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG).show();
        } else {
            // Locate the image folder in your SD Card
            baseDirectory = new File(Environment.getExternalStorageDirectory()
                    + File.separator + BASE_DIRECTORY_PATH);

            // Create a new folder if no folder named SDImageTutorial exist
            baseDirectory.mkdirs();
        }

        if (baseDirectory != null && baseDirectory.isDirectory()) {
            File[] files = baseDirectory.listFiles(
                    (File dir, String name) -> name.toLowerCase().endsWith(EXTENSION_TYPE));

            if (files != null) {
                Log.e("LENGTH", String.valueOf(files.length));
            }

//            for (File file: files) {
//                list.add(0, file.getAbsolutePath());
//            }
            return Arrays.asList(files);
        }

        return Arrays.asList();
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }


    /**
     * Thread for Async MLKit DbImage Labeler
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

    class SearchImage extends AsyncTask<File, String, List<ImageResult>> {
        final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String searchFolder = SP.getString("folderPicker", BASE_DIRECTORY_PATH);
        final int threshold = Integer.parseInt(SP.getString("threshold", "80"));

        int totalFiles = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ImageResult> doInBackground(File... imgs) {

//            String dlpath = Environment.getExternalStorageDirectory().getPath() + "/WhatsApp/Media/WhatsApp Images";

//            File f = new File(searchFolder);
//            File file[] = f.listFiles(
//                    (File dir, String name) -> name.toLowerCase().endsWith(EXTENSION_TYPE));

            long startTime = System.nanoTime();

            //query image
//            String targetImage = queryImgPaths[0];
            File targetImage = imgs[0];
            Bitmap resizedBmpQ = AverageHash.resizeTo8x8(targetImage);
            Bitmap grayscaleBmpQ = AverageHash.toGreyscale(resizedBmpQ);
            String hashQ = AverageHash.buildHash(grayscaleBmpQ);
            Log.e("AHASH_TARGET", targetImage.toString());

            List<ImageResult> imgRes = new ArrayList<>();

            for (File fl : imgs){
                if (fl == imgs[0])
                    continue;

                File desImage = fl;
                Bitmap resizedBmp = AverageHash.resizeTo8x8(desImage);
                Bitmap grayscaleBmp = AverageHash.toGreyscale(resizedBmp);
                String hashF = AverageHash.buildHash(grayscaleBmp);
                int distance = new Hamming(hashQ, hashF).getHammingDistance();
//                int percent = (100 - distance);
                int percent = (int) (((float) distance / 64) * 100);
                if (percent >= threshold){ //threshold here
                    ImageResult imgResult = new ImageResult();
                    imgResult.setImgUrl(desImage.toPath().toString());
                    imgResult.setPercentSimilarity(String.valueOf(percent));
                    imgRes.add(imgResult);
                    Log.e("AHASH_DISTANCE", "SIMILAR     | " + fl + " = " + percent + "%");
                } else {
                    Log.e("AHASH_DISTANCE", "NOT SIMILAR | " + fl + " = " + percent + "%");
                }
                totalFiles++;
            }

            long endTime = System.nanoTime();
            long runningTime = (endTime - startTime) / 1000000; //in milliseconds

            Log.e("AHASH", "Running time: " + runningTime);
            return imgRes;
        }

        @Override
        protected void onPostExecute(List<ImageResult> unused) {
            super.onPostExecute(unused);

            for (ImageResult res: unused) {
                textView1.append(res.getImgUrl() + "\n");
                textView1.append(res.getPercentSimilarity() + "\n");
            }

            //go to result activity
//            Intent intent = new Intent(MainActivity.this,ResultActivity.class);
//            intent.putExtra("data", imgRes); //Put your id to your next Intent
//            intent.putExtra("queryImg", queryImgPath);
//            intent.putExtra("fileCount", totalFiles);
//            intent.putExtra("runningTime", runningTime);
//            startActivity(intent);
//            totalFiles =0;
//            progBar.dismiss();
        }

    }
}
