package com.example.lab4_fragments.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.lab4_fragments.entities.Building;

import java.util.List;

@Dao
public interface BuildingDao {
    @Insert
    void insertBuilding(Building building);

    @Update
    void updateBuilding(Building building);

    @Delete
    void deleteBuilding(Building building);

    @Query("SELECT * FROM Edificaciones WHERE building_id = :buildingId")
    LiveData<Building> getBuildingById(int buildingId);

    @Query("SELECT * FROM Edificaciones")
    LiveData<List<Building>> getAllBuildings();

    @Query("SELECT * FROM Edificaciones WHERE category_id = :categoryId")
    LiveData<List<Building>> getBuildingsByCategoryId(int categoryId);
}
