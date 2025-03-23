package com.hooria.parentapp.model;

public class Guardian {

    private String guardianId;
    private String Gname;
    private String number;
    private String CNIC;
    private String Email;
    private String QRcodeData;

    public Guardian() {}

    // Getters and Setters
    public String getGuardianId() {
        return guardianId;
    }

    public void setGuardianId(String guardianId) {
        this.guardianId = guardianId;
    }

    public String getGname() {
        return Gname;
    }

    public void setGname(String Gname) {
        this.Gname = Gname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCNIC() {
        return CNIC;
    }

    public void setCNIC(String CNIC) {
        this.CNIC = CNIC;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getQRcodeData() {
        return QRcodeData;
    }

    public void setQRcodeData(String QRcodeData) {
        this.QRcodeData = QRcodeData;
    }
}
