package com.example.gjbdashboard;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.LegendLayout;
import com.anychart.anychart.Pie;
import com.anychart.anychart.ValueDataEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    int scrutiny = 0;
    int feasibility = 0;
    int approval = 0;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        AnyChartView deptWisePieChart = view.findViewById(R.id.deptWisePieChart);
        AnyChartView grievanceAnalysisPieChart = view.findViewById(R.id.grievanceAnalysisPieChart);

        setupPieChart(deptWisePieChart, view);
        setupPieChartGrievance(grievanceAnalysisPieChart);
        // Call the API and update charts directly
        makeApiRequestForChartData(deptWisePieChart, grievanceAnalysisPieChart);

        // Find the FloatingActionButton by ID
        FloatingActionButton fabDownload = view.findViewById(R.id.fabDownload);

        // Set a click listener for the FAB
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "FAB Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void makeApiRequestForChartData(AnyChartView dept, AnyChartView grievance) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://gmdwsb.indigidigital.in/api/public_dashboard";

        // Request a JSON response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("data")) {
                                JSONObject data = response.getJSONObject("data");
                                int zscrutiny = data.optInt("zscrutiny");
                                int zfeasibility = data.optInt("zfeasiblity");
                                int zapproval = data.optInt("zapproval");
                                int grievanceOpen = data.optInt("grievance_open");
                                int grievanceWip = data.optInt("grievance_wip");
                                int grievanceClosed = data.optInt("grievance_closed");

                                Log.d("zscrutiny", String.valueOf(zscrutiny));
                                Log.d("zfeasibility", String.valueOf(zfeasibility));
                                Log.d("zapproval", String.valueOf(zapproval));
                                Log.d("grievanceOpen", String.valueOf(grievanceOpen));
                                Log.d("grievanceWip", String.valueOf(grievanceWip));
                                Log.d("grievanceClosed", String.valueOf(grievanceClosed));
                                scrutiny = zscrutiny;
                                feasibility = zfeasibility;
                                approval = zapproval;

                                updatePieChart(dept, scrutiny, feasibility, approval);
                            } else {
                                // Handle the case where the 'data' key is not present in the response
                                Toast.makeText(getContext(), "Invalid response format", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            Toast.makeText(getContext(), "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void setupPieChart(AnyChartView anyChartView, View view) {
        if (anyChartView != null){
            Pie pie = AnyChart.pie();

            List<DataEntry> dataEntries = new ArrayList<>();

            dataEntries.add(new ValueDataEntry("Scrutiny Completed", 200));
            dataEntries.add(new ValueDataEntry("Feasibility Completed", 300));
            dataEntries.add(new ValueDataEntry("Authority Approved",500));

            pie.data(dataEntries);
            pie.getLabels().setPosition("center-top");
            pie.setTitle("DEPT. WISE ANALYSIS");
            pie.getLegend().setPosition("right").setItemsLayout(LegendLayout.VERTICAL).setAlign(String.valueOf(Paint.Align.CENTER));

            anyChartView.setChart(pie);

            final int delayMillis = 500;
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                public void run() {
                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Scrutiny Completed", scrutiny));
                    data.add(new ValueDataEntry("Feasibility Completed", 40));
                    data.add(new ValueDataEntry("Authority Approved", 100));
                    // apply new data to the series
                    pie.data(data);
                    handler.postDelayed(this, delayMillis);
                }
            };
            handler.postDelayed(runnable, delayMillis);
        }
    }

    private void updatePieChart(AnyChartView anyChartView, int scrutiny, int feasibility, int approval) {
        if (anyChartView != null) {
            Pie pie = AnyChart.pie();

            List<DataEntry> dataEntries = new ArrayList<>();
            dataEntries.add(new ValueDataEntry("Scrutiny Completed", scrutiny));
            dataEntries.add(new ValueDataEntry("Feasibility Completed", feasibility));
            dataEntries.add(new ValueDataEntry("Authority Approved", approval));

            pie.data(dataEntries);
            pie.getLabels().setPosition("center-top");
            pie.setTitle("DEPT. WISE ANALYSIS");
            pie.getLegend().setPosition("right").setItemsLayout(LegendLayout.VERTICAL).setAlign(String.valueOf(Paint.Align.CENTER));

            anyChartView.setChart(pie);
        }
    }

    private void setupPieChartGrievance(AnyChartView grievanceAnalysisPieChart) {
        if (grievanceAnalysisPieChart != null){
            Pie pie = AnyChart.pie();

            List<DataEntry> dataEntries = new ArrayList<>();
            dataEntries.add(new ValueDataEntry("Grievance Open", 145));
            dataEntries.add(new ValueDataEntry("Grievance Wip", 453));
            dataEntries.add(new ValueDataEntry("Grievance Solved", 321));

            pie.data(dataEntries);
            pie.getLabels().setPosition("center-top");
            pie.setTitle("GRIEVANCE ANALYSIS");
            pie.getLegend().setPosition("right").setItemsLayout(LegendLayout.VERTICAL).setAlign(String.valueOf(Paint.Align.CENTER));

            grievanceAnalysisPieChart.setChart(pie);
        }
    }
}

