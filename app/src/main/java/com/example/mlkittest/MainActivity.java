package com.example.mlkittest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static int PICK_IMAGE_MULTIPLE = 1;
    List<Uri> imageUris;

    ImageView imageView;
    Button button;
    TextView textView;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat_dog);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                imageUris = new ArrayList<Uri>();
                if(data.getData() != null){
                    Uri mImageUri = data.getData();
                    imageUris.add(mImageUri);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            imageUris.add(uri);
                        }
                        Log.v("LOG_TAG", "Selected Images" + imageUris.size());
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        processImagesWithMlkit();
        super.onActivityResult(requestCode, resultCode, data);
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
