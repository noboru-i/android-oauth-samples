package hm.orz.chaos114.oauthsamples.model;

import android.content.Context;
import android.support.annotation.NonNull;

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

import hm.orz.chaos114.oauthsamples.valueobject.PhotoObject;

public final class FacebookPhotoProvider implements PhotoProvider, GraphRequest.Callback {
    private static final String TAG = FacebookPhotoProvider.class.getSimpleName();

    Context mContext;
    List<PhotoObject> mList;
    GraphRequest request;

    public FacebookPhotoProvider(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public void fetchNext() {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (request == null) {
            // first access.
            new GraphRequest(accessToken,
                    "/me/photos/uploaded",
                    null,
                    HttpMethod.GET,
                    this).executeAndWait();
            return;
        }

        request.setCallback(this);
        request.executeAndWait();

    }

    @Override
    @NonNull
    public List<PhotoObject> getList() {
        return mList;
    }

    @Override
    public void onCompleted(GraphResponse graphResponse) {
        try {
            JSONObject obj = graphResponse.getJSONObject();
            JSONArray dataArray = obj.getJSONArray("data");
            request = graphResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);

            for (int i = 0; i < dataArray.length(); i++) {
                String id = dataArray.getJSONObject(i).getString("id");
                String imageUrl = dataArray.getJSONObject(i).optString("source");
                SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.JAPAN);
                Date date;
                try {
                    date = incomingFormat.parse(dataArray.getJSONObject(i).getString("created_time"));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                mList.add(new PhotoObject(id, imageUrl, date));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
