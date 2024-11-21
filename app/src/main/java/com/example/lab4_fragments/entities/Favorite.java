package com.example.lab4_fragments.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Favoritos",
        primaryKeys = {"user_id", "building_id"},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Building.class,
                        parentColumns = "building_id",
                        childColumns = "building_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class Favorite {
    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "building_id")
    public int buildingId;

    public long timestamp;
}
