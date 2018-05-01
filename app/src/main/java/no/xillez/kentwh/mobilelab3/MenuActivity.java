package no.xillez.kentwh.mobilelab3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.Serializable;


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

        this.signInButton = findViewById(R.id.sign_in_button);
        this.signInButton.setSize(SignInButton.SIZE_WIDE);
        this.account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if(this.userName == null){ // || this.userName != this.account.getDisplayName()) {
            this.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestProfile()
                    .requestEmail()
                    .build();
            this.signInClient = GoogleSignIn.getClient(getApplicationContext(), this.gso);

            this.signInButton.setOnClickListener(view -> {
                Intent signInIntent = this.signInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });

            // GoogleSignIn.requestPermissions(this, Activity.RESULT_OK, GoogleSignIn.getLastSignedInAccount(this), Games.SCOPE_GAMES_LITE);
            if (account == null) {
                signInButton.setEnabled(true);
            } else {
                signInButton.setEnabled(false);
                // Games.getGamesClient(getApplicationContext(), account).setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                sharedPreferences.edit().putString(getString(R.string.preference_username), account.getDisplayName()).apply();


                Log.i("test", "Name: " + account.getDisplayName());
            }
        } else {
            signInButton.setEnabled(false);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RC_SIGN_IN) {
            return;
        }
        this.signInButton.setEnabled(false);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void actionStartGame(View view){
        Intent intent = new Intent(this, GameActivity.class);
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


    /**
     * GoogleApiClient.ConnectionCallbacks
     * @param bundle
     */
   /* @Override
    public void onConnected(@Nullable Bundle bundle) {

    }
*/
    /**
     * GoogleApiClient.ConnectionCallbacks
     * @param i
     */
  /*  @Override
    public void onConnectionSuspended(int i) {

    }
*/
    /**
     * GoogleApiClient.OnConnectionFailedListener
     * @param connectionResult
     */
  /*  @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/
}




































/*
    initViews();
//signInSilently();



//        GoogleSignInApi

//Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//startActivityForResult(signInIntent, 9001);
*/








/*



    private void initViews() {
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener((v) -> {
            initGoogleApiClient();
            initiateGoogleSignIn();
            signInButton.setEnabled(false);
            Toast.makeText(this, "You are logged in!", Toast.LENGTH_LONG).show();
        });
    }

    private void initGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        //  mGoogleApiClient.connect();

    }


    private void initiateGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RC_SIGN_IN) {
            return;
        }

        // setGravityForPopups(int gravity)
    }

    private void signInSilently() {
      /*  GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .setAccountName("hello")
                .build();
*

//        OptionalPendingResult<GoogleSignInResult> pendingResult =
*
      OptionalPendingResult<Player> pendingResult =
                        Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            // There's immediate result available.
            //updateButtonsAndStatusFromSignInResult(pendingResult.get());
            Toast.makeText(this, ""+ pendingResult.get().getSignInAccount().getDisplayName(), Toast.LENGTH_SHORT).show();
        } else {
            // There's no immediate result ready, displays some progress indicator and waits for the
            // async callback.
            showProgressIndicator();
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    updateButtonsAndStatusFromSignInResult(result);
                    hideProgressIndicator();
                }
            });
        }*




    }

*/




    /*  private boolean isSignedIn() {
        Toast.makeText(this, ""+GoogleSignIn.getLastSignedInAccount(this), Toast.LENGTH_SHORT).show();
        Log.i("test", "GetAccount: "+GoogleSignIn.getLastSignedInAccount(this).getEmail());
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }


    private void signInSilently() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder().requestId()
                        .requestProfile()
                        .requestEmail()
                        .requestScopes(Games.SCOPE_GAMES)
                        .build()
        );

        signInClient.silentSignIn().addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        // The signed in account is stored in the task's result.
                        signedInAccount = task.getResult();
                        Toast.makeText(this, signedInAccount.getDisplayName(), Toast.LENGTH_SHORT).show();
                        Log.i("test", signedInAccount.getDisplayName());
                    } else {
                        // Player will need to sign-in explicitly using via UI
                        Log.i("test", "Log in was not successful");
                        signInButton = findViewById(R.id.sign_in_button);
                        signInButton.setSize(SignInButton.SIZE_WIDE);
                        signInButton.setOnClickListener((v) -> {
                            initGoogleApiClient();
                            initiateGoogleSignIn();
                            signInButton.setEnabled(false);
                            Toast.makeText(this, "You are logged in!", Toast.LENGTH_LONG).show();
                        });
                    }
                });

       // GamesClient gamesClient = new GamesClient(getApplicationContext(), googleSignInOptions);
    }

    private void initGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        //  mGoogleApiClient.connect();

    }


    private void initiateGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                signedInAccount = result.getSignInAccount();
                Toast.makeText(this, ""+signedInAccount.getDisplayName(), Toast.LENGTH_SHORT).show();
                Log.i("test", ""+signedInAccount.getDisplayName());
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isSignedIn()) {
            signInSilently();
        }else {
            Log.i("test", "Is already loggin in");
        }
    }
*/