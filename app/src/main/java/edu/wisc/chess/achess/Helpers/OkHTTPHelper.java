package edu.wisc.chess.achess.Helpers;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import edu.wisc.chess.achess.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHTTPHelper {

    private OkHttpClient client;
    // Variable to be returned by nested methods inside authenticateCredentials
    static String result = null;

    /**
     * This method is responsible for checking the user's entered credentials with the server using
     * HTTP. Based on the response code returned by the server, this method will take the user to
     * the home activity or restart the activity or display an error dialog. This method will also
     * store the user's authentication token to the database if their credentials are verified.
     *
     * @param name
     * @param pass
     */
    public String attemptLogin(String name, String pass) {
        client = new OkHttpClient();

        // Must be declared final to use in sub method.
        final String username = name;
        final String password = pass;

        // Fill the body of the request with a "username" and "password" field.
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        // Create the request.
        Request request = new Request.Builder()
                .url(Constants.authRoute)
                .post(formBody)
                .build();

        // Countdown to sync up the async call with the rest of the program.
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // Asynchronously call to the server using the request.
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {

                    // Get token from the body of the response.
                    result = extractToken(response.body().string());

                    // Signal to the outer method the call is finished.
                    countDownLatch.countDown();

                } else {

                    result = "error";

                    // Signal to the outer method the call is finished.
                    countDownLatch.countDown();

                }
            }

            // In the case of a login failure, restart the activity.
            public void onFailure(Call call, IOException e) {

                result = null;

                // Signal to the outer method the call is finished.
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * This method is responsible for signing up the user with the server using HTTP.
     *
     * @param name
     * @param pass
     * @throws IOException
     */
    public String attemptSignUp(String name, String pass) {
        client = new OkHttpClient();

        // Must be declared final to be used in the sub method.
        final String username = name;
        final String password = pass;

        // Fill the body of the request with a "username" and "password" field.
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        // Create the request.
        Request request = new Request.Builder()
                .url(Constants.signUpRoute)
                .post(formBody)
                .build();

        // Countdown to sync up the async call with the rest of the program.
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // Asynchronously call to the server using the request.
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 201) { //TODO: Find out the right status code for a new user created.

                    //TODO

                    // Get token from the body of the response.
                    result = extractToken(response.body().string());

                    // Signal to the outer method the call is finished.
                    countDownLatch.countDown();

                } else {

                    result = "error";

                    // Signal to the outer method the call is finished.
                    countDownLatch.countDown();

                }
            }

            // In the case of a sign up failure, restart the activity.
            public void onFailure(Call call, IOException e) {

                result = null;

                // Signal to the outer method the call is finished.
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * This method takes the raw string from a HTTP Response, which includes an authorization token,
     * and extracts the token represented by a String.
     *
     * rawResponse should be in the form:
     *
     * {"token":"openssl********","user_guid":"#######","password_reset":false,"metadata":[]}
     *
     * @param rawResponse is the body of a HTTP Response in a String format.
     * @return Returns the authorization token contained in the rawResponse.
     */
    public static String extractToken(String rawResponse) {
        String temp[] = rawResponse.split(",");
        String temp2[] = temp[0].split(":");
        String token = temp2[1].substring(1, temp2[1].length() - 1);
        return token;
    }

    /**
     * This method checks if the user has an active internet connection.
     *
     * @return Returns true if the user has an active internet connection and returns false if not.
     */
    public static boolean hasActiveInternetConnection(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
