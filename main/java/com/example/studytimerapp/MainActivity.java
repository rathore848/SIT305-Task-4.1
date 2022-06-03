package com.example.studytimerapp;

import static android.widget.Toast.makeText;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studytimerapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    SharedPreferences store_1, store_2;
    boolean in_progress, rest;
    long time_taken, pause, stop;
    long sec, sec_2, sec_3, mins, mins_2, mins_3, hrs, hrs_2, hrs_3;
    String clock, task, plain, check, username, previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        store_1 = getSharedPreferences("com.example.StudyTimerApp", MODE_PRIVATE);
        store_2 = getSharedPreferences("com.example.StudyTimerAp", MODE_PRIVATE);
        checkStorage();
    }

    public void StartButton(View v){
        if (binding.editText.getText().toString().isEmpty())
        {
            makeText(this, "Name of the task", Toast.LENGTH_LONG).show();
        }
        else
        {
            if(!in_progress){
                binding.chronometer.setBase(SystemClock.elapsedRealtime() - pause);
                binding.chronometer.start();
                time_taken = SystemClock.elapsedRealtime() - pause;
                in_progress = true;
            }
        }
    }

    public void PauseButton(View v){
        if(in_progress){
            binding.chronometer.stop();
            pause = SystemClock.elapsedRealtime() - binding.chronometer.getBase();
            in_progress = false;
            rest = false;
        }
    }

    public void StopButton(View v){
        if(in_progress){
            binding.chronometer.stop();
            stop = SystemClock.elapsedRealtime() - binding.chronometer.getBase();
            sec = (stop/1000) % 60;
            mins = (stop/1000) / 60;
            hrs = (stop/1000) / 3600;
            clock = String.format("%02d:%02d:%02d", hrs, mins, sec);
            in_progress = false;
        }
        else
        {
            if(!rest)
            {
                sec_2 = (pause/1000) % 60;
                mins_2 = (pause/1000) / 60;
                hrs_2 = (pause/1000) / 3600;
                clock =  String.format("%02d:%02d:%02d", sec_2, mins_2, hrs_2);
                rest = true;
            }
        }
        binding.chronometer.setBase(SystemClock.elapsedRealtime());
        pause = 0;
        in_progress = false;
        stop = 0;
        binding.textView.setText("You spent "+ clock +" on " + binding.editText.getText().toString() + " previously.");

        SharedPreferences.Editor editor = store_1.edit();
        SharedPreferences.Editor editor1 = store_2.edit();
        editor.putString(plain, binding.editText.getText().toString());
        editor1.putString(check, clock);
        editor.apply();
        editor1.apply();
    }

    public void checkStorage()
    {
        username = store_1.getString(plain,"");
        previous = store_2.getString(check,"");
        binding.textView.setText("You spent "+ previous +" on " + username + " last time");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("Time", pause);
        outState.putBoolean("Running", in_progress);
        outState.putLong("Current", time_taken);
        outState.putString("Name", binding.editText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pause = savedInstanceState.getLong("Time");
        in_progress = savedInstanceState.getBoolean("Running");
        time_taken = savedInstanceState.getLong("Current");
        task = savedInstanceState.getString("Name");

        if(in_progress)
        {
            binding.chronometer.setBase(time_taken);
            binding.chronometer.start();
        }

        if (!in_progress)
        {
            sec_3 = (pause/1000) % 60;
            mins_3 = (pause/1000) / 60;
            hrs_3 = (pause/1000) / 3600;
            if (pause < 360000)
            {
                binding.chronometer.setText(String.format("%02d:%02d", mins_3, sec_3));
            }
            else
            {
                binding.chronometer.setText(String.format("%02d:%02d:%02d", hrs_3, mins_3, sec_3));
            }
        }
    }
}