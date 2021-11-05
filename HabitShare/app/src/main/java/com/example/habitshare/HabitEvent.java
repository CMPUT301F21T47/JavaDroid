package com.example.habitshare;

public class HabitEvent extends Habit{

    private String comment;
    private String denoteDate;

    HabitEvent(String title, String denoteDate) {
        super(title, "----/--/--");
        this.denoteDate = denoteDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDenoteDate() {
        return denoteDate;
    }

    public void setDenoteDate(String denoteDate) {
        this.denoteDate = denoteDate;
    }
}
