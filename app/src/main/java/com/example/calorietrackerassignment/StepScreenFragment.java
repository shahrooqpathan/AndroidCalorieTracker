package com.example.calorietrackerassignment;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import localdatabase.DailySteps;
import localdatabase.DailyStepsDatabase;

import static android.content.Context.MODE_PRIVATE;

public class StepScreenFragment extends Fragment implements View.OnClickListener {
    private View vDisplayStepScreen;
    private DailyStepsDatabase db = null;

    private EditText addStepsText;
    private Button addStepsButton;
    private ListView stepsList;
    private String userId;
    private Button sendReportButton;


    List<HashMap<String, String>> unitListArray;
    SimpleAdapter myListAdapter;
    TextView stepsHeader;
    ListView unitList;
    //HashMap<String, String> map = new HashMap<String, String>();
    String[] colHEAD = new String[]{"STEPS", "DATE"};
    int[] dataCell = new int[]{R.id.tv_added_steps, R.id.tv_added_steps_date};


    //userId
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        vDisplayStepScreen = inflater.inflate(R.layout.fragment_step_screen, container, false);

        SharedPreferences forUserId = getActivity().getSharedPreferences("calorieTrackingData", MODE_PRIVATE);
        userId = forUserId.getString("userId", null);


        //Setting the database
        db = Room.databaseBuilder(getActivity(), DailyStepsDatabase.class, "DailyStepsDatabase").fallbackToDestructiveMigration().build();

        //Setting vairables and listeners
        addStepsText = vDisplayStepScreen.findViewById(R.id.input_steps);
        addStepsButton = vDisplayStepScreen.findViewById(R.id.btn_add_steps);
        stepsHeader = vDisplayStepScreen.findViewById(R.id.tv_steps_heading);
        stepsList = vDisplayStepScreen.findViewById(R.id.list_view_steps);

        //unitList = this.findViewById(R.id.list_view);
        unitListArray = new ArrayList<>();

        sendReportButton = vDisplayStepScreen.findViewById(R.id.btn_send_report);

        addStepsButton.setOnClickListener(this);
        sendReportButton.setOnClickListener(this);

        //searchFoodButton.setOnClickListener(this);
        myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
        stepsList.setAdapter(myListAdapter);

        stepsList.setOnItemClickListener(editSteps);


