package com.grayfox.android.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;

import com.grayfox.android.app.R;
import com.grayfox.android.app.dao.AccessTokenDao;

import roboguice.activity.RoboActionBarActivity;
import roboguice.fragment.provided.RoboPreferenceFragment;

import javax.inject.Inject;

public class SettingsActivity extends RoboActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public static class SettingsFragment extends RoboPreferenceFragment {

        private static final String TAG = "SettingsFragment";

        private Preference closeSessionPreference;
        private Preference versionPreference;
        private Preference licencesPreference;
        private PreferenceCategory appPreferences;

        @Inject private AccessTokenDao accessTokenDao;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            findPreferences();
            setupCloseSessionPreference();
            setupLicencesPreference();
            setupVersionPreference();
        }

        private void findPreferences() {
            closeSessionPreference  = findPreference("close_session_pref");
            licencesPreference = findPreference("licences_pref");
            versionPreference = findPreference("app_version_pref");
            appPreferences = (PreferenceCategory) findPreference("app_pref");
        }

        private void setupCloseSessionPreference() {
            if (accessTokenDao.fetchAccessToken() == null) appPreferences.removePreference(closeSessionPreference);
            else {
                closeSessionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.close_session_title)
                                .setMessage(R.string.close_session_alert_message)
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        accessTokenDao.deleteAccessToken();
                                        dialog.dismiss();
                                        getActivity().finish();
                                    }
                                })
                                .show();
                        return true;
                    }
                });
            }
        }

        private void setupLicencesPreference() {
            licencesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    WebView webView = new WebView(getActivity());
                    webView.loadUrl(getString(R.string.url_licenses));
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.licenses_dialog_tile)
                            .setView(webView)
                            .setCancelable(true)
                            .show();
                    return true;
                }
            });
        }

        private void setupVersionPreference() {
            try {
                String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                versionPreference.setSummary(versionName);
            } catch (PackageManager.NameNotFoundException ex) {
                Log.e(TAG, "Couldn't find version name", ex);
            }
        }
    }
}