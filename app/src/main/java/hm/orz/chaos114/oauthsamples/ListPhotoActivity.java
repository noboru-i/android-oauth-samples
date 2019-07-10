package hm.orz.chaos114.oauthsamples;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.oauthsamples.model.PhotoProvider;
import hm.orz.chaos114.oauthsamples.valueobject.PhotoObject;


public class ListPhotoActivity extends AppCompatActivity {
    private static final String TAG = ListPhotoActivity.class.getSimpleName();

    public static void startActivity(Context context, String provider) {
        Intent intent = new Intent(context, ListPhotoActivity.class);
        intent.putExtra("provider", provider);
        context.startActivity(intent);
    }

    @InjectView(R.id.list_photo_list)
    ListView mListView;

    private PhotoAdapter mAdapter;
    private PhotoProvider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo);

        ButterKnife.inject(this);

        String provider = getIntent().getStringExtra("provider");
        switch (provider) {
            case "instagram":
                mProvider = App.model().getInstagramPhotoProvider();
                break;
            case "facebook":
                mProvider = App.model().getFacebookPhotoProvider();
                break;
            case "twitter":
                mProvider = App.model().getTwitterPhotoProvider();
                break;
        }
        fetch();
    }

    private void setList(List<PhotoObject> list) {
        if (mAdapter == null) {
            View footer = getLayoutInflater().inflate(R.layout.list_footer, null);
            Button footerButton = (Button) footer.findViewById(R.id.next);
            footerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetch();
                }
            });
            mListView.addFooterView(footer);
            mAdapter = new PhotoAdapter(this, list);
            mListView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void fetch() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                mProvider.fetchNext();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setList(mProvider.getList());
            }
        }.execute();
    }

    private static class PhotoAdapter extends BaseAdapter {
        Context mContext;
        List<PhotoObject> mList;
        LayoutInflater mInflater;

        public PhotoAdapter(@NonNull Context context, @NonNull List<PhotoObject> list) {
            mContext = context;
            mList = list;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public PhotoObject getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.row_image, parent, false);
            }

            PhotoObject object = getItem(position);

            if (!TextUtils.isEmpty(object.getImageUrl())) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
                Picasso.get().load(object.getImageUrl()).into(imageView);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(object.getCreatedTime().toString());

            return convertView;
        }
    }
}
