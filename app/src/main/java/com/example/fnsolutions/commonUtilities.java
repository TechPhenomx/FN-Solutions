package com.example.fnsolutions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;

import org.aviran.cookiebar2.CookieBar;

public class commonUtilities {

    // Method to clear the text of an EditText
    public static void clearEditText(TextInputEditText editText) {
        if (editText != null) {
            editText.setText(""); // Clear the EditText
        }
    }

    public static void closeKeyboard(Context context, View view){
        view.clearFocus();

        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    // Show message using CookieBar
    public static void showMessage(Activity activity, String message, String title) {
        CookieBar.build(activity)
                .setTitle(title)
                .setMessage(message)
                .setCookiePosition(CookieBar.TOP)
                .setDuration(4000)  // Duration of the message
                .show();
    }

}
