package com.example.habitshare;

import android.text.Editable;

import java.util.Objects;

public class Habit{
    private String name;
    private String date;
    private String reason;
    private String frequency;

    public Habit(String name, String date, String frequency, String reason){
        this.name = name;
        this.date = date;
        this.frequency = frequency;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
