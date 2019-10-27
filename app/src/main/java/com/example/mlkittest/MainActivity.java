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
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mlkittest.Model.TimeGroupAlgorithm;
import com.example.mlkittest.Model.UnitImageFile;
import com.example.mlkittest.Model.UnitImageFileGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static int PICK_IMAGE_MULTIPLE = 1;
    ImageView imageView;
    Button button;
    TextView textView;
    Bitmap bitmap;

    List<Uri> imageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat_dog);

        button.setOnClickListener((View view) -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
        });

        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1001);
            }
        }

        List<String> filePaths = getListOfFile();

        List<UnitImageFile> selectedImages = new ArrayList<>();
        for (String path: filePaths) {
            selectedImages.add(new UnitImageFile(path));
        }

        TimeGroupAlgorithm algorithm = new TimeGroupAlgorithm();
        List<UnitImageFileGroup> processedGroups = algorithm.processImages(selectedImages);
        for (int groupIdx = 0; groupIdx < processedGroups.size(); groupIdx++) {
            textView.append("idx: " + groupIdx + "\n");

            List<UnitImageFile> images = processedGroups.get(groupIdx).getImages();
            textView.append("length: " + images.size() + "\n");

            for (int imageIdx = 0; imageIdx < images.size(); imageIdx++) {
                UnitImageFile image = images.get(imageIdx);
                textView.append("filename: " + image.getFilename() + "\n");
            }
            textView.append("\n\n\n");
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

    protected void processImagesWithMlkit() {
        textView.clearComposingText();
        for (Uri imageUri: imageUris) {
            UriLabeler uriLabeler = new UriLabeler(imageUri, new UriLabeler.UriLabelerListener() {
                @Override
                public void onSuccess(Uri uri, List<UriLabeler.Label> labels) {
                    String filename = null;
                    if (uri.getScheme().equals("content")) {
                        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                        try {
                            if (cursor != null && cursor.moveToFirst()) {
                                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    }

                    if (filename == null) {
                        filename = uri.getPath();
                        int cut = filename.lastIndexOf('/');
                        if (cut != -1) {
                            filename = filename.substring(cut + 1);
                        }
                    }

                    textView.append("{\n" +
                            "  \"filename\": \"" + filename + "\",\n" +
                            "  \"labels\": [\n" +
                            "    ");
                    for (UriLabeler.Label label : labels) {
                        textView.append(label.toString() + ",\n");
                    }

                    textView.append("  ]\n" +
                            "}\n\n\n");
                }

                @Override
                public void onFailure(Uri uri) {
                    Log.e("URILABELER", uri.toString());
                }
            });

            uriLabeler.processUri(getApplicationContext());
        }
    }
}
