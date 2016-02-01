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
package org.feedhenry.client.fh.auth;

import android.app.Activity;

import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.api.FHAuthRequest;

import org.feedhenry.client.fh.Resolution;

/**
 * Created by summers on 11/5/15.
 */
public class AuthResolution implements Resolution {
    private final FHAuthRequest authRequest;
    private Activity resolvingActivity;
    private final FHActCallback callback;

    public AuthResolution(FHAuthRequest authRequest, FHActCallback callback) {
        this.authRequest = authRequest;
        this.callback = callback;
    }

    @Override
    public void setup( Activity activity ) {
        this.resolvingActivity = activity ;
    }

    @Override
    public void run() {
        try {
            authRequest.setPresentingActivity(resolvingActivity);
            authRequest.executeAsync(callback);
        } catch (Exception e) {
            callback.fail(new FHResponse(null, null, e, e.getMessage()));
        }
    }
}
