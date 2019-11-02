package com.example.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.Room.Entity.DbLabel;

import java.util.List;

@Dao
public interface DbMlkitLabelDao {

    @Query("SELECT * FROM mlkit_label")
    List<DbLabel> getAll();

    @Query("SELECT * FROM mlkit_label WHERE image_id IN (:imageIds)")
    List<DbLabel> loadAllByImageIds(int[] imageIds);


    @Query("SELECT * FROM mlkit_label WHERE id = (:imageId)")
    List<DbLabel> loadAllByImageId(int imageId);

    @Insert
    void insertAll(DbLabel... dbLabels);

    @Delete
    void delete(DbLabel dbLabel);
}
