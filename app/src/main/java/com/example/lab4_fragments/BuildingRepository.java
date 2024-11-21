package com.example.lab4_fragments;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.lab4_fragments.database.AppDatabase;
import com.example.lab4_fragments.database.AppDatabaseSingleton;
import com.example.lab4_fragments.entities.Building;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildingRepository {
    private AppDatabase appDatabase;
    private LiveData<List<Building>> buildingList;

    private static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(4);

    public BuildingRepository(Context context) {
        appDatabase = AppDatabaseSingleton.getInstance(context);
        buildingList = appDatabase.buildingDao().getAllBuildings();
    }

    public LiveData<List<Building>> getBuildingList() {
        return buildingList;
    }

    public void insertBuilding(Building building) {
        databaseExecutor.execute(() -> {
            appDatabase.buildingDao().insertBuilding(building);
        });
    }

    // Puedes agregar más métodos según sea necesario
}
