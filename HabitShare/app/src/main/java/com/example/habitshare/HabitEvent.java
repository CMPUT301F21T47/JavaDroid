package com.example.habitshare;

/**
 * A class that contains the information for a habit event
 */
public class HabitEvent extends Habit{
    private String eventTitle;
    private String comment;
    private String denoteDate;
    private String location;
    private boolean hasImage;


    public HabitEvent(String title, String denoteDate) {
        super(title, "----/--/--");
        this.denoteDate = denoteDate;
        this.eventTitle = "";
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
     * @return the even title of the habit event
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Modifies the event title
     * @param eventTitle an event title in string data type
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Tells whether the habit event has an image or not
     * @return true if has image; false otherwise
     */
    public boolean isHasImage() {
        return hasImage;
    }

    /**
     * Set the habit event to hasImage
     * @param hasImage
     */
    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    /**
     * @return the location information of this habit event
     */
    public String getLocation() {
        return location;
    }

    /**
     * Modifies the location information of this habit event
     * @param location location information is string data type
     */
    public void setLocation(String location) {
        this.location = location;
    }
}

