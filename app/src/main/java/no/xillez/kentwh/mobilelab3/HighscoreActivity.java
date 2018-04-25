package no.xillez.kentwh.mobilelab3;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HighscoreActivity extends AppCompatActivity implements HighscoreFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
