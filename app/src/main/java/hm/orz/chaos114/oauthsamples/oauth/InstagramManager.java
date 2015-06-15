package hm.orz.chaos114.oauthsamples.oauth;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import hm.orz.chaos114.oauthsamples.R;
import hm.orz.chaos114.oauthsamples.utils.SharedPreferencesAccessor;

public final class InstagramManager {
    @NonNull
    public static Uri getUrl(@NonNull Context context) {

        String clientId = context.getString(R.string.instagram_client_id);
        String redirectUri = context.getString(R.string.instagram_redirect_uri);
        String uriString = String.format(Locale.getDefault(),
                "https://instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&response_type=token",
                clientId,
                redirectUri);
        return Uri.parse(uriString);
    }

    public static void saveToken(@NonNull Context context, Uri uri) {
        String accessToken = uri.getFragment().split("=")[1];
        SharedPreferencesAccessor accessor = new SharedPreferencesAccessor(context);
        accessor.saveInstagramAccessToken(accessToken);
    }

    @Nullable
    public static String test(@NonNull Context context) {
        SharedPreferencesAccessor accessor = new SharedPreferencesAccessor(context);
        String accessToken = accessor.getInstagramAccessToken();
        String clientSecret = context.getString(R.string.instagram_client_secret);

        String endpoint = "/users/self";
        Map<String, String> params = new TreeMap<>();
        params.put("access_token", accessToken);

        StringBuilder sigSource = new StringBuilder();
        sigSource.append(endpoint);
        for (TreeMap.Entry entry : params.entrySet()) {
            sigSource.append("|").append(entry.getKey()).append("=").append(entry.getValue());
        }

        String sig = toHmacSHA256(sigSource.toString(), clientSecret);

        HttpURLConnection con = null;
        try {
            URL url = new URL("https://api.instagram.com/v1" + endpoint + "?access_token=" + accessToken + "&sig=" + sig);
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = null;
            while((line = br.readLine()) != null) {
                android.util.Log.d("TEST", "line = " + line);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return null;
    }

    private static String toHmacSHA256(String data, String key) {
        try {
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), algorithm));
            return new String(Base64.encode(mac.doFinal(data.getBytes("UTF8")), Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
