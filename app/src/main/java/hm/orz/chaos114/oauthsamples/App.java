package hm.orz.chaos114.oauthsamples;

import android.app.Application;

import com.deploygate.sdk.DeployGate;
import com.facebook.FacebookSdk;

/**
 * Application extended class.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // DeployGate
        DeployGate.install(this);

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
