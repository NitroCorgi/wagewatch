package com.example.grindset;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Locale;

import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class StopwatchActivity extends AppCompatActivity {

    // Number of seconds displayed
    // on the stopwatch.
    private int seconds = 0;

    // Is the stopwatch running?
    private boolean running;

    private boolean wasRunning;

    private Calendar startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);
        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds
                    = savedInstanceState
                    .getInt("seconds");
            running
                    = savedInstanceState
                    .getBoolean("running");
            wasRunning
                    = savedInstanceState
                    .getBoolean("wasRunning");

            startTime = (Calendar) savedInstanceState.getSerializable("startTime");
        }
        runTimer();
    }

    // Save the state of the stopwatch
    // if it's about to be destroyed.
    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState
                .putBoolean("running", running);
        savedInstanceState
                .putBoolean("wasRunning", wasRunning);
        savedInstanceState.putSerializable("startTime", startTime);
    }

    // If the activity is paused,
    // stop the stopwatch.
    @Override
    protected void onPause()
    {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    // If the activity is resumed,
    // start the stopwatch
    // again if it was running previously.
    @Override
    protected void onResume()
    {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }

    // Start the stopwatch running
    // when the Start button is clicked.
    // Below method gets called
    // when the Start button is clicked.
    public void onClickStart(View view)
    {
        running = true;
    }

    // Stop the stopwatch running
    // when the Stop button is clicked.
    // Below method gets called
    // when the Stop button is clicked.
    public void onClickStop(View view)
    {
        running = false;
    }

    // Reset the stopwatch when
    // the Reset button is clicked.
    // Below method gets called
    // when the Reset button is clicked.
    public void onClickReset(View view)
    {
        running = false;
        seconds = 0;
    }

    public void onClickSetStartTime(View view) {
        final Calendar currentDateAndTime = Calendar.getInstance();

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startTime = Calendar.getInstance();
                startTime.set(Calendar.YEAR, year);
                startTime.set(Calendar.MONTH, monthOfYear);
                startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(StopwatchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startTime.set(Calendar.MINUTE, minute);
                    }
                }, currentDateAndTime.get(Calendar.HOUR_OF_DAY), currentDateAndTime.get(Calendar.MINUTE), true).show();
            }
        }, currentDateAndTime.get(Calendar.YEAR), currentDateAndTime.get(Calendar.MONTH), currentDateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Sets the NUmber of seconds on the timer.
    // The runTimer() method uses a Handler
    // to increment the seconds and
    // update the text view.
    private void runTimer()
    {

        // Get the text view.
        final TextView timeView
                = (TextView)findViewById(
                R.id.time_view);


        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(new Runnable() {
            @Override

            public void run()
            {
                if (startTime != null) {
                    long elapsedTime = Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis();
                    seconds = (int) (elapsedTime / 1000);


                    TextView timePassedTextView = findViewById(R.id.timePassedTextView);
                    String startTimeString = startTime.getTime().toString(); // Example start time string
                    String introText = getString(R.string.Intro, startTimeString);
                    timePassedTextView.setText(introText);
                }

                int days = seconds / 86400;
                int hours = (seconds / 3600) % 24;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d Days and %d:%02d:%02d", days, hours,
                                minutes, secs);

                // Set the text view text.
                timeView.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++;
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }



}