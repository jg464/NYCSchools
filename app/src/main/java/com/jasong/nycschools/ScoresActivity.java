package com.jasong.nycschools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ScoresActivity extends AppCompatActivity {

    // UI elements
    private TextView schoolNameBox;
    private TextView mathScoreBox;
    private TextView writingScoreBox;
    private TextView readingScoreBox;

    // School score data
    private String schoolName;
    private String math;
    private String writing;
    private String reading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        // Add a back button to the ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.scoreScreenTitle);

        // Get instances of the text boxes to populate with data
        schoolNameBox = (TextView) findViewById(R.id.schoolNameBox);
        mathScoreBox = (TextView) findViewById(R.id.mathScoreBox);
        writingScoreBox = (TextView) findViewById(R.id.writingScoreBox);
        readingScoreBox = (TextView) findViewById(R.id.readingScoreBox);

        // Set the school score data from the Intent
        this.schoolName = this.getIntent().getStringExtra("schoolName");
        this.math = this.getIntent().getStringExtra("math");
        this.writing = this.getIntent().getStringExtra("writing");
        this.reading = this.getIntent().getStringExtra("reading");

        this.setUpUI();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // If the back button is pressed, finish this activity and return to MainActivity
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Sets the text in the text boxes after the data has been populated from the Intent
    private void setUpUI() {
        this.schoolNameBox.setText(this.schoolName);
        this.mathScoreBox.setText("Average math score: " + math);
        this.writingScoreBox.setText("Average writing score: " + writing);
        this.readingScoreBox.setText("Average reading score: " + reading);
    }


}
