package com.example.myapplication.myDao;

import com.example.myapplication.model.AccelModel;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AccelDAO {

    @Insert
    void insert(AccelModel accelModel);

    @Query("Select * from (Select * from accelTable Order By id Desc Limit 10) Var1 Order By id Asc")
    List<AccelModel> getAccelList();
}
