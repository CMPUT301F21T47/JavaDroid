package com.example.habitshare;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A class that represents the user's habit
 */
public class Habit{
    private String date;
    private String title;
    private String reason;
    private String lastTimeDenoted;
    private int numberOfTimesShouldDenoted;
    private int numberOfTimesActuallyDenoted;
    private boolean isDisclosed;
    private boolean[] daysOfWeekList = new boolean[7];
    private boolean status;

    /**
     * Habit constructor
     * @param title habit title
     * @param date date of start
     */
    Habit(String title, String date){
        for(int i = 0; i < 7; i++){
            this.daysOfWeekList[i] = false;
        }
        this.reason = "";
        this.title = title;
        this.date = date;
        this.isDisclosed = false;
        this.status = false;
        this.lastTimeDenoted = "";
        this.numberOfTimesActuallyDenoted = 0;
        this.numberOfTimesShouldDenoted = 0;
    }

    /**
     * Habit constructor
     * @param title habit title
     * @param date date of start
     * @param reason reason of habit
     * @param daysOfWeek which days of a week to do the habit
     */
    Habit(String title, String date, String reason, String daysOfWeek){
        this.title = title;
        this.date = date;
        this.reason = reason;

        // set some default values
        this.lastTimeDenoted = "";
        this.status = false;
        this.numberOfTimesActuallyDenoted = 0;
        this.numberOfTimesShouldDenoted = 0;
        for(int i = 0; i < 7; i++){
            this.daysOfWeekList[i] = false;
        }

        // decoding the string daysOfWeek and encode it into boolean[] daysOfWeek
        if(daysOfWeek.contains("Sunday")) {
            this.daysOfWeekList[0] = true;
        }
        if(daysOfWeek.contains("Monday")) {
            this.daysOfWeekList[1] = true;
        }
        if(daysOfWeek.contains("Tuesday")) {
            this.daysOfWeekList[2] = true;
        }
        if(daysOfWeek.contains("Wednesday")) {
            this.daysOfWeekList[3] = true;
        }
        if(daysOfWeek.contains("Thursday")) {
            this.daysOfWeekList[4] = true;
        }
        if(daysOfWeek.contains("Friday")) {
            this.daysOfWeekList[5] = true;
        }
        if(daysOfWeek.contains("Saturday")) {
            this.daysOfWeekList[6] = true;
        }
    }

    /**
     * Date getter
     * @return date to start of a habit
     */
    public String getDate() {
        return date;
    }

    /**
     * status getter
     * @return a boolean value indicating this habit has been done in this week or not
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Status setter,set the status into either true or false
     * @param status a boolean value indicating this habit has been done in this week or not
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * date setter
     * @param date date to start of a habit
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * habit title getter
     * @return the title of the habit
     */
    public String getTitle() {
        return title;
    }

    /**
     * habit title setter
     * @param title the title of the habit
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * habit reason getter
     * @return a string that contains the reason for a habit
     */
    public String getReason() {
        return reason;
    }

    /**
     * see if a habit is set to public or not
     * @return true if the habit is public; false otherwise
     */
    public boolean getIsDisclosed(){
        return isDisclosed;
    }

    /**
     * make a habit to be public
     */
    public void setPublic(){
        this.isDisclosed = true;
    }

    /**
     * make a habit to be private
     */
    public void setPrivate(){
        this.isDisclosed = false;
    }

    /**
     * set a specific day of week to be done of a habit
     * @param position a position from 0-6 corresponds to Monday to Sunday
     */
    public void selectDayOfWeek(int position){
        this.daysOfWeekList[position] = true;
    }

    /**
     * Convert the boolean list into a string of selected days of week
     * @return a string of selected days of a week
     */
    public String getSelectDayOfWeek(){
        String days = "";
        String dayString = "";
        for(int i = 0; i < daysOfWeekList.length; i++) {
            if (daysOfWeekList[i]) {
                switch (i) {
                    case 1:
                        dayString = "Monday";
                        break;
                    case 2:
                        dayString = "Tuesday";
                        break;
                    case 3:
                        dayString = "Wednesday";
                        break;
                    case 4:
                        dayString = "Thursday";
                        break;
                    case 5:
                        dayString = "Friday";
                        break;
                    case 6:
                        dayString = "Saturday";
                        break;
                    case 0:
                        dayString = "Sunday";
                }
                if(!days.equals("")){
                    days += ", ";
                }
                days += dayString;
            }
        }

        return days;
    }

    /**
     * Self-explanatory
     * @return a boolean list that indicates the selected days of week
     */
    public boolean[] getSelectDayOfWeekList(){
        return daysOfWeekList;
    }

    /**
     * lastTimeDenoted getter
     * @return a string that represents the last time this habit was denoted
     */
    public String getLastTimeDenoted() {
        return lastTimeDenoted;
    }

    /**
     * lastTimeDenoted setter
     * @param lastTimeDenoted a string that represents the last time this habit was denoted
     */
    public void setLastTimeDenoted(String lastTimeDenoted) {
        this.lastTimeDenoted = lastTimeDenoted;
    }

    /**
     * Changes the number of times a habit was actually denoted
     * @param numberOfTimesActuallyDenoted the number of times that a habit that was actually be denoted
     */
    public void setNumberOfTimesActuallyDenoted(int numberOfTimesActuallyDenoted) {
        this.numberOfTimesActuallyDenoted = numberOfTimesActuallyDenoted;
    }

    /**
     * @return number of times this habit was actually denoted
     */
    public int getNumberOfTimesActuallyDenoted(){
        return this.numberOfTimesActuallyDenoted;
    }

    /**
     * Calculate the rate of denoting a habit on time
     * @return the rate of denoting a habit on time
     */
    public double getDenoteOnTimeRate(){
        double result;
        if(numberOfTimesShouldDenoted != 0){
            result = ((double)numberOfTimesActuallyDenoted)/((double)numberOfTimesShouldDenoted);
        }
        else{
            result = 1;
        }
        return result;
    }

    /**
     * update the number of times should be denoted (when necessary)
     * @throws ParseException
     */
    public void updateNumberOfTimesShouldDenoted() throws ParseException {
        int dayOfWeek;
        int counter = 0; // a counter to keep track of the number of times that needs to be denoted during the time period

        // get the date of start in Date data type
        Date startDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        startDate = format.parse(this.date);

        // get the date of current date in Date data type
        Calendar cal = Calendar.getInstance();
        Date endDate = Calendar.getInstance().getTime();

        // skip one day from the last time denoted
        startDate = incrementDate(startDate);

        // iterate from the last time denoted to current day
        for (Date date = startDate; date.before(endDate);) {
            cal.setTime(date);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            // if there is a day that the user should denote the habit, then counter plus one
            if(daysOfWeekList[dayOfWeek-1]){
                counter += 1;
            }
            date = incrementDate(date);
        }

        this.numberOfTimesShouldDenoted = counter;
    }


    /**
     * Compute the date incremented by 1
     * @param date the date that needs to be incremented by 1
     * @return the date that has been incremented by 1
     */
    private Date incrementDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);  // number of days to add to your existing date
        date = calendar.getTime();
        return date;
    }

}
