package edu.wisc.chess.achess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.wisc.chess.achess.Helpers.DatabaseHelper;
import edu.wisc.chess.achess.Helpers.OkHTTPHelper;

/**
 * This method is tied to the Login Activity and handles all the functions associated with logging
 * in the user.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextUsername, mEditTextPassword;
    private DatabaseHelper DBHelper = new DatabaseHelper();
    private OkHTTPHelper okhttpHelper = new OkHTTPHelper();

    /**
     * This method sets the content view to the Login Activity, calls a method to initialize the
     * realm database and sets up the buttons on the Login Activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        DBHelper.setupDatabase();

        setUpButtons();
    }

    /**
     * This method handles the formatting and displaying of AlertDialogs to the user.
     *
     * @param message is the message that will appear on the AlertDialog.
     */
    public void displayDialog(String message) {
        displayDialog(message, LoginActivity.this);
    }

    /**
     * This method handles the formatting and displaying of AlertDialogs to the user.
     *
     * @param message is the message that will appear on the AlertDialog.
     * @param context context of the activity you wish to display the dialog on.
     */
    public void displayDialog(String message, Context context) {

        // Create Alert Dialog Builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        AlertDialog alertDialog;

        // Create TextView object so that the message can be formatted.
        TextView dialogMessage = new TextView(this);
        dialogMessage.setText("\n" + message);
        dialogMessage.setTextSize(16);
        dialogMessage.setGravity(Gravity.CENTER);

        // Set dialog message.
        dialogBuilder.setView(dialogMessage);

        // Set alert message using a builder and create the dialog.
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.setNegativeButton("Cancel", null);
        alertDialog = dialogBuilder.create();

        // Show the dialog to the user.
        alertDialog.show();

        // Create a Button object so that it can be formatted to be in the center of the dialog.
        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
    }

    /**
     * This method sets the onClick function of the login button on the Login Activity.
     */
    private void setUpButtons() {
        Button loginButton = findViewById(R.id.login_button);
        mEditTextUsername = findViewById(R.id.username);
        mEditTextPassword = findViewById(R.id.password);
        // Object to be passed in order to check for an active internet connection.
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // No internet connection detected.
                if (!(okhttpHelper.hasActiveInternetConnection(connectivityManager))) {
                    displayDialog(getResources().getString(R.string.internet_connection_required));
                }
                // No username entered in username field.
                else if (mEditTextUsername.length() == 0) {
                    displayDialog(getResources().getString(R.string.error_username_required));

                    // Place cursor in username field.
                    mEditTextUsername.requestFocus();
                }
                // No password entered in password field.
                else if (mEditTextPassword.length() == 0) {
                    displayDialog(getResources().getString(R.string.error_password_required));

                    // Place cursor in Password field.
                    mEditTextPassword.requestFocus();
                }
                // Initial checks passed, now authenticate credentials.
                else {

                    // Authenticate the user's entered username and password.
                    String token = okhttpHelper.attemptLogin(mEditTextUsername.getText().toString().trim(),
                                            mEditTextPassword.getText().toString().trim());

                    if (!token.equals("error") && !token.equals(null)) {

                        // Store the authorization token in the database.
                        DBHelper.saveCredentials(token);

                        // Go to the Home Activity.
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                    } else if (token == "error") {

                        // Display error dialog to user.
                        displayDialog(getResources().getString(R.string.error_invalid_username_or_password));

                        // Clear text entry fields.
                        mEditTextUsername.getText().clear();
                        mEditTextPassword.getText().clear();

                        // Place cursor in the Username text field
                        mEditTextUsername.requestFocus();

                    } else if (token == null) {

                        Intent intent = getIntent();
                        // Close the activity
                        finish();
                        // Restart the activity.
                        startActivity(intent);
                        // Display error dialog to the user.
                        displayDialog(getResources().getString(R.string.error_logging_in));
                        // Place cursor in the Username text field.
                        mEditTextUsername.requestFocus();

                    }
                }
            }
        });
    }
}
