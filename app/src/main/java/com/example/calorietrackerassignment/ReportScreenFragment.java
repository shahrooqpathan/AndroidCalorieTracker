package com.example.calorietrackerassignment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReportScreenFragment extends Fragment implements View.OnClickListener {
    private View vReportScreen;


    final Calendar myCalendar = Calendar.getInstance();

    String calorieDate;
    String calorieGoal;


    String userId;


    int editTextTracker; //Set 0 - pie chart , 1 - bar chart start, 2 - bar chart end

    PieChart calorieOverviewChartPie;
    BarChart calorieOverviewChartBar;
    EditText pieChartDateText;
    EditText barChartStartDateText;
    EditText barChartEndDateText;
    Button pieChartButton;
    Button barChartButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences forUserId = getActivity().getSharedPreferences("calorieTrackingData", Context.MODE_PRIVATE);
        userId  = forUserId.getString("userId",null);

        vReportScreen = inflater.inflate(R.layout.fragment_report_screen, container, false);

        pieChartDateText = vReportScreen.findViewById(R.id.input_report_date_for_pie);
        barChartStartDateText = vReportScreen.findViewById(R.id.input_report_bar_date_start);
        barChartEndDateText = vReportScreen.findViewById(R.id.input_report_bar_date_end);
        pieChartButton = vReportScreen.findViewById(R.id.btn_generate_pie_report);
        barChartButton = vReportScreen.findViewById(R.id.btn_generate_bar_report);
        pieChartButton.setOnClickListener(this);
        barChartButton.setOnClickListener(this);


        calorieOverviewChartPie = vReportScreen.findViewById(R.id.pie_chart_calories);
        calorieOverviewChartBar = vReportScreen.findViewById(R.id.bar_chart_calories);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                switch (editTextTracker){
                    case 0:
                        pieChartDateText.setText(sdf.format(myCalendar.getTime()));
                        calorieDate = pieChartDateText.getText().toString();
                        break;
                    case 1:
                        barChartStartDateText.setText(sdf.format(myCalendar.getTime()));
                        break;
                    case 2:
                        barChartEndDateText.setText(sdf.format(myCalendar.getTime()));
                        break;

                }


            }

        };

        pieChartDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextTracker = 0;
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        barChartStartDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextTracker = 1;
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        barChartEndDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editTextTracker = 2;
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return vReportScreen;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //On click listener for Pie Chart Button
            case R.id.btn_generate_pie_report:
                if (pieChartDateText.getText().toString().isEmpty())
                {
                    pieChartDateText.setError("enter a date for report");
                }
                else{
                    pieChartDateText.setError(null);
                    calorieOverviewChartPie.setVisibility(View.VISIBLE);
                    calorieOverviewChartBar.setVisibility(View.INVISIBLE);
                    GetCaloriesConsumed getCaloriesConsumed = new GetCaloriesConsumed();
                    getCaloriesConsumed.execute(userId, calorieDate);
                }
                break;
            // On click listener for Bar Chart button
            case R.id.btn_generate_bar_report:
                if(barChartStartDateText.getText().toString().isEmpty()|| barChartEndDateText.getText().toString().isEmpty())
                {
                    if (barChartStartDateText.getText().toString().isEmpty())
                        barChartStartDateText.setError("enter start date for report");
                    else
                        barChartStartDateText.setError(null);
                    if (barChartEndDateText.getText().toString().isEmpty())
                        barChartEndDateText.setError("enter end date for report");
                    else
                        barChartEndDateText.setError(null);
                }
                else
                {
                    barChartStartDateText.setError(null);
                    calorieOverviewChartPie.setVisibility(View.INVISIBLE);
                    calorieOverviewChartBar.setVisibility(View.VISIBLE);
                    GetCaloriesDataForDates getCaloriesDataForDates = new GetCaloriesDataForDates();
                    getCaloriesDataForDates.execute(userId, barChartStartDateText.getText().toString(), barChartEndDateText.getText().toString());
                }

                break;
        }


    }


    private class GetCaloriesConsumed extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return RestClient.findCalorieReportData(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String searchResult) {

            String caloriesConsumedTotal = "";
            try {
                JSONArray jsonArray = new JSONArray(searchResult);
                double dCaloriesConsumed = new JSONArray(searchResult).getJSONObject(0).getDouble("totalCaloriesConsumed");
                double dCaloriesBurnt = new JSONArray(searchResult).getJSONObject(0).getDouble("totalCaloriesBurned");
                double dRemainingCalories = new JSONArray(searchResult).getJSONObject(0).getDouble("remainingCalories");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject caloriesConsumed = jsonArray.getJSONObject(i);
                    caloriesConsumedTotal = caloriesConsumed.getString("totalCaloriesConsumed");
                }
                List<PieEntry> entries = new ArrayList<>();
                entries.add(new PieEntry((float) dCaloriesConsumed, "Calories Consumed"));
                entries.add(new PieEntry((float) dCaloriesBurnt, "Calories Burned"));
                entries.add(new PieEntry((float) dRemainingCalories, "Remaining Calories"));

                PieDataSet set = new PieDataSet(entries, "Your Calorie Data");
                set.setValueFormatter(new PercentFormatter(calorieOverviewChartPie));
                PieData data = new PieData(set);

                calorieOverviewChartPie.setData(data);
                calorieOverviewChartPie.setUsePercentValues(true);

                calorieOverviewChartPie.getLegend().setEnabled(false);

                calorieOverviewChartPie.getDescription().setEnabled(false);
                set.setColors(ColorTemplate.COLORFUL_COLORS);
                calorieOverviewChartPie.invalidate();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private class GetCaloriesDataForDates extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            return RestClient.findReportDataForUserDateRange(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String searchResult) {

            calorieOverviewChartPie.setVisibility(View.INVISIBLE);

            calorieOverviewChartBar.getDescription().setEnabled(false);
            calorieOverviewChartBar.setPinchZoom(false);

            calorieOverviewChartBar.setDrawBarShadow(false);

            calorieOverviewChartBar.setDrawGridBackground(false);

            ArrayList dateValues = new ArrayList();
            ArrayList caloriesBurnedValues = new ArrayList();
            ArrayList caloriesConsumedValues = new ArrayList();
            try {
                String caloriesBurned = "";
                String caloriesConsumed ="";
                JSONArray jsonArray = new JSONArray(searchResult);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject reportData = jsonArray.getJSONObject(i);
                    dateValues.add(reportData.getJSONObject("reportPK").getString("reportDate").substring(0,10));
                    caloriesBurned = reportData.getString("caloriesBurned");
                    caloriesConsumed = reportData.getString("caloriesConsumed");
                    Float f = Float.parseFloat(caloriesBurned);
                    caloriesBurnedValues.add(new BarEntry(i, Float.parseFloat(caloriesBurned)));
                    caloriesConsumedValues.add(new BarEntry(i, Float.parseFloat(caloriesConsumed)));
                }

                // bar width and stuff
                float groupSpace = 0.4f;
                float barSpace = 0f;
                float barWidth = 0.3f;
                int groupCount = jsonArray.length();

                BarDataSet set1, set2;
                set1 = new BarDataSet(caloriesBurnedValues, "Burned");
                set1.setColor(Color.rgb(139, 195, 74));
                set2 = new BarDataSet(caloriesConsumedValues, "Consumed");
                set2.setColor(Color.rgb(255, 87, 34));
                BarData data = new BarData(set1, set2);
                data.setValueFormatter(new LargeValueFormatter());
                calorieOverviewChartBar.setData(data);
                calorieOverviewChartBar.getBarData().setBarWidth(barWidth);
                calorieOverviewChartBar.getXAxis().setAxisMinimum(0);
                calorieOverviewChartBar.getXAxis().setAxisMaximum(0 + calorieOverviewChartBar.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                calorieOverviewChartBar.groupBars(0, groupSpace, barSpace);
                calorieOverviewChartBar.getData().setHighlightEnabled(false);
                calorieOverviewChartBar.invalidate();

                //Setting the legend
                Legend l = calorieOverviewChartBar.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(true);
                l.setYOffset(20f);
                l.setXOffset(0f);
                l.setYEntrySpace(0f);
                l.setTextSize(8f);

                //X-axis
                XAxis xAxis = calorieOverviewChartBar.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisMaximum(jsonArray.length());
                xAxis.setGranularityEnabled(true);
                xAxis.setCenterAxisLabels(true);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dateValues));
                //Y-axis
                calorieOverviewChartBar.getAxisRight().setEnabled(false);
                YAxis leftAxis = calorieOverviewChartBar.getAxisLeft();
                leftAxis.setValueFormatter(new LargeValueFormatter());
                leftAxis.setDrawGridLines(true);
                leftAxis.setSpaceTop(35f);
                leftAxis.setAxisMinimum(0f);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }



}



