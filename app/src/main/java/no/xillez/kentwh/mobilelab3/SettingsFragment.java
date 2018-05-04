package no.xillez.kentwh.mobilelab3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings from an XML resource
        addPreferencesFromResource(R.xml.settings);

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);


    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {


    }
}
