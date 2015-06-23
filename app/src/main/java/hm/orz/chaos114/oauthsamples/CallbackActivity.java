package hm.orz.chaos114.oauthsamples;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;
import hm.orz.chaos114.oauthsamples.oauth.TwitterManager;
import twitter4j.ResponseList;

/**
 * OAuth provider callback activity.
 */
public class CallbackActivity extends ActionBarActivity {
    private static final String TAG = CallbackActivity.class.getSimpleName();

    @InjectView(R.id.callback_textview)
    TextView mTextView;
    @InjectView(R.id.callback_sample_image)
    ImageView mSampleImageView;

    public static void startActivity(Context context, String provider) {
        Intent intent = new Intent(context, CallbackActivity.class);
        intent.putExtra("provider", provider);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        ButterKnife.inject(this);

        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = getIntent().getData();
            Log.d(TAG, "uri = " + uri);
            if (uri != null && uri.getHost().equals("android-oauth-samples.chaos114.orz.hm")) {
                if (uri.getPath().equals("/instagram")) {
                    callbackInstagram(uri);
                } else if (uri.getPath().equals("/twitter")) {
                    callbackTwitter(uri);
                }
            }
            return;
        }

        String provider = getIntent().getStringExtra("provider");
        if ("facebook".equals(provider)) {
            callbackFacebook();
        }
    }

    private void callbackInstagram(Uri uri) {
        InstagramManager.saveToken(CallbackActivity.this, uri);

        ListPhotoActivity.startActivity(this, "instagram");
        finish();
    }

    private void callbackFacebook() {
        ListPhotoActivity.startActivity(this, "facebook");
        finish();
    }

    private void callbackTwitter(final Uri uri) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String token = uri.getQueryParameter("oauth_token");
                String verifier = uri.getQueryParameter("oauth_verifier");

                TwitterManager.auth(CallbackActivity.this, token, verifier);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ListPhotoActivity.startActivity(CallbackActivity.this, "twitter");
                finish();
            }
        };
        task.execute();
    }
}
