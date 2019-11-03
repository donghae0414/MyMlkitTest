package com.example.mlkittest.Model;

import android.util.Log;

import com.example.mlkittest.Model.dbscan.Cluster;
import com.example.mlkittest.Model.dbscan.DBSCAN;
import com.example.mlkittest.Model.dbscan.DataPoint;
import com.example.mlkittest.Model.dbscan.ImageDataPoint;
import com.example.mlkittest.Model.imagehash.Hamming;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimGroupAlgorithm implements IGroupAlgorithm {
    private static final int MAX_DISTANCE = 16;
    private static final int MIN_POINTS = 1;

    @Override
    public List<UnitImageFileGroup> processImages(List<UnitImageFile> images) {

        // DataPoint 초기화
        List<DataPoint> points = new ArrayList<>();
        for (UnitImageFile image: images) {
            points.add(new ImageDataPoint(image));
        }


        // DBSCAN cluster
        DBSCAN dbscan = new DBSCAN(MAX_DISTANCE, MIN_POINTS);
        dbscan.setPoints(points);
        dbscan.cluster();


        // Group화
        List<UnitImageFileGroup> groups = new ArrayList<>();

        List<Cluster> clusters = dbscan.getClusters();
        for (Cluster c: clusters) {
            Log.e("SIM_GROUP_ALGORITHM", c.toString());

            groups.add(new UnitImageFileGroup());
            UnitImageFileGroup group = groups.get(groups.size() - 1);

            for (DataPoint point: c.getPoints()) {
                ImageDataPoint iPoint;

                if (point instanceof ImageDataPoint) {
                    iPoint = (ImageDataPoint) point;
                    UnitImageFile file = iPoint.getFile();

                    group.addImage(file);
                }
            }
        }

        return groups;
    }








}
