package com.example.lab4_fragments.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.lab4_fragments.entities.RoomEntity;

import java.util.List;

@Dao
public interface RoomDao {
    @Insert
    void insertRoom(RoomEntity room);

    @Update
    void updateRoom(RoomEntity room);

    @Delete
    void deleteRoom(RoomEntity room);

    @Query("SELECT * FROM Habitaciones WHERE building_id = :buildingId")
    LiveData<List<RoomEntity>> getRoomsByBuildingId(int buildingId);
}
