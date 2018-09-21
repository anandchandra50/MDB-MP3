package com.ac.mdbsocials;

import android.content.Context;
import android.widget.Toast;

/// Common functions for classes, etc.
public class Utils {

    // checks if email and pass are valid
    public static boolean isValid(Context context, String email, String pass) {
        if (email.isEmpty() || !isValidEmail(email)) {
            displayError(context, context.getString(R.string.invalid_email));
            return false;
        } else if (pass.isEmpty()) {
            displayError(context, context.getString(R.string.enter_password));
            return false;
        } else if (pass.length() < 6) {
            displayError(context, context.getString(R.string.password_length));
            return false;
        } else {
            // no errors, create user
            return true;
        }
    }

    public static boolean isValid(Context context, String email, String pass, String confirmPass) {
        if (email.isEmpty() || !isValidEmail(email)) {
            displayError(context, context.getString(R.string.invalid_email));
            return false;
        } else if (pass.isEmpty() || confirmPass.isEmpty()) {
            displayError(context, context.getString(R.string.enter_password));
            return false;
        } else if (pass.length() < 6 || confirmPass.length() < 6) {
            displayError(context, context.getString(R.string.password_length));
            return false;
        } else if (!pass.equals(confirmPass)) {
            displayError(context, context.getString(R.string.passwords_match));
            return false;
        } else {
            return true;
        }
    }

    // display an error in registering
    public static void displayError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // checks if an email is valid
    private static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
