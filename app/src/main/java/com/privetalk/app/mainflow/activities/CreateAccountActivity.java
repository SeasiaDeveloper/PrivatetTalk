package com.privetalk.app.mainflow.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;

import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.PriveTalkMapFragment;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.utilities.DatePickerFragment;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.LocationUtilities;
import com.privetalk.app.utilities.PasswordStrengthIndicator;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.PriveTalkRadioButton;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.privetalk.app.R.string.error;


/**
 * Created by zachariashad on 28/12/15.
 */
public class CreateAccountActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int GET_VERIFICATION_CODE = 0;
    private static final int ENTER_VERIFICATION_CODE = 1;
    private static final int BUTTON = 0;
    private static final int PROGRESSBAR = 1;

    public static final String DATE_RECEIVER_TAG = "date-receiver";
    private PriveTalkTextView myBirtday, myGenre, lookingFor, getCode, goBack, startDating;
    private PriveTalkEditText myName, myLocation, password, myEmail, confirmPass, verificationCode;
    private ViewSwitcher viewSwitcher;
    private ViewSwitcher startDatingSwitcher;
    private ViewSwitcher gpsButtonContainer;
    private CurrentUser currentUser;
    private ImageView requestAnotherCode;
    private ProgressBar progressBar;
    private ImageView passStrength, myNameTick, myBirthdayTick,
            myLocationTick, /*myGenreMale, myGenreFemale*/myGenreTick,
            lookingGenreMale, lookingGenreFemale, lookingTick,
            myEmailTick, passwordTick, confirmTick;
    private PriveTalkRadioButton acceptTermsRadioButton;
    private View gpsButton;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Location mLastLocation;
    private JsonObjectRequest getVerificationCode;
    private JsonObjectRequest registerUserRequest;
    private TextView terms, privacyPolicy, safetyGuide;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private AsyncTask<Void, Void, String> getLocationAdditionalInfo;
    private android.os.Handler mHandler;
    private boolean isPaused = true;
    private boolean loginWithEmail = false;
    private AlertDialog countryCodeDialog;
    private Spinner genderSpinner,lookingForSpinner;

    private RelativeLayout verificationCodeRelative;

    private boolean registerUserEnabled = false;

    /*
    BROADCAST RECEIVER, called after user chooses birthday
     */
    private BroadcastReceiver onDateSelected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int extras[] = intent.getExtras().getIntArray("extras");
            final Calendar c = Calendar.getInstance();
            if (extras != null) {
                c.set(extras[DatePickerFragment.YEAR],
                        extras[DatePickerFragment.MONTH], extras[DatePickerFragment.DAY]);
            }

            Date date = c.getTime();

            if (((System.currentTimeMillis() - date.getTime()) / 1000) < PriveTalkConstants.EIGTHEEN_YEARS_SECONDS) {
                Toast.makeText(CreateAccountActivity.this, R.string.you_must_be_eighteen, Toast.LENGTH_SHORT).show();
            } else {
                if (extras != null) {
                    myBirtday.setText(String.valueOf(extras[2])
                            + "/" + String.valueOf(extras[1] + 1) + "/" + String.valueOf(extras[0]));
                }
                myBirthdayTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this,
                        R.color.verification_green), PorterDuff.Mode.SRC_IN);
                myBirthdayTick.setTag(true);
            }
            checkifAllSet();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mHandler = new android.os.Handler();
        verificationCodeRelative = (RelativeLayout) findViewById(R.id.verificationCodeRelative);
        loginWithEmail = getIntent().getBooleanExtra(PriveTalkConstants.LOGIN_WITH_EMAIL, false);
        toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setElevation(8);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (savedInstanceState != null) {
            currentUser = (CurrentUser) savedInstanceState.getSerializable(PriveTalkConstants.BUNDLE_CURRENT_USER);
        } else {
            currentUser = (CurrentUser) getIntent().getSerializableExtra(PriveTalkConstants.BUNDLE_CURRENT_USER);
        }
        if (currentUser == null) currentUser = new CurrentUser();
        if (currentUser.email == null || currentUser.email.isEmpty()) {
            loginWithEmail = true;
        }

        //gender spinner
        genderSpinner = findViewById(R.id.genderSpinner);
        lookingForSpinner=findViewById(R.id.genderLookingFor);
        String[] items = new String[]{"Please select", "Male", "Female"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_text, items);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        genderSpinner.setAdapter(adapter);

        String[] lookingForItems = new String[]{"Please select", "Male", "Female","Both"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this,  R.layout.spinner_text, lookingForItems);
        adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        lookingForSpinner.setAdapter(adapter2);

        googleApiStuff();
        initViews();
        myBirtday.setText("28/08/1992");
        myBirthdayTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this,
                R.color.verification_green), PorterDuff.Mode.SRC_IN);
        myBirthdayTick.setTag(true);
        //get location
        getLocation();
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getAdditionalInfo();
        }
        toolbar.setVisibility(View.GONE);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(onDateSelected, new IntentFilter(DATE_RECEIVER_TAG));
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1f;
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    @Override
    protected void onPause() {
        if (registerUserRequest != null) registerUserRequest.cancel();
        if (getVerificationCode != null) getVerificationCode.cancel();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onDateSelected);
        isPaused = true;
        super.onPause();
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!network_enabled && !gps_enabled) {
            buildAlertMessageNoGps();
        } else {
            // gpsButtonContainer.setDisplayedChild(PROGRESSBAR);
            if (mGoogleApiClient.isConnected()) getLocationName();
            else mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        if (getLocationAdditionalInfo != null) getLocationAdditionalInfo.cancel(true);
        super.onStop();
    }


    //implemented methods for location services
    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        getLocationName();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //initialize google location api
    private void googleApiStuff() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    private void initViews() {
        //gpsButtonContainer = (ViewSwitcher) findViewById(R.id.gpsButtonContainer);
        terms = (TextView) findViewById(R.id.termsCreateAccount);
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        myName = (PriveTalkEditText) findViewById(R.id.myName);
        myBirtday = (PriveTalkTextView) findViewById(R.id.myBirthday);
        myLocation = (PriveTalkEditText) findViewById(R.id.myLocation);
        //myGenre = (PriveTalkTextView) findViewById(R.id.myGenre);
       // lookingFor = (PriveTalkTextView) findViewById(R.id.lookingFor);
        password = (PriveTalkEditText) findViewById(R.id.password);
        passStrength = (ImageView) findViewById(R.id.passwordStrength);
        // gpsButton = gpsButtonContainer.getChildAt(BUTTON);
        //myGenreMale = (ImageView) findViewById(R.id.myGenreMale);
        //myGenreFemale = (ImageView) findViewById(R.id.myGenreFemale);
        myEmail = (PriveTalkEditText) findViewById(R.id.myEmail);
        confirmPass = (PriveTalkEditText) findViewById(R.id.confirmPass);
        //lookingGenreMale = (ImageView) findViewById(R.id.lookingGenreMale);
        //lookingGenreFemale = (ImageView) findViewById(R.id.lookingGenreFemale);
        goBack = (PriveTalkTextView) findViewById(R.id.goBack);
        startDatingSwitcher = (ViewSwitcher) findViewById(R.id.startDatingSwitcher);
        startDating = (PriveTalkTextView) startDatingSwitcher.getChildAt(BUTTON);
        myNameTick = (ImageView) findViewById(R.id.myNameTick);
        myBirthdayTick = (ImageView) findViewById(R.id.myBirthdayTick);
        myLocationTick = (ImageView) findViewById(R.id.myLocationTick);
        myGenreTick = (ImageView) findViewById(R.id.myGenreTick);
        lookingTick = (ImageView) findViewById(R.id.lookingTick);
        myEmailTick = (ImageView) findViewById(R.id.myEmailTick);
        passwordTick = (ImageView) findViewById(R.id.passwordTick);
        confirmTick = (ImageView) findViewById(R.id.confirmTick);
        acceptTermsRadioButton = (PriveTalkRadioButton) findViewById(R.id.acceptTerms);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.myViewSwitcher);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        getCode = (PriveTalkTextView) viewSwitcher.getCurrentView();
        requestAnotherCode = (ImageView) findViewById(R.id.refreshButton);
        verificationCode = (PriveTalkEditText) viewSwitcher.getChildAt(ENTER_VERIFICATION_CODE);

        //set tags
        //myGenreMale.setTag(false); //commented code
        //myGenreFemale.setTag(false);
        genderSpinner.setTag(false);
        lookingForSpinner.setTag(false);
        //lookingGenreFemale.setTag(false);
        //lookingGenreMale.setTag(false);
        myNameTick.setTag(false);
        myBirthdayTick.setTag(false);
        myLocationTick.setTag(false);
        myGenreTick.setTag(false);
        lookingTick.setTag(false);
        myEmailTick.setTag(false);
        passwordTick.setTag(false);
        confirmTick.setTag(false);
        requestAnotherCode.setTag(false);

        if (!loginWithEmail) {
            myEmail.setEnabled(false);
            myEmail.requestFocus();
            verificationCodeRelative.setVisibility(View.GONE);
        }

        if (loginWithEmail) {
            viewSwitcher.setVisibility(View.VISIBLE);
        } else {
            viewSwitcher.setVisibility(View.GONE);
        }

        goBack.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                onBackPressed();
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

                    if (stringie.length() >= 6) {
                        passwordTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                        passwordTick.setTag(true);
                    } else {
                        passwordTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                        passwordTick.setTag(false);
                    }
                } else {
                    if (s.length() >= 6) {
                        passwordTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                        passwordTick.setTag(true);
                    } else {
                        passwordTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                        passwordTick.setTag(false);
                    }

                }
                passwordMatching();
                checkifAllSet();

                new PasswordStrengthIndicator(password.getText().toString()) {
                    @Override
                    public void passwordStrength(int strength, String message) {
                        PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(strength * 75, ViewGroup.LayoutParams.MATCH_PARENT);
                        passStrength.setLayoutParams(params);
                    }

                };
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String stringie = s.toString().replace(" ", "");
                if (!stringie.equals(s.toString())) {
                    confirmPass.setText(stringie);
                    confirmPass.setSelection(stringie.length());
                }

                passwordMatching();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        myName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    myNameTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                    myNameTick.setTag(true);
                } else {
                    myNameTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                    myNameTick.setTag(false);
                }
                checkifAllSet();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       /* myBirtday.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "start_date_picker");
            }
        });*/


        myEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String stringie = s.toString().replace(" ", "");
                if (!stringie.equals(s.toString())) {
                    myEmail.setText(stringie);
                    myEmail.setSelection(stringie.length());

                    if (isEmailValid(stringie)) {
                        myEmailTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                        myEmailTick.setTag(true);
                    } else {
                        myEmailTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                        myEmailTick.setTag(false);
                    }
                } else {

                    if (isEmailValid(s)) {
                        myEmailTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                        myEmailTick.setTag(true);
                    } else {
                        myEmailTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                        myEmailTick.setTag(false);
                    }
                }
                checkifAllSet();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        myLocation.setFocusable(false);
        myLocation.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.mapfragmentPlaceholder, new PriveTalkMapFragment());
                trans.addToBackStack(null);
                trans.commit();
                toolbar.setVisibility(View.VISIBLE);
                KeyboardUtilities.closeKeyboard(CreateAccountActivity.this, ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
            }
        });

        myLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    myLocationTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                    myLocationTick.setTag(true);
                } else {
                    myLocationTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                    myLocationTick.setTag(false);
                }

                checkifAllSet();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        verificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startDating.setEnabled(true);
                if (allSet())
                    startDating.getBackground().setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                checkifAllSet();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       /* gpsButton.setOnTouchListener(new FadeOnTouchListener() {
                                         @Override
                                         public void onClick(View view, MotionEvent event) {
                                             locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                             boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                             boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                                             if (!network_enabled && !gps_enabled) {
                                                 buildAlertMessageNoGps();
                                             } else {
                                                 // gpsButtonContainer.setDisplayedChild(PROGRESSBAR);
                                                 if (mGoogleApiClient.isConnected()) getLocationName();
                                                 else mGoogleApiClient.connect();
                                             }
                                         }
                                     }
        );*/

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //if (!(boolean) genderSpinner.getTag()) {
                if (position == 0) {
                    genderSpinner.setTag(false);
                    myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                    myGenreTick.setTag(false);
                } else {
                    genderSpinner.setTag(true);
                    myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                    myGenreTick.setTag(true);
                }
                //}
                checkifAllSet();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lookingForSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    lookingForSpinner.setTag(false);
                    lookingTick.setTag(false);
                    lookingTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
                } else {
                    lookingForSpinner.setTag(true);
                    lookingTick.setTag(true);
                    lookingTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                }
                checkifAllSet();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //commented code
       /* myGenreMale.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (!(boolean) myGenreMale.getTag()) {
                    myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                    myGenreTick.setTag(true);
                    myGenreMale.setTag(true);
                    myGenreFemale.setTag(false);
                    myGenreMale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.male_icon_selected));
                    myGenreFemale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.female_icon));
                }

                checkifAllSet();
            }
        });

        myGenreFemale.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (!(boolean) myGenreFemale.getTag()) {
                    myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                    myGenreTick.setTag(true);
                    myGenreFemale.setTag(true);
                    myGenreMale.setTag(false);
                    myGenreMale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.male_icon));
                    myGenreFemale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.female_icon_selected));
                }

                checkifAllSet();
            }
        });*/

