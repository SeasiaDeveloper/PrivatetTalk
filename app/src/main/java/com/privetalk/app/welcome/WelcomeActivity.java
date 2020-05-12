package com.privetalk.app.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.media.MediaCas;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.internal.StatusCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.ETagDatasource;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.EtagObject;
import com.privetalk.app.mainflow.activities.CreateAccountActivity;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.BitmapUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.ImageHelper;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;
import com.privetalk.app.welcome.fragments.CreateAccountFragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<People.LoadPeopleResult> {

    private static final int RC_SIGN_IN = 69;
    private static final int REQUEST_CODE_RESOLVE_ERR = 70;
    private ViewPager viewPager;
    private ViewPagerAdapter mAdapter;
    private ImageView pageOne, pageTwo, pageThree, pageFour, pageFive, pageSix, pageSeven;
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;

    private View rootView;
    private CreateAccountDialog createAccountDialog;
    private SignInDialog signInDialog;

    //dialogs animations (in & out)
    private Animation in, out;

    //dialogs params
    private RelativeLayout.LayoutParams params;

    private GoogleApiClient mGoogleApiClient;

    private static final int FRAGMENT_WELCOME = 0;
    private static final int FRAGMENT_PRIVE_IS_DIFFERENT = 1;
    private static final int FRAGMENT_VERIFIED = 2;
    private static final int FRAGMENT_REWARDS = 3;
    private static final int FRAGMENT_SELF_EXPIRING = 4;
    private static final int FRAGMENT_NO_PHOTO = 5;
    private static final int FRAGMENT_CREATE_ACCOUNT = 6;
    private int previousPosition = 0;
    private JsonObjectRequest userLogIn;
    private JsonObjectRequest currentUserInfoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Configuration c = getResources().getConfiguration();
        if (c.fontScale > 1f)
            c.fontScale = 1f;

        rootView = findViewById(R.id.welcomeActivityRootView);

        googleApiStuff();

        facebookStuff();

        getViews();

        //if user already saw welcome screens
        if (getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PriveTalkConstants.KEY_WELCOME_SCREENS, false))
            viewPager.setCurrentItem(FRAGMENT_CREATE_ACCOUNT);


        /*private Session.StatusCallback callback =
                new com.facebook.Session.StatusCallback()
                {
                    @Override
                    public void call(com.facebook.Session session,
                                     SessionState state, Exception exception) {
                        onSessionStateChange(session, state, exception);
                    }
                };*/
    }

    @Override
    public void onBackPressed() {
        if (createAccountDialog != null && createAccountDialog.isVisible)
            createAccountDialog.dismiss();
        else if (signInDialog != null && signInDialog.isVisible)
            signInDialog.dismiss();
        else
            super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (userLogIn != null)
            userLogIn.cancel();
        super.onPause();
    }

    //login to vk
    private void vkStuff() {
        VKSdk.login(this, VKScopes.EMAIL);
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
        callbackManager = CallbackManager.Factory.create();
        // Set permissions
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile, email")); //"user_friends, public_profile, email"

        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                if (response.getError() != null) {
                                    PriveTalkUtilities.showSomethingWentWrongDialog(getApplicationContext());
                                    return;
                                }

                                CurrentUser currentUser = new CurrentUser(object);
                                Intent intent = new Intent(WelcomeActivity.this, CreateAccountActivity.class);
                                intent.putExtra(PriveTalkConstants.LOGIN_WITH_EMAIL, false);
                                Bundle bundle = new Bundle();
                                try {
                                    String email1 = response.getJSONObject().getString("email");
                                    String gender = response.getJSONObject().getString("gender");
                                    String birthday = response.getJSONObject().getString("birthday");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                bundle.putSerializable(PriveTalkConstants.BUNDLE_CURRENT_USER, currentUser);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender,birthday,location"); //age,dob,
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1f;
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

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
//
                                    final CurrentUser currentUser = new CurrentUser(obj, res.email);
                                    Intent intent = new Intent(WelcomeActivity.this, CreateAccountActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(PriveTalkConstants.BUNDLE_CURRENT_USER, currentUser);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                                super.attemptFailed(request, attemptNumber, totalAttempts);
                            }

                            @Override
                            public void onError(VKError error) {
                                super.onError(error);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(WelcomeActivity.this, "Authorization failed.\nPlease try again.", Toast.LENGTH_SHORT).show();
////                Log.d("VK Error", "error");
                // User didn't pass Authorization
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        //callback activity result for facebook api
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void getViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pageOne = (ImageView) findViewById(R.id.pageOne);
        pageTwo = (ImageView) findViewById(R.id.pageTwo);
        pageThree = (ImageView) findViewById(R.id.pageThree);
        pageFour = (ImageView) findViewById(R.id.pageFour);
        pageFive = (ImageView) findViewById(R.id.pageFive);
        pageSix = (ImageView) findViewById(R.id.pageSix);
        pageSeven = (ImageView) findViewById(R.id.pageSeven);
        in = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        out = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void logInFacebook() {
      facebookLoginButton.performClick();
    }

    //create a new account dialog
    public void showNewAccountDialog() {
        createAccountDialog = new CreateAccountDialog(this, (RelativeLayout) rootView, getLayoutInflater());
        createAccountDialog.show();
    }

    //create new sign in dialog
    public void showSignInDialog() {
        signInDialog = new SignInDialog(this, (RelativeLayout) rootView, getLayoutInflater());
        signInDialog.show();
    }


    //sign in user to application
    private void userSignIn() {

        Map<String, String> postParam = new HashMap<>();
        postParam.put("username", signInDialog.email.getText().toString());
        postParam.put("password", signInDialog.password.getText().toString());

        userLogIn = new JsonObjectRequest(Request.Method.POST,
                Links.LOGIN, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    private NetworkResponse networkResponse;

                    @Override
                    public void onResponse(JSONObject response) {

//                        System.out.println("Login Response: " + response.toString());

                        signInDialog.loginProgressBar.setVisibility(View.GONE);
//                        System.out.println("Sign in Response: " + response);
                        final String token = response.optString("token");
                        final String email = response.optString("mail");

                        if (token.isEmpty()) {
                            PriveTalkUtilities.showSomethingWentWrongDialog(getApplicationContext());
                            return;
                        }

                        currentUserInfoRequest = new JsonObjectRequest(Links.MY_PROFILE, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                if (networkResponse.statusCode == 304) {
                                    System.out.println("Status code: 304 IN SIGN IN");
                                } else {

//                                    System.out.println("Save the Current USer!!!!!!!!");
                                    CurrentUser currentUser = new CurrentUser(response, token, signInDialog.email.getText().toString());
                                    CurrentUserDatasource.getInstance(WelcomeActivity.this).saveCurrentUser(currentUser);
                                }


//                                System.out.println("Etag: " + networkResponse.headers.get("ETag"));
//                                System.out.println("Accept-Ranges: " + networkResponse.headers.get("Accept-Ranges"));
//                                System.out.println("Connection: " + networkResponse.headers.get("Connection"));
//                                System.out.println("Content-Length: " + networkResponse.headers.get("Content-Length"));
//                                System.out.println("Date: " + networkResponse.headers.get("Date"));
//                                System.out.println("Last-Modified: " + networkResponse.headers.get("Last-Modified"));
//                                System.out.println("Server: " + networkResponse.headers.get("Server"));


                                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error.networkResponse != null) {
                                    signInDialog.signInButton.setEnabled(true);

                                    try {
                                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));

                                        System.out.println("Error Message: " + new String(error.networkResponse.data, "UTF-8"));
                                        Toast.makeText(WelcomeActivity.this, R.string.permission_login, Toast.LENGTH_LONG).show();
                                        //Toast.makeText(WelcomeActivity.this, jsonObject.optString("detail"), Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(WelcomeActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(WelcomeActivity.this, getString(R.string.wrong_username_or_password), Toast.LENGTH_SHORT).show();
//
                                }
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Authorization", "Token " + token);
                                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
//                                if (ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()) != null) {
//                                    headers.put("If-None-Match", ETagDatasource.getInstance(PriveTalkApplication.getInstance()).getEΤagForLink(getUrl()).etag);
//                                }
//                                System.out.println("URL : " + getUrl());
                                return headers;
                            }

                            @Override
                            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                                networkResponse = response;
                                ETagDatasource.getInstance(PriveTalkApplication.getInstance()).saveETagForLink(new EtagObject(getUrl(), response));
                                return super.parseNetworkResponse(response);
                            }
                        };

                        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(currentUserInfoRequest);


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                signInDialog.loginProgressBar.setVisibility(View.GONE);
                signInDialog.signInButton.setEnabled(true);
                if (error.networkResponse != null) {

                    try {

                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        JSONArray jsonArray = jsonObject.optJSONArray("code");
                        if (jsonArray != null) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                            builder.setTitle(getString(R.string.your_account_deactivated));
                            builder.setMessage(getString(R.string.reactivate));
                            builder.setPositiveButton(getString(R.string.contact_us), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto", "support@privetalk.com", null));
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                    startActivity(Intent.createChooser(emailIntent, "Contact PriveTalk"));
                                }
                            });
                            builder.setNeutralButton(getString(R.string.cancel), null);
                            builder.create().show();
                            return;
                        }

                        System.out.println(new String(error.networkResponse.data, "UTF-8"));

                        Toast.makeText(WelcomeActivity.this, jsonObject.getJSONArray("non_field_errors").getString(0), Toast.LENGTH_SHORT).show();

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
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(userLogIn);

    }

    //set selected indicator
    private void setIndicator(int position) {
        //set previous page unselected icon
        switch (previousPosition) {
            case FRAGMENT_WELCOME:
                pageOne.setImageResource(R.drawable.welcome_pager_unselected);
                break;
            case FRAGMENT_PRIVE_IS_DIFFERENT:
                pageTwo.setImageResource(R.drawable.welcome_pager_unselected);
                break;
            case FRAGMENT_VERIFIED:
                pageThree.setImageResource(R.drawable.welcome_pager_unselected);
                break;
            case FRAGMENT_REWARDS:
                pageFour.setImageResource(R.drawable.welcome_pager_unselected);
                break;
            case FRAGMENT_SELF_EXPIRING:
                pageFive.setImageResource(R.drawable.welcome_pager_unselected);
                break;
            case FRAGMENT_NO_PHOTO:
                pageSix.setImageResource(R.drawable.welcome_pager_unselected);
                break;
            case FRAGMENT_CREATE_ACCOUNT:
                pageSeven.setImageResource(R.drawable.welcome_pager_unselected);
                break;
        }

        previousPosition = position;

        //set selected icon for selected view
        switch (position) {
            case FRAGMENT_WELCOME:
                pageOne.setImageResource(R.drawable.welcome_pager_selected);
                break;
            case FRAGMENT_PRIVE_IS_DIFFERENT:
                pageTwo.setImageResource(R.drawable.welcome_pager_selected);
                break;
            case FRAGMENT_VERIFIED:
                pageThree.setImageResource(R.drawable.welcome_pager_selected);
                break;
            case FRAGMENT_REWARDS:
                pageFour.setImageResource(R.drawable.welcome_pager_selected);
                break;
            case FRAGMENT_SELF_EXPIRING:
                pageFive.setImageResource(R.drawable.welcome_pager_selected);
                break;
            case FRAGMENT_NO_PHOTO:
                pageSix.setImageResource(R.drawable.welcome_pager_selected);
                break;
            case FRAGMENT_CREATE_ACCOUNT:
                pageSeven.setImageResource(R.drawable.welcome_pager_selected);
                break;
        }
    }


    /*
    GOOGLE API CALLBACKS:
    OnConnected
    onConnectionSuspended
    onConnectionFailed
    onResult
     */


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
            createAccountDialog.googleLoginButton.setEnabled(true);
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


    //get all user information available in google+
    private void getUserInfoGooglePlus() {

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

            Person currentPerson = Plus.PeopleApi
                    .getCurrentPerson(mGoogleApiClient);

            if (currentPerson == null) {
                PriveTalkUtilities.showSomethingWentWrongDialog(getApplicationContext());
                return;
            }

            try {
                CurrentUser currentUser = new CurrentUser(currentPerson, Plus.AccountApi.getAccountName(mGoogleApiClient));
                Intent intent = new Intent(WelcomeActivity.this, CreateAccountActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(PriveTalkConstants.BUNDLE_CURRENT_USER, currentUser);
                intent.putExtras(bundle);
                startActivity(intent);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                createAccountDialog.googleLoginButton.setEnabled(true);
            }
        }
    }


    /**
     * Simple view pager adapter
     */
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        // static final int NUM_OF_PAGES = 7;
        static final int NUM_OF_PAGES = 1;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:
                    return new CreateAccountFragment();
                /*case FRAGMENT_WELCOME:
                    return new WelcomeFragment();
                case FRAGMENT_PRIVE_IS_DIFFERENT:
                    return new PriveIsDefferentFragment();
                case FRAGMENT_CREATE_ACCOUNT:
                    return new CreateAccountFragment();
                case FRAGMENT_VERIFIED:
                    return new VerifiedProfilesFragment();
                case FRAGMENT_REWARDS:
                    return new WeeklyRewardsFragment();
                case FRAGMENT_SELF_EXPIRING:
                    return new SelfExpiringMessagesFragment();
                case FRAGMENT_NO_PHOTO:
                    return new NoPhotoFragment();*/
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }


        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }

    private class SignInDialog {

        private RelativeLayout rootView;
        private View dialogView;
        private View dismissDialog, signInButton;
        private ImageView imageView;
        private boolean isVisible = false;

        //public views accessed by parent activity
        private ProgressBar loginProgressBar;
        private PriveTalkEditText email, password;
        private View forgotPasswordView;


        public SignInDialog(final Activity activity, RelativeLayout rootView, LayoutInflater inflater) {
            this.rootView = rootView;

            dialogView = inflater.inflate(R.layout.dialog_sign_in, null);

            //background imageview with blured screenshot
            imageView = new ImageView(activity);
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(ImageHelper.blurBitmap(activity, BitmapUtilities.getBitmapFromView(rootView), 20.f));

            forgotPasswordView = dialogView.findViewById(R.id.forgotPassword);
            forgotPasswordView.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Intent intent = new Intent(WelcomeActivity.this, ForgotPasswordActivity.class);
                    activity.startActivity(intent);
                }
            });

            //all dialog views
            dismissDialog = dialogView.findViewById(R.id.closeDialog);
            signInButton = dialogView.findViewById(R.id.dialgogSignInButton);
            loginProgressBar = (ProgressBar) dialogView.findViewById(R.id.loginProgressBar);
            email = (PriveTalkEditText) dialogView.findViewById(R.id.signInEmailEditText);
            password = (PriveTalkEditText) dialogView.findViewById(R.id.password);

            signInButton.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    signInButton.setEnabled(false);
                    loginProgressBar.setVisibility(View.VISIBLE);
                    userSignIn();
                }
            });


            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String stringie = s.toString().replace(" ", "");
                    if (!stringie.equals(s.toString())) {
                        password.setText(stringie);
                        password.setSelection(stringie.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String stringie = s.toString().replace(" ", "");
                    if (!stringie.equals(s.toString())) {
                        email.setText(stringie);
                        email.setSelection(stringie.length());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    dismiss();
                }
            });

            dialogView.findViewById(R.id.outsideArea).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        }

        public void show() {
            isVisible = true;
            dialogView.setLayoutParams(params);

            rootView.addView(imageView);
            rootView.addView(dialogView);

            dialogView.startAnimation(in);

        }

        public void dismiss() {
            KeyboardUtilities.closeKeyboard(WelcomeActivity.this, rootView);
            isVisible = false;
            dialogView.startAnimation(out);

            out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rootView.removeView(dialogView);
                    rootView.removeView(imageView);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }


    private class CreateAccountDialog {

        private RelativeLayout rootView;
        private View dialogView;
        private View dismissDialog;
        private ImageView imageView;
        private boolean isVisible = false;

        //dialog views
        private View googleLoginButton;
        private View fbLoginButton;
        private View vkLoginButton;
        private View signUpWithMail;
        private View btnFbLogin;

        public CreateAccountDialog(final Activity activity, RelativeLayout rootView, LayoutInflater inflater) {
            this.rootView = rootView;

            dialogView = inflater.inflate(R.layout.dialog_create_account, null);

            //get views
            googleLoginButton = dialogView.findViewById(R.id.googleLogin);
            fbLoginButton = dialogView.findViewById(R.id.facebookLogin);
            btnFbLogin = dialogView.findViewById(R.id.btnFbLogin);

            vkLoginButton = dialogView.findViewById(R.id.vkIcon);
            signUpWithMail = dialogView.findViewById(R.id.createAccountEmailContainer);
            dismissDialog = dialogView.findViewById(R.id.closeDialog);

            //background imageview with blur screenshot
            imageView = new ImageView(activity);
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(ImageHelper.blurBitmap(activity, BitmapUtilities.getBitmapFromView(rootView), 20.f));

            signUpWithMail.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Intent intent = new Intent(activity, CreateAccountActivity.class);
                    intent.putExtra(PriveTalkConstants.LOGIN_WITH_EMAIL, true);
                    startActivity(intent);
                }
            });


            dismissDialog.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    dismiss();
                }
            });

            fbLoginButton.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    facebookLoginButton.performClick();
                }
            });

            btnFbLogin.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    facebookLoginButton.performClick();
                }
            });

            googleLoginButton.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    googleLoginButton.setEnabled(false);
                    if (mGoogleApiClient.isConnected()) {
                        getUserInfoGooglePlus();
                    } else {
                        mGoogleApiClient.connect();
                    }
                }
            });

            vkLoginButton.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    vkStuff();
                }
            });

            dialogView.findViewById(R.id.outsideArea).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        }

        public void show() {
            isVisible = true;
            dialogView.setLayoutParams(params);
            rootView.addView(imageView);
            rootView.addView(dialogView);
            dialogView.startAnimation(in);
            in.setAnimationListener(null);
        }

        public void dismiss() {
            isVisible = false;
            dialogView.startAnimation(out);

            out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rootView.removeView(dialogView);
                    rootView.removeView(imageView);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

}
