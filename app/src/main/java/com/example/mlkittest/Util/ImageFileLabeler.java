package com.example.mlkittest.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mlkittest.Model.Label;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageFileLabeler {
    public interface ImageFileLabelerListener {
        void onSuccess(File file, List<Label> labels);
        void onFailure(File file);
    }

    private FirebaseVisionImageLabeler labeler;

    File file;
    List<Label> labels;
    private ImageFileLabelerListener listener;

    public ImageFileLabeler (File file, ImageFileLabelerListener listener) {
        this.file = file;
        this.labels = new ArrayList<>();

        this.listener = listener;
        labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
    }

    public void process() {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromBitmap(bitmap);

        labels.clear();
        labeler
            .processImage(firebaseImage)
            .addOnSuccessListener((List<FirebaseVisionImageLabel> imagelabels) -> {
                for (FirebaseVisionImageLabel label : imagelabels) {
                    String text = label.getText();
                    String entityId = label.getEntityId();
                    Float confidence = label.getConfidence();
                    labels.add(new Label(text, entityId, confidence));
                }

                listener.onSuccess(file, labels);
            })
            .addOnFailureListener((@NonNull Exception e) -> {
                    Log.e("FIREBASE_IMAGE_LABEL", e.getMessage());
                    listener.onFailure(file);
                });
    }
}
