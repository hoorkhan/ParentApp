package com.hooria.parentapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {UserEntity.class,StudentEntity.class, PickUpReportEntity.class, GuardianEntity.class}, version = 1)
@TypeConverters(Converters.class)
public  abstract class AppDatabase  extends RoomDatabase {
    public abstract StudentDao studentDao();
    public abstract PickUpReportDao pickUpReportDao();
    public abstract GuardianDao guardianDao();
    public abstract UserDao userDao();
    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "guardian_local_db").build();
                }
            }
        }
        return INSTANCE;
    }
}


