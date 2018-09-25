package edu.wisc.chess.achess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.wisc.chess.achess.Helpers.DatabaseHelper;


/**
 * This class is the Home page of the app where the majority of the features will be located.
 */
public class HomeActivity extends AppCompatActivity {

    private Button mButtonLogout;
    private DatabaseHelper DBHelper = new DatabaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        mButtonLogout = (Button) findViewById(R.id.logout);

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logout();
            }
        });
    }

    /**
     * This method removes the users credentials from the database and redirects the app back to the
     * view controller.
     */
    private void logout() {
        DBHelper.attemptLogout();

        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * This method overrides the function of the back button to refresh the page instead. This
     * prevents the user from getting to the Main Activity/View Controller which has nothing on it.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
