package com.privetalk.app.welcome;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkEditText;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zeniosagapiou on 16/03/16.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private PriveTalkEditText priveTalkEditText;
    private View sendEmailButton;
    private View progressBarForgotPassword;
    private boolean isProgressBarShown;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setElevation(8);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);

        priveTalkEditText = (PriveTalkEditText) findViewById(R.id.forgotPasswordEdittext);

        progressBarForgotPassword = findViewById(R.id.progressBarForgotPassword);

        sendEmailButton = findViewById(R.id.forgotPasswordButton);
        sendEmailButton.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                progressBarForgotPassword.setVisibility(View.VISIBLE);
                isProgressBarShown = true;

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", priveTalkEditText.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest forgotPasswordRequest = new JsonObjectRequest(Request.Method.POST, Links.FORGOT_PASSWORD, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("Response: " + response.toString());
                        isProgressBarShown = false;
                        progressBarForgotPassword.setVisibility(View.GONE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(PriveTalkApplication.getInstance(), R.style.AppDialogTheme);
                        builder.setMessage(getString(R.string.pleaseCheckEmail));
                        builder.setNeutralButton(getString(R.string.okay), null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        alertDialog.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        isProgressBarShown = false;
                        progressBarForgotPassword.setVisibility(View.GONE);

                        if (error.networkResponse != null) {

                            try {
                                System.out.println("Error Message: " + new String(error.networkResponse.data, "UTF-8"));
                                JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                                Toast.makeText(ForgotPasswordActivity.this, jsonObject.optString("email"), Toast.LENGTH_SHORT).show();
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
                };

                VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(forgotPasswordRequest);

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (!isProgressBarShown)
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1f;
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
