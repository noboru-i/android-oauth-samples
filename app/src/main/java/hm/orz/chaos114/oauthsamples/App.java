package hm.orz.chaos114.oauthsamples;

import android.app.Application;

import com.deploygate.sdk.DeployGate;
import com.facebook.FacebookSdk;

/**
 * Application extended class.
 */
public class App extends Application {
    private static App instance;
    private static ModelLocator mModelLocator;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // DeployGate
        DeployGate.install(this);

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());


        // initialize
        mModelLocator = ModelLocator.getInstance(this);
    }

    public static App getInstance() {
        return instance;
    }

    public static ModelLocator model() {
        return mModelLocator;
    }
}
