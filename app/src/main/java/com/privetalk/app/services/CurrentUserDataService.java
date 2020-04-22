//package net.cocooncreations.privetalk.services;
//
//import android.app.IntentService;
//import android.content.Intent;
//
//import com.android.volley.Request;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.RequestFuture;
//
//import org.json.JSONObject;
//
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
///**
// * Created by zeniosagapiou on 03/03/16.
// */
//public class CurrentUserDataService extends IntentService{
//
//    public boolean isServiceRunning;
//
//    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
//    public CurrentUserDataService(String name) {
//        super(name);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//
//        if (isServiceRunning)
//            return;
//
//        isServiceRunning = true;
//
//        RequestFuture<JSONObject> future = RequestFuture.newFuture();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "", new JSONObject(), future, future);
//        volleyRequestQueue.add(request);
//
//        try {
//            JSONObject jsonObject = future.get(30, TimeUnit.SECONDS);
//
//        } catch (InterruptedException e) {
//            // exception handling
//        } catch (ExecutionException e) {
//            // exception handling
//        } catch (TimeoutException e) {
//            // exception handling
//        }
//    }
//}
