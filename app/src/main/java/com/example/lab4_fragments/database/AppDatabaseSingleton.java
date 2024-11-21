package com.example.lab4_fragments.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.lab4_fragments.dao.BuildingDao;
import com.example.lab4_fragments.dao.CategoryDao;
import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.entities.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppDatabaseSingleton {
    private static AppDatabase INSTANCE;
    private static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Precargar datos
                            databaseExecutor.execute(() -> {
                                loadInitialData(context);
                            });
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }

    private static void loadInitialData(Context context) {
        AppDatabase db = getInstance(context);
        BuildingDao buildingDao = db.buildingDao();
        CategoryDao categoryDao = db.categoryDao();

        // Cargar datos desde edificaciones.txt
        try {
            InputStream is = context.getAssets().open("edificaciones.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    String title = parts[0];
                    String categoryName = parts[1];
                    String description = parts[2];
                    String imageName = parts[3];
                    double latitude = Double.parseDouble(parts[4]);
                    double longitude = Double.parseDouble(parts[5]);

                    // Verificar si la categoría ya existe
                    Category category = categoryDao.getCategoryByName(categoryName);
                    int categoryId;
                    if (category == null) {
                        category = new Category();
                        category.categoryName = categoryName;
                        categoryId = (int) categoryDao.insertCategory(category);
                    } else {
                        categoryId = category.categoryId;
                    }

                    Building building = new Building();
                    building.title = title;
                    building.categoryId = categoryId;
                    building.description = description;
                    building.imageResId = imageName;
                    building.latitude = latitude;
                    building.longitude = longitude;

                    buildingDao.insertBuilding(building);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
