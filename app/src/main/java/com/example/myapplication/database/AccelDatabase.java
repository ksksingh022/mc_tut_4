package com.example.myapplication.database;

import android.content.Context;

import com.example.myapplication.model.AccelModel;
import com.example.myapplication.myDao.AccelDAO;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {AccelModel.class},version = 10)
public abstract class AccelDatabase extends RoomDatabase {
    public abstract AccelDAO accelDAO();
    public static AccelDatabase accelDatabaseinstance;
    public static AccelDatabase getInstance(Context context){

        if(accelDatabaseinstance == null){
            accelDatabaseinstance = Room.databaseBuilder(context.getApplicationContext(), AccelDatabase.class, "my_database").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return accelDatabaseinstance;

    }
}
