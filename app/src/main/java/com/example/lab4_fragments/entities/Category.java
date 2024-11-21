package com.example.lab4_fragments.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Categorias")
public class Category {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "category_id")
    public int categoryId;

    @ColumnInfo(name = "category_name")
    public String categoryName;
}
