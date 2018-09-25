package edu.wisc.chess.achess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
import okhttp3.OkHttpClient;

/**
 * This class is tied to the Splash Screen Activity and handles all functions related to signing up the
 * user.
 */
public class SplashScreen extends AppCompatActivity {

    EditText mEditTextUsername, mEditTextPassword, mEditTextConfirmPassword;
    Button mButtonSignUp;
    Button mButtonAlreadyAMember;
    private DatabaseHelper DBHelper = new DatabaseHelper();
    private OkHTTPHelper okhttpHelper = new OkHTTPHelper();

    private OkHttpClient client;

    /**
     * This method sets the content view to the Splash Screen Activity, sets up the realm database,
     * sets up the buttons on the Splash Screen Activity, and assigns a variable to the EditText
     * fields.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);

        DBHelper.setupDatabase();

        mEditTextUsername = (EditText) findViewById(R.id.username);
        mEditTextPassword = (EditText) findViewById(R.id.password);
        mEditTextConfirmPassword = (EditText) findViewById(R.id.confirm_password);

        setUpButtons();
    }

    /**
     * This method handles the formatting and displaying of AlertDialogs to the user.
     *
     * @param message is the message that will appear on the AlertDialog.
     */
    private void displayDialog(String message) {
        displayDialog(message, SplashScreen.this);
    }

    /**
     * This method handles the formatting and displaying of AlertDialogs to the user.
     *
     * @param message is the message that will appear on the AlertDialog.
     * @param context context of the activity you wish to display the dialog on.
     */
    private void displayDialog(String message, Context context) {
        // Create Alert Dialog Builder
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        AlertDialog alertDialog;

        // Create TextView object so that the text can be formatted.
        TextView dialogMessage = new TextView(this);
        dialogMessage.setText("\n" + message);
        dialogMessage.setTextSize(18);
        dialogMessage.setGravity(Gravity.CENTER_HORIZONTAL);

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
        LinearLayout.LayoutParams positiveButtonLL =
                (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.MATCH_PARENT;
        positiveButton.setLayoutParams(positiveButtonLL);
    }

    /**
     * This method overrides the function of the back button to refresh the page instead. This
     * prevents the user from getting to the Main Activity/View Controller which has nothing on it.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
    }

    /**
     * This method sets the onClick function of the buttons in the Splash Screen Activity.
     */
    public void setUpButtons() {

        // Assign objects to the buttons in the activity.
        mButtonSignUp = (Button) findViewById(R.id.signup_button);
        mButtonAlreadyAMember = (Button) findViewById(R.id.already_a_member_button);
        // Object to be passed in order to check for an active internet connection.
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        mButtonSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {

                if (!(okhttpHelper.hasActiveInternetConnection(connectivityManager))) {
                    displayDialog(getResources().getString(R.string.internet_connection_required));
                }
                // No username entered in username field.
                else if (mEditTextUsername.length() == 0) {
                    displayDialog(getResources().getString(R.string.error_username_required));

                    // Place cursor in Username field.
                    mEditTextUsername.requestFocus();
                }
                // No password entered in password field.
                else if (mEditTextPassword.length() == 0) {
                    displayDialog(getResources().getString(R.string.error_password_required));

                    // Place cursor in Password field.
                    mEditTextPassword.requestFocus();
                }
                // No password entered in confirm password field.
                else if (mEditTextConfirmPassword.length() == 0) {
                    displayDialog(getResources().getString(R.string.error_reenter_password_required));

                    // Place cursor in Password field.
                    mEditTextConfirmPassword.requestFocus();
                }
                // Confirm password field doesn't match the password field.
                else if (!(mEditTextConfirmPassword.getText().toString()
                        .equals(mEditTextPassword.getText().toString()))) {
                    displayDialog(getResources().getString(R.string.error_confirm_password));

                    // Place cursor in Password field.
                    mEditTextConfirmPassword.requestFocus();
                }
                // Credentials pass initial checks.
                else {
                    // FIXME: This whole else statement may not be right, it hasn't been tested yet.
                    String token = okhttpHelper.attemptSignUp(mEditTextUsername.getText().toString().trim(),
                                            mEditTextPassword.getText().toString().trim());

                    if (token != "error" && token != null) {

                        // Store the authorization token in the database.
                        DBHelper.saveCredentials(token);

                        // Go to the Home Activity.
                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));

                    } else if (token == "error") {

                        // Display error dialog to user.
                        displayDialog(getResources().getString(R.string.error_invalid_username_or_password));

                        // Clear text entry fields.
                        mEditTextUsername.getText().clear();
                        mEditTextPassword.getText().clear();
                        mEditTextConfirmPassword.getText().clear();

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

        mButtonAlreadyAMember.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            }
        });
    }
}