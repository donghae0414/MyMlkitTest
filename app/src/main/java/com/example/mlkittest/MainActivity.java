package com.example.mlkittest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button button;
    TextView textView;

    Bitmap bitmap;
    FirebaseVisionImage firebaseImage;
    FirebaseVisionImageLabeler labeler;
    long start;
    long end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cat_dog);

        labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //objectDetection();
//                //weird album
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(intent, 101);
//                }
                //we can select gallery app
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, 101);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                try {
                    start = System.currentTimeMillis();
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                    imageView.setImageBitmap(bitmap);
                    firebaseImage = FirebaseVisionImage.fromBitmap(bitmap);
//                    Uri uri = data.getData();
//                    firebaseImage = FirebaseVisionImage.fromFilePath(getApplicationContext(), uri);
//                    imageView.setImageURI(uri);
                    textView.setText("객체들\n");
                    labeler.processImage(firebaseImage)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                                    for (FirebaseVisionImageLabel label : labels) {
                                        String text = label.getText();
                                        String entityId = label.getEntityId();
                                        String confidence = Float.toString(label.getConfidence());
                                        textView.append("text : " + text + ", entityID : " + entityId + ", confidence : " + confidence + "\n");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
                                    textView.append("Fail");
                                }
                            });
                    end = System.currentTimeMillis();
                    textView.append("소요시간 : " + (end - start) + "\n");
                } catch (Exception e) {
                }
            }
        }
    }

//    public void objectDetection(){ // objectDetection photo in drawable folder
//        imageView.setImageBitmap(bitmap);
//        firebaseImage = FirebaseVisionImage.fromBitmap(bitmap);
//
//        labeler.processImage(firebaseImage)
//                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
//                    @Override
//                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
//                        for(FirebaseVisionImageLabel label : labels){
//                            String text = label.getText();
//                            String entityId = label.getEntityId();
//                            String confidence = Float.toString(label.getConfidence());
//                            textView.append("text : " + text + ", entityID : " + entityId + ", confidence : " + confidence + "\n");
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_LONG).show();
//                        textView.append("Fail");
//                    }
//                });
//    }
}
