package com.example.Room.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

@Entity(tableName = "image",
        foreignKeys = @ForeignKey(entity = DbImageGroup.class,
                                  parentColumns = "id",
                                  childColumns = "group_id",
                                  onUpdate = CASCADE,
                                  onDelete = SET_NULL),
        indices = @Index("group_id"))
public class DbImage {
    @PrimaryKey (autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "path")
    public String path;

    @ColumnInfo(name = "status")
    public int status;

    @ColumnInfo(name = "group_id")
    public Integer groupId;
}
