package hm.orz.chaos114.oauthsamples;

import android.content.Context;

import hm.orz.chaos114.oauthsamples.model.FacebookPhotoProvider;
import hm.orz.chaos114.oauthsamples.model.InstagramPhotoProvider;
import hm.orz.chaos114.oauthsamples.model.TwitterPhotoProvider;
import lombok.Data;

@Data
public final class ModelLocator {
    private static ModelLocator instance;

    private InstagramPhotoProvider instagramPhotoProvider;
    private FacebookPhotoProvider facebookPhotoProvider;
    private TwitterPhotoProvider twitterPhotoProvider;

    private ModelLocator() {
    }

    public static synchronized ModelLocator getInstance(Context context) {
        if (instance == null) {
            instance = new ModelLocator();

            instance.instagramPhotoProvider = new InstagramPhotoProvider(context);
            instance.facebookPhotoProvider = new FacebookPhotoProvider(context);
            instance.twitterPhotoProvider = new TwitterPhotoProvider(context);
        }
        return instance;
    }
}
