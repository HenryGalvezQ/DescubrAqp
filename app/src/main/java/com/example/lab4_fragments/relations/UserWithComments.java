package com.example.lab4_fragments.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.lab4_fragments.entities.Comment;
import com.example.lab4_fragments.entities.User;

import java.util.List;

public class UserWithComments {
    @Embedded
    public User user;

    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_id"
    )
    public List<Comment> comments;
}
