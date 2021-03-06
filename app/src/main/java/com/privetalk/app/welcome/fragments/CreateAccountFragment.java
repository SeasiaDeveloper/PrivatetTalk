package com.privetalk.app.welcome.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.privetalk.app.R;
import com.privetalk.app.services.GetLocationService;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.welcome.WelcomeActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachariashad on 18/12/15.
 */
public class CreateAccountFragment extends Fragment {

    private WelcomeActivity welcomeActivity;
    private View rootView;
    private View createAccount, signIn;
    private ImageView closeDialog;
    private LayoutInflater inflater;
    private TextView terms;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.GET_ACCOUNTS,

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;

        welcomeActivity = (WelcomeActivity) getActivity();
        rootView = inflater.inflate(R.layout.fragment_create_account, container, false);
        printHashKey(getActivity());
        initViews();
        if (checkPermissions()) {
            Intent locationServices = new Intent(getActivity(), GetLocationService.class);
            getActivity().startService(locationServices);
        }
        //  permissions  granted.
        //}

        //checkPermission();

        return rootView;
    }

    private void initViews() {
        createAccount = rootView.findViewById(R.id.createAccount);
        signIn = rootView.findViewById(R.id.signIn);
        terms = (TextView) rootView.findViewById(R.id.tvTermsAndConditions);
        terms.setMovementMethod(LinkMovementMethod.getInstance());

//        ((TextView)rootView.findViewById(R.id.htmlTesting)).setText(Html.fromHtml("<b>" + "What" + "</b>" + "are you"));
        createAccount.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
               // welcomeActivity.logInFacebook();
                welcomeActivity.showNewAccountDialog();
            }
        });

        signIn.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                welcomeActivity.showSignInDialog();
            }
        });
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


   /* private void checkPermission() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED||Manifest.permission.ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        } else {
            // Permission has already been granted
            Intent locationServices = new Intent(getActivity(), GetLocationService.class);
            getActivity().startService(locationServices);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Result", "coarse location permission granted");
                    Intent locationServices = new Intent(getActivity(), GetLocationService.class);
                    getActivity().startService(locationServices);
                }
            }
        }
    }
    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("ke", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("Sd", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("sd", "printHashKey()", e);
        }
    }
}

