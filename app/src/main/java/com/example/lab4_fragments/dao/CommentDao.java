package com.example.lab4_fragments.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.lab4_fragments.entities.Comment;

import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    void insertComment(Comment comment);

    @Update
    void updateComment(Comment comment);

    @Delete
    void deleteComment(Comment comment);

    @Query("SELECT * FROM Comentarios WHERE building_id = :buildingId")
    LiveData<List<Comment>> getCommentsByBuildingId(int buildingId);

    @Query("SELECT * FROM Comentarios WHERE user_id = :userId")
    LiveData<List<Comment>> getCommentsByUserId(int userId);
}
