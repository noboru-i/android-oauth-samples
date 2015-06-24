package hm.orz.chaos114.oauthsamples.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hm.orz.chaos114.oauthsamples.oauth.InstagramManager;
import hm.orz.chaos114.oauthsamples.oauth.TwitterManager;
import hm.orz.chaos114.oauthsamples.valueobject.PhotoObject;
import twitter4j.ExtendedMediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;

public final class TwitterPhotoProvider implements PhotoProvider {
    private static final String TAG = TwitterPhotoProvider.class.getSimpleName();

    Context mContext;
    List<PhotoObject> mList;
    String mMaxId;

    public TwitterPhotoProvider(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public void fetchNext() {
        ResponseList<Status> statuses = TwitterManager.query(mContext, mMaxId);
        mMaxId = Long.toString(statuses.get(statuses.size() - 1).getId());
        List<twitter4j.Status> imageList = TwitterManager.filterOnlyImage(statuses);
        imageList = TwitterManager.filterOnlyNotRetweet(imageList);
        for (twitter4j.Status status : imageList) {
            for (ExtendedMediaEntity extendedMediaEntity : status.getExtendedMediaEntities()) {
                String id = Long.toString(status.getId());
                String url = extendedMediaEntity.getMediaURL();
                Date date = status.getCreatedAt();
                mList.add(new PhotoObject(id, url, date));
            }
        }
    }

    @Override
    @NonNull
    public List<PhotoObject> getList() {
        return mList;
    }
}
