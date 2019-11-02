package com.example.Room.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "rep_image",
        foreignKeys = {
            @ForeignKey(entity = DbImage.class,
                        parentColumns = "id",
                        childColumns = "image_id"),
            @ForeignKey(entity = DbImageGroup.class,
                    parentColumns = "id",
                    childColumns = "group_id")
        },
        indices = {
            @Index("group_id"),
            @Index("image_id")
        })

public class DbRepImage {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "image_id")
    public Integer imageId;

    @ColumnInfo(name = "group_id")
    public Integer groupId;
}
