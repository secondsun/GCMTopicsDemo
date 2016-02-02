package org.feedhenry.demo.gcmtopicsdemo.injection;

import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Bus;

import org.feedhenry.client.fh.FHClient;
import org.feedhenry.demo.gcmtopicsdemo.GCMTopicDemoApplication;
import org.feedhenry.demo.gcmtopicsdemo.MainActivity;
import org.feedhenry.demo.gcmtopicsdemo.SplashScreenActivity;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                GCMTopicDemoApplication.class, MainActivity.class, SplashScreenActivity.class
        }
)
public class ApplicationModule {

    private final Context context;

    private final Bus bus = new Bus();

    public ApplicationModule(Context context) {
        this.context = context.getApplicationContext();
    }


    @Provides
    @Singleton
    public FHClient provideFHClient() {

        final AtomicReference<FHClient> clientRef = new AtomicReference<>();

        FHClient fhclient = new FHClient.Builder(context, bus)
                .build();
        clientRef.set(fhclient);;

        return fhclient;
    }

    @Provides
    @Singleton
    public Bus getBus() {
        return this.bus;
    }

    @Provides
    @Singleton
    public GoogleApiClient provideGoogleAPIClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestServerAuthCode("272275396485-ke3ehqieoois0g69r66dh3ap7uc3d56h.apps.googleusercontent.com")
                    .requestIdToken("272275396485-ke3ehqieoois0g69r66dh3ap7uc3d56h.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
        GoogleApiClient apiClient = new GoogleApiClient.Builder(context)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        return apiClient;
    }


}
