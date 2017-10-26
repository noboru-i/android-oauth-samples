package hm.orz.chaos114.oauthsamples;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import hm.orz.chaos114.oauthsamples.databinding.ActivityShareBinding;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = ShareActivity.class.getSimpleName();

    ActivityShareBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share);
        binding.setHandler(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
        }
    }

    public void onClickTwitter(View view) {
        Log.d("TAG", "onClickTwitter");
        Picasso.with(this).load("http://my-android-server.appspot.com/image/high.png")
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "temp.jpg", null);
                        Uri imageUri = Uri.parse(url);

                        final Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/jpeg");
                        intent.putExtra(Intent.EXTRA_TEXT, "http://my-android-server.appspot.com/\n" + binding.title.getText().toString());
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);

                        final PackageManager packManager = getPackageManager();
                        int flag;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            flag = PackageManager.MATCH_ALL;
                        } else {
                            flag = PackageManager.MATCH_DEFAULT_ONLY;
                        }
                        final List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(intent, flag);

                        for (ResolveInfo resolveInfo : resolvedInfoList) {
                            if (resolveInfo.activityInfo.packageName.equals("com.twitter.android")
                                    && resolveInfo.activityInfo.name.contains("DMActivity")) {
                                intent.setClassName(resolveInfo.activityInfo.packageName,
                                        resolveInfo.activityInfo.name);
                                startActivity(intent);
                                return;
                            }
                        }

                        throw new RuntimeException("not found Twitter");
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        // no-op
                        Log.d("TAG", "onBitmapFailed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // no-op
                        Log.d("TAG", "onPrepareLoad");
                    }
                });
    }

    public void onClickFacebookMessenger(View view) {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://my-android-server.appspot.com/"))
                .setContentDescription(binding.title.getText().toString())
                .setImageUrl(Uri.parse("http://my-android-server.appspot.com/image/high.png"))
                .setContentTitle("title")
                .build();
        MessageDialog.show(ShareActivity.this, linkContent);
    }

    public void onClickKakao(View view) {
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder(binding.title.getText().toString(),
                        "http://my-android-server.appspot.com/image/high.png",
                        LinkObject.newBuilder()
                                // need to save domain https://developers.kakao.com/docs/android/kakaotalk-link#기본-템플릿-Link
                                .setWebUrl("http://my-android-server.appspot.com/")
                                .setMobileWebUrl("http://my-android-server.appspot.com/").build())
                        .setDescrption("description text")
                        .build())
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Log.d(TAG, "errorResult = " + errorResult);
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                Log.d(TAG, "result = " + result);
            }
        });
    }
}
