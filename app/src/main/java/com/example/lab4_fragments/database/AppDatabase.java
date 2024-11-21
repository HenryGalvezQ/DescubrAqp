package com.example.lab4_fragments.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.lab4_fragments.dao.BuildingDao;
import com.example.lab4_fragments.dao.CategoryDao;
import com.example.lab4_fragments.dao.CommentDao;
import com.example.lab4_fragments.dao.FavoriteDao;
import com.example.lab4_fragments.dao.RoomDao;
import com.example.lab4_fragments.dao.UserDao;
import com.example.lab4_fragments.entities.Building;
import com.example.lab4_fragments.entities.Category;
import com.example.lab4_fragments.entities.Comment;
import com.example.lab4_fragments.entities.Favorite;
import com.example.lab4_fragments.entities.RoomEntity;
import com.example.lab4_fragments.entities.User;

@Database(entities = {User.class, Category.class, Building.class, Comment.class, Favorite.class, RoomEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract BuildingDao buildingDao();
    public abstract CommentDao commentDao();
    public abstract FavoriteDao favoriteDao();
    public abstract RoomDao roomDao();

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);
}
