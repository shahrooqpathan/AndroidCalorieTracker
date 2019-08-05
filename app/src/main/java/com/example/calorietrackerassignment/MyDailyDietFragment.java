package com.example.calorietrackerassignment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class MyDailyDietFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private View vDisplayDailyDietScreen;
    private String allFoodJson;

    private TextView welcome;
    private EditText searchFoodText;
    private Button searchFoodButton;
    private ImageView searchFoodImage;
    private Spinner foodCategorySpinner;
    private Spinner foodItemsSpinner;
    private TextView ndbResultTextView;

    private TextView foodSnippetCard;
    private Button storeNewFoodButton;
    private Button cancelStoringButton;
    private Button addConsumptionButton;

    //fields for new food
    String ndbno;
    String newFoodName;
    String newFoodCategory;
    String newFoodCalorie;
    String newFoodUnit;
    String newFoodServingAmount;
    String newFoodFat;
    String servingUnitAndAmount;
    Food newFood;

    //For consumption purpose
    String userId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vDisplayDailyDietScreen = inflater.inflate(R.layout.fragment_my_daily_diet, container, false);

        newFood = new Food();
        //Setting variables and listeners
        searchFoodText = vDisplayDailyDietScreen.findViewById(R.id.edt_search_food);
        searchFoodButton = vDisplayDailyDietScreen.findViewById(R.id.btn_search_food);
        welcome = vDisplayDailyDietScreen.findViewById(R.id.tv_daily_diet_message);
        searchFoodImage = vDisplayDailyDietScreen.findViewById(R.id.iv_result_food);
        foodCategorySpinner = vDisplayDailyDietScreen.findViewById(R.id.spinner_food_category);
        foodItemsSpinner = vDisplayDailyDietScreen.findViewById(R.id.spinner_food_item);
        addConsumptionButton = vDisplayDailyDietScreen.findViewById(R.id.btn_add_consumption);
        ndbResultTextView = vDisplayDailyDietScreen.findViewById(R.id.tv_ndb_search_result);

        foodSnippetCard = vDisplayDailyDietScreen.findViewById(R.id.tv_result_food_snippet);
        storeNewFoodButton = vDisplayDailyDietScreen.findViewById(R.id.btn_add_food_to_db);
        cancelStoringButton = vDisplayDailyDietScreen.findViewById(R.id.btn_cancel_add_food);
        storeNewFoodButton.setOnClickListener(this);
        cancelStoringButton.setOnClickListener(this);
        addConsumptionButton.setOnClickListener(this);
        searchFoodButton.setOnClickListener(this);
        return vDisplayDailyDietScreen;
    }


    @Override
    public void onClick(View v) {
        //SharedPreferences spMyUnits = getActivity().getSharedPreferences("myUnits", Context.MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.btn_search_food:

                String searchQuery = searchFoodText.getText().toString();
                if (searchQuery.isEmpty()) {
                    searchFoodText.setError("enter a food to add");

                }
                else{
                    SearchAsyncTask searchAsyncTask = new SearchAsyncTask();
                    searchAsyncTask.execute(searchQuery);
                }

                break;
            case R.id.btn_add_food_to_db:
                AddNewFoodAsyncTask addNewFoodAsyncTask = new AddNewFoodAsyncTask();
                addNewFoodAsyncTask.execute();
                break;
            case R.id.btn_cancel_add_food:
                hideResults();
                break;
            case R.id.btn_add_consumption:
                Toast.makeText(getActivity(), "Consumption Added", Toast.LENGTH_SHORT).show();
                AddConumptionAsyncTask addConumptionAsyncTask = new AddConumptionAsyncTask();
                addConumptionAsyncTask.execute();
                break;

        }

    }

    public void hideResults() {
        ndbResultTextView.setVisibility(View.GONE);
        vDisplayDailyDietScreen.findViewById(R.id.rl_search_result).setVisibility(View.GONE);
        storeNewFoodButton.setVisibility(View.GONE);
        cancelStoringButton.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCategory = foodCategorySpinner.getSelectedItem().toString();
        String foodItems = "";
        JSONArray jsonArray = null;
        try {
            if (allFoodJson != null) {
                jsonArray = new JSONArray(allFoodJson);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject allCategoriesJSON = jsonArray.getJSONObject(i);
                    String currentCategoty = allCategoriesJSON.getString("category");
                    if (currentCategoty.equals(selectedCategory)) {
                        foodItems = foodItems + allCategoriesJSON.getString("name") + "~";
                    }

                }
                List<String> categoryFood = Arrays.asList(foodItems.split("~"));

                List<String> newList = categoryFood.stream().distinct().collect(Collectors.<String>toList());
                final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, newList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                foodItemsSpinner.setAdapter(spinnerAdapter);

            }

        } catch (JSONException e) {

            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class AddConumptionAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String foodToAddToConsumption = foodItemsSpinner.getSelectedItem().toString();
            SharedPreferences forData = getActivity().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE);
            userId = forData.getString("userId", null);
            String foodJson = RestClient.findFoodByName(foodToAddToConsumption);
            String foodId = "";
            try {
                //Extracting the good information
                foodId = new JSONArray(foodJson).getJSONObject(0).getString("foodId");

                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                String addConsumption = RestClient.addConsumption(userId, foodId, sdf.format(new Date()), "1");
                int servingCount = 0;
                if (!addConsumption.equals("successully inserted consumption")) {
                    String consumptionJson = RestClient.findConsumptionDetails(userId, foodId, sdf.format(new Date()));
                    servingCount = new JSONArray(consumptionJson).getJSONObject(0).getInt("servingsConsumed") + 1;
                    RestClient.updateConsumption(userId, foodId, sdf.format(new Date()), Integer.toString(servingCount));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String searchResult) {

        }
    }

    private class SearchAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String foodResult = SearchNdbAPI.searchFoodFromNDB(params[0]);


            try {
                JSONObject foodQueryResult = new JSONObject(foodResult);
                newFoodName = foodQueryResult.getJSONObject("list").getJSONArray("item").getJSONObject(0).getString("name");
                newFoodCategory = foodQueryResult.getJSONObject("list").getJSONArray("item").getJSONObject(0).getString("group");
                ;
                ndbno = foodQueryResult.getJSONObject("list").getJSONArray("item").getJSONObject(0).getString("ndbno");

                foodResult = SearchNdbAPI.searchFoodNutrientsFromNDB(ndbno);
                foodQueryResult = new JSONObject(foodResult);

                servingUnitAndAmount = foodQueryResult.getJSONObject("report").getJSONArray("foods").getJSONObject(0).getString("measure");
                newFoodCalorie = foodQueryResult.getJSONObject("report").getJSONArray("foods").getJSONObject(0).getJSONArray("nutrients").getJSONObject(0).getString("value");
                newFoodFat = foodQueryResult.getJSONObject("report").getJSONArray("foods").getJSONObject(0).getJSONArray("nutrients").getJSONObject(1).getString("value");


            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
            // if()
            //foodCategory =

            return SearchGoogleAPI.search(params[0], new String[]{"num"}, new String[]{"1"});

        }

        @Override
        protected void onPostExecute(String searchResult) {
            //TextView tv = (TextView) findViewById(R.id.tv);
            if (!searchResult.equals("")) {
                searchFoodImage.setVisibility(View.VISIBLE);
                String toGetSnippet = searchResult.split("JSONSEPERATOR")[0];
                String toGetImageURL = searchResult.split("JSONSEPERATOR")[1];
                //String poop =
                Picasso.get().load(SearchGoogleAPI.getImageURL(toGetImageURL)).into(searchFoodImage);
                foodSnippetCard.setText(SearchGoogleAPI.getSnippet(toGetSnippet));
            }

            ndbResultTextView.setText("\nYour Search Result \n\n" +
                    "Food Name: " + newFoodName + "\n" +
                    "Food Category: " + newFoodCategory + "\n" +
                    "Servings: " + servingUnitAndAmount + "\n" +
                    "Calories: " + newFoodCalorie + "\n" +
                    "Fat: " + newFoodFat);

            ndbResultTextView.setVisibility(View.VISIBLE);
            vDisplayDailyDietScreen.findViewById(R.id.rl_search_result).setVisibility(View.VISIBLE);
            storeNewFoodButton.setVisibility(View.VISIBLE);
            cancelStoringButton.setVisibility(View.VISIBLE);

            //newFoodName = newFoodName.replace("\"","");
            newFoodServingAmount = servingUnitAndAmount.split(" ", 2)[0];
            newFoodUnit = servingUnitAndAmount.split(" ", 2)[1];
            ;

            ndbResultTextView.setVisibility(View.VISIBLE);

            newFood = new Food("0", newFoodName, newFoodCategory, newFoodCalorie, newFoodUnit, newFoodFat, newFoodServingAmount);

        }
    }

    //Async Task to get all the food
    private class AllFoodAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return RestClient.findAllFood();
        }

        //@RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(String searchResult) {

            allFoodJson = searchResult;
            String allCategories = "";
            try {
                JSONArray jsonArray = new JSONArray(searchResult);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject allCategoriesJSON = jsonArray.getJSONObject(i);
                    allCategories = allCategories + allCategoriesJSON.getString("category") + "~";
                }
                //allCategories= allCategories.replaceFirst(",$", ""); //Getting rid of the blank element
                List<String> categories = Arrays.asList(allCategories.split("~"));

                List<String> newList = categories.stream().distinct().collect(Collectors.<String>toList());
                final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, newList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                foodCategorySpinner.setAdapter(spinnerAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class AddNewFoodAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String exist = RestClient.checkfoodExists(newFood.getName());
            if (!exist.equals("[]") || exist.equals("")) {

                return "food not added";
            } else {
                String foodId = Integer.toString(Integer.parseInt(RestClient.getFoodCount()) + 1);

                newFood.setFoodId(foodId);
                RestClient.addFood(newFood);
            }
            return "New food added";
        }

        @Override
        protected void onPostExecute(String searchResult) {
            //TextView tv = (TextView) findViewById(R.id.tv);

            //To refresh the spinners
            if (searchResult.equals("food not added"))
                Toast.makeText(getActivity(), "That food already exists in the system silly!", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getActivity(), "Sucessfully added this food item!", Toast.LENGTH_LONG).show();
                newFood = new Food();
                AllFoodAsyncTask getAllFood = new AllFoodAsyncTask();
                getAllFood.execute();
                hideResults();
            }


        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setting values to the spinners
        AllFoodAsyncTask getAllFood = new AllFoodAsyncTask();
        getAllFood.execute();
        foodCategorySpinner.setOnItemSelectedListener(this);
    }


}
