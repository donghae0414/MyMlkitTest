package com.example.mlkittest.Model.dbscan;

import android.util.Log;

import com.example.mlkittest.Model.UnitImageFile;
import com.example.mlkittest.Model.imagehash.Hamming;

import java.io.File;


public class ImageDataPoint implements DataPoint {
    private int clusterId = -1;
    private UnitImageFile file;

    public ImageDataPoint(UnitImageFile file) {
        this.file = file;
    }

    public double distance(DataPoint datapoint) {
        ImageDataPoint p2 = null;
        if (datapoint instanceof ImageDataPoint) {
            p2 = (ImageDataPoint) datapoint;
        }

        if (p2 == null) {
            Log.e("IMAGE_DATA_POINT", "서로 다른 형식의 DataPoint 입니다. ");
            return 99999999.9;
        }

        return (double) new Hamming(this.getHash(), p2.getHash())
                                .getHammingDistance();
    }

    public UnitImageFile getFile() {
        return this.file;
    }

    private String getHash() {
        return file.getImageHash();
    }

    @Override
    public void setCluster(int id) {
        this.clusterId = id;
    }

    @Override
    public int getCluster() {
        return this.clusterId;
    }

    @Override
    public int getX() { return 0; }

    @Override
    public int getY() { return 0; }

    @Override
    public String toString() {
        return file.getFile().toPath().toString();
    }
}
