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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;

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
                callbackInstagram(uri);
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

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return InstagramManager.test(CallbackActivity.this);
            }

            @Override
            protected void onPostExecute(String s) {
                mTextView.setText(s);

                setSampleImage(s);
            }
        }.execute();
    }

    private void callbackFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        new GraphRequest(accessToken,
                "/me/photos/uploaded",
                null,
                HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                try {
                    String responseString = graphResponse.getRawResponse();
                    mTextView.setText(responseString);

                    JSONObject obj = graphResponse.getJSONObject();
                    JSONArray dataArray = obj.getJSONArray("data");
                    String imageUrl = dataArray.getJSONObject(0).getString("source");
                    Picasso.with(CallbackActivity.this).load(imageUrl).into(mSampleImageView);
                } catch (JSONException e) {
                    Toast.makeText(CallbackActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).executeAsync();

    }

    private void setSampleImage(String jsonString) {
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray dataArray = obj.getJSONArray("data");
            String lowResulutionImageUrl = dataArray.getJSONObject(0).getJSONObject("images").getJSONObject("low_resolution").getString("url");
            Picasso.with(this).load(lowResulutionImageUrl).into(mSampleImageView);
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
