package hm.orz.chaos114.oauthsamples.oauth;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import hm.orz.chaos114.oauthsamples.R;
import hm.orz.chaos114.oauthsamples.utils.SharedPreferencesAccessor;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter API access management class.
 */
public final class TwitterManager {
    private static final String TAG = TwitterManager.class.getSimpleName();

    private TwitterManager() {
    }

    @Nullable
    public static String getAuthorizationURL(@NonNull Context context) {
        OAuthAuthorization oauth = createOAuthAuthorization(context);

        try {
            RequestToken requestToken = oauth.getOAuthRequestToken("oauth-samples://android-oauth-samples.chaos114.orz.hm/twitter");
            saveRequestToken(context, requestToken);
            return requestToken.getAuthorizationURL();
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

    public static void auth(@NonNull Context context, @Nullable String token, @Nullable String verifier) {
        if (token == null || verifier == null) {
            // canceled
            return;
        }

        OAuthAuthorization oauth = createOAuthAuthorization(context);
        try {
            RequestToken requestToken = loadRequestToken(context);
            AccessToken accessToken = oauth.getOAuthAccessToken(requestToken, verifier);
            SharedPreferencesAccessor accessor = new SharedPreferencesAccessor(context);
            accessor.saveTwitterAccessToken(accessToken);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static ResponseList<Status> query(@NonNull Context context, String maxId) {
        SharedPreferencesAccessor accessor = new SharedPreferencesAccessor(context);
        AccessToken accessToken = accessor.getTwitterAccessToken();

        Twitter twitter = createTwitter(context);
        twitter.setOAuthAccessToken(accessToken);
        try {
            Paging paging = new Paging();
            if (maxId != null) {
                long maxIdNum = Long.valueOf(maxId) - 1;
                paging.setMaxId(maxIdNum);
            }
            return twitter.getUserTimeline(paging);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static List<Status> filterOnlyImage(@NonNull List<Status> source) {
        List<Status> imageList = new ArrayList<>();
        for (Status status : source) {
            MediaEntity[] mediaEntities = status.getMediaEntities();
            if (mediaEntities == null || mediaEntities.length == 0) {
                continue;
            }

            imageList.add(status);
        }

        return imageList;
    }

    @NonNull
    public static List<Status> filterOnlyNotRetweet(@NonNull List<Status> source) {
        List<Status> imageList = new ArrayList<>();
        for (Status status : source) {
            if (status.getRetweetedStatus() != null) {
                continue;
            }

            imageList.add(status);
        }

        return imageList;
    }

    @NonNull
    private static OAuthAuthorization createOAuthAuthorization(@NonNull Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        OAuthAuthorization oauth = new OAuthAuthorization(createConfiguration());
        oauth.setOAuthConsumer(consumerKey, consumerSecret);

        return oauth;
    }

    @NonNull
    private static Twitter createTwitter(@NonNull Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);


        Twitter twitter = new TwitterFactory(createConfiguration()).getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        return twitter;
    }

    @NonNull
    private static Configuration createConfiguration() {
        return new ConfigurationBuilder()
                .setTrimUserEnabled(true)
                .setIncludeEntitiesEnabled(false)
                .setIncludeMyRetweetEnabled(false)
                .build();
    }

    private static void saveRequestToken(Context context, RequestToken requestToken) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(context.openFileOutput("TwitterRequestToken", Activity.MODE_PRIVATE));
            oos.writeObject(requestToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // no-op
                    Log.e(TAG, "close error.", e);
                }
            }
        }
    }

    private static RequestToken loadRequestToken(Context context) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(context.openFileInput("TwitterRequestToken"));
            return (RequestToken) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // no-op
                    Log.e(TAG, "close error.", e);
                }
            }
        }
    }
}
