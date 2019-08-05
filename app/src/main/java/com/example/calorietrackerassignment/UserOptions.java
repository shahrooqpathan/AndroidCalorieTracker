package com.example.calorietrackerassignment;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ReportFragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import localdatabase.DailySteps;
import localdatabase.DailyStepsDatabase;

public class UserOptions extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MY_PREFS_NAME = "calorieTrackingData";


    private DailyStepsDatabase db = null;
    //Fields for Shared preference purposes
    private String userId;
    private String caloriesBurntAtRest;
    private String caloriesPerStep;
    private int totalSteps;

    //for Alarm purposes
    private AlarmManager alarmMgr;
    private Intent alarmIntent;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_options);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent thisIntent = getIntent();
        String userData = thisIntent.getExtras().getString("LoggedUser");
        String userName = "";
        String userAddress = "";
        String email ="";

        //Setting up alarm service
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmIntent = new Intent(this, ReportIntentService.class);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 00);
        //setting trigger time for alarm
        long triggerTime = calendar.getTimeInMillis();
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent);



        try {
            JSONArray jsonArray = new JSONArray(userData);
            email = jsonArray.getJSONObject(0).getJSONObject("appUser").getString("email");
            userName = jsonArray.getJSONObject(0).getJSONObject("appUser").getString("name");
            userName = userName + " " + jsonArray.getJSONObject(0).getJSONObject("appUser").getString("surname");
            userAddress = jsonArray.getJSONObject(0).getJSONObject("appUser").getString("address");
            userAddress = userAddress + "," + jsonArray.getJSONObject(0).getJSONObject("appUser").getString("postcode");
            userId = jsonArray.getJSONObject(0).getJSONObject("appUser").getString("userId");

        } catch (Exception e) {
            System.out.println("EXCEPTION" + e);
        }

        db = Room.databaseBuilder(getApplicationContext(), DailyStepsDatabase.class, "DailyStepsDatabase").fallbackToDestructiveMigration().build();

        ReadDatabaseForTotalSteps readDatabaseForTotalSteps = new ReadDatabaseForTotalSteps();
        readDatabaseForTotalSteps.execute(userId);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setTitle("Calorie Tracker");
        FragmentManager fragmentManager = getFragmentManager();

        getApplicationContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.clear();
        editor.putString("name", userName);
        editor.putString("address", userAddress);
        editor.putString("userId", userId);
        editor.putString("calorieGoal", "0");

        //Calories thing
        GetCaloriesData getCaloriesData = new GetCaloriesData();
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        String calorieDate = sdf.format(new Date());
        getCaloriesData.execute(userId, calorieDate);
        editor.commit();
        TextView emailText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_drawer_email);
        emailText.setText(email);
        TextView profileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_drawer_name);
        profileName.setText(userName);
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeScreenFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(UserOptions.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment nextFragment = null;
        switch (id) {
            case R.id.nav_steps_screen:
                nextFragment = new StepScreenFragment();
                break;
            case R.id.nav_home:
                nextFragment = new HomeScreenFragment();
                break;
            case R.id.nav_diet_screen:
                nextFragment = new MyDailyDietFragment();
                break;
            case R.id.nav_map_screen:
                nextFragment = new MapScreenFragment();
                break;
            case R.id.nav_calorie_tracker_screen:
                nextFragment = new CalorieTrackerScreenFragment();
                break;
            case R.id.nav_reports:
                nextFragment = new ReportScreenFragment();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, nextFragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class ReadDatabaseForTotalSteps extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            List<DailySteps> dailySteps = db.dailyStepDao().getAll();
            totalSteps = 0;
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            if (!(dailySteps.isEmpty() || dailySteps == null)) {
                for (DailySteps steps : dailySteps) {
                    if (Integer.toString(steps.getUserId()).equals(userId))
                        totalSteps = totalSteps + steps.getStepTaken();

                }

            }
            editor.putString("totalSteps", Integer.toString(totalSteps));
            editor.commit();
            return "";
        }
    }


    private class GetCaloriesData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            caloriesBurntAtRest = RestClient.findCaloriesConsumedAtRest(userId);
            caloriesPerStep = RestClient.findCaloriesConsumedPerStep(userId);
            return RestClient.findCaloriesConsumedByUserAndDate(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String searchResult) {

            String caloriesConsumedTotal = "";
            try {
                JSONArray jsonArray = new JSONArray(searchResult);
                double dCaloriesBurntAtRest = new JSONArray(caloriesBurntAtRest).getJSONObject(0).getDouble("caloriesBurntAtRest");
                double dCaloriesPerStep = new JSONArray(caloriesPerStep).getJSONObject(0).getDouble("caloriesPerStep");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject caloriesConsumed = jsonArray.getJSONObject(i);
                    caloriesConsumedTotal = caloriesConsumed.getString("totalCaloriesConsumed");
                }
                double totalCaloriesBurnt = dCaloriesBurntAtRest + (dCaloriesPerStep * totalSteps);

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                editor.putString("caloriesConsumed", caloriesConsumedTotal);
                editor.putString("caloriesBurned", Integer.toString((int) Math.round(totalCaloriesBurnt)));
                editor.commit();

                System.out.println(Arrays.asList(getApplicationContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).getAll()));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
