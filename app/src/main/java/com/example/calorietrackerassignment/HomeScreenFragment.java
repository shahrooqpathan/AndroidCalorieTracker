package com.example.calorietrackerassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeScreenFragment extends Fragment implements View.OnClickListener{
    View vDisplayHomeScreen;
    private TextView welcome;
    private TextView currentTime;
    private EditText calorieGoal;

    private Button setGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayHomeScreen = inflater.inflate(R.layout.fragment_main, container, false);
        SharedPreferences forUsername = getActivity().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE);
        String name  = forUsername.getString("name",null);
        //String units = spMyUnits.getString("message", null);
        //Initialising all the required UI elements
        welcome = vDisplayHomeScreen.findViewById(R.id.tv_welcome_message);
        currentTime = vDisplayHomeScreen.findViewById(R.id.tv_current_time);
        calorieGoal = vDisplayHomeScreen.findViewById(R.id.input_calorie_goal);
        calorieGoal.setText(forUsername.getString("calorieGoal",null));

        setGoal = vDisplayHomeScreen.findViewById(R.id.btn_set_calorie_goal);
        setGoal.setOnClickListener(this);

        String myFormat = "dd-MM-yyyy hh:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        welcome.setText("Welcome "+name);
        currentTime.setText("The Date and Time is :"+sdf.format(new Date()));
        return vDisplayHomeScreen;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_set_calorie_goal:
                if (calorieGoal.getText().toString().isEmpty())
                {
                    calorieGoal.setError("set a valid goal");
                }else{
                    calorieGoal.setError(null);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE).edit();
                    editor.putString("calorieGoal", calorieGoal.getText().toString());
                    editor.apply();
                    Toast.makeText(getActivity(),"New Goal Set",Toast.LENGTH_SHORT).show();

                }
                break;

        }

    }


}
