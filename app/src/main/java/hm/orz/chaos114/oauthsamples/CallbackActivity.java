package hm.orz.chaos114.oauthsamples;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;
import hm.orz.chaos114.oauthsamples.utils.SharedPreferencesAccessor;

public class CallbackActivity extends ActionBarActivity {

    @InjectView(R.id.callback_textview)
    TextView mTextView;

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
                }
            }.execute();
        }
    }
}
