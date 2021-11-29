package com.example.habitshare;


/**
 * A class that represents the user's habit
 */
public class Habit{
    private String date;
    private String title;
    private String reason;
    private String lastTimeDenoted;
    private boolean isDisclosed;
    private boolean[] daysOfWeekList = new boolean[7];
    private boolean status;

    private int timesDone = 0;
    private int timesNotDone = 0;

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
        this.lastTimeDenoted = "";
        String dayString = "";
        int j;
        for(int i = 0; i < 7; i++){
            this.daysOfWeekList[i] = false;
        }
        // decoding the string daysOfWeek and encode it into boolean[] daysOfWeek
        for(int i = 0; i < daysOfWeek.length(); i++){
            if(daysOfWeek.charAt(i) == ',' || i == daysOfWeek.length() - 1){
                if(i == daysOfWeek.length() - 1){
                    dayString += daysOfWeek.charAt(i);
                }
                if(dayString.equals("Monday")){
                    j = 1;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Tuesday")){
                    j = 2;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Wednesday")){
                    j = 3;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Thursday")){
                    j = 4;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Friday")){
                    j = 5;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Saturday")){
                    j = 6;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Sunday")){
                    j = 0;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
            }
            else if(daysOfWeek.charAt(i) != ' '){
                dayString += daysOfWeek.charAt(i);
            }
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
     * habit reason setter
     * @param reason a string that contains the reason for a habit
     */
    public void setReason(String reason) {
        this.reason = reason;
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
     * redo the selection of a specific day of week to be done of a habit
     * @param position a position from 0-6 corresponds to Monday to Sunday
     */
    public void unselectDayOfWeek(int position){
        this.daysOfWeekList[position] = false;
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

    public void addTimesDone(){
        timesDone += 1;
    }//end addTimesDone

    public void subTimesDone(){
        timesDone -= 1;
    }//end subTimesDone

    public void addTimesNotDone(){
        timesNotDone += 1;
    }//end addTimesNotDone

    public void subTimesNotDone(){
        timesNotDone -= 1;
    }//end subTimesNotDone

}
