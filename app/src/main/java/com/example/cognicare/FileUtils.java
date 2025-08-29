package com.example.cognicare;

import android.content.Context;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    // Method to save text to a file
    public static void saveTextToFile(Context context, String filename, String text) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

