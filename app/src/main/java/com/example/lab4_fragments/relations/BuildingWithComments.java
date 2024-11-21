package com.example.lab4_fragments.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.entities.Comment;

import java.util.List;

public class BuildingWithComments {
    @Embedded
    public Building building;

    @Relation(
            parentColumn = "building_id",
            entityColumn = "building_id"
    )
    public List<Comment> comments;
}
