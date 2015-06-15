package hm.orz.chaos114.oauthsamples;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class CallbackActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);

        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = getIntent().getData();

            TextView textView = (TextView) findViewById(R.id.callback_textview);
            textView.setText(uri.getFragment().split("=")[1]);
        }
    }
}
