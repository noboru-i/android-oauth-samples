package hm.orz.chaos114.oauthsamples;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;
import hm.orz.chaos114.oauthsamples.utils.SharedPreferencesAccessor;


public class CallbackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = getIntent().getData();
            InstagramManager.saveToken(CallbackActivity.this, uri);

            SharedPreferencesAccessor accessor = new SharedPreferencesAccessor(CallbackActivity.this);

            TextView textView = (TextView) findViewById(R.id.callback_textview);
            textView.setText(accessor.getInstagramAccessToken());

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    InstagramManager.test(CallbackActivity.this);
                    return null;
                }
            }.execute();
        }
    }
}
