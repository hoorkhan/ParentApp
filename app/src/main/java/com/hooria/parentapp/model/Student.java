package com.hooria.parentapp.model;

public class Student {

        private String studentId;
        private String Sname;
        private String reg;
        private String studentClass;
        private String section;
        private String CNIC;
        private String image;


        public Student() {}

        public String getImage() {
                return image;
        }

        public void setImage(String image) {
                this.image = image;
        }

        // Getters and Setters
        public String getStudentId() {
                return studentId;
        }

        public void setStudentId(String studentId) {
                this.studentId = studentId;
        }

        public String getSname() {
                return Sname;
        }

        public void setSname(String Sname) {
                this.Sname = Sname;
        }

        public String getReg() {
                return reg;
        }

        public void setReg(String reg) {
                this.reg = reg;
        }

        public String getStudentClass() {
                return studentClass;
        }

        public void setStudentClass(String studentClass) {
                this.studentClass = studentClass;
        }

        public String getSection() {
                return section;
        }

        public void setSection(String section) {
                this.section = section;
        }

        public String getCNIC() {
                return CNIC;
        }

        public void setCNIC(String CNIC) {
                this.CNIC = CNIC;
        }
}