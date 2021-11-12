package com.example.habitshare;

public class HabitEvent extends Habit{

    private String evenTitle;
    private String comment;
    private String denoteDate;
    private String location;
    private boolean hasImage;

    HabitEvent(String title, String denoteDate) {
        super(title, "----/--/--");
        this.denoteDate = denoteDate;
        this.evenTitle = "";
        this.hasImage = false;
    }

    /**
     * Comment getter
     * @return a string that contains a comment of a habit event
     */
    public String getComment() {
        return comment;
    }

    /**
     * Comment setter
     * @param comment a string that contains a comment of a habit event
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Denote date getter
     * @return get the date of when the habit event is denoted
     */
    public String getDenoteDate() {
        return denoteDate;
    }

    /**
     * Denote date setter
     * @param denoteDate the date of when the habit event is denoted
     */
    public void setDenoteDate(String denoteDate) {
        this.denoteDate = denoteDate;
    }

    public String getEvenTitle() {
        return evenTitle;
    }

    public void setEvenTitle(String imageLocation) {
        this.evenTitle = imageLocation;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

