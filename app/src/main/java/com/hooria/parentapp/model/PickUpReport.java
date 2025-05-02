package com.hooria.parentapp.model;

import java.util.Date;

public class PickUpReport {

    private String CNIC;
    private String Sname;
    private Date pickUpTime;
    private String guardianUID;

    public PickUpReport() {}

    public String getCNIC() {
        return CNIC;
    }

    public void setCNIC(String CNIC) {
        this.CNIC = CNIC;
    }

    public void setSname(String sname) {
        Sname = sname;
    }

    public void setPickUpTime(Date pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public String getSname() {
        return Sname;
    }

    public String getGuardianUID() {
        return guardianUID;
    }

    public void setGuardianUID(String guardianUID) {
        this.guardianUID = guardianUID;
    }

    public Date getPickUpTime() {
        return pickUpTime;
    }
}