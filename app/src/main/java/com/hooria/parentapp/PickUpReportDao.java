package com.hooria.parentapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PickUpReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReports(List<PickUpReportEntity> reports);

    @Query("SELECT * FROM pick_up_activities ORDER BY pickUpTime DESC")
    List<PickUpReportEntity> getAllReports();
}
