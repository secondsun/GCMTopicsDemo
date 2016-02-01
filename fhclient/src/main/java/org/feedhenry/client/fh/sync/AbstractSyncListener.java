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
package org.feedhenry.client.fh.sync;

import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

public abstract class AbstractSyncListener implements FHSyncListener {
    @Override
    public void onSyncStarted(NotificationMessage notificationMessage) {

    }

    @Override
    public void onSyncCompleted(NotificationMessage notificationMessage) {

    }

    @Override
    public void onUpdateOffline(NotificationMessage notificationMessage) {

    }

    @Override
    public void onCollisionDetected(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public void onLocalUpdateApplied(NotificationMessage notificationMessage) {

    }

    @Override
    public void onDeltaReceived(NotificationMessage notificationMessage) {

    }

    @Override
    public void onSyncFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onClientStorageFailed(NotificationMessage notificationMessage) {

    }
}
