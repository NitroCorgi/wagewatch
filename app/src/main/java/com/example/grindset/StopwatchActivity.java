package com.example.grindset;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

    private double monthlyWageEuro = 0.0;

    private final int daysOfCurrentMonth = getDaysOfCurrentMonth();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        Button setWageButton = findViewById(R.id.setWageButton);

        setWageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSetWage();
            }
        });

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
            monthlyWageEuro = savedInstanceState.getDouble("monthlyWageEuro");

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
        savedInstanceState.putDouble("monthlyWageEuro", monthlyWageEuro);
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

    public void onClickSetWage() {
        // Create an AlertDialog with an EditText field for numeric input
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Monthly Wage (Euro)")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String wageStr = input.getText().toString();
                        try {
                            monthlyWageEuro = Double.parseDouble(wageStr);
                            updateWageTextView();
                        } catch (NumberFormatException e) {
                            // Handle if the entered wage is not a valid number
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                })
                .show();
    }

    private void updateWageTextView() {
        // Format the monthly wage string
        String wageText = String.format(Locale.getDefault(), "Monthly Wage: %.2f euros", monthlyWageEuro);

        // Set the formatted text to the TextView
        TextView wageTextView = findViewById(R.id.wageTextView);
        wageTextView.setText(wageText);
    }

    private int getDaysOfCurrentMonth() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        // Set the calendar to the first day of the next month
        calendar.set(Calendar.MONTH, currentMonth + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Subtracting one day gets us the last day of the current month
        calendar.add(Calendar.DATE, -1);
        return calendar.get(Calendar.DAY_OF_MONTH);
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

                double earnedWage = (monthlyWageEuro / (daysOfCurrentMonth * 86400)) * seconds;
                String wageEarnedText = String.format(Locale.getDefault(), "During this time, you have earned %.5f EUR.", earnedWage);

                // Set the formatted text to the TextView
                TextView moneyEarnedTextView = findViewById(R.id.moneyEarnedTextView);
                moneyEarnedTextView.setText(wageEarnedText);

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