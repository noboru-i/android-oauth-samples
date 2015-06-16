package hm.orz.chaos114.oauthsamples;

import android.app.Application;

import com.facebook.FacebookSdk;

/**
 * Application extended class.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
