package com.example.cognicare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyQuizManager {
    private static final String PREF_LAST_QUIZ_DATE = "last_quiz_date";

    public static boolean canTakeQuiz(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lastQuizDate = preferences.getString(PREF_LAST_QUIZ_DATE, "");

        // Get today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // If the last quiz date matches today's date, quiz has already been taken today
        return !lastQuizDate.equals(currentDate);
    }

    public static void markQuizTaken(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_LAST_QUIZ_DATE, currentDate);
        editor.apply();
    }
}

