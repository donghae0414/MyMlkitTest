package com.example.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.Room.Entity.DbImage;

import java.util.List;

@Dao
public interface DbImageDao {
    @Query("SELECT * FROM image")
    List<DbImage> getAll();

    @Query("SELECT * FROM image WHERE id IN (:ids)")
    List<DbImage> loadAllByIds(int[] ids);


    @Query("SELECT * FROM image WHERE id = (:id)")
    List<DbImage> loadAllById(int id);


    @Query("SELECT * FROM image WHERE id = (:groupId)")
    List<DbImage> loadAllByGroupId(int groupId);


    @Insert
    void insert(DbImage dbImage);

    @Insert
    void insertAll(DbImage... dbImages);

    @Delete
    void delete(DbImage dbImage);

    @Query("SELECT COUNT(*) from image")
    int count();

}
