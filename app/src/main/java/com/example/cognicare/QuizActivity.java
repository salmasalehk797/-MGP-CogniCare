package com.example.cognicare;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class QuizActivity extends AppCompatActivity {

    private TextView questionTextView, scoreTextView, countdownTextView;
    private EditText answerEditText;
    private Button nextButton, backbutton;

    // ==== State Machine ====
    private enum QuestionType { MEMORIZE, MATH, ODD_WORD, PERSONAL, RECALL, FINISHED }
    private QuestionType currentType = QuestionType.MEMORIZE;

    // Timers
    private CountDownTimer activeTimer;

    // Global score/state
    private int score = 0;
    private int correctMemorized = 0;

    // Phase counters
    private int mathLevel = 0;      // 1..5
    private int oddLevel = 0;       // 1..5
    private int personalLevel = 0;  // 1..5

    // Memorize words
    private List<String> wordBank;
    private List<String> memorizedWords; // 3 words selected at start

    // Expected answer for current question
    private String expectedAnswer = null; // used by math/odd/personal

    // For odd-word questions
    private final Random rng = new Random();

    // Track asked personal question indices to avoid repeats
    private final Set<Integer> askedPersonalIndices = new HashSet<>();

    // Personal Q/A
    private List<String> personalQuestions;
    private List<String> personalAnswers; // aligned with questions

    //Incorrect Answers in memorized words
    private List<String> incorrect_words;
    FrameLayout instructionOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        instructionOverlay = findViewById(R.id.instructions);

        questionTextView = findViewById(R.id.questionTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        countdownTextView = findViewById(R.id.countdownTextView);
        answerEditText = findViewById(R.id.answerEditText);
        nextButton = findViewById(R.id.nextButton);
        backbutton = findViewById(R.id.bckbutton);

        Button btnOkStart = findViewById(R.id.btnStart);
        btnOkStart.setOnClickListener(v -> {
            instructionOverlay.setVisibility(View.GONE);
            initializeBanks();
            startMemorizePhase();
        });

        backbutton.setOnClickListener(v -> {
            cancelActiveTimer();
            startActivity(new Intent(QuizActivity.this, MainActivity.class));
        });

        nextButton.setOnClickListener(v -> handleNextButtonClick());
    }

    // =========================
    // ======= FLOW CORE =======
    // =========================

    private void handleNextButtonClick() {
        switch (currentType) {
            case MEMORIZE:
                // User taps Next to skip waiting; proceed to math
                cancelActiveTimer();
                startMathPhase();
                break;
            case MATH:
                evaluateMathAnswer();
                break;
            case ODD_WORD:
                evaluateOddWordAnswer();
                break;
            case PERSONAL:
                evaluatePersonalAnswer();
                break;
            case RECALL:
                evaluateRecallAnswer();
                break;
            case FINISHED:
                endQuiz();
                break;
        }
    }

    // =========================
    // ===== MEMORIZE PHASE ====
    // =========================

    private void startMemorizePhase() {
        currentType = QuestionType.MEMORIZE;
        memorizedWords = getRandomWords(wordBank, 3);

        questionTextView.setText("Memorize the following words");
        answerEditText.setText(TextUtils.join(", ", memorizedWords));
        countdownTextView.setText("");

        startTimer(30_000, this::startMathPhase);
    }

    // =========================
    // ======= MATH PHASE ======
    // =========================

    private void startMathPhase() {
        // Move to next math level or transition to next phase
        if (mathLevel >= 5) { // finished math
            startOddWordPhase();
            return;
        }
        currentType = QuestionType.MATH;
        mathLevel++;

        clearInput();

        int numOperands = Math.min(2 + mathLevel / 2, 4); // level-based growth (2..4)
        List<Integer> operands = generateRandomOperands(numOperands);
        List<Character> operators = generateRandomOperators(numOperands - 1);
        int correct = calculateCorrectAnswer(operands, operators);
        expectedAnswer = String.valueOf(correct);

        questionTextView.setText(formatQuestion(operands, operators) + "\n(1 point)");

        startTimer(70_000, () -> {
            // time's up -> treat as incorrect and move on
            showToast("⏰ Time's up!", 2000);
            proceedAfterMath(false);
        });
    }

    private void evaluateMathAnswer() {
        String user = answerEditText.getText().toString().trim();
        boolean correct = false;
        if (!user.isEmpty()) {
            // numeric parse guard
            try {
                correct = user.equals(expectedAnswer);
            } catch (Exception ignored) { /* leave as false */ }
        }
        proceedAfterMath(correct);
    }

    private void proceedAfterMath(boolean correct) {
        cancelActiveTimer();
        if (correct) {
            score++;
            scoreTextView.setText("Score: " + score);
            showToast("Level " + mathLevel + "\n✅ Correct! (Current Score: " + score + ")", 5000);
        } else {
            showToast("Level " + mathLevel + "\n❌ Incorrect! (Correct Answer: " + expectedAnswer + ")", 5000);
        }
        startMathPhase();
    }

    // =========================
    // ===== ODD WORD PHASE ====
    // =========================

    private void startOddWordPhase() {
        if (oddLevel >= 5) { // finished odd-word
            startPersonalPhase();
            return;
        }
        currentType = QuestionType.ODD_WORD;
        oddLevel++;
        clearInput();

        // Build fresh category lists every question to avoid carry-over state
        List<String> fruits = new ArrayList<>(Arrays.asList("Apples", "Oranges", "Strawberries", "Watermelon", "Cherry", "Grapes"));
        List<String> veggies = new ArrayList<>(Arrays.asList("Kale", "Pepper", "Cucumber", "Carrot", "Onions", "Cabbage"));
        List<String> kitchenTools = new ArrayList<>(Arrays.asList("Ladle", "Whisk", "Grater", "Spatula", "Peeler", "Tongs"));
        List<String> workshopTools = new ArrayList<>(Arrays.asList("Hacksaw", "Wrench", "Hammer", "Chisel", "Crowbar", "Screwdriver"));

        List<List<String>> categories = Arrays.asList(fruits, veggies, kitchenTools, workshopTools);

        // pick two distinct categories: one related (3 words) and one opposite (1 word)
        int catA = rng.nextInt(categories.size());
        int catB;
        do { catB = rng.nextInt(categories.size()); } while (catB == catA);

        List<String> related = getRandomWords(categories.get(catA), 3);
        List<String> odd = getRandomWords(categories.get(catB), 1);

        // Fallback safety if banks too small
        if (related == null || odd == null) {
            related = getRandomWords(fruits, 3);
            odd = getRandomWords(veggies, 1);
        }

        List<String> all = new ArrayList<>();
        all.addAll(related);
        all.addAll(odd);
        Collections.shuffle(all);

        expectedAnswer = odd.get(0).toLowerCase();

        questionTextView.setText("Which of the following words doesn't belong?\n" + TextUtils.join(", ", all) + "\n(1 point)");

        startTimer(70_000, () -> {
            showToast("⏰ Time's up!", 2000);
            proceedAfterOddWord(false);
        });
    }

    private void evaluateOddWordAnswer() {
        String user = answerEditText.getText().toString().trim().toLowerCase();
        boolean correct = !TextUtils.isEmpty(user) && user.equals(expectedAnswer);
        proceedAfterOddWord(correct);
    }

    private void proceedAfterOddWord(boolean correct) {
        cancelActiveTimer();
        if (correct) {
            score++;
            scoreTextView.setText("Score: " + score);
            showToast("Level " + (mathLevel+oddLevel) + "\n✅ Correct! (Current Score: " + score + ")", 5000);
        } else {
            showToast("Level " + (mathLevel+oddLevel) + "\n❌ Incorrect! (Correct Answer: " + expectedAnswer + ")", 5000);
        }
        startOddWordPhase();
    }

    // =========================
    // ==== PERSONAL PHASE =====
    // =========================

    private void startPersonalPhase() {
        if (personalLevel >= 5) { // finished personal
            startRecallPhase();
            return;
        }
        currentType = QuestionType.PERSONAL;
        personalLevel++;
        clearInput();

        ensurePersonalBanks();

        // choose a question index not asked yet
        int idx = pickUnaskedPersonalIndex();
        askedPersonalIndices.add(idx);

        questionTextView.setText(personalQuestions.get(idx) + "\n(1 point)");
        expectedAnswer = personalAnswers.get(idx);

        startTimer(70_000, () -> {
            showToast("⏰ Time's up!", 2000);
            proceedAfterPersonal(false);
        });
    }

    private void evaluatePersonalAnswer() {
        String user = answerEditText.getText().toString().trim().toLowerCase();
        boolean correct = !TextUtils.isEmpty(expectedAnswer) && expectedAnswer.equalsIgnoreCase(user);
        proceedAfterPersonal(correct);
    }

    private void proceedAfterPersonal(boolean correct) {
        cancelActiveTimer();
        if (correct) {
            score++;
            scoreTextView.setText("Score: " + score);
            showToast("Level " + (mathLevel+oddLevel+personalLevel) + "\n✅ Correct! (Current Score: " + score + ")", 5000);
        } else {
            showToast("Level " + (mathLevel+oddLevel+personalLevel) + "\n❌ Incorrect! (Correct Answer: " + expectedAnswer + ")", 5000);
        }
        startPersonalPhase();
    }

    // =========================
    // ====== RECALL PHASE =====
    // =========================

    private void startRecallPhase() {
        currentType = QuestionType.RECALL;
        clearInput();
        countdownTextView.setText("");
        questionTextView.setText("Please repeat the words you memorized!\nSeparate words with spaces.\n(5 points per word)");
        nextButton.setText("Finish");
    }

    private void evaluateRecallAnswer() {
        incorrect_words = new ArrayList<>();
        String input = answerEditText.getText().toString().trim();

        Set<String> target = new HashSet<>();
        for (String w : memorizedWords) target.add(w.toLowerCase());

        Set<String> userSet = new HashSet<>();
        if (!input.isEmpty()) {
            String[] userWords = input.split("\\s+");
            for (String w : userWords) userSet.add(w.toLowerCase());
        }

        // Count correct
        correctMemorized = 0;
        for (String w : memorizedWords) {
            if (userSet.contains(w.toLowerCase())) {
                correctMemorized++;
            } else {
                incorrect_words.add(w); // missed word
            }
        }

        score += correctMemorized * 5;
        currentType = QuestionType.FINISHED;
        handleNextButtonClick();
    }

    private void endQuiz() {
        DailyQuizManager.markQuizTaken(this);
        String msg;

        if (correctMemorized == memorizedWords.size()) {
            msg = "🎉 You got all words right!\nFinal Score: " + score;
        } else if (correctMemorized > 0) {
            msg = "You got " + correctMemorized + " words right!\n"
                    + "Missed Words: " + TextUtils.join(", ", incorrect_words)
                    + "\nFinal Score: " + score;
        } else {
            msg = "❌ You missed all the words.\n"
                    + "Words were: " + TextUtils.join(", ", memorizedWords)
                    + "\nFinal Score: " + score;
        }

        showToast(msg, 5000);
        startActivity(new Intent(QuizActivity.this, MainActivity.class));
    }


    // =========================
    // ======= UTILITIES =======
    // =========================

    private void initializeBanks() {
        wordBank = new ArrayList<>(Arrays.asList(
                "Apple", "Chair", "Blue", "Table", "Car", "Dog", "Book", "Key", "Window", "Sun"
        ));
    }

    private void ensurePersonalBanks() {
        if (personalQuestions != null && personalAnswers != null) return;

        personalQuestions = new ArrayList<>();
        personalAnswers = new ArrayList<>();

        UserEntity user = MyApp.getDatabase().UserDao().getUser();
        // Names
        String[] fullNameParts = user.getFullName().trim().split("\\s+");
        String firstName = fullNameParts.length > 0 ? fullNameParts[0] : "";
        String lastName = fullNameParts.length > 1 ? fullNameParts[fullNameParts.length - 1] : "";
        // Birthdate -> age
        String birthDateString = user.getBirthdate().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int age = 0;
        try {
            LocalDate birthDate = LocalDate.parse(birthDateString, formatter);
            age = Period.between(birthDate, LocalDate.now()).getYears();
        } catch (Exception ignored) {}

        // Build questions
        personalQuestions.add("What is your first name?");
        personalQuestions.add("What is your last name?");
        personalQuestions.add("How old are you?");
        personalQuestions.add("How many siblings do you have?");
        personalQuestions.add("How many children do you have?");
        personalQuestions.add("How many grandchildren do you have?");
        personalQuestions.add("What is your mother's name?");
        personalQuestions.add("What is your father's name?");
        personalQuestions.add("What is your favorite color?");
        personalQuestions.add("What country do you currently live?");

        // Build answers (lowercased & trimmed to compare robustly)
        personalAnswers.add(firstName.toLowerCase());
        personalAnswers.add(lastName.toLowerCase());
        personalAnswers.add(String.valueOf(age));
        personalAnswers.add(String.valueOf(user.getNumSiblings()));
        personalAnswers.add(String.valueOf(user.getNumChildren()));
        personalAnswers.add(String.valueOf(user.getNumGrandchildren()));
        personalAnswers.add(safeLower(user.getMotherName()));
        personalAnswers.add(safeLower(user.getFatherName()));
        personalAnswers.add(safeLower(user.getFavoriteColor()));
        personalAnswers.add(safeLower(user.getCurrentLocation()));
    }

    private String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private int pickUnaskedPersonalIndex() {
        if (askedPersonalIndices.size() >= personalQuestions.size()) {
            askedPersonalIndices.clear();
        }
        int idx;
        do {
            idx = rng.nextInt(personalQuestions.size());
        } while (askedPersonalIndices.contains(idx));
        return idx;
    }

    private void startTimer(long millis, Runnable onFinish) {
        cancelActiveTimer();
        activeTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long ms) {
                countdownTextView.setText("Time left: " + (ms / 1000) + " seconds");
            }
            public void onFinish() {
                onFinish.run();
            }
        }.start();
    }

    private void cancelActiveTimer() {
        if (activeTimer != null) {
            activeTimer.cancel();
            activeTimer = null;
        }
    }

    private void clearInput() {
        answerEditText.getText().clear();
    }

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

    // ===== Math helpers =====

    private List<Integer> generateRandomOperands(int numOperands) {
        List<Integer> operands = new ArrayList<>();
        for (int i = 0; i < numOperands; i++) operands.add(rng.nextInt(9) + 1); // 1..9
        return operands;
    }

    private List<Character> generateRandomOperators(int numOperators) {
        List<Character> operators = new ArrayList<>();
        for (int i = 0; i < numOperators; i++) operators.add(randomOperator());
        return operators;
    }

    private char randomOperator() {
        int r = rng.nextInt(3); // +,-,*
        if (r == 0) return '+';
        if (r == 1) return '-';
        return '*';
    }

    private int calculateCorrectAnswer(List<Integer> operands, List<Character> operators) {
        // Handle * first
        List<Integer> tempOperands = new ArrayList<>(operands);
        List<Character> tempOps = new ArrayList<>(operators);

        for (int i = 0; i < tempOps.size();) {
            if (tempOps.get(i) == '*') {
                int mul = tempOperands.get(i) * tempOperands.get(i + 1);
                tempOperands.set(i, mul);
                tempOperands.remove(i + 1);
                tempOps.remove(i);
            } else i++;
        }

        int result = tempOperands.get(0);
        for (int i = 0; i < tempOps.size(); i++) {
            char op = tempOps.get(i);
            int val = tempOperands.get(i + 1);
            if (op == '+') result += val; else if (op == '-') result -= val;
        }
        return result;
    }

    private String formatQuestion(List<Integer> operands, List<Character> operators) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < operands.size(); i++) {
            if (i > 0) sb.append(operators.get(i - 1));
            sb.append(operands.get(i));
        }
        sb.append(" = ?");
        return sb.toString();
    }

    // ===== General helpers =====

    public static <T> List<T> getRandomWords(List<T> source, int count) {
        if (source == null || source.isEmpty() || count <= 0) return null;
        if (count > source.size()) count = source.size();
        List<T> pool = new ArrayList<>(source);
        Collections.shuffle(pool);
        return new ArrayList<>(pool.subList(0, count));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelActiveTimer();
    }
}
