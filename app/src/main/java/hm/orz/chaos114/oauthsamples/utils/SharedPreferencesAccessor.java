package hm.orz.chaos114.oauthsamples.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import twitter4j.auth.AccessToken;

/**
 * Accessor for SharedPreferences.
 * Do not direct use SharedPreferences in other class.
 */
public final class SharedPreferencesAccessor {
    private final Context mContext;

    public SharedPreferencesAccessor(Context context) {
        mContext = context;
    }

    public void saveInstagramAccessToken(String accessToken) {
        save("instagram_access_token", accessToken);
    }

    public String getInstagramAccessToken() {
        return get("instagram_access_token");
    }

    public void saveTwitterAccessToken(AccessToken accessToken) {
        save("twitter_access_token", accessToken.getToken());
        save("twitter_access_token_secret", accessToken.getTokenSecret());
    }

    public AccessToken getTwitterAccessToken() {
        String token = get("twitter_access_token");
        String tokenSecret = get("twitter_access_token_secret");
        return new AccessToken(token, tokenSecret);
    }

    private void save(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String get(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sp.getString(key, null);
    }
}
