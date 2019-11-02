package com.example.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.Room.Entity.DbRepImage;

import java.util.List;

@Dao
public interface DbRepImageDao {

    @Query("SELECT * FROM rep_image")
    List<DbRepImage> getAll();

    @Query("SELECT * FROM rep_image WHERE id = (:id)")
    List<DbRepImage> loadAllById(int id);

    @Query("SELECT * FROM rep_image WHERE id IN (:ids)")
    List<DbRepImage> loadAllByIds(int[] ids);


    @Query("SELECT * FROM rep_image WHERE group_id = (:groupId)")
    List<DbRepImage> loadAllByImageGroupId(int groupId);

    @Insert
    void insertAll(DbRepImage... dbRepImages);

    @Delete
    void delete(DbRepImage dbRepImage);
}
