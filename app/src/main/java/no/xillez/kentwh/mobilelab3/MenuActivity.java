package no.xillez.kentwh.mobilelab3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

// TODO: Implement points per sec to make it more fun for the user

public class MenuActivity extends AppCompatActivity implements MenuNavigationFragment.OnFragmentInteractionListener {
    private GoogleSignInOptions gso;
    private SignInButton signInButton;
    private GoogleSignInAccount account;
    private GoogleSignInClient signInClient;


    private final static int RC_SIGN_IN = 9001;
    private SharedPreferences sharedPreferences;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get shared preferences file in private mode.
        this.sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Get the data from shared preferences.
        this.userName = this.sharedPreferences.getString(getString(R.string.preference_username), null);

        // Get the sign in button from view and set the size.
        this.signInButton = findViewById(R.id.sign_in_button);
        this.signInButton.setSize(SignInButton.SIZE_WIDE);


        // If there isn't userName in shared pref.
        if(this.userName == null){
            // Start sign in process.
            this.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestProfile()
                    .requestEmail()
                    .build();
            // Gets the client to start the sign in activity.
            this.signInClient = GoogleSignIn.getClient(getApplicationContext(), this.gso);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RC_SIGN_IN) {
            return;
        }

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try
        {
            this.account = task.getResult(ApiException.class);
        } catch (ApiException e)
        {
            e.printStackTrace();
        }
        // Get the last signed in account.
        //this.account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        // If account is null.
        if (account == null) {
            // Enable the button.
            signInButton.setEnabled(true);
        } else if (account.getDisplayName() != null){

            // Disable the button.
            signInButton.setEnabled(false);
            // Add the account name into shared pref.
            this.sharedPreferences.edit().putString(getString(R.string.preference_username), account.getDisplayName()).apply();
            this.userName = account.getDisplayName();

            Toast.makeText(getApplicationContext(), "Welcome "+ account.getDisplayName(), Toast.LENGTH_LONG).show();
        } else if (account.getDisplayName() == null){
            Toast.makeText(getApplicationContext(), "Couldn't get the user name! Try again!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void actionStartGame(View view){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(getString(R.string.preference_username), userName);
        startActivity(intent);
    }

    public void actionStartSetting(View view){
        // TODO: Start settings activity
    }

    public void actionStartHighScore(View view){
        // TODO: Start high score activity
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }
}