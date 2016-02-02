package org.feedhenry.demo.gcmtopicsdemo;

import android.app.Application;

import org.feedhenry.demo.gcmtopicsdemo.injection.ApplicationModule;

import dagger.ObjectGraph;

/**
 * Created by summers on 2/1/16.
 */
public class GCMTopicDemoApplication extends Application {
    private ObjectGraph objectGraph;

    @Override public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(new ApplicationModule(this));
        objectGraph.inject(this);
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }
}
