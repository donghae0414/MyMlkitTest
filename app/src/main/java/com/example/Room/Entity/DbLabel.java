package com.example.Room.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "mlkit_label",
        foreignKeys = @ForeignKey(entity = DbImage.class,
                                  parentColumns = "id",
                                  childColumns = "image_id"),
        indices = {
            @Index("image_id")
        })
public class DbLabel {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "confidence")
    public double confidence;

    @ColumnInfo(name = "entity_id")
    public String entityId;

    @ColumnInfo(name = "image_id")
    public Integer imageId;
}
