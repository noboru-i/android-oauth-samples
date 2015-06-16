package hm.orz.chaos114.oauthsamples;

import android.app.Application;
import android.util.Log;

import com.facebook.FacebookSdk;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
