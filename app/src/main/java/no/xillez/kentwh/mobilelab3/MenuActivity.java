package no.xillez.kentwh.mobilelab3;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity implements MenuNavigationFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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
}
