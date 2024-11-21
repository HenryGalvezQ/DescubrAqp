package com.example.lab4_fragments.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lab4_fragments.entities.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Insert
    long insertUserReturnId(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM Usuarios WHERE user_id = :userId")
    LiveData<User> getUserById(int userId);

    @Query("SELECT * FROM Usuarios")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM Usuarios WHERE email = :email")
    LiveData<User> getUserByEmail(String email);

    @Query("SELECT * FROM Usuarios WHERE email = :email")
    User getUserByEmailSync(String email);
}
