// com/example/lab4_fragments/entities/RoomEntity.java
package com.example.lab4_fragments.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Cuartos")
public class RoomEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "room_id")
    public int roomId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "x1")
    public float x1;

    @ColumnInfo(name = "y1")
    public float y1;

    @ColumnInfo(name = "x2")
    public float x2;

    @ColumnInfo(name = "y2")
    public float y2;

    // Constructor
    public RoomEntity(String name, float x1, float y1, float x2, float y2) {
        this.name = name;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    // Opcional: Puedes añadir getters si prefieres encapsulación
}
