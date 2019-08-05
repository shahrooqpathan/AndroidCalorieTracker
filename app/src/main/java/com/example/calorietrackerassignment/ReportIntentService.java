package com.example.calorietrackerassignment;

import android.app.IntentService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import localdatabase.DailyStepsDatabase;


public class ReportIntentService extends IntentService {

    static int counter = 0;

    public ReportIntentService() {
        super("ReportIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        counter++;
        Date currentTime = Calendar.getInstance().getTime();
        String strTime = currentTime.toString();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("service", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString("service", " " + counter + "  " + strTime);
        spEditor.apply();
        Log.i("message   ", "The number of runs:  " + counter + " times" + "  " + strTime);
        SendReportAsyncTask sendReportAsyncTask = new SendReportAsyncTask();
        sendReportAsyncTask.execute();
        DeleteDatabase deleteDatabase = new DeleteDatabase();
        deleteDatabase.execute();



    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    public class SendReportAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            SharedPreferences forData = getApplicationContext().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE);

            String userId = forData.getString("userId", null);
            String caloriesConsumed = forData.getString("caloriesConsumed", null);
            String caloriesBurned = forData.getString("caloriesBurned", null);
            String calorieGoal = forData.getString("calorieGoal", null);
            String totalSteps = forData.getString("totalSteps", null);


            String myFormat = "dd-MM-yyyy hh:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);


            return RestClient.sendReportToServer(userId, sdf.format(new Date()), caloriesConsumed, caloriesBurned, calorieGoal, totalSteps);
        }
    }

        private class DeleteDatabase extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... params) {
                DailyStepsDatabase db = Room.databaseBuilder(getApplicationContext(), DailyStepsDatabase.class, "DailyStepsDatabase").fallbackToDestructiveMigration().build();
                db.dailyStepDao().deleteAll();
                return null;
            }

            protected void onPostExecute(Void param) {
                SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("calorieTrackingData", MODE_PRIVATE).edit();
                editor.putString("totalSteps", "0");
                editor.commit();
                Toast.makeText(getApplicationContext(), "All Data deleted", Toast.LENGTH_SHORT).show();
            }
        }

}

