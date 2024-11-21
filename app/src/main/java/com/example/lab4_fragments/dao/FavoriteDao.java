package com.example.lab4_fragments.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.lab4_fragments.entities.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    void insertFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);

    @Query("SELECT * FROM Favoritos WHERE user_id = :userId")
    LiveData<List<Favorite>> getFavoritesByUserId(int userId);

    @Query("SELECT * FROM Favoritos WHERE building_id = :buildingId")
    LiveData<List<Favorite>> getFavoritesByBuildingId(int buildingId);
}
