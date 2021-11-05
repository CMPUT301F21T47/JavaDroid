package com.example.habitshare;

import static java.security.AccessController.getContext;

import android.widget.Toast;

public class Habit {
    private String date;
    private String title;
    private String reason;
    private boolean isDisclosed;
    private boolean[] daysOfWeekList = new boolean[7];
    private boolean status;

    Habit(String title, String date){
        for(int i = 0; i < 7; i++){
            this.daysOfWeekList[i] = false;
        }
        this.reason = "";
        this.title = title;
        this.date = date;
        this.isDisclosed = false;
        this.status = false;
    }

    Habit(String title, String date, String reason, String daysOfWeek){
        this.title = title;
        this.date = date;
        this.reason = reason;
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
                    j = 0;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Tuesday")){
                    j = 1;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Wednesday")){
                    j = 2;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Thursday")){
                    j = 3;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Friday")){
                    j = 4;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Saturday")){
                    j = 5;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
                if(dayString.equals("Sunday")){
                    j = 6;
                    daysOfWeekList[j] = true;
                    dayString = "";
                }
            }
            else if(daysOfWeek.charAt(i) != ' '){
                dayString += daysOfWeek.charAt(i);
            }
        }
    }

    public String getDate() {
        return date;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setDate(String date) {
        date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean getIsDisclosed(){
        return isDisclosed;
    }

    public void setPublic(){
        this.isDisclosed = true;
    }

    public void setPrivate(){
        this.isDisclosed = false;
    }

    public void selectDayOfWeek(int position){
        this.daysOfWeekList[position] = true;
    }

    public void unselectDayOfWeek(int position){
        this.daysOfWeekList[position] = false;
    }

    public String getSelectDayOfWeek(){
        String days = "";
        String dayString = "";
        for(int i = 0; i < daysOfWeekList.length; i++) {
            if (daysOfWeekList[i]) {
                switch (i) {
                    case 0:
                        dayString = "Monday";
                        break;
                    case 1:
                        dayString = "Tuesday";
                        break;
                    case 2:
                        dayString = "Wednesday";
                        break;
                    case 3:
                        dayString = "Thursday";
                        break;
                    case 4:
                        dayString = "Friday";
                        break;
                    case 5:
                        dayString = "Saturday";
                        break;
                    case 6:
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

    public boolean[] getSelectDayOfWeekList(){
        return daysOfWeekList;
    }

    public boolean changeStatus(){
        return !status;
    }
}
