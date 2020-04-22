package com.privetalk.app.utilities;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by zachariashad on 13/01/16.
 */
public class VolleySingleton {

    private static VolleySingleton mInstance;
    private static DefaultRetryPolicy defaultRetryPolicy;
    private static final int DEFAULT_TIMEOUT = 30000;
//    private static final int ONE_SEC_TIMEOUT = 1000;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleySingleton(Context context) {
        mCtx = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
            defaultRetryPolicy =
                    new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        return mInstance;
    }

//    public static synchronized VolleySingleton getmInstance(Context context) {
//        if (mInstance == null) {
//            mInstance = new VolleySingleton(context);
//            defaultRetryPolicy =
//                    new DefaultRetryPolicy(ONE_SEC_TIMEOUT,
//                            0,
//                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        }
//        return mInstance;
//    }

    public synchronized RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext(), new MyHurlStack());
        }
        return mRequestQueue;
    }

    public void addRequest(Request request) {
        request.setRetryPolicy(defaultRetryPolicy);
        request.setShouldCache(false);
        getRequestQueue().add(request);
    }

    public void addRequest(Request request, String tag) {
        request.setRetryPolicy(defaultRetryPolicy);
        request.setTag(tag);
        request.setShouldCache(false);
        getRequestQueue().add(request);
    }

    public void cancelAllCommunity(){
        getRequestQueue().cancelAll(PriveTalkConstants.COMMUNITY_TAG);
    }

    private class MyHurlStack extends HurlStack {

        @Override
        protected HttpURLConnection createConnection(URL url) throws IOException {

            HttpURLConnection connection = super.createConnection(url);
            connection.setRequestProperty("connection", "close");

            return connection;
        }

        @Override
        public org.apache.http.HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {

            HttpResponse response = super.performRequest(request, additionalHeaders);
            try {

//            response.getStatusLine().getStatusCode() == 403 ||
//                if (response.getStatusLine().getStatusCode() == 401) {
//                    Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.ANAUTHORIZED_SESSION);
//                    intent.putExtras(bundle);
//                    LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);
//                }

                return response;


            }catch (Exception ex){
                ex.printStackTrace();
                return response;
            }

        }


    }
}