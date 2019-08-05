package com.example.calorietrackerassignment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class CalorieTrackerScreenFragment extends Fragment {
    private View vCalorieTrackerScreen;

    private TextView calorieSetText;
    private TextView caloriesConsumedText;
    private TextView caloriesBurntText;




    String calorieDate = "22-05-2019";
    String calorieGoal;
    String userId;
    int steps;
    String caloriesBurntAtRest;
    String caloriesPerStep;
    double totalCaloriesBurnt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vCalorieTrackerScreen = inflater.inflate(R.layout.fragment_calore_tracker_screen, container, false);

        calorieSetText = vCalorieTrackerScreen.findViewById(R.id.tv_calories_set);
        caloriesConsumedText = vCalorieTrackerScreen.findViewById(R.id.tv_calories_consumed);
        caloriesBurntText = vCalorieTrackerScreen.findViewById(R.id.tv_calories_burnt);
        //Setting Calorie Goal
        return vCalorieTrackerScreen;


    }

    private class GetCaloriesConsumed extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            caloriesBurntAtRest = RestClient.findCaloriesConsumedAtRest(userId);
            caloriesPerStep = RestClient.findCaloriesConsumedPerStep(userId);
            return RestClient.findCaloriesConsumedByUserAndDate(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String searchResult) {

            String caloriesConsumedTotal="";
            try {
                JSONArray jsonArray = new JSONArray(searchResult);
                double dCaloriesBurntAtRest = new JSONArray(caloriesBurntAtRest).getJSONObject(0).getDouble("caloriesBurntAtRest");
                double dCaloriesPerStep = new JSONArray(caloriesPerStep).getJSONObject(0).getDouble("caloriesPerStep");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject caloriesConsumed = jsonArray.getJSONObject(i);
                    caloriesConsumedTotal = caloriesConsumed.getString("totalCaloriesConsumed");
                }
                totalCaloriesBurnt = dCaloriesBurntAtRest + (dCaloriesPerStep * steps);
                caloriesConsumedText.setText(caloriesConsumedTotal);
                caloriesBurntText.setText(Integer.toString((int) Math.round(totalCaloriesBurnt)));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences forData = getActivity().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE);
        calorieGoal = forData.getString("calorieGoal", null);
        userId = forData.getString("userId", null);
        steps = Integer.parseInt(forData.getString("totalSteps", null));
        if (calorieGoal != null)
            calorieSetText.setText(calorieGoal);
        GetCaloriesConsumed getCaloriesConsumed = new GetCaloriesConsumed();
        getCaloriesConsumed.execute(userId,calorieDate);
    }
}
