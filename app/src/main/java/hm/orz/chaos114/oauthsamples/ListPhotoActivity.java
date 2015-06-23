package hm.orz.chaos114.oauthsamples;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;
import hm.orz.chaos114.oauthsamples.oauth.TwitterManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import twitter4j.ExtendedMediaEntity;
import twitter4j.ResponseList;


public class ListPhotoActivity extends ActionBarActivity {
    private static final String TAG = ListPhotoActivity.class.getSimpleName();

    public static void startActivity(Context context, String provider) {
        Intent intent = new Intent(context, ListPhotoActivity.class);
        intent.putExtra("provider", provider);
        context.startActivity(intent);
    }

    @InjectView(R.id.list_photo_list)
    ListView mListView;

    private PhotoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo);

        ButterKnife.inject(this);

        String provider = getIntent().getStringExtra("provider");
        switch (provider) {
            case "instagram":
                fetchInstagram();
                break;
            case "facebook":
                fetchFacebook();
                break;
            case "twitter":
                fetchTwitter();
                break;
        }
    }

    private void setList(List<PhotoObject> list) {
        mAdapter = new PhotoAdapter(this, list);
        mListView.setAdapter(mAdapter);
    }

    private void fetchInstagram() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return InstagramManager.fetchMedia(ListPhotoActivity.this);
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONObject obj = new JSONObject(s);
                    JSONArray dataArray = obj.getJSONArray("data");

                    List<PhotoObject> list = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        String lowResulutionImageUrl = dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                        list.add(new PhotoObject(lowResulutionImageUrl));
                    }
                    setList(list);
                } catch (JSONException e) {
                    Toast.makeText(ListPhotoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        };
        task.execute();
    }

    private void fetchFacebook() {

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        new GraphRequest(accessToken,
                "/me/photos/uploaded",
                null,
                HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                try {
                    JSONObject obj = graphResponse.getJSONObject();
                    JSONArray dataArray = obj.getJSONArray("data");
//                    String next = obj.getJSONObject("paging").getString("next");
//                    GraphRequest request = graphResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
//                    request.setCallback(new GraphRequest.Callback() {
//
//                        @Override
//                        public void onCompleted(GraphResponse graphResponse) {
//                            try {
//                                JSONObject obj = graphResponse.getJSONObject();
//                                Log.d(TAG, "obj2 = " + obj.toString());
//                                String next = obj.getJSONObject("paging").getString("next");
//                                Log.d(TAG, "next2 = " + next);
//                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                    });
//                    request.executeAsync();

                    List<PhotoObject> list = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        String imageUrl = dataArray.getJSONObject(i).getString("source");
                        list.add(new PhotoObject(imageUrl));
                    }
                    setList(list);
                } catch (JSONException e) {
                    Toast.makeText(ListPhotoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).executeAsync();
    }

    private void fetchTwitter() {
        new AsyncTask<Void, Void, ResponseList<twitter4j.Status>>() {

            @Override
            protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
                return TwitterManager.query(ListPhotoActivity.this);
            }

            @Override
            protected void onPostExecute(ResponseList<twitter4j.Status> statuses) {
                List<twitter4j.Status> imageList = TwitterManager.filterOnlyImage(statuses);
                List<PhotoObject> list = new ArrayList<>();
                for (twitter4j.Status status : imageList) {
                    Log.d(TAG, "status = " + status);
                    for (ExtendedMediaEntity extendedMediaEntity : status.getExtendedMediaEntities()) {
                        String url = extendedMediaEntity.getMediaURL();
                        list.add(new PhotoObject(url));
                    }
                }
                setList(list);
            }
        }.execute();
    }

    @Data
    @AllArgsConstructor(suppressConstructorProperties = true)
    class PhotoObject {
        private String imageUrl;
    }

    private static class PhotoAdapter extends BaseAdapter {
        Context mContext;
        List<PhotoObject> mList;
        LayoutInflater mInflater;

        public PhotoAdapter(@NonNull Context context, @NonNull List<PhotoObject> list) {
            mContext = context;
            mList = list;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            Picasso.with(mContext).load(object.getImageUrl()).into(imageView);
            Log.d(TAG, "imageUrl = " + object.getImageUrl());

            return convertView;
        }
    }
}
