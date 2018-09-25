package edu.wisc.chess.achess;

import android.content.Context;
import android.net.ConnectivityManager;

import org.junit.Test;

import edu.wisc.chess.achess.Helpers.OkHTTPHelper;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class HTTPHelperTest {

    private OkHTTPHelper okhttpHelper = new OkHTTPHelper();

    @Test
    public void extractTokenTest() {
        String rawResponse = "{\"token\":\"openssl********\",\"user_guid\":\"#######\",\"password_reset\":false,\"metadata\":[]}";
        String expectedToken = "openssl********";

        String extractedToken = okhttpHelper.extractToken(rawResponse);

//        System.out.println("Expected  Token: " + expectedToken +
//                         "\nExtracted Token: " + extractedToken);

        assertEquals(expectedToken, extractedToken);
    }

    @Test
    public void attemptSuccessfulLoginTest() {
        String username = "test user";
        String password = "testtest";

        String token = okhttpHelper.attemptLogin(username, password);

        assertNotNull(token);
        assertThat(token, not("error"));
    }

    @Test
    public void attemptInvalidLoginTest() {
        String username = "null user";
        String password = "null pass";

        String token = okhttpHelper.attemptLogin(username, password);

        assertEquals(token, "error");
    }
}
