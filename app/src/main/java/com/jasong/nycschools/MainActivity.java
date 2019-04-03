package com.jasong.nycschools;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    // UI elements
    private ListView schoolListView;
    private ProgressBar spinner;
    private Button retryButton;

    // NYC Schools API addresses
    private static final String schoolListEndpoint = "https://data.cityofnewyork.us/resource/s3k6-pzi2.json";
    private static final String satScoresEndpoint = "https://data.cityofnewyork.us/resource/f9bf-2cp4.json";

    // Lists to hold school names and score lists
    private ArrayList<String> schoolList;
    private ArrayList<SchoolScores> scoresList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up ListView
        this.schoolListView = (ListView) findViewById(R.id.schoolListView);

        // Set up a loading indicator while the data is retrieved
        this.spinner = (ProgressBar) findViewById(R.id.loadingSpinner);
        this.spinner.setVisibility(View.VISIBLE);

        // Set up retry button and its onClickListener and hide it until an error occurs
        this.retryButton = (Button) findViewById(R.id.retryButton);
        this.retryButton.setVisibility(View.INVISIBLE);
        this.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the button itself
                v.setVisibility(View.INVISIBLE);

                // Display the spinner again as the download is attempted
                ProgressBar spinner = (ProgressBar) findViewById(R.id.loadingSpinner);
                spinner.setVisibility(View.VISIBLE);

                // Get school list from API
                GetJsonFromApiTask schoolNamesTask = new GetJsonFromApiTask(MainActivity.this);
                schoolNamesTask.execute(schoolListEndpoint);
            }
        });

        // Get school list from API
        GetJsonFromApiTask schoolNamesTask = new GetJsonFromApiTask(this);
        schoolNamesTask.execute(schoolListEndpoint);
    }

    // Given more time, I would like to use a RecyclerView rather than a ListView on this Activity
    private void setUpListView() {
        // Populates the ListView with the list of school names
        ListAdapter schoolListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,this.schoolList);
        schoolListView.setAdapter(schoolListAdapter);

        this.schoolListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected school name as String
                String selectedSchool = String.valueOf(parent.getItemAtPosition(position));

                // Obtain the list of scores for the specified school name
                SchoolScores scores = getScoresForSchool(selectedSchool);

                // Display the scores screen if there exists scores for the specified school
                if(scores != null) {
                    Intent scoresActivityIntent = new Intent(getApplicationContext(), ScoresActivity.class);

                    scoresActivityIntent.putExtra("schoolName",scores.getSchoolName());
                    scoresActivityIntent.putExtra("math",scores.getAvgMathScore());
                    scoresActivityIntent.putExtra("writing",scores.getAvgWritingScore());
                    scoresActivityIntent.putExtra("reading",scores.getAvgReadingScore());

                    startActivity(scoresActivityIntent);
                } else {
                    // Otherwise display a message that no scores exist for the specified school
                    Toast.makeText(MainActivity.this, "No scores exist for " + selectedSchool,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // Returns the list of SAT scores for the provided school name, or null if none exist
    private SchoolScores getScoresForSchool(String schoolName) {
        for(SchoolScores scores : scoresList) {
            if (scores.getSchoolName().equalsIgnoreCase(schoolName)) {
                return scores;
            }
        }
        return null;
    }

    // Called each time an instance of GetJsonFromApiTask completes
    public void apiDownloadCompleted(JSONArray json, String endpoint) {
        // If no data was returned, display an error and do not continue. Also show the retry button
        if(json == null) {
            Toast.makeText(MainActivity.this,"An error occurred while downloading school data",Toast.LENGTH_SHORT).show();

            this.spinner.setVisibility(View.INVISIBLE);
            this.retryButton.setVisibility(View.VISIBLE);

            return;
        }

        switch(endpoint) {
            // Completed downloading school name list
            case schoolListEndpoint:
                try {
                    // Iterate over each school's JSON object
                    int counter = 0;
                    this.schoolList = new ArrayList<>();

                    while(counter < json.length()) {
                        // Pull the school name from the JSON object and add it to the list of school names
                        JSONObject currentElement = json.getJSONObject(counter);
                        this.schoolList.add(currentElement.getString("school_name"));

                        counter++;
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"An error occurred while processing the list of school names",Toast.LENGTH_SHORT).show();
                    this.spinner.setVisibility(View.INVISIBLE);
                    this.retryButton.setVisibility(View.VISIBLE);
                }

                // Get SAT score list from API now that the school name list has completed downloading
                GetJsonFromApiTask satScoresTask = new GetJsonFromApiTask(this);
                satScoresTask.execute(satScoresEndpoint);

                break;
            // Completed downloading SAT score list
            case satScoresEndpoint:
                try {
                    // Iterate over each school's JSON object
                    int counter = 0;
                    this.scoresList = new ArrayList<>();

                    while(counter < json.length()) {
                        // Pull the school name and score list from the JSON object and create a SchoolScores object containing the data
                        JSONObject currentElement = json.getJSONObject(counter);

                        String name = currentElement.getString("school_name");
                        String math = currentElement.getString("sat_math_avg_score");
                        String writing = currentElement.getString("sat_writing_avg_score");
                        String reading = currentElement.getString("sat_critical_reading_avg_score");

                        // Add the SchoolScores object to the list of scores
                        this.scoresList.add(new SchoolScores(name, math, writing, reading));

                        counter++;
                    }

                    // Sort school name list alphabetically
                    Collections.sort(this.schoolList, String.CASE_INSENSITIVE_ORDER);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this,"An error occurred while processing the list of SAT scores",Toast.LENGTH_SHORT).show();
                    this.spinner.setVisibility(View.INVISIBLE);
                    this.retryButton.setVisibility(View.VISIBLE);
                }

                // Now that both API downloads have completed, the ListView can be populated, and the loading indicator can be hidden
                this.spinner.setVisibility(View.INVISIBLE);
                this.setUpListView();

                break;
        }
    }
}
