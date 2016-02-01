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

/**
 * Created by summers on 10/29/15.
 */
public class FHAuthClientConfig {


    private String authPolicyId;
    private Activity callingActivity;

    public FHAuthClientConfig(String authPolicyId) {
        this.authPolicyId = authPolicyId;
    }

    public FHAuthClientConfig setAuthPolicyId(String authPolicyId) {
        this.authPolicyId = authPolicyId;
        return this;
    }

    public String getAuthPolicyId() {
        return authPolicyId;
    }

    public Activity getCallingActivity() {
        return callingActivity;
    }

    public FHAuthClientConfig setCallingActivity(Activity callingActivity) {
        this.callingActivity = callingActivity;
        return this;
    }
}