        return vDisplayStepScreen;
    }

    private AdapterView.OnItemClickListener editSteps = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            LinearLayout tvTemp = ((LinearLayout) view);
            final HashMap<String, String> map = new HashMap<String, String>();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

            //saving values we dont want to change
            final String preservedDate = unitListArray.get(position).get("DATE");
            final String preservedUserId = unitListArray.get(position).get("USERID");
            final String preservedStepNo = unitListArray.get(position).get("STEPNO");

            //Making an Edit Text button
            final EditText input = new EditText(getActivity());
            input.setText(unitListArray.get(position).get("STEPS"));
            alertDialog.setView(input);
            alertDialog.setTitle("Edit Steps");
            alertDialog.setMessage("Edit Steps for time:" + preservedDate);
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    UpdateDatabase updateSteps = new UpdateDatabase();
                    updateSteps.execute(preservedStepNo, preservedDate, input.getText().toString(), preservedUserId);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("STEPS", input.getText().toString());
                    map.put("DATE", preservedDate);
                    map.put("STEPNO", preservedStepNo);

                    unitListArray.set(position, map);
                    myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
                    stepsList.setAdapter(myListAdapter);
                    return;
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    return;
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.show();

        }
    };

    @Override
    public void onClick(View v) {
        //SharedPreferences spMyUnits = getActivity().getSharedPreferences("myUnits", Context.MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.btn_add_steps:
                String steps = addStepsText.getText().toString();


                if (steps.isEmpty()) {
                    addStepsText.setError("enter a step entry");

                } else {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("STEPS", steps);
                    map.put("DATE", new Date().toString());
                    //map.put("STEPNO", steps);
                    unitListArray.add(map);
                    myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
                    stepsList.setAdapter(myListAdapter);

                    InsertDatabase addDatabase = new InsertDatabase();
                    addDatabase.execute(userId, steps, new Date().toString());
                }
                break;
            case R.id.btn_send_report:
                sendReportToDatabase();
                stepsHeader.setVisibility(View.INVISIBLE);
                unitListArray.clear();
                myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
                stepsList.setAdapter(myListAdapter);

                break;
        }

    }

    public void sendReportToDatabase(){
        SendReportAsyncTask sendReportAsyncTask = new SendReportAsyncTask();
        sendReportAsyncTask.execute();
        DeleteDatabase deleteDatabase = new DeleteDatabase();
        deleteDatabase.execute();
    }


    private class InsertDatabase extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (!(addStepsText.getText().toString().isEmpty())) {
                String[] details = addStepsText.getText().toString().split(" ");
                if (details.length == 1) {
                    DailySteps dailySteps = new DailySteps(Integer.parseInt(params[0]), Integer.parseInt(params[1]), params[2]);
                    long id = db.dailyStepDao().insert(dailySteps);
                    return (id + "");
                } else
                    return "";
            } else
                return "";
        }

        @Override
        protected void onPostExecute(String details) {
            ReadDatabase readNewData = new ReadDatabase();
            readNewData.execute();
            myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
            stepsList.setAdapter(myListAdapter);
            addStepsText.setText("");
            addStepsText.clearFocus();
        }
    }

    private class ReadDatabase extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            List<DailySteps> dailySteps = db.dailyStepDao().getAll();
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("calorieTrackingData", MODE_PRIVATE).edit();
            int totalSteps = 0;
            if (!(dailySteps.isEmpty() || dailySteps == null)) {
                unitListArray.clear();
                for (DailySteps steps : dailySteps) {
                    if(steps.getUserId() == Integer.parseInt(userId)) {
                        totalSteps = totalSteps + steps.getStepTaken();
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("STEPS", Integer.toString(steps.getStepTaken()));
                        map.put("DATE", steps.getStepDate());
                        map.put("USERID", Integer.toString(steps.getUserId()));
                        map.put("STEPNO", Integer.toString(steps.getEntryNumber()));
                        unitListArray.add(map);
                        myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
                    }
                }
                editor.putString("totalSteps", Integer.toString(totalSteps));
                editor.commit();

                return "stepsPresent";
            } else {
                editor.putString("totalSteps", Integer.toString(totalSteps));
                editor.commit();
                return "";
            }
        }


        @Override
        protected void onPostExecute(String details) {
            //textView_read.setText("All data: " + details);
            myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
            stepsList.setAdapter(myListAdapter);
            if(details.equals("stepsPresent"))
                stepsHeader.setVisibility(View.VISIBLE);
            else
                stepsHeader.setVisibility(View.INVISIBLE);
        }
    }

    private class UpdateDatabase extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            DailySteps steps = null;
            //String[] details = editText.getText().toString().split(" ");
            steps = db.dailyStepDao().findByStepNo(params[0]);

            steps.setStepTaken(Integer.parseInt(params[2]));
            //steps.setUserId(Integer.parseInt(params[4]));


            if (steps != null) {
                db.dailyStepDao().updateSteps(steps);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getActivity(), "Successfully updated the entry", Toast.LENGTH_SHORT).show();
        }


    }

    private class DeleteDatabase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            db.dailyStepDao().deleteAll();
            return null;
        }

        protected void onPostExecute(Void param) {

            SharedPreferences.Editor editor = getActivity().getSharedPreferences("calorieTrackingData", MODE_PRIVATE).edit();
            editor.putString("totalSteps", "0");
            editor.commit();
            Toast.makeText(getActivity(), "All Data deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ReadDatabase readEntries = new ReadDatabase();
        readEntries.execute();
        //myListAdapter = new SimpleAdapter(getActivity(), unitListArray, R.layout.steps_list_view, colHEAD, dataCell);
        //stepsList.setAdapter(myListAdapter);
    }

    private class SendReportAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            SharedPreferences forData = getActivity().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE);

            String userId = forData.getString("userId", null);
            String caloriesConsumed = forData.getString("caloriesConsumed", null);
            String caloriesBurned = forData.getString("caloriesBurned", null);
            String calorieGoal = forData.getString("calorieGoal", null);
            String totalSteps = forData.getString("totalSteps", null);


            String myFormat = "dd-MM-yyyy hh:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);


            return RestClient.sendReportToServer(userId,sdf.format(new Date()),caloriesConsumed,caloriesBurned,calorieGoal,totalSteps);
        }


    }

}
