package com.ac.mdbsocials;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Common functions shared between classes
 */
public class Utils {

    /**
     * checks if email and pass are valid
     * @param context
     * @param email
     * @param pass
     * @return
     */
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

    /**
     * Same thing as isValid, except with confirm pass this time
     * @param context
     * @param email
     * @param pass
     * @param confirmPass
     * @return
     */
    public static boolean isValid(Context context, String email, String pass, String confirmPass) {
        if (!isValid(context, email, pass)) {
            return false;
        } else if (!pass.equals(confirmPass)) {
            displayError(context, context.getString(R.string.passwords_match));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Show a toast for an error
     * @param context
     * @param message
     */
    public static void displayError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * See if email has valid format
     * @param target
     * @return
     */
    private static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Fetches image from URL
     * @param src
     * @return
     */
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    static class FetchImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromURL(strings[0]);
        }
    }
}
