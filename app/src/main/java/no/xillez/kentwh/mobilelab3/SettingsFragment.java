package no.xillez.kentwh.mobilelab3;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
}
