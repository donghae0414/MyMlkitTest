package com.example.mlkittest.Model;

import android.util.Log;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UnitImageFile {
    private File file;
    private LocalDateTime creationTime;

    public UnitImageFile(String path) {
        this.file  = new File(path);
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
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

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
            ExifInterface exif = new ExifInterface(file.toPath().toString());
            return exif;
        } catch (IOException e) {
            Log.e("UnitImage", "읽을 파일이 잘못되었습니다. ");
            return null;
        }
    }

    public String getFilename() {
        return file.getName();
    }
    public File getFile() { return file; }
}