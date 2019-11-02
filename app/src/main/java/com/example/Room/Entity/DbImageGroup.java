package com.example.Room.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "image_group")
public class DbImageGroup {
    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "start_time")
    public Date startTime;

    @ColumnInfo(name = "finish_time")
    public Date finishTime;
}
