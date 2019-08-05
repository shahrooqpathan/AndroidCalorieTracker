package com.example.calorietrackerassignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class RestActivity extends AppCompatActivity {
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_apis);

        Button findAllFoodBtn = (Button) findViewById(R.id.btnFindAll);
        findAllFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodAsyncTask getAllFood = new FoodAsyncTask();
                getAllFood.execute();
            }
        });

        Button findSpecifiedFood = (Button) findViewById(R.id.btnFindSpecifiedFood);
        final EditText editText = (EditText) findViewById(R.id.foodName);
        findSpecifiedFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodAsyncTask getSpecifiedFood = new FoodAsyncTask();
                String foodName = editText.getText().toString();
                getSpecifiedFood.execute(foodName);
            }
        });

    }
    //private class FoodAsyncTask extends AsyncTask<Void, Void, String> {
    private class FoodAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return RestClient.findFoodByName(params[0]);
        }

        //@Override
        /*protected String doInBackground(String... params) {
            return RestClient.findAllFood();
            //return  RestClient.findFoodByName(params[0]);
        }*/


        @Override
        protected void onPostExecute(String food) {
            TextView resultTextView = (TextView) findViewById(R.id.tvResult);
            String foodName = null;
            String foodCalorie = null;
            String foodCategory = null;
            System.out.println("food Name"+foodName);
            try {


                //JSONObject jsonObject = new JSONObject(food);
                //System.out.println(jsonObject.toString());
                //JSONArray jsonArray = jsonObject.getJSONArray(food);
                JSONArray jsonArray = new JSONArray(food);
                foodName = jsonArray.getJSONObject(0).getString("name");
                foodCalorie = jsonArray.getJSONObject(0).getString("calorie");
                foodCategory = jsonArray.getJSONObject(0).getString("category");
                System.out.println("food Name"+foodName);
            }
            catch (Exception e)
            {
                System.out.println("EXCEPTION" +e);
            }
            resultTextView.setText("Food Name: "+foodName+"\nFood Calorie: "+foodCalorie+"\nFood Category: "+foodCategory);
        }
    }
}
