package com.example.Room.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "post",
        foreignKeys = @ForeignKey(entity = DbImageGroup.class,
                parentColumns = "id",
                childColumns = "group_id"),
        indices = {
            @Index("group_id")
        })
public class DbPost {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "group_id")
    public Integer groupId;
}
