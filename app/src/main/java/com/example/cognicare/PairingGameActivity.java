package com.example.cognicare;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PairingGameActivity extends AppCompatActivity {

    private TextView timerTextView, movesTextView, messageTextView, taskTextView;
    private ArrayList<Button> buttons;
    private Button bckbutton;
    private int[] pairs;
    private CountDownTimer timer;
    private long startTime;
    private int currentLevel = 0;
    private int maxLevels = 6;
    private int movesLeft = 5;
    private List<String> functionalLevelsPool;
    private List<String> chosenFunctionalLevels;
    // Which kind of level is active
    private enum LevelKind { FUNCTIONAL, CATEGORY }
    private LevelKind currentLevelKind;
    // Track selections
    private final ArrayList<Button> selectedButtons = new ArrayList<>();
    // For functional levels
    private final Map<String, String> functionalPairs = new HashMap<>();
    // For category levels
    private final Map<String, String> wordToCategory = new HashMap<>();
    private int requiredMatchSize = 2;
    FrameLayout instructionOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing_game);
        instructionOverlay = findViewById(R.id.instructions);

        movesTextView = findViewById(R.id.movesTextView);
        //messageTextView = findViewById(R.id.messageTextView);
        taskTextView = findViewById(R.id.taskTextView);
        bckbutton = findViewById(R.id.bckbutton);

        buttons = new ArrayList<>();
        buttons.add(findViewById(R.id.button1));
        buttons.add(findViewById(R.id.button2));
        buttons.add(findViewById(R.id.button3));
        buttons.add(findViewById(R.id.button4));
        buttons.add(findViewById(R.id.button5));
        buttons.add(findViewById(R.id.button6));
        attachClickHandlers();

        pairs = new int[buttons.size() / 2]; // Assuming an even number of buttons

        Button btnOkStart = findViewById(R.id.btnStart);
        btnOkStart.setOnClickListener(v -> {
            instructionOverlay.setVisibility(View.GONE);
            initializeGame(); // your initializeGame()
        });

        //initializeGame();

        bckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(PairingGameActivity.this, MainActivity.class));
            }
        });
    }

    private void attachClickHandlers() {
        View.OnClickListener listener = v -> {
            Button clicked = (Button) v;
            if (clicked.getVisibility() != View.VISIBLE) return;
            // Toggle off if already selected
            if (selectedButtons.contains(clicked)) {
                selectedButtons.remove(clicked);
                clicked.setBackgroundColor(getResources().getColor(R.color.button_default));
                return;
            }

            // Select (do NOT disable)
            selectedButtons.add(clicked);
            clicked.setBackgroundColor(getResources().getColor(R.color.button_selected));

            // Wait until we have the required number of selections (2 or 3)
            if (currentLevelKind == LevelKind.FUNCTIONAL) {
                // Functional levels are always pairs
                if (selectedButtons.size() == 2) {
                    String w1 = selectedButtons.get(0).getText().toString();
                    String w2 = selectedButtons.get(1).getText().toString();
                    boolean correct = w2.equals(functionalPairs.get(w1));

                    if (correct) {
                        showToast("✅ Correct!");
                        for (Button b : selectedButtons) {
                            b.setVisibility(View.INVISIBLE);
                            b.setBackgroundColor(getResources().getColor(R.color.button_default));
                        }
                        selectedButtons.clear();
                        if (areAllButtonsCleared()) startNextLevel();
                    } else {
                        resetSelection();
                    }
                }
            } else { // CATEGORY
                if (!selectedButtons.isEmpty()) {
                    // figure out how many words belong to this category
                    String category = wordToCategory.get(selectedButtons.get(0).getText().toString());
                    int targetSize = 0;
                    for (String w : wordToCategory.keySet()) {
                        if (wordToCategory.get(w).equals(category)) {
                            targetSize++;
                        }
                    }

                    // only check once the user has selected all words of that category
                    if (selectedButtons.size() == targetSize) {
                        boolean correct = allSameCategory(selectedButtons);

                        if (correct) {
                            showToast("✅ Correct!");
                            for (Button b : selectedButtons) {
                                b.setVisibility(View.INVISIBLE);
                                b.setBackgroundColor(getResources().getColor(R.color.button_default));
                            }
                            selectedButtons.clear();
                            if (areAllButtonsCleared()) startNextLevel();
                        } else {
                            resetSelection();
                        }
                    }
                }
            }

        };

        for (Button b : buttons) {
            b.setOnClickListener(listener);
        }
    }

    private void resetSelection() {
        for (Button b : selectedButtons) {
            b.setEnabled(true);
            b.setBackgroundColor(getResources().getColor(R.color.button_default));
        }
        selectedButtons.clear();
        handleWrongMatch();
    }


    // Helper: all selected buttons belong to the same category
    private boolean allSameCategory(List<Button> selected) {
        String cat = null;
        for (Button b : selected) {
            String w = b.getText().toString();
            String c = wordToCategory.get(w);
            if (c == null) return false;
            if (cat == null) cat = c;
            else if (!cat.equals(c)) return false;
        }
        return true;
    }

    private boolean areAllButtonsCleared() {
        for (Button b : buttons) {
            if (b.getVisibility() == View.VISIBLE) return false;
        }
        return true;
    }


    private void generateCategoryLevel() {
        currentLevelKind = LevelKind.CATEGORY;
        wordToCategory.clear();
        functionalPairs.clear();

        // Example pools (fill yours as arrays/ArrayLists earlier)
        List<String> fruits = Arrays.asList("Apple", "Orange", "Banana", "Grapes", "Mango", "Pear", "Peach", "Kiwi", "Lemon", "Cherry");
        List<String> vegetables = Arrays.asList("Kale", "Pepper", "Carrot", "Onion", "Peas", "Spinach", "Peas", "Cabbage", "Lettuce", "Potato");
        List<String> utensils = Arrays.asList("Ladle", "Whisk", "Spoon", "Fork", "Knife", "Pan", "Pot", "Spatula", "Grater", "Tongs");
        List<String> tools = Arrays.asList("Hammer", "Wrench", "Screwdriver", "Drill", "Saw", "Pliers", "Chisel", "Level", "Tape", "Axe");

        List<String> selectedWords = new ArrayList<>();

        Random random = new Random();
        int numofCategories = random.nextInt(2) + 2;

        // Pick 2 categories at random
        List<List<String>> categories = Arrays.asList(fruits, vegetables, utensils, tools);
        Collections.shuffle(categories);

        List<String> cat1 = categories.get(0);
        List<String> cat2 = categories.get(1);

        // Each category: 3 random words
        if(numofCategories == 2){
            int numofWords = random.nextInt(2) + 3;
            if(numofWords == 3){
                requiredMatchSize = 3;
                Collections.shuffle(cat1); Collections.shuffle(cat2);
                selectedWords.addAll(cat1.subList(0, 3));
                selectedWords.addAll(cat2.subList(0, 3));

                // Record categories
                for (String w : cat1.subList(0, 3)) wordToCategory.put(w, "Cat1");
                for (String w : cat2.subList(0, 3)) wordToCategory.put(w, "Cat2");
            }else if(numofWords == 4){
                requiredMatchSize = 4; //will expect user to also select 4 for the 2
                Collections.shuffle(cat1); Collections.shuffle(cat2);
                selectedWords.addAll(cat1.subList(0, 4));
                selectedWords.addAll(cat2.subList(0, 2));

                // Record categories
                for (String w : cat1.subList(0, 4)) wordToCategory.put(w, "Cat1");
                for (String w : cat2.subList(0, 2)) wordToCategory.put(w, "Cat2");
            }
        }

        if(numofCategories == 3){
            requiredMatchSize = 2;
            List<String> cat3 = categories.get(3);
            Collections.shuffle(cat1); Collections.shuffle(cat2); Collections.shuffle(cat3);
            selectedWords.addAll(cat1.subList(0, 2));
            selectedWords.addAll(cat2.subList(0, 2));
            selectedWords.addAll(cat3.subList(0, 2));

            // Record categories
            for (String w : cat1.subList(0, 2)) wordToCategory.put(w, "Cat1");
            for (String w : cat2.subList(0, 2)) wordToCategory.put(w, "Cat2");
            for (String w : cat3.subList(0, 2)) wordToCategory.put(w, "Cat3");
        }

        Collections.shuffle(selectedWords);

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setText(selectedWords.get(i).trim());
            buttons.get(i).setVisibility(View.VISIBLE);
            buttons.get(i).setEnabled(true);
        }

        taskTextView.setText("Match items from the same category!");
    }

    private void generateFunctionalLevel(String levelName) {
        currentLevelKind = LevelKind.FUNCTIONAL;
        requiredMatchSize = 2;
        functionalPairs.clear();
        wordToCategory.clear();
        String action = "do something";

        List<String> words = new ArrayList<>();

        switch (levelName) {
            case "cleanHouse":
                action = "clean the house";
                words.addAll(Arrays.asList("Mop", "Floor", "Vacuum", "Carpets", "Wipe", "Windows"));
                functionalPairs.put("Mop", "Floor"); functionalPairs.put("Floor", "Mop");
                functionalPairs.put("Vacuum", "Carpets"); functionalPairs.put("Carpets", "Vacuum");
                functionalPairs.put("Wipe", "Windows"); functionalPairs.put("Windows", "Wipe");
                break;

            case "makeBreakfast":
                action = "make breakfast";
                words.addAll(Arrays.asList("Eggs", "Fry", "Toast", "Butter", "Milk", "Coffee"));
                functionalPairs.put("Eggs","Fry"); functionalPairs.put("Fry","Eggs");
                functionalPairs.put("Toast","Butter"); functionalPairs.put("Butter","Toast");
                functionalPairs.put("Milk","Coffee"); functionalPairs.put("Coffee","Milk");
                break;

            case "washDishes":
                action = "wash the dishes";
                words.addAll(Arrays.asList("Dishes", "Sponge", "Soap", "Sink"));
                functionalPairs.put("Soap","Sponge"); functionalPairs.put("Sponge","Soap");
                functionalPairs.put("Dishes","Sink"); functionalPairs.put("Sink","Dishes");
                break;

            case "driveCar":
                action = "drive the car";
                words.addAll(Arrays.asList("Remote","Key","Button","Ignition","Start","Engine"));
                functionalPairs.put("Ignition","Key"); functionalPairs.put("Key","Ignition");
                functionalPairs.put("Button","Remote"); functionalPairs.put("Remote","Button");
                functionalPairs.put("Start","Engine"); functionalPairs.put("Engine","Start");
                break;
        }

        Collections.shuffle(words);

        for (int i = 0; i < buttons.size(); i++) {
            if (i < words.size()) {
                buttons.get(i).setText(words.get(i));
                buttons.get(i).setVisibility(View.VISIBLE);
                buttons.get(i).setEnabled(true);
            } else {
                buttons.get(i).setVisibility(View.INVISIBLE);
            }
        }

        taskTextView.setText("Pair the related to words " + action + "!");
    }

    private void initializeGame() {
        // Reset
        currentLevel = 0;
        movesLeft = 5;

        // Pool of functional levels
        functionalLevelsPool = new ArrayList<>();
        functionalLevelsPool.add("cleanHouse");
        functionalLevelsPool.add("makeBreakfast");
        functionalLevelsPool.add("washDishes");
        functionalLevelsPool.add("driveCar");

        // Shuffle and pick 3 unique functional levels
        Collections.shuffle(functionalLevelsPool);
        chosenFunctionalLevels = functionalLevelsPool.subList(0, 3);

        // Start the first level
        startNextLevel();
    }

    private void startNextLevel() {
        if (currentLevel >= maxLevels) {
            // Game finished successfully
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("MESSAGE", "🎉 Congratulations! You completed all levels.");
            startActivity(intent);
            finish();
            return;
        }

        if (movesLeft <= 0) {
            // Game over
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("MESSAGE", "❌ Game Over! You ran out of lives.");
            startActivity(intent);
            finish();
            return;
        }

        if (currentLevel % 2 == 0) {
            // Even level → Category matching
            generateCategoryLevel();
        } else {
            // Odd level → Functional pairing
            String levelName = chosenFunctionalLevels.get((currentLevel - 1) / 2);
            generateFunctionalLevel(levelName);
        }

        currentLevel++;
    }

    private void handleWrongMatch() {
        movesLeft--;
        movesTextView.setText("Lives: " + movesLeft);
        if (movesLeft <= 0) {
            startNextLevel(); // Will trigger game over check
        } else {
            showToast("❌ Wrong! Lives left: " + movesLeft);
        }
    }

    private void showToast(String message) {
        Toast toast = new Toast(this);
        View view = View.inflate(this, R.layout.toast_layout, null);
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel(); // Cancel timer to avoid memory leaks
        }
    }
}
