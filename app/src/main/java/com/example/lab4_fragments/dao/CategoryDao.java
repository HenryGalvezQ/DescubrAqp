package com.example.lab4_fragments.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.lab4_fragments.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insertCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Query("SELECT * FROM Categorias WHERE category_id = :categoryId")
    LiveData<Category> getCategoryById(int categoryId);

    @Query("SELECT * FROM Categorias WHERE category_id = :categoryId")
    Category getCategoryByIdSync(int categoryId);

    @Query("SELECT * FROM Categorias")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM Categorias WHERE category_name = :categoryName")
    Category getCategoryByName(String categoryName);
}
