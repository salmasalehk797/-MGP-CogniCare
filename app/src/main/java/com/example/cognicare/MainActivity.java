package com.example.cognicare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

//import com.example.cognicare.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //private ActivityMainBinding binding;
    private YourViewModel viewModel;
    private UserDao userDao;
    //private static AppDatabase database;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "MyPrefs";
    private static final String IS_SETUP_COMPLETED = "isSetupCompleted";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(YourViewModel.class);

        super.onCreate(savedInstanceState);
        //database = Room.databaseBuilder(this, AppDatabase.class, "my-database").allowMainThreadQueries() // For simplicity (not recommended for production).build();

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        if (!isSetupCompleted()) {
            // Show the one-time setup layout
            View oneTimeSetupLayout = findViewById(R.id.oneTimeSetupLayout);
            View mainContentLayout = findViewById(R.id.mainContentLayout);

            oneTimeSetupLayout.setVisibility(View.VISIBLE);
            mainContentLayout.setVisibility(View.GONE);

            Button completeSetupButton = findViewById(R.id.setupbtn);
            completeSetupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, FormActivity.class));
                    // Save the setup completion flag
                    setSetupCompleted(true);

                    // Hide the one-time setup layout
                    oneTimeSetupLayout.setVisibility(View.GONE);
                    mainContentLayout.setVisibility(View.VISIBLE);
                }
            });
        }


        Button numbersGameButton = findViewById(R.id.numbersgamebtn);
        Button dailyQuizButton = findViewById(R.id.dailyquizbtn);
        Button personalButton = findViewById(R.id.personalbtn);
        Button pairingGameButton = findViewById(R.id.paringgamebtn);

        numbersGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NumbersGameActivity.class));
            }
        });

        dailyQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DailyQuizManager.canTakeQuiz(MainActivity.this)) {
                    // Start the quiz activity
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    startActivity(intent);
                } else {
                    // Quiz already taken today
                    Toast.makeText(MainActivity.this, "Quiz already taken today! Come back tomorrow.", Toast.LENGTH_SHORT).show();
                }
                //startActivity(new Intent(MainActivity.this, QuizActivity.class));
            }
        });

        pairingGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PairingGameActivity.class));
            }
        });

        personalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
            }
        });

        // 🔹 Check for incoming message
        String message = getIntent().getStringExtra("MESSAGE");
        if (message != null && !message.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

    }

//    public static AppDatabase getDatabase() {
//        return database;
//    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Button numbersGameButton = findViewById(R.id.numbersgamebtn);
        Button dailyQuizButton = findViewById(R.id.dailyquizbtn);
        Button personalButton = findViewById(R.id.personalbtn);
        Button pairingGameButton = findViewById(R.id.paringgamebtn);

        numbersGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NumbersGameActivity.class));
            }
        });

        dailyQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DailyQuizManager.canTakeQuiz(MainActivity.this)) {
                    // Start the quiz activity
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    startActivity(intent);
                } else {
                    // Quiz already taken today
                    Toast.makeText(MainActivity.this, "Quiz already taken today! Come back tomorrow.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        pairingGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PairingGameActivity.class));
            }
        });

        personalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PersonalInfoActivity.class));
            }
        });
    }


    private boolean isSetupCompleted() {
        return sharedPreferences.getBoolean(IS_SETUP_COMPLETED, false);
    }

    private void setSetupCompleted(boolean isCompleted) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_SETUP_COMPLETED, isCompleted);
        editor.apply();
    }


}