package com.example.mlkittest.Model.dbscan;

public interface DataPoint {
    double distance(DataPoint datapoint);

    void setCluster(int id);

    int getCluster();

    int getX();

    int getY();
}