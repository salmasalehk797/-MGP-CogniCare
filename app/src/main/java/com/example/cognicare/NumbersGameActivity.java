package com.example.cognicare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class NumbersGameActivity extends AppCompatActivity {

    private TextView numberTextView, livesTextView;
    private Button[] squareButtons;
    private Button bckbutton;
    private ArrayList<Integer> numbersOnButtons;
    private int lives, level, targetSum, currentSum;
    FrameLayout instructionOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers_game);
        instructionOverlay = findViewById(R.id.instructions);

        numberTextView = findViewById(R.id.numberTextView);
        livesTextView = findViewById(R.id.livesTextView);
        squareButtons = new Button[6];
        squareButtons[0] = findViewById(R.id.squareButton1);
        squareButtons[1] = findViewById(R.id.squareButton2);
        squareButtons[2] = findViewById(R.id.squareButton3);
        squareButtons[3] = findViewById(R.id.squareButton4);
        squareButtons[4] = findViewById(R.id.squareButton5);
        squareButtons[5] = findViewById(R.id.squareButton6);
        bckbutton = findViewById(R.id.bvkbutton);

        numbersOnButtons = new ArrayList<>();
        lives = 5;
        level = 1;
        currentSum = 0;

        Button btnOkStart = findViewById(R.id.btnStart);
        btnOkStart.setOnClickListener(v -> {
            instructionOverlay.setVisibility(View.GONE);
            generateNumbers(level);
        });

        bckbutton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("MESSAGE", "🎉 Congratulations! You've successfully completed until level " + level +", with " + lives + " lives left.");
            startActivity(intent);
            finish();
        });

        for (int i = 0; i < squareButtons.length; i++) {
            final int index = i;
            squareButtons[i].setOnClickListener(v -> toggleSelection(index));
        }
    }

    /** Handle selecting/unselecting buttons */
    private void toggleSelection(int index) {
        Button btn = squareButtons[index];
        int value = Integer.parseInt(btn.getText().toString());

        if (btn.isSelected()) {
            // Unselect → subtract value
            btn.setSelected(false);
            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light)); // reset color
            currentSum -= value;
        } else {
            // Select → add value
            btn.setSelected(true);
            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light)); // highlight
            currentSum += value;
        }

        // Check conditions
        if (currentSum == targetSum) {
            showToast("✅ Correct!", 3000);
            level++;
            generateNumbers(level);
            currentSum = 0;
        } else if (currentSum > targetSum) {
            showToast("❌ Sum (" + currentSum + ") is too high! Resetting.", 5000);
            lives--;
            livesTextView.setText("Lives: " + lives);
            resetSelections();
            currentSum = 0;

            if (lives == 0) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("MESSAGE", "👾 Game Over! Level reached: " + level);
                startActivity(intent);
                finish();
            }
        }
    }

    /** Generate numbers and place them on buttons */
    private void generateNumbers(int level) {
        Random random = new Random();
        numbersOnButtons.clear();

        // Difficulty scaling
        int maxNumber = Math.min(10 + level * 2, 50);
        int numCorrect = random.nextInt(3) + 2; // 2–4 numbers must sum to target
        ArrayList<Integer> correctSet = new ArrayList<>();

        targetSum = 0;
        for (int i = 0; i < numCorrect; i++) {
            int n = random.nextInt(maxNumber) + 1;
            correctSet.add(n);
            targetSum += n;
        }

        // Fill rest with random numbers
        numbersOnButtons.addAll(correctSet);
        while (numbersOnButtons.size() < 6) {
            numbersOnButtons.add(random.nextInt(maxNumber) + 1);
        }

        Collections.shuffle(numbersOnButtons);

        // Assign to buttons
        for (int i = 0; i < 6; i++) {
            squareButtons[i].setText(String.valueOf(numbersOnButtons.get(i)));
            squareButtons[i].setSelected(false);
            squareButtons[i].setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            squareButtons[i].setEnabled(true);
        }

        numberTextView.setText("Click numbers that sum to: " + targetSum);
        livesTextView.setText("Lives: " + lives);
    }

    /** Reset selections after wrong attempt */
    private void resetSelections() {
        for (Button btn : squareButtons) {
            btn.setSelected(false);
            btn.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            btn.setEnabled(true);
        }
    }

    /** Custom toast */
    private void showToast(String message, int durationInMillis) {
        Toast toast = new Toast(this);
        View view = View.inflate(this, R.layout.toast_layout, null);
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);

        // Show it repeatedly until duration passes
        final int interval = 1000; // 1 second
        final long endTime = System.currentTimeMillis() + durationInMillis;

        Handler handler = new Handler();
        Runnable showRunnable = new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() < endTime) {
                    toast.show();
                    handler.postDelayed(this, interval);
                }
            }
        };
        handler.post(showRunnable);
    }
}
