package com.grayfox.android.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import com.grayfox.android.R;
import com.grayfox.android.client.AppUsersApiRequest;
import com.grayfox.android.client.BaseApiRequest;
import com.grayfox.android.dao.AppAccessTokenDao;

import javax.inject.Inject;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Connects this app with Foursquare.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
@ContentView(R.layout.activity_sign_in)
public class SignInActivity extends RoboActionBarActivity {

    private static final int REQUEST_CODE_FOURSQUARE_CONNECT = 200;
    private static final String TAG = Activity.class.getSimpleName();

    @InjectView(R.id.connect_to_foursquare_button) private Button connectToFoursquareButton;

    @Inject private AppAccessTokenDao appAccessTokenDao;
    @Inject private AppUsersApiRequest appUsersApiRequest;

    private ProgressDialog registerProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (appAccessTokenDao.fetchAccessToken() != null) finishAndGotoMainActivity();
        else {
            setTitle(R.string.title_activity_sign_in);
            connectToFoursquareButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = FoursquareOAuth.getConnectIntent(SignInActivity.this, getString(R.string.foursquare_client_id));
                    if (FoursquareOAuth.isPlayStoreIntent(intent)) {
                        toastMessage(R.string.foursquare_not_installed_message);
                        startActivity(intent);
                    } else startActivityForResult(intent, REQUEST_CODE_FOURSQUARE_CONNECT);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FOURSQUARE_CONNECT:
                onCompleteFoursquareConnect(resultCode, data);
                break;
            default: super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    private void onCompleteFoursquareConnect(int resultCode, Intent data) {
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();
        if (exception == null) {
            String code = codeResponse.getCode();
            registerProgressDialog = ProgressDialog.show(this, null, getString(R.string.register_in_progress), true, false);
            appUsersApiRequest
                    .foursquareAuthorizationCode(code)
                    .asyncAccessToken(new BaseApiRequest.RequestCallback<String>() {

                        @Override
                        public void onSuccess(String accessToken) {
                            registerProgressDialog.dismiss();
                            finishAndGotoMainActivity();
                        }

                        @Override
                        public void onFailure(String reason) {
                            registerProgressDialog.dismiss();
                            toastMessage(reason);
                        }
                    });
        } else {
            if (exception instanceof FoursquareCancelException) toastMessage(R.string.foursquare_auth_canceled);
            else if (exception instanceof FoursquareDenyException)toastMessage(R.string.foursquare_auth_denied);
            else if (exception instanceof FoursquareOAuthException) {
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(getString(R.string.foursquare_oauth_error_message_format, errorMessage, errorCode));
            } else toastMessage(getString(R.string.foursquare_unknown_error_message_format, exception.getMessage()));
        }
    }

    private void finishAndGotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void toastMessage(int messageId) {
        Toast.makeText(getApplicationContext(), 
                messageId, 
                Toast.LENGTH_SHORT)
                .show();
    }
    
    private void toastMessage(String message) {
        Toast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT)
                .show();
    }
}