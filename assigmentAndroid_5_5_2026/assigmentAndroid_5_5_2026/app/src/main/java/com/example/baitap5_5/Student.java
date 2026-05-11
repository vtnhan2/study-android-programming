package com.example.baitap5_5;

import java.io.Serializable;

public class Student implements Serializable {
    private int mssv;
    private String name;
    private String faculty;
    private String department;

    public Student(int mssv, String name, String faculty, String department) {
        this.mssv = mssv;
        this.name = name;
        this.faculty = faculty;
        this.department = department;
    }

    public int getMssv() {
        return mssv;
    }

    public void setMssv(int mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
