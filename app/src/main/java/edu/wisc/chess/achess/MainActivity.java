package edu.wisc.chess.achess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

import edu.wisc.chess.achess.Helpers.DatabaseHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The place where the app starts. This class acts as the view controller, sending the user to
 * different activities based on if they are logged in or not.
 */
public class MainActivity extends AppCompatActivity {

    // Responsible for interacting with the local database.
    private DatabaseHelper DBHelper;

    // Used to make HTTP requests.
    private OkHttpClient client;

    /**
     * This method sets the view to be
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBHelper = new DatabaseHelper();
        DBHelper.setupDatabase();

        // Check the user's logged in status and redirect accordingly.
        validateUserToken();
    }

    /**
     * This method obtains any tokens stored in the local database and tries to authenticate that
     * token with the server. Based on the response of the HTTP response, the view is changed to
     * the HomeActivity or the SplashScreenActivity(SignUpActivity).
     */
    public void validateUserToken() {

        String token = DBHelper.obtainCredentials();
        client = new OkHttpClient();

        if (token != null) {

            // Fill the body of the request with a token field.
            RequestBody formBody = new FormBody.Builder()
                    .add("token", token)
                    .build();

            // Create the request.
            Request request = new Request.Builder()
                    .url(Constants.authTokenRoute)
                    .post(formBody)
                    .build();

            // Asynchronously call to the server.
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                public void onResponse(Call call, Response response) throws IOException {
                    // If the user successfully logged in then send to the Home Screen.
                    if (response.code() == 200) {
                        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(homeIntent);
                    }
                    // If the user's token received an error then delete credentials from the
                    // database and go to the Sign Up activity.
                    else {
                        DBHelper.attemptLogout();
                        Intent signupIntent = new Intent(getApplicationContext(), SplashScreen.class);
                        startActivity(signupIntent);
                    }
                }
                // If the HTTP request failed then delete the credentials stored in the database
                // and redirect to the Sign Up activity.
                public void onFailure(Call call, IOException e) {
                    DBHelper.attemptLogout();
                    Intent signupIntent = new Intent(getApplicationContext(), SplashScreen.class);
                    startActivity(signupIntent);
                }
            });
        }
        // If token = null then the user isn't logged in and is sent to the Sign Up screen by default.
        else {
            Intent signupIntent = new Intent(this, SplashScreen.class);
            startActivity(signupIntent);
        }
    }
}
