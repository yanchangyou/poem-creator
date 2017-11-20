package com.ware4.poem.poemcreator.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.*;

import okhttp3.*;

/**
 * 客户端包装器
 */
public class HttpClientWrapper {

    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");

    static OkHttpClient httpClient = new OkHttpClient();


    static {
        try {
            setSSL(httpClient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setSSL(OkHttpClient httpClient) throws Exception {
//        SSLContext sc = SSLContext.getInstance("SSL");
//        TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//
//            @Override
//            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//            }
//        } };
//        sc.init(null, trustManager, new SecureRandom());
        OkHttpClient.Builder builder = httpClient.newBuilder();
//        builder.sslSocketFactory(sc.getSocketFactory(), (X509TrustManager) trustManager[0]);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public static String httpGet(String url) throws IOException {
        return httpGet(url, new Headers.Builder().build());
    }

    public static String httpPostJson(String url, String json) throws IOException {
        return httpPostJson(url, json, new Headers.Builder().build());
    }

    public static String httpGet(String url, Headers headers) throws IOException {
        Request request = new Request.Builder().headers(headers).url(url).build();
        Response response = httpClient.newCall(request).execute();
        return response.body().string(); // 返回的是string 类型，json的mapper可以直接处理
    }

    public static String httpPostJson(String url, String json, Headers headers) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder().headers(headers).url(url).post(requestBody).build();
        Response response = httpClient.newCall(request).execute();

        return response.body().string();
    }

    public static String httpPostForm(String url, Map<String, String> param, Headers headers) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();

        for (String key : param.keySet()) {
            builder.add(key, param.get(key));
        }

        Request request = new Request.Builder().headers(headers).url(url).post(builder.build()).build();
        Response response = httpClient.newCall(request).execute();

        return response.body().string();
    }

}
