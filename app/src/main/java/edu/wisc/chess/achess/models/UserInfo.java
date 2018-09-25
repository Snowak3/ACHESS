package edu.wisc.chess.achess.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * This class serves as the objects to be stored in the Realm Database.
 */
public class UserInfo extends RealmObject {
    @PrimaryKey
    private String userID;
    private String accessToken;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String ID) {
        userID = ID;
    }

    public String getToken() {
        return accessToken;
    }

    public void setToken(String token) {
        accessToken = token;
    }
}
