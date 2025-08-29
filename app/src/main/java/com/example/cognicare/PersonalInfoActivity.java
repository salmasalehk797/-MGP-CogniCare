package com.example.cognicare;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PersonalInfoActivity extends AppCompatActivity {

    private TextView fullNameTextView, ageTextView, numSiblingsTextView, motherNameTextView, fatherNameTextView,
            highschoolNameTextView, favoriteColorTextView, birthDateTextView, numChildrenTextView, numGrandchildrenTextView,
            currentLocationTextView, caretakerNameTextView, emailTextView;
    private YourViewModel yourViewModel;
    private Button bckbutton, editbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        bckbutton = findViewById(R.id.backbutton);
        bckbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(PersonalInfoActivity.this, MainActivity.class));
            }
        });

        editbtn = findViewById(R.id.editButton);
        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(PersonalInfoActivity.this, EditDataActivity.class));
            }
        });

        UserEntity user = MyApp.getDatabase().UserDao().getUser();

        fullNameTextView = findViewById(R.id.fullNameTextView);
        ageTextView = findViewById(R.id.ageTextView);
        numSiblingsTextView = findViewById(R.id.numSiblingsTextView);
        motherNameTextView = findViewById(R.id.motherNameTextView);
        fatherNameTextView = findViewById(R.id.fatherNameTextView);
        highschoolNameTextView = findViewById(R.id.highschoolNameTextView);
        favoriteColorTextView = findViewById(R.id.favoriteColorTextView);
        birthDateTextView = findViewById(R.id.birthdateTextView);
        numChildrenTextView = findViewById(R.id.numChildrenTextView);
        numGrandchildrenTextView = findViewById(R.id.numGrandchildrenTextView);
        currentLocationTextView = findViewById(R.id.currentLocationTextView);
        caretakerNameTextView = findViewById(R.id.caretakerNameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        String birthDateString = user.getBirthdate().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(birthDateString, formatter);
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        fullNameTextView.setText("Full Name: " + (user.getFullName() != null ? user.getFullName() : ""));
        ageTextView.setText("Age: " + age);
        numSiblingsTextView.setText("Number of Siblings: " + user.getNumSiblings());
        motherNameTextView.setText("Mother's Name: " + (user.getMotherName() != null ? user.getMotherName() : ""));
        fatherNameTextView.setText("Father's Name: " + (user.getFatherName() != null ? user.getFatherName() : ""));
        highschoolNameTextView.setText("Your High School's Name: " + (user.getHighschoolName() != null ? user.getHighschoolName() : ""));
        favoriteColorTextView.setText("Favorite Color: " + (user.getFavoriteColor() != null ? user.getFavoriteColor() : ""));
        birthDateTextView.setText("Birthdate: " + (user.getBirthdate() != null ? user.getBirthdate() : ""));
        numChildrenTextView.setText("Number of Children: " + user.getNumChildren());
        numGrandchildrenTextView.setText("Number of Grandchildren: " + user.getNumGrandchildren());
        currentLocationTextView.setText("Your Current Residence: " + (user.getCurrentLocation() != null ? user.getCurrentLocation() : ""));

        caretakerNameTextView.setText("Your Caretaker's Name: " + (user.getCaretakerName() != null ? user.getCaretakerName() : ""));
        emailTextView.setText("Your Caretaker's Email: " + (user.getEmail() != null ? user.getEmail() : ""));

    }

}
