package com.privetalk.app.mainflow.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.inappbilling.IabHelper;
import com.privetalk.app.inappbilling.IabResult;
import com.privetalk.app.inappbilling.Inventory;
import com.privetalk.app.inappbilling.Purchase;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zachariashad on 02/02/16.
 */
public class RoyalUserPlansFragment extends FragmentWithTitle {

    private static final String TAG = "RoyalUserPlansFragment";
    private static final int THREE_MONTHS = 0;
    private static final int SIX_MONTHS = 1;
    private static final int TWELVE_MONTHS = 2;

    private static final int RC_REQUEST = 6325;

    private static final String SKU_PLAN_A = "one_month";
    private static final String SKU_PLAN_B = "three_month";
    private static final String SKU_PLAN_C = "six_month";
    private static final String SKU_PLAN_D = "twelve_month";
//    private static final String SKU_PLAN_TEST = "plan_test";

    private ViewSwitcher rootView;
    private View planAButton, planBButton, planCButton, planDButton;
    private IabHelper mHelper;
    private ArrayList<String> skuDetailses;
    private PriveTalkTextView threeMonthsDiscount, sixMonthsDiscount, tweelveMonthsDiscount, oneMonthPlan, threeMonthPlan, sixMonthPlan, twelveMonthPlan;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Starting setup.");

        mHelper = ((MainActivity) getActivity()).mHelper;

    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
//                complain("Failed to query inventory: " + result);
                return;
            }



            Log.d(TAG, "Query inventory was successful.");

            rootView.showNext();

            try {

//                Currency.getInstance(Locale.getDefault()).getSymbol()
                String currency = inventory.getSkuDetails(SKU_PLAN_A).getPrice().substring(0, 1);

                currency = isNumeric(currency) ? "€" : currency;

                oneMonthPlan.setText(getDiscount(currency, (float) inventory.getSkuDetails(SKU_PLAN_A).getmPriceAmountMicros() / 1000000f));

                threeMonthPlan.setText(getDiscount(currency, (float) inventory.getSkuDetails(SKU_PLAN_B).getmPriceAmountMicros() / 3000000f));

                sixMonthPlan.setText(getDiscount(currency, (float) inventory.getSkuDetails(SKU_PLAN_C).getmPriceAmountMicros() / 6000000f));

                twelveMonthPlan.setText(getDiscount(currency, (float) inventory.getSkuDetails(SKU_PLAN_D).getmPriceAmountMicros() / 12000000f));

                float oneMonthPrice = inventory.getSkuDetails(SKU_PLAN_A).getmPriceAmountMicros();

                threeMonthsDiscount.setText(getDiscountPercentage(inventory.getSkuDetails(SKU_PLAN_B).getmPriceAmountMicros() / 3, oneMonthPrice));
                sixMonthsDiscount.setText(getDiscountPercentage(inventory.getSkuDetails(SKU_PLAN_C).getmPriceAmountMicros() / 6, oneMonthPrice));
                tweelveMonthsDiscount.setText(getDiscountPercentage(inventory.getSkuDetails(SKU_PLAN_D).getmPriceAmountMicros() / 12, oneMonthPrice));

            }catch (Exception ex){
                ex.printStackTrace();
            }


            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };


    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result.getMessage());
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            subscribe(purchase);

            alert(getString(R.string.thanks_for_subscribing));
        }
    };



    private boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    private void subscribe(Purchase purchase) {

        rootView.showPrevious();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("package_name", purchase.getPackageName());
            jsonObject.put("subscription_id", purchase.getSku());
            jsonObject.put("token", purchase.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest subscribeRequest = new JsonObjectRequest(Request.Method.POST, Links.SUBSCRIBE, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                rootView.showNext();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                rootView.showNext();

                if (error.networkResponse != null) {

                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                return headers;
            }

        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(subscribeRequest);
    }


    boolean verifyDeveloperPayload(Purchase p) {

        String payload = p.getDeveloperPayload();

        if (payload.equals(CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails().id + "payload")) {
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = (ViewSwitcher) inflater.inflate(R.layout.fragment_royal_user_plans, container, false);

        threeMonthsDiscount = (PriveTalkTextView) rootView.findViewById(R.id.threeMonthsDiscount);
        sixMonthsDiscount = (PriveTalkTextView) rootView.findViewById(R.id.sixMonthsDiscount);
        tweelveMonthsDiscount = (PriveTalkTextView) rootView.findViewById(R.id.tweelveMonthsDiscount);
        oneMonthPlan = (PriveTalkTextView) rootView.findViewById(R.id.oneMonthPlan);
        twelveMonthPlan = (PriveTalkTextView) rootView.findViewById(R.id.twelveMonthsPlan);
        threeMonthPlan = (PriveTalkTextView) rootView.findViewById(R.id.threeMonthsPlan);
        sixMonthPlan = (PriveTalkTextView) rootView.findViewById(R.id.sixMonthsPlan);


        planAButton = rootView.findViewById(R.id.planAImage);

        planAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().royal_user)
                    showDialog();
                else
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.one_month_royal_plan)
                            .setMessage(R.string.about_to_purchase_1_month_royal_user)
//                            .setMessage(R.string.are_you_sure_this_plan)
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHelper.launchSubscriptionPurchaseFlow(getActivity(), SKU_PLAN_A, RC_REQUEST, mPurchaseFinishedListener
                                            , CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().userID + "payload");
                                }
                            }).setNegativeButton(R.string.later, null)
                            .create().show();
            }
        });

        planBButton = rootView.findViewById(R.id.planBImage);

        planBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().royal_user)
                    showDialog();
                else
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.three_months_royal_plan)
                            .setMessage(R.string.about_to_purchase_3_month_royal_user)
