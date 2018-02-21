package ～;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.IllegalFormatConversionException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import java.net.URLEncoder;

/**
 * Created by greentea774 on 2018/02/21.
 */
public class OauthConnect {

    //SharedPrerence用のキー
    private static final String TOKEN = "token";
    private static final String TOKEN_SECRET = "token_secret";
    private static final String PREF_NAME = "twitter_access_token";

    //リクエストトークン取得用のパラメータ
    private static String RequestTokenURL = "https://api.twitter.com/oauth/request_token";
    private static String CallbackURL = "oauthtest://twitter";
    static String consumerkey = "コンシューマキー";
    static String consumerSecret = "コンシューマシークレット";
    static String oauthToken = ""; // リクエストトークン取得時は利用しない
    static String oauthTokenSecret = ""; // リクエストトークン取得時は利用しない
    static String method = "POST";


    //リクエストトークンの取得
    public static void get_RequestToken() throws InvalidKeyException {

        // OAuthにおいて利用する共通パラメーター
        // パラメーターはソートする必要があるためSortedMapを利用
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("oauth_consumer_key", consumerkey);
        params.put("oauth_signature_method", "HMAC-SHA1");
        params.put("oauth_timestamp", String.valueOf(getUnixTime()));
        params.put("oauth_nonce", String.valueOf(Math.random()));
        params.put("oauth_version", "1.0");
        {
            /*
			 * 署名（oauth_signature）の生成
			 */
            // パラメーターを連結する
            String paramStr = "";
            for (Map.Entry<String, String> param : params.entrySet()) {
                paramStr += "&" + param.getKey() + "=" + param.getValue();
            }
            paramStr = paramStr.substring(1);

            /* 署名対象テキスト（signature base string）の作成 */
            String text = method + "&" + urlEncode(RequestTokenURL) + "&"
                    + urlEncode(paramStr);

            // 署名キーの作成
            String key = urlEncode(consumerSecret) + "&"
                    + urlEncode(oauthTokenSecret);

            // HMAC-SHA1で署名を生成
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = null;
            try {
                mac = Mac.getInstance(signingKey.getAlgorithm());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(text.getBytes());
            String signature = Base64.encodeToString(rawHmac, Base64.DEFAULT);

            // 署名をパラメータに追加
            params.put("oauth_signature", signature);
        }

        // Authorizationヘッダの作成
        String paramStr = "";
        for (Map.Entry<String, String> param : params.entrySet()) {
            paramStr += ", " + param.getKey() + "=\""
                    + URLEncoder.encode(param.getValue()) + "\"";
        }
            paramStr = paramStr.substring(2);
            final String authorizationHeader = "OAuth " + paramStr;

            //非同期処理開始
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    HttpsURLConnection connection = null;
                    try {

                        //URLオブジェクトの生成
                        URL url = new URL(RequestTokenURL);
                        connection = (HttpsURLConnection) url.openConnection();
                        connection.setRequestMethod(method);
                        //リクエストボディの入出力を行う許可を与える
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setRequestProperty("Authorization", authorizationHeader);
                        //APIにアクセス
                        connection.connect();
                        //レスポンスの読み込み
                        BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream()));
                        String response;
                        while ((response = reader.readLine()) != null) {
                            System.out.println(response);
                        }

                    } catch (IOException e) {
                        //エラーレスポンスの読み込み
                        BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getErrorStream()));
                        String response;
                        try {
                            while ((response = reader.readLine()) != null) {
                                System.out.println(response);
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        e.printStackTrace();
                    } finally {


                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String url) {
                    if (url != null) {

                    } else {
                        // 失敗。。。
                    }
                }
            };
            task.execute();
        }

    private static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getUnixTime() {
        return (int) (System.currentTimeMillis() / 1000L);
    }
    }

