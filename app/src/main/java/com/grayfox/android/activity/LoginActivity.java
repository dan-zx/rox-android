package com.grayfox.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;

import com.grayfox.android.R;
import com.grayfox.android.data.dao.FoursquareAuthDao;
import com.grayfox.android.data.dao.impl.DaoFactory;

/**
 * Connects this app with Foursquare.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class LoginActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_FOURSQUARE_CONNECT = 200;
    private static final String TAG = Activity.class.getSimpleName();

    private FoursquareAuthDao foursquareAuthDao;
    private Button connectToFoursquareButton;
    private TextView connectToFoursquareText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        foursquareAuthDao = new DaoFactory(this).getFoursquareAuthDAO();
        boolean isConnectedToFoursquare = foursquareAuthDao.fetchAuthCode() != null;
        connectToFoursquareButton = (Button) findViewById(R.id.connect_to_foursquare_button);
        connectToFoursquareText = (TextView) findViewById(R.id.connected_to_foursquare_text);
        connectToFoursquareText.setVisibility(isConnectedToFoursquare ? View.VISIBLE : View.GONE);
        connectToFoursquareButton.setVisibility(isConnectedToFoursquare ? View.GONE : View.VISIBLE);
        connectToFoursquareButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = FoursquareOAuth.getConnectIntent(LoginActivity.this, getString(R.string.foursquare_client_id));
                if (FoursquareOAuth.isPlayStoreIntent(intent)) {
                    toastMessage(R.string.foursquare_not_installed_message);
                    startActivity(intent);
                } else startActivityForResult(intent, REQUEST_CODE_FOURSQUARE_CONNECT);
            }
        });
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
            foursquareAuthDao.saveAuthCode(code);
            Log.d(TAG, "AuthCode="+code);
            connectToFoursquareText.setVisibility(View.VISIBLE);
            connectToFoursquareButton.setVisibility(View.GONE);
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