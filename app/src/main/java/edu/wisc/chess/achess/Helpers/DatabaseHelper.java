package edu.wisc.chess.achess.Helpers;

import android.content.Context;

import java.util.UUID;

import edu.wisc.chess.achess.models.UserInfo;
import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.internal.SyncObjectServerFacade.getApplicationContext;

/**
 * This class handles all the manipulation of realm objects and database actions.
 */
public class DatabaseHelper {

    // Used to interact with the Realm database.
    Realm realm;

    // Used to store the list of user credentials.
    RealmResults<UserInfo> credentials;

    /**
     * This method finds any saved credentials in the database and deletes them. All the credentials
     * are deleted because only one user should be logged into the app at a time.
     */
    public void attemptLogout() {
        realm = realm.getDefaultInstance();

        // Find and delete all saved credentials in the database.
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UserInfo> credentials = realm.where(UserInfo.class).findAll();
                credentials.deleteAllFromRealm();
            }
        });

        realm.close();
    }

    /**
     * This method retrieves the credentials of any logged in user and returns the authorization
     * token string of the currently logged in user.
     *
     * @return Returns the String representation of the authorization token.
     */
    public String obtainCredentials() {
        // Find the list of credentials objects in the database.
        credentials = realm.where(UserInfo.class).findAll();

        // Find and return the first element as only one should be stored at any time.
        if (credentials.size() > 0) {
            return credentials.first().getToken();
        } else {
            return null;
        }
    }

    /**
     * This method first checks if the entered user exists and they don't, then adds them to the
     * realm database. This method should be called after the user has been authenticated with the
     * appropriate http response code (200).
     *
     * @param token Access token received from the server that is used to authenticate the user.
     * @return Returns true if the token entered was saved and returns false if it wasn't or was
     *          already in the database.
     */
    public boolean saveCredentials(String token) {
        if (token == null) return false;

        realm = Realm.getDefaultInstance();
        credentials = realm.where(UserInfo.class).findAll();

        // Compare the token parameter with the credentials stored in the database to avoid
        // duplicates.
        for (UserInfo credentials : credentials) {
            if (token.equals(credentials.getToken())) {
                return false;
            }
        }

        // If the credentials don't already exist, then add them to the database.
        realm.beginTransaction();

        // Create credentials object and add to Realm DB.
        UserInfo userCredentials = realm.createObject(UserInfo.class, UUID.randomUUID().toString());
        userCredentials.setToken(token);

        realm.commitTransaction();

        return true;
    }

    /**
     * This method initializes the realm database and gets the default instance.
     *
     * @param context Current application context.
     */
    public void setupDatabase(Context context) {
        realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    /**
     * This method initializes the realm database and gets the default instance.
     */
    public void setupDatabase() {
        realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
    }
}
