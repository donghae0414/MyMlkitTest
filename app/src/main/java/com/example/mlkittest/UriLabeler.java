package com.example.mlkittest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mlkittest.Model.Label;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UriLabeler {
    public interface UriLabelerListener {
        void onSuccess(Uri uri, List<Label> labels);
        void onFailure(Uri uri);
    }

    private FirebaseVisionImage firebaseImage;
    private FirebaseVisionImageLabeler labeler;

    Uri uri;
    List<Label> labels;
    private UriLabelerListener listener;

    public UriLabeler (Uri uri, UriLabelerListener listener) {
        this.uri = uri;
        this.labels = new ArrayList<Label>();

        this.listener = listener;
        labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
    }

    public void processUri(Context context) {
        try {
            InputStream in = context.getContentResolver()
                                    .openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();

            firebaseImage = FirebaseVisionImage.fromBitmap(bitmap);
        } catch (IOException e){
            Log.e("MLKIT_INPUT_IMAGE", e.getMessage());
        }

        labels.clear();
        labeler.processImage(firebaseImage)
            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                @Override
                public void onSuccess(List<FirebaseVisionImageLabel> imagelabels) {
                    for (FirebaseVisionImageLabel label : imagelabels) {
                        String text = label.getText();
                        String entityId = label.getEntityId();
                        Float confidence = label.getConfidence();
                        labels.add(new Label(text, entityId, confidence));
                    }

                    listener.onSuccess(uri, labels);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FIREBASE_IMAGE_LABEL", e.getMessage());
                    listener.onFailure(uri);
                }
            });
    }
}
