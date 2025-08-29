package com.example.cognicare;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditDataActivity extends AppCompatActivity {

    private YourViewModel viewModel;
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private Button cancelbtn;
    private UserEntity user = MyApp.getDatabase().UserDao().getUser();

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static final String PHONE_REGEX =
            "^\\+(?:[0-9] ?){6,14}[0-9]$";

    public static boolean isValidPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
    private EditText fullNameEditText, ageEditText, numSiblingsEditText, motherNameEditText, fatherNameEditText,
            highschoolNameEditText, favoriteColorEditText, birthDateEditText, numChildrenEditText, numGrandchildrenEditText,
            currentLocationEditText, caretakerNameEditText, emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        cancelbtn = findViewById(R.id.cancelButton);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(EditDataActivity.this, MainActivity.class));
            }
        });

        viewModel = new ViewModelProvider(this).get(YourViewModel.class);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        numSiblingsEditText = findViewById(R.id.numSiblingsEditText);
        motherNameEditText = findViewById(R.id.motherNameEditText);
        fatherNameEditText = findViewById(R.id.fatherNameEditText);
        highschoolNameEditText = findViewById(R.id.highschoolNameEditText);
        favoriteColorEditText = findViewById(R.id.favoriteColorEditText);
        birthDateEditText = findViewById(R.id.birthdateEditText);
        numChildrenEditText = findViewById(R.id.numChildrenEditText);
        numGrandchildrenEditText = findViewById(R.id.numGrandchildrenEditText);
        currentLocationEditText = findViewById(R.id.currentLocationEditText);

        caretakerNameEditText = findViewById(R.id.caretakerFullNamEditText);
        emailEditText = findViewById(R.id.emailEditText);

        Button confirmButton = findViewById(R.id.confirmButton);

        fullNameEditText.setText(user.getFullName()); //1
        numSiblingsEditText.setText(String.valueOf(user.getNumSiblings())); //3
        motherNameEditText.setText(user.getMotherName()); //4
        fatherNameEditText.setText(user.getFatherName()); //5
        highschoolNameEditText.setText(user.getHighschoolName()); //6
        favoriteColorEditText.setText(user.getFavoriteColor()); //7
        birthDateEditText.setText(user.getBirthdate()); //8
        numChildrenEditText.setText(String.valueOf(user.getNumChildren())); //9
        numGrandchildrenEditText.setText(String.valueOf(user.getNumGrandchildren())); //10
        currentLocationEditText.setText(user.getCurrentLocation()); //11

        caretakerNameEditText.setText(user.getCaretakerName());
        emailEditText.setText(user.getEmail());

        birthDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditDataActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format the date as yyyy-MM-dd
                        String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        birthDateEditText.setText(formattedDate);
                    },
                    year, month, day
            );

            // Optional: set max date to today so user can't select future dates
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName, age, numSiblings, motherName, fatherName, highschoolName, favoriteColor,
                        birthDate, numChildren, numGrandchildren, currentLocation, caretakerName, emeil, phoneNumber;

                TextInputLayout fullNameInputLayout = findViewById(R.id.fullNameTextInputLayout);
                TextInputLayout numSiblingsInputLayout = findViewById(R.id.numSiblingsTextInputLayout);
                TextInputLayout motherNameInputLayout = findViewById(R.id.motherNameTextInputLayout);
                TextInputLayout fatherNameInputLayout = findViewById(R.id.fatherNameTextInputLayout);
                TextInputLayout highschoolNameInputLayout = findViewById(R.id.highschoolTextInputLayout);
                TextInputLayout favoriteColorInputLayout = findViewById(R.id.favoriteColorTextInputLayout);
                TextInputLayout birthdateInputLayout = findViewById(R.id.birthdateTextInputLayout);
                TextInputLayout numChildrenInputLayout = findViewById(R.id.numChildrenTextInputLayout);
                TextInputLayout numGrandchildrenInputLayout = findViewById(R.id.numGrandchildrenTextInputLayout);
                TextInputLayout currentLocationInputLayout = findViewById(R.id.currentLocationTextInputLayout);

                TextInputLayout caretakerNameInputLayout = findViewById(R.id.caretakerFullNameTextInputLayout);
                TextInputLayout emailInputLayout = findViewById(R.id.emailTextInputLayout);

                fullName = fullNameEditText.getText().toString().trim(); //1
                //age = ageEditText.getText().toString().trim(); //2
                numSiblings = numSiblingsEditText.getText().toString().trim(); //3
                motherName = motherNameEditText.getText().toString().trim(); //4
                fatherName = fatherNameEditText.getText().toString().trim(); //5
                highschoolName = highschoolNameEditText.getText().toString().trim(); //6
                favoriteColor = favoriteColorEditText.getText().toString().trim(); //7
                birthDate = birthDateEditText.getText().toString().trim(); //8
                numChildren = numChildrenEditText.getText().toString().trim(); //9
                numGrandchildren = numGrandchildrenEditText.getText().toString().trim(); //10
                currentLocation = currentLocationEditText.getText().toString().trim(); //11

                caretakerName = caretakerNameEditText.getText().toString().trim();
                emeil = emailEditText.getText().toString().trim();

                if (fullName.isEmpty()) {
                    fullNameInputLayout.setError("Full name is required");
                    fullNameInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    fullNameInputLayout.setError(null); // Clear error if valid
                }

                if (numSiblings.isEmpty()) {
                    numSiblingsInputLayout.setError("Number of siblings is required");
                    numSiblingsInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    numSiblingsInputLayout.setError(null); // Clear error if valid
                }

                if (motherName.isEmpty()) {
                    motherNameInputLayout.setError("Mother's name is required");
                    motherNameInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    motherNameInputLayout.setError(null); // Clear error if valid
                }

                if (fatherName.isEmpty()) {
                    fatherNameInputLayout.setError("Father's name is required");
                    fatherNameInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    fatherNameInputLayout.setError(null); // Clear error if valid
                }

                if (highschoolName.isEmpty()) {
                    highschoolNameInputLayout.setError("High school's name is required");
                    highschoolNameInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    highschoolNameInputLayout.setError(null); // Clear error if valid
                }

                if (favoriteColor.isEmpty()) {
                    favoriteColorInputLayout.setError("Favorite color is required");
                    favoriteColorInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    favoriteColorInputLayout.setError(null); // Clear error if valid
                }

                if (birthDate.isEmpty()) {
                    birthdateInputLayout.setError("Birthdate is required");
                    birthdateInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    birthdateInputLayout.setError(null); // Clear error if valid
                }

                if (numChildren.isEmpty()) {
                    numChildrenInputLayout.setError("Number of children is required");
                    numChildrenInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    numChildrenInputLayout.setError(null); // Clear error if valid
                }

                if (numGrandchildren.isEmpty()) {
                    numGrandchildrenInputLayout.setError("Number of grandchildren is required");
                    numGrandchildrenInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    numGrandchildrenInputLayout.setError(null); // Clear error if valid
                }

                if (currentLocation.isEmpty()) {
                    currentLocationInputLayout.setError("Current location is required");
                    currentLocationInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    currentLocationInputLayout.setError(null); // Clear error if valid
                }

                if (caretakerName.isEmpty()) {
                    caretakerNameInputLayout.setError("Caretaker's name is required");
                    caretakerNameInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    caretakerNameInputLayout.setError(null); // Clear error if valid
                }

                if (emeil.isEmpty()) {
                    emailInputLayout.setError("Email is required");
                    emailInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    emailInputLayout.setError(null); // Clear error if valid
                }

                if (!isValidEmail(emeil)) {
                    emailInputLayout.setError("Email is not valid! Please, enter a valid email.");
                    emailInputLayout.requestFocus(); // Focus on the empty field
                    return; // Stop further processing
                }else {
                    emailInputLayout.setError(null); // Clear error if valid
                }

                saveUserData();
                //SharedPreferences prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                //prefs.edit().putBoolean("isSetupCompleted", true).apply();
                startActivity(new Intent(EditDataActivity.this, PersonalInfoActivity.class));
            }
        });
    }

    private void saveUserData() {
        String fullName = fullNameEditText.getText().toString().trim();
        int numSiblings = Integer.parseInt(numSiblingsEditText.getText().toString().trim());
        String motherName = motherNameEditText.getText().toString().trim();
        String fatherName = fatherNameEditText.getText().toString().trim();
        String highschoolName = highschoolNameEditText.getText().toString().trim();
        String favoriteColor = favoriteColorEditText.getText().toString().trim();
        String birthdate = birthDateEditText.getText().toString().trim();
        int numChildren = Integer.parseInt(numChildrenEditText.getText().toString().trim());
        int numGrandchildren = Integer.parseInt(numGrandchildrenEditText.getText().toString().trim());
        String currentLocation = currentLocationEditText.getText().toString().trim();

        String caretakerName = caretakerNameEditText.getText().toString().trim();
        String emeil = emailEditText.getText().toString().trim();

        user.setFullName(fullName);
        //user.setAge(age);
        user.setNumSiblings(numSiblings);
        user.setMotherName(motherName);
        user.setFatherName(fatherName);
        user.setHighschoolName(highschoolName);
        user.setFavoriteColor(favoriteColor);
        user.setBirthdate(birthdate);
        user.setNumChildren(numChildren);
        user.setNumGrandchildren(numGrandchildren);
        user.setCurrentLocation(currentLocation);

        user.setCaretakerName(caretakerName);
        user.setEmail(emeil);

        MyApp.getDatabase().UserDao().insert(user);

    }

}