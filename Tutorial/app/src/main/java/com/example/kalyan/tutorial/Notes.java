package com.example.kalyan.tutorial;

/**
 * Created by KALYAN on 25-01-2018.
 */

public class Notes {

    private String name,subject;

    public Notes(String name,String subject){
        this.name = name;
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
