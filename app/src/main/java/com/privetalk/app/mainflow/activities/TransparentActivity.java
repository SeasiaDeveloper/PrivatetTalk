package com.privetalk.app.mainflow.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.utilities.Constants;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;
import com.privetalk.app.utilities.dialogs.AuthenticationDialog;
import com.privetalk.app.utilities.dialogs.AuthenticationListener;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKScopes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zeniosagapiou on 24/02/16.
 */
public class TransparentActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult>, AuthenticationListener {

    private static final int REQUEST_CODE_RESOLVE_ERR = 70;
    private static final int RC_SIGN_IN = 69;

    private Animation inAnimation, outAnimation;
    private View googleLoginButton;
    private View fbLoginButton;
    private View vkLoginButton;
    private View fadeInView;
    private View dismissDialog;
    RelativeLayout dialogContainer;
    private GoogleApiClient mGoogleApiClient;
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    private View progressBar;
    private AuthenticationDialog authenticationDialog;
    private AuthenticationListener mListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        inAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in_2);
        outAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out_2);
        mListner = this;

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {

                setContentView(R.layout.dialog_social_verification);

                progressBar = findViewById(R.id.progressBarVerification);

                fadeInView = findViewById(R.id.fade_in_view);
                dialogContainer = (RelativeLayout) findViewById(R.id.dialogContainer);
                googleLoginButton = findViewById(R.id.googleLogin);
                googleLoginButton.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {

                        if (mGoogleApiClient.isConnected()) {
                            getUserInfoGooglePlus();
                        } else {
                            mGoogleApiClient.connect();
                        }

                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                fbLoginButton = findViewById(R.id.btnfacebookLogin); // //facebookLogin
                fbLoginButton.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        dialogContainer.setVisibility(View.INVISIBLE);
                        authenticationDialog = new AuthenticationDialog(TransparentActivity.this, mListner);
                        authenticationDialog.setCancelable(true);
                        authenticationDialog.show();
                        //dismiss();

                        // facebookLoginButton.performClick();
                    }
                });
                vkLoginButton = findViewById(R.id.vkIcon);
                vkLoginButton.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        VKSdk.login(TransparentActivity.this, VKScopes.EMAIL);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                dismissDialog = findViewById(R.id.closeDialog);

                dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        dismiss();
                    }
                });

                fadeInView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                fadeInView.animate().alpha(0.4f).setDuration(400);
                dialogContainer.startAnimation(inAnimation);

                googleApiStuff();

                facebookStuff();
            }
        }, 500);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1f;
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

    public void dismiss() {

        dialogContainer.startAnimation(outAnimation);
        fadeInView.animate().alpha(0f).setDuration(400);
        outAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //initialize google api client
    private void googleApiStuff() {
        //create a new google api client for google plus
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    //initialize facebook login button
    private void facebookStuff() {

        //initialize facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());

        facebookLoginButton = new LoginButton(this);
        facebookLoginButton.setReadPermissions(Arrays.asList("user_friends, public_profile, email, user_birthday, user_location"));
        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                progressBar.setVisibility(View.VISIBLE);

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                if (response.getError() != null) {
                                    PriveTalkUtilities.showSomethingWentWrongDialog(getApplicationContext());
                                    if (progressBar != null)
                                        progressBar.setVisibility(View.GONE);
                                    return;
                                }

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("fb_id", object.optString("id"));
                                    jsonObject.put("fb_username", object.optString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                verifyAccount(jsonObject);

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                // App code
//                Log.v("LoginActivity", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                // App code
//                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }

    //get all user information available in google+
    private void getUserInfoGooglePlus() {

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

            Person currentPerson = Plus.PeopleApi
                    .getCurrentPerson(mGoogleApiClient);

            if (currentPerson == null) {
                PriveTalkUtilities.showSomethingWentWrongDialog(getApplicationContext());
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                return;
            }
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("gplus_id", currentPerson.getId());
                verifyAccount(jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        getUserInfoGooglePlus();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    //resolve error if connection failed
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
        if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                }
            } finally {
                personBuffer.release();
            }
        } else {
            Log.e("Error", "Error requesting visible circles: " + loadPeopleResult.getStatus());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        RESULT CODE AFTER GOOGLE ACCOUNT SIGN IN
         */
        if (requestCode == RC_SIGN_IN || requestCode == REQUEST_CODE_RESOLVE_ERR) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        /*
        RESULT CODE AFTER VK AUTHEDICATION/LOGIN
         */
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(final VKAccessToken res) {

                // User passed Authorization
                VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {

                        //make a new request for user location,birthdate and genre
                        final VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                                "sex,city,bdate,country"));

                        request.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {

                                try {

                                    JSONObject obj = response.json.getJSONArray("response").getJSONObject(0);

                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("vk_id", obj.optString("id"));
                                    verifyAccount(jsonObject);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                                super.attemptFailed(request, attemptNumber, totalAttempts);
                                if (progressBar != null)
                                    progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                                if (progressBar != null)
                                    progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(VKError error) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                Toast.makeText(TransparentActivity.this, "Authorization failed.\nPlease try again.", Toast.LENGTH_SHORT).show();
////                Log.d("VK Error", "error");
                // User didn't pass Authorization
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        //callback activity result for facebook api
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void verifyAccount(JSONObject formParameteres) {

        JsonObjectRequest verifyAccountRequest = new JsonObjectRequest(Request.Method.POST,
                Links.MY_PROFILE, formParameteres,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);


                        CurrentUser previousUser = CurrentUserDatasource.getInstance(TransparentActivity.this).getCurrentUserInfo();
                        CurrentUser newUser = new CurrentUser(response,
                                previousUser.token,
                                previousUser.email);
                        CurrentUserDatasource.getInstance(TransparentActivity.this).saveCurrentUser(newUser);

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_CURRENT_USER_UPDATED));

                        Toast.makeText(TransparentActivity.this.getApplicationContext(), getString(R.string.social_media_account_connected), Toast.LENGTH_SHORT).show();

                        finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {

                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        Toast.makeText(TransparentActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Token " + CurrentUserDatasource.getInstance(getApplicationContext()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(verifyAccountRequest);


    }

    @Override
    public void onCodeReceived(String code) {

        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.CLIENT_ID, getResources().getString(R.string.instagram_client_id));
            obj.put(Constants.CODE, code);
            obj.put(Constants.CLIENT_SECRET, getResources().getString(R.string.instagram_client_secret));
            obj.put(Constants.GRANT_TYPE, getResources().getString(R.string.instagram_grant_type));
            obj.put(Constants.REDIRECT_URL, getResources().getString(R.string.instagram_redirect_url));
            RequestToken(code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void RequestToken(final String code) {
        StringRequest verifyAccountRequest = new StringRequest(Request.Method.POST, Links.GET_INSTAGRAM_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Toast.makeText(TransparentActivity.this, response, Toast.LENGTH_LONG).show();
                        JSONObject person = null;
                        try {
                            person = new JSONObject(response);
                            String accessToken = person.getString("access_token");
                            String userId = person.getString("user_id");
                            RequestUserData(accessToken,userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TransparentActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.CLIENT_ID, getResources().getString(R.string.instagram_client_id));
                params.put(Constants.CODE, code);
                params.put(Constants.CLIENT_SECRET, getResources().getString(R.string.instagram_client_secret));
                params.put(Constants.GRANT_TYPE, getResources().getString(R.string.instagram_grant_type));
                params.put(Constants.REDIRECT_URL, getResources().getString(R.string.instagram_redirect_url));
                return params;
            }

        };


       /* StringRequest verifyAccountRequest = new StringRequest(Request.Method.POST,
                Links.GET_INSTAGRAM_TOKEN,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);


                        Toast.makeText(TransparentActivity.this.getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                      //  finish();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {

                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        Toast.makeText(TransparentActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", "Token " + CurrentUserDatasource.getInstance(getApplicationContext()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Constants.CLIENT_ID,getResources().getString(R.string.instagram_client_id));

                return params;
            }
*/
        //obj.put(Constants.CLIENT_ID,getResources().getString(R.string.instagram_client_id));

        // };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(verifyAccountRequest);

    }

    private void RequestUserData(final String accessToken,final String userId) {
        String url=Links.GET_INSTAGRAM_USER_DATA+userId+"?access_token="+accessToken+"&fields=username,id";
                //https://graph.instagram.com/17841400167659145?access_token=IGQVJWUGpQMjN5MFZApWTE4dmNhdm5PNFJaVHV4MjFoN2o3MmVPa2x3MXZASajlrZATU3UXJOVEEzMnY4VUwwSVhPdXk4VGJSb3VLc1c3UWtIYnpSZAHhFR24xaEM5bk5yUU41bHdLTTZAXSy1DdkR2U0lRaXN4VjF0SHg0OU5v&fields=username,id
        StringRequest verifyAccountRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(TransparentActivity.this, response, Toast.LENGTH_LONG).show();
                        JSONObject person = null;
                        try {
                            person = new JSONObject(response);
                            String userName = person.getString("username");
                            String id = person.getString("id");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("instagram_id", id);
                            verifyAccount(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TransparentActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {

            /*@Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.CLIENT_ID, getResources().getString(R.string.instagram_client_id));
                params.put(Constants.CODE, code);
                params.put(Constants.CLIENT_SECRET, getResources().getString(R.string.instagram_client_secret));
                params.put(Constants.GRANT_TYPE, getResources().getString(R.string.instagram_grant_type));
                params.put(Constants.REDIRECT_URL, getResources().getString(R.string.instagram_redirect_url));
                return params;
            }
*/
        };
        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(verifyAccountRequest);

    }

}
