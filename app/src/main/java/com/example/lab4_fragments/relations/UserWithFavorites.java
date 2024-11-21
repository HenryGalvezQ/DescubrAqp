package com.example.lab4_fragments.relations;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.entities.Favorite;
import com.example.lab4_fragments.entities.User;

import java.util.List;

public class UserWithFavorites {
    @Embedded
    public User user;

    @Relation(
            parentColumn = "user_id",
            entityColumn = "building_id",
            associateBy = @Junction(Favorite.class)
    )
    public List<Building> favoriteBuildings;
}
