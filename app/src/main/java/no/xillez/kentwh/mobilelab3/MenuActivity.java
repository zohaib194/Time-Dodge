package no.xillez.kentwh.mobilelab3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * Activity for handling interaction with MenuNavigationFragment.
 * It acts as the entry point for the application.
 */
public class MenuActivity extends AppCompatActivity{
    private final static int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    private GoogleSignInAccount account;
    private GoogleSignInClient signInClient;
    private SharedPreferences sharedPreferences;
    private String userName;
    private Long bestScore;


    /**
     * On creation of the program, gets user information and handles login with google play.
     *
     * @param savedInstanceState previously saved states.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get shared settings file in private mode.
        this.sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Get the data from shared settings.
        this.userName = this.sharedPreferences.getString(getString(R.string.preference_username), null);
        this.bestScore = this.sharedPreferences.getLong(getString(R.string.preference_bestscore), 0);

        // Get the sign in button from view and set the size.
        this.signInButton = findViewById(R.id.sign_in_button);
        this.signInButton.setSize(SignInButton.SIZE_WIDE);


        // If there isn't userName in shared pref.
        if (this.userName == null) {
            // Start sign in process.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("419752442556-pdgcavo1hl5tgj7h43a2rqsl3kep7gm1.apps.googleusercontent.com")
                    .requestProfile()
                    .requestEmail()
                    .build();
            // Gets the client to start the sign in activity.
            this.signInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

            // Event listener on button.
            this.signInButton.setOnClickListener(view -> {
                // Start the sign in activity for result.
                Intent signInIntent = this.signInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });


        } else {
            // Disable the button.
            signInButton.setEnabled(false);
        }
    }


    /**
     * Handles the result of logging in to google play
     *
     * @param requestCode Identifies the request.
     * @param resultCode Indicates if the request succeeded .
     * @param data Result content.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check that the request is the expected one.
        if (requestCode != RC_SIGN_IN) {
            return;
        }

        // Gets the google account
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            this.account = task.getResult(ApiException.class);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        // If account is null.
        if (account == null) {
            // Enable the button.
            signInButton.setEnabled(true);
        } else if (account.getDisplayName() != null) {

            // Disable the button.
            signInButton.setEnabled(false);
            // Add the account name into shared pref.
            this.sharedPreferences.edit().putString(getString(R.string.preference_username), account.getDisplayName()).apply();
            this.userName = account.getDisplayName();

            Toast.makeText(getApplicationContext(), "Welcome " + account.getDisplayName(), Toast.LENGTH_LONG).show();
        } else if (account.getDisplayName() == null) {
            Toast.makeText(getApplicationContext(), "Couldn't get the user name! Try again!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Starts the main game.
     *
     * @param view Contains ui components.
     */
    public void actionStartGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);

        intent.putExtra(getString(R.string.preference_username), userName);
        intent.putExtra(getString(R.string.preference_bestscore), bestScore);
        startActivity(intent);
    }

    /**
     * Takes the user to the settings menu.
     *
     * @param view Contains ui components.
     */
    public void actionStartSetting(View view) {

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Takes the user to the online scoreboard.
     *
     * @param view Contains ui components.
     */
    public void actionStartHighScore(View view) {

        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }
}