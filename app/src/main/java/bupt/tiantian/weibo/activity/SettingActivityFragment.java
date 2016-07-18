package bupt.tiantian.weibo.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.util.NetChecker;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingActivityFragment extends PreferenceFragment {

    public SettingActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        final ListPreference picSizePref = (ListPreference) findPreference(SettingActivity.KEY_PREF_PIC_SIZE);
        picSizePref.setSummary(picSizePref.getEntry());
        picSizePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newValueString=String.valueOf(newValue);
                NetChecker.setLargePicFlag(getActivity(),newValueString);
                preference.setSummary(((ListPreference) preference).getEntries()[Integer.valueOf(newValueString)]);
                return true;
            }
        });
    }
}
