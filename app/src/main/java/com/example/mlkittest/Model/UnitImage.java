package com.example.mlkittest.Model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class UnitImage {
    private Uri uri;
    private LocalDateTime creationTime;
    private ContentResolver contentResolver;

    public UnitImage(Uri uri, ContentResolver contentResolver) {
        this.uri = uri;
        this.contentResolver = contentResolver;
    }

    public LocalDateTime getCreationTime() {
        if (this.creationTime != null) {
            return this.creationTime;
        }

        if (hasExifCreationTime()) {
            this.creationTime = getExifCreationTime();
        } else {
            this.creationTime = getFileCreationTime();
        }

        return this.creationTime;
    }

    // EXIF 메타 데이터에서 촬영시각 가져오기
    // 출력데이터 형식 (삼성 기준 - YYYY:MM:DD HH:mm:ss)
    private LocalDateTime getExifCreationTime() {
        ExifInterface exif = getExif();
        if (exif != null) {
            String creationTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
            return ExifDatetimeInterface.getLocalDateTime(creationTime, "yyyy:MM:dd HH:mm:ss");
        }

        return null;
    }

    // 파일 생성 시각 (수정 시각)에서 가져오기
    private LocalDateTime getFileCreationTime() {
        String path = this.getPath();
        File uriFile = new File(path);
        try {
            BasicFileAttributes attrs = Files.readAttributes(uriFile.toPath(), BasicFileAttributes.class);

            // creation time or modified time;
            FileTime creationTime = attrs.creationTime();
            return LocalDateTime.ofInstant(
                    creationTime.toInstant(),
                    ZoneId.of("JST"));
        } catch (IOException e) {
            Log.e("UnitImage", "읽을 파일이 잘못되었습니다. ");
        }

        return null;
    }

    private boolean hasExifCreationTime() {
        ExifInterface exif = getExif();
        if (exif != null) {
            return exif.getAttribute(ExifInterface.TAG_DATETIME) != null;
        }

        return false;
    }

    private ExifInterface getExif() {
        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            return exif;
        } catch (IOException e) {
            Log.e("UnitImage", "읽을 파일이 잘못되었습니다. ");
            return null;
        }
    }

    public String getPath() {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    public String getFilename() {
        String filename = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
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

        return filename;
    }
}

class ExifDatetimeInterface {
    // 삼성: "yyyy:MM:dd HH:mm:ss"
    public static LocalDateTime getLocalDateTime(String exifDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(exifDateTime, formatter);

        return dateTime;
    }
}
