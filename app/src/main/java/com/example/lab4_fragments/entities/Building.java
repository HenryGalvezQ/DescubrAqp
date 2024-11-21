package com.example.lab4_fragments.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Edificaciones",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "category_id",
                childColumns = "category_id",
                onDelete = ForeignKey.SET_NULL))
public class Building {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "building_id")
    public int buildingId;

    @ColumnInfo(name = "category_id")
    public Integer categoryId; // Puede ser null si se borra la categoría

    public String title;

    public String description;

    @ColumnInfo(name = "image_res_id")
    public String imageResId;

    public double latitude;

    public double longitude;
}
