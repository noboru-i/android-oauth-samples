package hm.orz.chaos114.oauthsamples.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;
import hm.orz.chaos114.oauthsamples.valueobject.PhotoObject;

public final class InstagramPhotoProvider implements PhotoProvider {
    private static final String TAG = InstagramPhotoProvider.class.getSimpleName();

    Context mContext;
    List<PhotoObject> mList;

    public InstagramPhotoProvider(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public void fetchNext() {
        String maxId = null;
        if (mList.size() > 0) {
            maxId = mList.get(mList.size() - 1).getId();
        }
        String result = InstagramManager.fetchMedia(mContext, maxId);
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray dataArray = obj.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                String id = dataArray.getJSONObject(i).getString("id");
                String lowResulutionImageUrl = dataArray.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                Date date = new Date(Long.parseLong(dataArray.getJSONObject(i).getString("created_time")) * 1000);
                mList.add(new PhotoObject(id, lowResulutionImageUrl, date));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NonNull
    public List<PhotoObject> getList() {
        return mList;
    }
}
