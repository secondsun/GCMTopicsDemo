/**
 * Copyright 2015 Red Hat, Inc., and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.feedhenry.client.fh;

import android.content.Context;
import android.util.Log;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api2.FHAuthSession;
import com.feedhenry.sdk.exceptions.FHNotReadyException;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.utils.DataManager;
import com.feedhenry.sdk2.FHHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;

import org.feedhenry.client.fh.auth.Account;
import org.feedhenry.client.fh.auth.FHAuthClientConfig;
import org.feedhenry.client.fh.auth.FHAuthUtil;
import org.feedhenry.client.fh.events.InitFailed;
import org.feedhenry.client.fh.events.InitSuccessful;
import org.feedhenry.client.fh.sync.FHSyncClientConfig;
import org.json.fh.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class FHClient {

    private static final String TAG = FHClient.class.getSimpleName();
    private Context appContext;
    private FHSyncListener syncListener;
    private FHSyncClient syncClient;
    private FHAuthClientConfig authConfig;
    private FHSyncClientConfig syncBuilder;
    private Account account;
    private boolean isConnected = false;
    private boolean isConnecting = false;
    private final Bus bus;
    private InitFailed failure = null;
    private InitSuccessful success = null;

    static {com.feedhenry.sdk.FHHttpClient.setTimeout(60000);}

    private FHClient(Bus bus) {
        this.bus = bus;
        bus.register(this);
    }



    public void connect() {

        if (isConnected || isConnecting) {
            return;
        }
        isConnecting = true;
        FH.init(appContext, new FHActCallback() {


            @Override
            public void success(final FHResponse fhResponse) {

                if (authConfig != null) {
                    performAuthThenSync(fhResponse);
                } else {
                    try {
                        setupSync(fhResponse);
                    } catch (Exception e) {

                        postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
                    }
                }
            }

            @Override
            public void fail(FHResponse fhResponse) {
                postConnectFailureRunner(fhResponse);
            }
        });

    }

    private void performAuthThenSync(final FHResponse fhResponse) {
        final FHAuthSession session = new FHAuthSession(DataManager.getInstance(), new FHHttpClient());
        if (!session.exists()) {
            postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
        } else {
            try {
                session.verify(new com.feedhenry.sdk.api.FHAuthSession.Callback() {

                    @Override
                    public void handleSuccess(boolean b) {
                        if (!b) {
                            postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                        } else {
                            try {
                                FH.cloud("/account/me", "GET", null, null, new FHActCallback() {
                                    @Override
                                    public void success(FHResponse fhResponse) {
                                        Log.d(TAG + " getME", fhResponse.getJson().toString());
                                        setAccount(GsonUtil.GSON.fromJson(fhResponse.getJson().toString(), Account.class));
                                        if (syncBuilder != null) {
                                            syncBuilder.addMetaData(FHAuthSession.SESSION_TOKEN_KEY, session.getToken());
                                        }
                                        setupSync(fhResponse);
                                    }

                                    @Override
                                    public void fail(FHResponse fhResponse) {
                                        postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                            }

                        }
                    }

                    @Override
                    public void handleError(FHResponse fhResponse) {
                        postAuthenticationRequired(fhResponse, new AuthCompleteCallback(session));
                    }
                }, false);


            } catch (Exception e) {
                postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
            }

        }
    }

    @Produce
    public InitFailed produceFailure() {
        return failure;
    }

    @Produce
    public InitSuccessful produceSuccessful() {
        return success;
    }

    private void postCheckAccount(FHResponse fhResponse) {
        try {
            FH.cloud("/account/login", "POST", new Header[0], fhResponse.getJson(), new FHActCallback() {
                @Override
                public void success(FHResponse fhResponse) {
                    setAccount(GsonUtil.GSON.fromJson(fhResponse.getJson().toString(), Account.class));
                    setupSync(fhResponse);

                }

                @Override
                public void fail(FHResponse fhResponse) {
                    throw new RuntimeException(fhResponse.getErrorMessage());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void setupSync(FHResponse fhResponse) {
        if (syncBuilder != null) {
            FHSyncConfig syncConfig = new FHSyncConfig();
            syncConfig.setAutoSyncLocalUpdates(syncBuilder.isAutoSyncLocalUpdates());
            syncConfig.setCrashCountWait(syncBuilder.getCrashCountWait());
            syncConfig.setNotifyClientStorageFailed(syncBuilder.isNotifyClientStorageFailed());
            syncConfig.setNotifyDeltaReceived(syncBuilder.isNotifyDeltaReceived());
            syncConfig.setNotifyLocalUpdateApplied(syncBuilder.isNotifyLocalUpdateApplied());
            syncConfig.setNotifyOfflineUpdate(syncBuilder.isNotifyOfflineUpdate());
            syncConfig.setNotifyRemoteUpdateApplied(syncBuilder.isNotifyRemoteUpdateApplied());
            syncConfig.setNotifySyncStarted(syncBuilder.isNotifySyncStarted());
            syncConfig.setNotifySyncFailed(syncBuilder.isNotifySyncFailed());
            syncConfig.setNotifySyncComplete(syncBuilder.isNotifySyncComplete());
            syncConfig.setNotifySyncCollisions(syncBuilder.isNotifySyncCollisions());
            syncConfig.setNotifyUpdateFailed(syncBuilder.isNotifyRemoteUpdateFailed());
            syncConfig.setResendCrashedUpdates(syncBuilder.isResendCrashedUpdates());
            syncConfig.setSyncFrequency(syncBuilder.getSyncFrequencySeconds());
            syncConfig.setUseCustomSync(syncBuilder.isUseCustomSync());

            syncClient = FHSyncClient.getInstance();
            syncClient.init(appContext, syncConfig, syncListener);

            JSONObject queryParams = syncBuilder.getQueryParams();
            JSONObject metaData = syncBuilder.getMetaData();

            try {
                for (String dataSet : syncBuilder.getDataSets()) {
                    syncClient.manage(dataSet, null, queryParams, metaData);
                }
                postConnectSuccessRunner(fhResponse);
            } catch (Exception e) {
                postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
            }
        }
    }


    private void postConnectSuccessRunner(FHResponse fhResponse) {
        isConnected = true;
        isConnecting = false;
        this.failure = null;
        bus.post(this.success = new InitSuccessful(fhResponse));
    }

    private void postConnectFailureRunner(FHResponse fhResponse) {
        isConnecting = false;
        this.success = null;
        bus.post(this.failure = new InitFailed(new ConnectionFailure(fhResponse)));
    }

    private void postConnectFailureRunner(ConnectionFailure failure) {
        isConnecting = false;
        this.success = null;
        bus.post(this.failure = new InitFailed(failure));
    }

    private void postAuthenticationRequired(FHResponse fhResponse, final FHActCallback callback) {

        Resolution authResolver = null;
        try {
            authResolver = FHAuthUtil.buildAuthResolver(this.authConfig, callback);
        } catch (FHNotReadyException e) {
            postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
        }
        ConnectionFailure failure = new ConnectionFailure(fhResponse, authResolver, FHAuthUtil.SIGN_IN_REQUIRED);
        postConnectFailureRunner(failure);
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public FHSyncClient getSyncClient() {
        return syncClient;
    }

    public URL getCloudUrl(String path) {
        try {
            return new URL(FH.getCloudHost() + path);
        } catch (MalformedURLException | FHNotReadyException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    public static class Builder {
        protected FHSyncClientConfig syncBuilder;
        protected FHAuthClientConfig authBuilder;

        private final Context context;
        private final Bus bus;

        public Builder(Context context, Bus bus) {
            this.context = context.getApplicationContext();
            this.bus = bus;
        }

        public Builder addFeature(FHAuthClientConfig authBuilder) {
            this.authBuilder = authBuilder;
            return this;
        }

        public Builder addFeature(FHSyncClientConfig syncBuilder) {
            this.syncBuilder = syncBuilder;
            return this;
        }

        public FHClient build() {
            FHClient client = new FHClient(bus);
            client.appContext = context;

            if (this.syncBuilder != null) {

                client.syncListener = syncBuilder.getSyncListener();
                client.syncBuilder = syncBuilder;
            }

            if (this.authBuilder != null) {
                client.authConfig = authBuilder;
            }


            return client;
        }


    }

    public boolean isConnected() {
        return isConnected;
    }

    private class AuthCompleteCallback implements FHActCallback {

        private final FHAuthSession session;

        public AuthCompleteCallback(FHAuthSession session) {
            this.session = session;
        }

        @Override
        public void success(FHResponse fhAuthResponse) {
            try {
                if (syncBuilder != null) {
                    syncBuilder.addMetaData(FHAuthSession.SESSION_TOKEN_KEY, session.getToken());
                }
                Log.d("Connect", fhAuthResponse.getJson().toString());
                postCheckAccount(fhAuthResponse);
            } catch (Exception e) {
                postConnectFailureRunner(new FHResponse(null, null, e, e.getMessage()));
            }
        }

        @Override
        public void fail(FHResponse fhResponse) {
            postConnectFailureRunner(fhResponse);
        }

    }
}

