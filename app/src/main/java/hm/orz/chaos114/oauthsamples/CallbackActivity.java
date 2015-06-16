package hm.orz.chaos114.oauthsamples;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;
import hm.orz.chaos114.oauthsamples.utils.SharedPreferencesAccessor;

public class CallbackActivity extends ActionBarActivity {

    @InjectView(R.id.callback_textview)
    TextView mTextView;
    @InjectView(R.id.callback_sample_image)
    ImageView mSampleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        ButterKnife.inject(this);

        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = getIntent().getData();
            InstagramManager.saveToken(CallbackActivity.this, uri);

            SharedPreferencesAccessor accessor = new SharedPreferencesAccessor(CallbackActivity.this);

            mTextView.setText(accessor.getInstagramAccessToken());

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