//                            .setMessage(R.string.are_you_sure_this_plan)
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHelper.launchSubscriptionPurchaseFlow(getActivity(), SKU_PLAN_B, RC_REQUEST, mPurchaseFinishedListener
                                            , CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().userID + "payload");
                                }
                            }).setNegativeButton(R.string.later, null)
                            .create().show();
            }
        });
        planCButton = rootView.findViewById(R.id.planCImage);
        planCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().royal_user)
                    showDialog();
                else
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.six_months_royal_plan)
                            .setMessage(R.string.about_to_purchase_6_month_royal_user)
//                            .setMessage(R.string.are_you_sure_this_plan)
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHelper.launchSubscriptionPurchaseFlow(getActivity(), SKU_PLAN_C, RC_REQUEST, mPurchaseFinishedListener
                                            , CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().userID + "payload");
                                }
                            }).setNegativeButton(R.string.later, null)
                            .create().show();
            }
        });

        planDButton = rootView.findViewById(R.id.planDImage);
        planDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().royal_user)
                    showDialog();
                else
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.twelve_months_royal_plan)
                            .setMessage(R.string.about_to_purchase_12_month_royal_user)
//                            .setMessage(R.string.are_you_sure_this_plan)
                            .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHelper.launchSubscriptionPurchaseFlow(getActivity(), SKU_PLAN_D, RC_REQUEST, mPurchaseFinishedListener
                                            , CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().userID + "payload");
                                }
                            }).setNegativeButton(R.string.later, null)
                            .create().show();
            }
        });


        skuDetailses = new ArrayList<>();
        skuDetailses.add(SKU_PLAN_A);
        skuDetailses.add(SKU_PLAN_B);
        skuDetailses.add(SKU_PLAN_C);
        skuDetailses.add(SKU_PLAN_D);
//        skuDetailses.add(SKU_PLAN_TEST);

        if (mHelper.mSetupDone) {
            mHelper.queryInventoryAsync(true, skuDetailses, mGotInventoryListener);
        } else {

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {


                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        complain("Problem setting up in-app billing: " + result);
                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;


                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Log.d(TAG, "Setup successful. Querying inventory.");

                    try {
                        mHelper.queryInventoryAsync(true, skuDetailses, mGotInventoryListener);
                    }catch (IllegalStateException ex){
                        complain(getString(R.string.something_went_wrong));
                    }
                }
            });
        }

        return rootView;
    }


    private String getDiscount(String currency, float price) {
        return getString(R.string.for_b) + currency + String.format(java.util.Locale.US, "%.2f", price) + getString(R.string.per_month);

    }


    private String getDiscountPercentage(final float planPrice, final float oneMonthPlanPrice) {
        float percent = ((oneMonthPlanPrice - planPrice) / oneMonthPlanPrice) * 100;
        return String.valueOf((int) percent) + "%";
    }

    private void showDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(getString(R.string.purchase_another_plan))
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.royal_user_plans_action_bar);
    }


    void complain(String message) {
        Log.e(TAG, "**** Error: " + message);
        alert(message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getContext());
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().onBackPressed();
            }
        });
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        System.out.println("ON ACTIVITY RESULT CALLED IN FRAGMENT");
        super.onActivityResult(requestCode, resultCode, data);
    }
}
