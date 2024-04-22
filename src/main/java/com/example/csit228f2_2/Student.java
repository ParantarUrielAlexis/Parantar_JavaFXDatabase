package com.example.csit228f2_2;

public class Student {
    private String firstName;
    private String lastName;
    private String schoolProgram;



    public Student(String fName, String lName, String schoolProgram) {
        this.firstName = fName;
        this.lastName = lName;
        this.schoolProgram = schoolProgram;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSchoolProgram() {
        return schoolProgram;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSchoolProgram(String schoolProgram) {
        this.schoolProgram = schoolProgram;
    }
}
