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

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.api.FHAuthRequest;
import com.feedhenry.sdk.exceptions.FHNotReadyException;

import org.feedhenry.client.fh.Resolution;

/**
 * Created by secondsun on 10/30/15.
 */
public class FHAuthUtil {
    public static final int SIGN_IN_REQUIRED = 0x8083;
    private static final String TAG = "FHAuthUtil";

    public static Resolution buildAuthResolver(FHAuthClientConfig authConfig, final FHActCallback callback) throws FHNotReadyException {
        final FHAuthRequest authRequest = FH.buildAuthRequest();
        authRequest.setAuthPolicyId(authConfig.getAuthPolicyId());


        authRequest.setPresentingActivity(authConfig.getCallingActivity());
        authRequest.setAuthUser(authConfig.getAuthPolicyId(),authConfig.getUsername(),authConfig.getPassword());

        return new AuthResolution(authRequest, callback);

    }

}
