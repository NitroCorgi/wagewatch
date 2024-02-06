package com.example.grindset;

import android.app.AlertDialog;
import android.os.Handler;
import android.view.View;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class StopwatchActivity extends AppCompatActivity {

    private int seconds = 0;
    private Calendar startTime;
    private final int daysOfCurrentMonth = getDaysOfCurrentMonth();
    private double monthlyWageEuro = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopwatch);

        Button setWageButton = findViewById(R.id.setWageButton);

        setWageButton.setOnClickListener(this::onClickSetWage);

        if (savedInstanceState != null) {
            seconds
                    = savedInstanceState
                    .getInt("seconds");
            monthlyWageEuro = savedInstanceState.getDouble("monthlyWageEuro");

            startTime = (Calendar) savedInstanceState.getSerializable("startTime");
        }
        startTime = getFirstDayOfMonth();
        runTimer();
    }

    @Override
    public void onSaveInstanceState(
            @NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState
                .putInt("seconds", seconds);
        savedInstanceState.putDouble("monthlyWageEuro", monthlyWageEuro);
        savedInstanceState.putSerializable("startTime", startTime);
    }


    public void onClickSetWage(View view) {
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Monthly Wage (Euro)")
                .setView(input)
                .setPositiveButton("OK", (dialog, whichButton) -> {
                    String wageStr = input.getText().toString();
                    try {
                        monthlyWageEuro = Double.parseDouble(wageStr);
                        updateWageTextView();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancel", (dialog, whichButton) -> {
                })
                .show();
    }

    private void updateWageTextView() {
        String wageText = String.format(Locale.getDefault(), "Monthly Wage: %.2f EUR", monthlyWageEuro);

        TextView wageTextView = findViewById(R.id.wageTextView);
        wageTextView.setText(wageText);
    }

    private Calendar getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    private int getDaysOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.MONTH, currentMonth + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        calendar.add(Calendar.DATE, -1);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }


    private void runTimer()
    {

        final TextView timeView
                = (TextView)findViewById(
                R.id.time_view);

        final Handler handler
                = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run()
            {
                if (startTime != null) {
                    long elapsedTime = Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis();
                    seconds = (int) (elapsedTime / 1000);


                    TextView timePassedTextView = findViewById(R.id.timePassedTextView);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(startTime.getTime());
                    String introText = getString(R.string.Intro, formattedDate);
                    timePassedTextView.setText(introText);
                }

                int days = seconds / 86400;
                int hours = (seconds / 3600) % 24;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d Days, %d:%02d:%02d", days, hours,
                                minutes, secs);

                timeView.setText(time);

                double earnedWage = (monthlyWageEuro / (daysOfCurrentMonth * 86400)) * seconds;
                String wageEarnedText = String.format(Locale.getDefault(), "During this month, you have earned \n %.5f EUR.", earnedWage);

                TextView moneyEarnedTextView = findViewById(R.id.moneyEarnedTextView);
                moneyEarnedTextView.setText(wageEarnedText);

                seconds++;

                handler.postDelayed(this, 1000);
            }
        });
    }

}