package org.feedhenry.demo.gcmtopicsdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHAuthRequest;
import com.feedhenry.sdk.api2.FHAuthSession;
import com.feedhenry.sdk.utils.DataManager;
import com.feedhenry.sdk2.FHHttpClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.feedhenry.client.fh.ConnectionFailure;
import org.feedhenry.client.fh.FHClient;
import org.feedhenry.client.fh.auth.Account;
import org.feedhenry.client.fh.auth.FHAuthUtil;
import org.feedhenry.client.fh.events.InitFailed;
import org.feedhenry.client.fh.events.InitSuccessful;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 2/1/16.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final int SIGN_IN = 0xdeadbeef;
    public static final int RC_SIGN_IN = 0x100;

    @Bind(R.id.progress_bar)
    ProgressBar progress;
    @Bind(R.id.login_button)
    Button logInButton;

    @Inject
    GoogleApiClient googleApiClient;

    @Inject
    FHClient fhClient;
    @Inject
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);
        ButterKnife.bind(this);
        initInjection();

    }

    private void initInjection() {
        ((GCMTopicDemoApplication) getApplicationContext()).getObjectGraph().inject(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!fhClient.isConnected()) {
            fhClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Subscribe
    public void onInit(InitSuccessful event) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RC_SIGN_IN == requestCode) {
            if (resultCode != RESULT_OK) {
                return;
            }
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);



            FHAuthRequest authRequest = new FHAuthRequest(getApplicationContext(), new FHAuthSession(DataManager.getInstance(), new FHHttpClient()));
            authRequest.setAuthUser("Android", result.getSignInAccount().getIdToken(), "!");
            try {
                authRequest.executeAsync(new FHActCallback() {
                    @Override
                    public void success(FHResponse fhResponse) {
                        Log.d("SIGN_ING", fhResponse.getRawResponse());
                        fhClient.connect();
                    }

                    @Override
                    public void fail(FHResponse fhResponse) {
                        Log.e("SIGN_IN", fhResponse.getErrorMessage(), fhResponse.getError());
                        Toast.makeText(SplashScreenActivity.this, fhResponse.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Subscribe
    public void onInitError(final InitFailed event) {
        final ConnectionFailure failure = event.getConnectionFailure();
        if (failure.getResponseCode() == FHAuthUtil.SIGN_IN_REQUIRED) {
            progress.setVisibility(View.GONE);
            logInButton.setVisibility(View.VISIBLE);
            logInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleApiClient apiClient = googleApiClient;
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
                    startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN);
                }
            });
        } else {
            Log.e("SPLASH", failure.getResponse().getJson().toString());
            throw new RuntimeException(failure.getResponse().getErrorMessage());
        }
    }


}
