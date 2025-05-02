package com.hooria.parentapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "pick_up_activities")
public class PickUpReportEntity {

        @PrimaryKey(autoGenerate = true)
        public int id;
        public String Sname;
        public Date pickUpTime;
    }

