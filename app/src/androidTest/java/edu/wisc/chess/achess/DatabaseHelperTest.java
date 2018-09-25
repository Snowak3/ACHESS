package edu.wisc.chess.achess;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import edu.wisc.chess.achess.Helpers.DatabaseHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DatabaseHelperTest {
    private DatabaseHelper DBHelper;

    private Context mMockContext;

    @Before
    public void setUp() {
        mMockContext = InstrumentationRegistry.getTargetContext();

        DBHelper = new DatabaseHelper();

        DBHelper.setupDatabase(mMockContext);

        DBHelper.attemptLogout();
    }

    @Test
    public void obtainCredentialsTest() {
        String testToken = "testToken";

        DBHelper.saveCredentials(testToken);

        String returnString = DBHelper.obtainCredentials();

        assertEquals(testToken, returnString);
    }

    @Test
    public void attemptLogoutTest() {
        String testToken = "testToken";

        DBHelper.saveCredentials(testToken);

        // Check to see a credentials object was stored properly.
        assertNotNull(DBHelper.obtainCredentials());

        // Delete credentials in the database.
        DBHelper.attemptLogout();

        // Check to see if no credentials are stored in the database.
        assertNull(DBHelper.obtainCredentials());
    }

    @Test
    public void attemptLogoutEmptyDBText() {
        // Test to see if any errors are thrown when trying to log out when the user has already
        // been logged out.
        if (DBHelper.obtainCredentials() == null) {
            DBHelper.attemptLogout();
        }
    }

    /**
     * This class tests if the saveCredentials method properly checks for repeat entries into the
     * database.
     */
    @Test
    public void saveCredentialsTest() {
        String testToken = "testToken";

        assertTrue(DBHelper.saveCredentials(testToken));

        assertFalse(DBHelper.saveCredentials(testToken));

    }

    @Test
    public void saveCredentialsNullTokenTest() {
        String testToken = null;

        assertFalse(DBHelper.saveCredentials(testToken));
    }
}
