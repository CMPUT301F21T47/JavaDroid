package com.example.habitshare;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScheduledJobService extends JobService {

    private static final String TAG = "ScheduledJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "Job Started");

        doBackgroundWork(params);

        return true;
    }//end onStartJob

    private void doBackgroundWork(JobParameters params){

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<Habit> HabitDataList = MyHabitFragment.getHabitDataList();

                for (int i = 0; i < HabitDataList.size(); i++){
                    Log.d(TAG, "Currently resetting denotions of HabitList at " + i);

                    if (jobCancelled){
                        return;
                    }

                    Boolean isValidDay = false;
                    String validDays = HabitDataList.get(i).getSelectDayOfWeek();
                    final Calendar c = Calendar.getInstance();
                    int currentDay = c.get(Calendar.DAY_OF_WEEK);

                    if ((currentDay == 2) && (validDays.contains("Monday"))){
                        isValidDay = true;
                    }
                    if ((currentDay == 3) && (validDays.contains("Tuesday"))){
                        isValidDay = true;
                    }
                    if ((currentDay == 4) && (validDays.contains("Wednesday"))){
                        isValidDay = true;
                    }
                    if ((currentDay == 5) && (validDays.contains("Thursday"))){
                        isValidDay = true;
                    }
                    if ((currentDay == 6) && (validDays.contains("Friday"))){
                        isValidDay = true;
                    }
                    if ((currentDay == 7) && (validDays.contains("Saturday"))){
                        isValidDay = true;
                    }
                    if ((currentDay == 1) && (validDays.contains("Sunday"))){
                        isValidDay = true;
                    }


                    if (isValidDay) {
                        if (HabitDataList.get(i).getStatus()) {
                            HabitDataList.get(i).addTimesDone();
                        } else {
                            HabitDataList.get(i).addTimesNotDone();
                        }
                        HabitDataList.get(i).setStatus(false);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }//end for

                /*
                //Example code that counts to 20 when the scheduled job is executed
                for (int i = 0; i < 20; i++){
                    Log.d(TAG, "run: " + i);

                    if (jobCancelled){
                        return;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                */

                Log.d(TAG, "Job Finished");
                jobFinished(params, false);

            }
        }).start();

    }//end doBackgroundWork

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;

        return true;
    }//end onStopJob
}//end ScheduledJobService
