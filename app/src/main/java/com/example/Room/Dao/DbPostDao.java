package com.example.Room.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.Room.Entity.DbPost;

import java.util.List;

@Dao
public interface DbPostDao {

    @Query("SELECT * FROM post")
    List<DbPost> getAll();

    @Query("SELECT * FROM post WHERE id IN (:ids)")
    List<DbPost> loadAllByIds(int[] ids);


    @Query("SELECT * FROM post WHERE group_id = (:groupId)")
    List<DbPost> loadAllByImageGroupId(int groupId);

    @Insert
    void insertAll(DbPost... posts);

    @Delete
    void delete(DbPost post);
}
