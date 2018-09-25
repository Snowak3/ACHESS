package edu.wisc.chess.achess;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * This class is responsible for setting the default realm configuration as well as all the original
 * functions of the Application class.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the realm.
        Realm.init(this);

        // Create a realm configuration.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("credentials.realm")
                .schemaVersion(0)
                .build();

        // Set the default configuration to the above realmConfig.
        Realm.setDefaultConfiguration(realmConfig);
    }
}