/*        lookingGenreMale.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (!(boolean) lookingGenreMale.getTag()) {
                    lookingGenreMale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.male_icon_selected));
                    lookingGenreMale.setTag(true);
                } else {
                    lookingGenreMale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.male_icon));
                    lookingGenreMale.setTag(false);
                }

                lookingTick.setTag((boolean) lookingGenreMale.getTag() || (boolean) lookingGenreFemale.getTag());
                lookingTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, (boolean) lookingGenreMale.getTag() || (boolean) lookingGenreFemale.getTag() ?
                        R.color.verification_green : R.color.tick_grey), PorterDuff.Mode.SRC_IN);


                checkifAllSet();
            }
        });

        lookingGenreFemale.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (!(boolean) lookingGenreFemale.getTag()) {
                    lookingGenreFemale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.female_icon_selected));
                    lookingGenreFemale.setTag(true);
                } else {
                    lookingGenreFemale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.female_icon));
                    lookingGenreFemale.setTag(false);
                }

                lookingTick.setTag((boolean) lookingGenreMale.getTag() || (boolean) lookingGenreFemale.getTag());
                lookingTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, (boolean) lookingGenreMale.getTag() || (boolean) lookingGenreFemale.getTag() ?
                        R.color.verification_green : R.color.tick_grey), PorterDuff.Mode.SRC_IN);

                checkifAllSet();
            }
        });*/

        getCode.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                requestVerificationCode();
            }
        });

        requestAnotherCode.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                requestAnotherCode.setVisibility(View.INVISIBLE);
                requestVerificationCode();
            }
        });


       /* ((RadioGroup) acceptTermsRadioButton.getParent()).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkifAllSet();
            }
        });*/

        startDating.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                registerUserEnabled = true;

                if (loginWithEmail) {

                    if (/*(acceptTermsRadioButton.isChecked() &&*/ allSet() && !verificationCode.getText().toString().isEmpty()) {

                        if (LocationUtilities.getCountryCode() == null) {
                            showCountryCodeDialog();
                        } else {
                            registerUser();
                        }
                    }

                } else if (/*acceptTermsRadioButton.isChecked() &&*/ allSet()) {

                    if (LocationUtilities.getCountryCode() == null) {
                        showCountryCodeDialog();
                    } else {
                        registerUser();
                    }


                } else
                    new AlertDialog.Builder(CreateAccountActivity.this)
                            .setTitle(getString(error))
                            .setMessage(getString(R.string.must_accept_terms_and_contitions))
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
            }
        });

        //check if there are any stored user info in case user signed in with GPLUS, FB OR VK.
        if (currentUser.name != null) {

            myEmail.setText(currentUser.email != null ? currentUser.email : "");
            myName.setText(currentUser.name != null ? currentUser.name : "");
            myLocation.setText(currentUser.location != null ? currentUser.location : "");


            if (currentUser.birthday != 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                myBirtday.setText(dateFormat.format(currentUser.birthday));
                myBirthdayTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                myBirthdayTick.setTag(true);
            }

            if (Integer.valueOf(currentUser.gender.value) == CurrentUser.MALE) { //commented code
                genderSpinner.setSelection(1);
                genderSpinner.setTag(true);
                myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                myGenreTick.setTag(true);
                /*if (!(boolean) myGenreMale.getTag()) {
                    myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                    myGenreTick.setTag(true);
                    myGenreMale.setTag(true);
                    myGenreFemale.setTag(false);
                    myGenreMale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.male_icon_selected));
                    myGenreFemale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.female_icon));
                }*/
            } else if (Integer.valueOf(currentUser.gender.value) == CurrentUser.FEMALE) {
                genderSpinner.setSelection(2);
                genderSpinner.setTag(true);
                myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                myGenreTick.setTag(true);
                /*if (!(boolean) myGenreFemale.getTag()) {
                    myGenreTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
                    myGenreTick.setTag(true);
                    myGenreFemale.setTag(true);
                    myGenreMale.setTag(false);
                    myGenreMale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.male_icon));
                    myGenreFemale.setImageDrawable(ContextCompat.getDrawable(CreateAccountActivity.this, R.drawable.female_icon_selected));
                }*/
            }
        }

        checkifAllSet();
    }

    private void showCountryCodeDialog() {


        AlertDialog.Builder countryCodeBuilder = new AlertDialog.Builder(CreateAccountActivity.this);
        countryCodeBuilder.setMessage(getString(R.string.country_code_error));
        countryCodeBuilder.setNegativeButton(R.string.reposition, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                dialog.dismiss();
            }
        });

        countryCodeBuilder.setPositiveButton(getString(R.string.try_again_country_code), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                setCountryCodeAlertDialogRefreshing();

                getAdditionalInfo();

                dialog.dismiss();
            }
        });
        AlertDialog dialog = countryCodeBuilder.create();
        dialog.show();

    }

    private void setCountryCodeAlertDialogRefreshing() {


        countryCodeDialog = new AlertDialog.Builder(this)
                .setView(R.layout.layout_progress_country_code)
                .setCancelable(false)
                .create();

        countryCodeDialog.show();

    }


    //chaeck if all fields are filled and enabled/disable verification code button
    private void checkifAllSet() {
        if (allSet()) {
            getCode.setBackgroundColor(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green));
            getCode.setEnabled(true);

            if (loginWithEmail)
                startDating.getBackground()
                        .setColorFilter(ContextCompat.getColor(CreateAccountActivity.this,
                                /*acceptTermsRadioButton.isChecked() &&*/ !verificationCode.getText().toString().isEmpty() ?
                                        R.color.tick_green : R.color.disable_button_gray), PorterDuff.Mode.SRC_IN);
            else
                startDating.getBackground()
                        .setColorFilter(ContextCompat.getColor(CreateAccountActivity.this,
                             /*   acceptTermsRadioButton.isChecked() ?*/
                                        R.color.tick_green /*: R.color.disable_button_gray*/), PorterDuff.Mode.SRC_IN);

        } else {
            if (viewSwitcher.getDisplayedChild() != GET_VERIFICATION_CODE)
                viewSwitcher.setDisplayedChild(GET_VERIFICATION_CODE);
            getCode.setBackgroundColor(ContextCompat.getColor(CreateAccountActivity.this, R.color.disable_button_gray));
            startDating.getBackground().setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.disable_button_gray), PorterDuff.Mode.SRC_IN);
            getCode.setEnabled(false);
        }

    }

    //method to check if passwords matches
    private void passwordMatching() {
        if (confirmPass.getText().toString().length() >= 6 && confirmPass.getText().toString().equals(password.getText().toString())) {
            confirmTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.verification_green), PorterDuff.Mode.SRC_IN);
            confirmTick.setTag(true);
        } else {
            confirmTick.setColorFilter(ContextCompat.getColor(CreateAccountActivity.this, R.color.tick_grey), PorterDuff.Mode.SRC_IN);
            confirmTick.setTag(false);
        }
        checkifAllSet();
    }

    //checks if the given email is a valid mail (*@*.*)
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    //get location name by coordinates
    private void getLocationName() {

        if (mLastLocation != null) {
            new AsyncTask<Void, String, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    double longitude = mLastLocation.getLongitude();
                    double latitude = mLastLocation.getLatitude();
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
                    String locationToPrint = "";

                    try {
                        LocationUtilities.saveLocation((float) latitude, (float) longitude);
                        List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 10);
                        if (null != listAddresses && listAddresses.size() > 0) {
                            LocationUtilities.setPostalCode(listAddresses.get(0).getPostalCode());
                            LocationUtilities.setCountryCode(listAddresses.get(0).getCountryCode());
                            LocationUtilities.setAdminArea(listAddresses.get(0).getAdminArea() == null ? listAddresses.get(0).getLocality() : listAddresses.get(0).getAdminArea());
                            //return listAddresses.get(0).getAddressLine(1);

                            for (Address address : listAddresses) {

                                String adminArea = address.getAdminArea();
                                String countryName = address.getCountryName();
                                String cityNameForUSA = address.getLocality();

                                if (countryName.equals("United States") || address.getCountryCode().equals("US")) {
                                    Log.d("USA", "USA");

                                    if (cityNameForUSA != null) {
                                        return cityNameForUSA;
                                    }
                                }

                                if (adminArea != null) {
                                    return adminArea;
                                } else if (countryName != null) {
                                    if (countryName.equals("United States") || address.getCountryCode().equals("US"))
                                        locationToPrint = cityNameForUSA;
                                    else
                                        locationToPrint = countryName;
                                }
                            }
                            return locationToPrint;
                        }
                    } catch (IOException e) {
//                        getLocationName();
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
//                    if (myLocation == null)
//                        return;
                    //gpsButtonContainer.setDisplayedChild(BUTTON);
                    myLocation.setText(s);
                }
            }.execute();
        } else {
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
//            gpsButtonContainer.setDisplayedChild(BUTTON);
//            Toast.makeText(CreateAccountActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }


    //a dialog informing user that gps is not enabled and drives user to gps/location settings
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.enable_gps_msg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void registerUser() {

        startDatingSwitcher.setDisplayedChild(PROGRESSBAR);

        //get all user info
        currentUser.email = myEmail.getText().toString();
        currentUser.name = myName.getText().toString();
        currentUser.location = myLocation.getText().toString();

        //commented code
        /*currentUser.gender.value = ((boolean) myGenreMale.getTag() && (boolean) myGenreFemale.getTag()) ? "0" :
                ((boolean) myGenreMale.getTag()) ? String.valueOf(CurrentUser.MALE) : String.valueOf(CurrentUser.FEMALE);*/

       /* currentUser.lookingFor = ((boolean) lookingGenreFemale.getTag() && (boolean) lookingGenreMale.getTag()) ? 0 : ((boolean) lookingGenreMale.getTag()) ? 1 : 2;*/

        if ((boolean) genderSpinner.getTag()) {
            if (genderSpinner.getSelectedItemPosition() == 1) {
                currentUser.gender.value = String.valueOf(CurrentUser.MALE);
            } else if (genderSpinner.getSelectedItemPosition() == 2) {
                currentUser.gender.value = String.valueOf(CurrentUser.FEMALE);
            }
        }

        if ((boolean) lookingForSpinner.getTag()) {
            if (lookingForSpinner.getSelectedItemPosition() == 1) {
                currentUser.lookingFor = CurrentUser.MALE;
            } else if (lookingForSpinner.getSelectedItemPosition() == 2) {
                currentUser.lookingFor = CurrentUser.FEMALE;
            } else if (lookingForSpinner.getSelectedItemPosition() == 3) {
                currentUser.lookingFor = CurrentUser.BOTH;
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        try {
            currentUser.birthday = dateFormat.parse(myBirtday.getText().toString()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int genderValue = 0;
        if (genderSpinner.getSelectedItemPosition() == 1) {
            genderValue = 1;
        } else if (genderSpinner.getSelectedItemPosition() == 2) {
            genderValue = 2;
        }

        int lookingForValue = 0;
        if (lookingForSpinner.getSelectedItemPosition() == 1) {
            lookingForValue = 1;
        } else if (lookingForSpinner.getSelectedItemPosition() == 2) {
            lookingForValue = 2;
        } else if (lookingForSpinner.getSelectedItemPosition() == 3) {
            lookingForValue = 0;
        }

        Map<String, Object> postParam = new HashMap<>();
        postParam.put("email", currentUser.email);
        //postParam.put("password","Abc@123"); //password.getText().toString()
        postParam.put("name", currentUser.name);
        postParam.put("looking_for",lookingForValue);//((boolean) lookingGenreFemale.getTag() && (boolean) lookingGenreMale.getTag()) ? 0 : ((boolean) lookingGenreMale.getTag()) ? 1 : 2);
        //  postParam.put("gender", ((boolean) myGenreMale.getTag()) ? 1 : 2);//commented code
        postParam.put("gender", genderValue);
        postParam.put("birthday", myBirtday.getText().toString());
        postParam.put("location", currentUser.location);

        Log.d(currentUser.location, "register user with location");
        //System.out.println("current user location : " + currentUser.location);

        if (currentUser.fb_id != null && currentUser.fb_id.length() > 0) {
            postParam.put("fb_id", currentUser.fb_id);
        }

        if (currentUser.fb_username != null && currentUser.fb_username.length() > 0) {
            postParam.put("fb_username", currentUser.fb_username);
        }

        if (currentUser.gplus_id != null && currentUser.gplus_id.length() > 0) {
            postParam.put("gplus_id", currentUser.gplus_id);
        }

        if (currentUser.vk_id != null && currentUser.vk_id.length() > 0) {
            postParam.put("vk_id", currentUser.vk_id);
        }

        if (loginWithEmail) {
            postParam.put("verification_code", verificationCode.getText().toString());
            postParam.put("skip_verification", false);
        } else {
            postParam.put("skip_verification", true);
        }

        System.out.println("Registration Params: " + (new JSONObject(postParam)).toString());

        registerUserRequest = new JsonObjectRequest(Request.Method.POST,
                Links.REGISTER, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        startDatingSwitcher.setDisplayedChild(BUTTON);
                        try {

                            currentUser.token = response.getString("token");
                            CurrentUserDatasource.getInstance(CreateAccountActivity.this).saveCurrentUser(currentUser);
                            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            PriveTalkUtilities.getUserInfoFromServer();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                error.getMessage();
                startDatingSwitcher.setDisplayedChild(BUTTON);


                if (error.networkResponse != null) {


                    if (error.networkResponse.statusCode == 400) {
                        try {

                            JSONObject object = new JSONObject(new String(error.networkResponse.data));

                            Toast.makeText(CreateAccountActivity.this, object.optString("detail"), Toast.LENGTH_SHORT).show();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(CreateAccountActivity.this, R.string.verification_code_wrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else
                    Toast.makeText(CreateAccountActivity.this, R.string.something_went_wrong_with_ver_code, Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 400)
                    Toast.makeText(CreateAccountActivity.this, R.string.verification_code_wrong, Toast.LENGTH_SHORT).show();

                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(registerUserRequest);

    }

    private void requestVerificationCode() {

        requestAnotherCode.setVisibility(View.INVISIBLE);
        getCode.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        Map<String, String> postParam = new HashMap<>();
        postParam.put("email", myEmail.getText().toString());
        Log.d("testEmail", " email : " + myEmail.getText().toString());

        getVerificationCode = new JsonObjectRequest(Request.Method.POST, Links.VERIFY,
                new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                if (PriveTalkConstants.LOG_DEBUG) {
                    Log.d("testEmail", " response : " + response.toString());
                }

                if (viewSwitcher.getDisplayedChild() != ENTER_VERIFICATION_CODE)
                    viewSwitcher.showNext();

                progressBar.setVisibility(View.INVISIBLE);
                requestAnotherCode.setVisibility(View.VISIBLE);
                requestAnotherCode.setTag(true);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (PriveTalkConstants.LOG_DEBUG) {
                    Log.d("testEmail", " error : " + error.toString());
                }

                progressBar.setVisibility(View.INVISIBLE);
                if ((boolean) requestAnotherCode.getTag()) {
                    requestAnotherCode.setVisibility(View.VISIBLE);
                }
                getCode.setEnabled(true);
                if (error.networkResponse != null) {
                    try {
                        JSONObject object = new JSONObject(new String(error.networkResponse.data));
                        Toast.makeText(CreateAccountActivity.this, object.getJSONArray("email").getString(0), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(CreateAccountActivity.this, getString(R.string.problem_verifying_email), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));
                Log.d("testEmail", " headers : " + headers.toString());
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

                if (response.statusCode == 204) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.INVISIBLE);
                            requestAnotherCode.setVisibility(View.VISIBLE);
                            requestAnotherCode.setTag(true);
                            getCode.setVisibility(View.INVISIBLE);

                            new AlertDialog.Builder(CreateAccountActivity.this)
                                    .setCancelable(false)
                                    .setMessage(getString(R.string.already_have_code))
                                    .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }
                    });

                } else if (response.statusCode == 400) {
                    showAlertDialog(getString(R.string.problem_verifying_email));
                } else if (response.statusCode == 200) {
                    try {
                        JSONObject data = new JSONObject(new String(response.data));
                        long expires = PriveTalkConstants.conversationDateFormat.parse(data.optString("expires")).getTime()
                                - (System.currentTimeMillis() - PriveTalkUtilities.getGlobalTimeDifference());
                        expires = expires / 60000;
                        showAlertDialog(getString(R.string.verification_code_already_Send)
                                + String.valueOf(expires <= 0 ? 1 : expires)
                                + getString(R.string.try_again));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (response.statusCode == 201) {
                    showAlertDialog(getString(R.string.email_send));
                }

                return super.parseNetworkResponse(response);
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getVerificationCode);

    }


    private void showAlertDialog(final String message) {

        if (isPaused || message == null)
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new AlertDialog.Builder(CreateAccountActivity.this)
                        .setCancelable(false)
                        .setMessage(message)
                        .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    private void getAdditionalInfo() {

        getLocationAdditionalInfo = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPostExecute(String string) {
                if (string == null)
                    string = getString(R.string.set_by_user);

                if (PriveTalkConstants.LOG_DEBUG)
                    Log.d("countryCodeTest", "on post execute");

                myLocation.setText(string);

                if (countryCodeDialog != null)
                    countryCodeDialog.dismiss();

                if (LocationUtilities.getCountryCode() == null)
                    showCountryCodeDialog();
                else if (registerUserEnabled) {
                    Log.d("countryCodeTest", LocationUtilities.getCountryCode());
                    registerUser();
                }


            }

            @Override
            protected String doInBackground(Void... params) {
                float lat = LocationUtilities.getSelectedLatitude();
                float lng = LocationUtilities.getSelectedLongitude();

                LocationUtilities.saveLocation(lat, lng);

                if (lat == 0 && lng == 0)
                    return "";

                Geocoder geocoder = new Geocoder(CreateAccountActivity.this, Locale.US);

                // lat,lng, your current location
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 10);

                    if (addresses != null && !addresses.isEmpty()) {

                        String locationToPrint = "";

                        for (Address address : addresses) {

                            LocationUtilities.setPostalCode(address.getPostalCode());
                            LocationUtilities.setAdminArea(address.getAdminArea() == null ? addresses.get(0).getLocality() : addresses.get(0).getAdminArea());
                            LocationUtilities.setCountryCode(address.getCountryCode());

                            String adminArea = address.getAdminArea();
                            String countryName = address.getCountryName();
                            String cityNameForUSA = address.getLocality();

                            if (countryName != null && (countryName.equals("United States") || address.getCountryCode().equals("US"))) {
                                Log.d("USA", "USA");

                                if (cityNameForUSA != null) {
                                    return cityNameForUSA;
                                }
                            }

                            if (adminArea != null) {
                                return adminArea;
                            } else {
                                if (countryName != null && (countryName.equals("United States") || address.getCountryCode().equals("US")))
                                    locationToPrint = cityNameForUSA;
                                else
                                    locationToPrint = countryName;
                            }
                        }
                        return locationToPrint;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            }
        }.execute();
    }

    private boolean allSet() {
        if (loginWithEmail) {
            return ((boolean) myNameTick.getTag() && (boolean) myBirthdayTick.getTag() &&
                    (boolean) myLocationTick.getTag() && (boolean) myGenreTick.getTag() && //commented code
                    (boolean) lookingTick.getTag() /*&& (boolean) myEmailTick.getTag() &&
                    (boolean) passwordTick.getTag() && (boolean) confirmTick.getTag()*/);
        } else {
            return ((boolean) myNameTick.getTag() && (boolean) myBirthdayTick.getTag() &&
                    (boolean) myLocationTick.getTag() && (boolean) myGenreTick.getTag() &&
                    (boolean) lookingTick.getTag() /*&& (boolean) myEmailTick.getTag() &&
                    (boolean) passwordTick.getTag() && (boolean) confirmTick.getTag()*/);
        }

    }

}
