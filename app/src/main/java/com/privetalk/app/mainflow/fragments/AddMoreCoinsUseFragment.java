package com.privetalk.app.mainflow.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CoinsPlanDatasource;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserDetailsDatasource;
import com.privetalk.app.database.objects.CoinsPlan;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.inappbilling.IabHelper;
import com.privetalk.app.inappbilling.IabResult;
import com.privetalk.app.inappbilling.Inventory;
import com.privetalk.app.inappbilling.Purchase;
import com.privetalk.app.inappbilling.SkuDetails;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zachariashad on 03/02/16.
 */
public class AddMoreCoinsUseFragment extends FragmentWithTitle {

    private final int VIEW_IN_APP = 1;
    private final int VIEW_PROGRESSDIALOG = 0;

    private final int VOUCHER_VIEW_TICK = 0;
    private final int VOUCHER_VIEW_PROGRESSDIALOG = 1;

    private static final String TAG = "AddMoreCoinsUseFragment";
    private static final int RC_REQUEST = 6325;
    private ViewSwitcher rootView;
    private View planAbutton, planBbutton, planCbutton;
    private PriveTalkTextView purchaseBonusTextView;
    private PriveTalkTextView planAcoins, planBcoins, planCcoins;
    private PriveTalkTextView planAextraCoins, planBextraCoins, planCextraCoins;
    private PriveTalkTextView planAprice, planBprice, planCprice;
    private PriveTalkTextView coinsBalanceTextView;

    private CoinsPlan coinsPlanC;
    private CoinsPlan coinsPlanB;
    private CoinsPlan coinsPlanA;
    private IabHelper mHelper;

    CurrentUser currentUser;
    private TextView useCoins;
    private boolean usePurchaseBonus;
    private PriveTalkTextView voucherTextView;
    private ViewSwitcher voucherViewSwitcher;
    private ImageView voucherImageView;
    private int voucherBonus;
    private AlertDialog loadingCoinsDialog;

    private BroadcastReceiver coinsPlansDownloadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (loadingCoinsDialog != null)
                loadingCoinsDialog.dismiss();

            Log.v(TAG, "coinsPlansReceived");

            try {
                ArrayList<CoinsPlan> coinPlans = CoinsPlanDatasource.getInstance(getContext()).getCoinPlans();

                for (CoinsPlan coinsPlan : coinPlans) {

                    Log.v(TAG, "ADDING COIN PLAN");

                    if (coinsPlan.id == 1) {
                        coinsPlanA = coinsPlan;
                    } else if (coinsPlan.id == 2) {
                        coinsPlanB = coinsPlan;
                    } else if (coinsPlan.id == 3) {
                        coinsPlanC = coinsPlan;
                    }
                }

                planAbutton.setTag(coinsPlanA);
                planBbutton.setTag(coinsPlanB);
                planCbutton.setTag(coinsPlanC);

                startInAppFlow();

            } catch (Exception e) {
//                alert(getString(R.string.something_went_wrong));
                e.printStackTrace();
            }
        }
    };

    private ArrayList<String> skuDetailses;

    private BroadcastReceiver recursiveBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (loadingCoinsDialog != null)
                loadingCoinsDialog.dismiss();
            alert(getString(R.string.could_not_inapp_coins));
        }
    };


    private void alert(String alert) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(alert);
        builder.setNeutralButton(getString(R.string.okay), null);
        builder.create().show();
    }

    private void startInAppFlow() {

        Log.v(TAG, "STARTING APP FLOW");

        skuDetailses = new ArrayList<>();
        skuDetailses.add(coinsPlanA.android_product_id);
        skuDetailses.add(coinsPlanB.android_product_id);
        skuDetailses.add(coinsPlanC.android_product_id);

        if (mHelper.mSetupDone) {
            mHelper.queryInventoryAsync(true, skuDetailses, mGotInventoryListener);
        } else {

            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "Setup finished.");

                    if (!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        alert("Problem setting up in-app billing: " + result);
                        return;
                    }

                    // Have we been disposed of in the meantime? If so, quit.
                    if (mHelper == null) return;

                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Log.d(TAG, "Setup successful. Querying inventory.");

                    mHelper.queryInventoryAsync(true, skuDetailses, mGotInventoryListener);
                }
            });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingCoinsDialog = new AlertDialog.Builder(getContext())
                .setView(R.layout.layout_progress_dialog)
                .setCancelable(false)
                .create();

        loadingCoinsDialog.show();

        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
        currentUser.currentUserDetails = CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails();

        mHelper = ((MainActivity) getActivity()).mHelper;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        rootView = (ViewSwitcher) inflater.inflate(R.layout.fragment_add_more_coins_use, container, false);

        voucherViewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.verificationViewSwitcher);
        voucherImageView = (ImageView) voucherViewSwitcher.findViewById(R.id.verifyIcon);

        useCoins = (TextView) rootView.findViewById(R.id.useCoins);
        useCoins.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                if (usePurchaseBonus) {
                    usePurchaseBonus = false;
                    updateCoinsAmounts();
                    useCoins.setText(getString(R.string.use));
                    purchaseBonusTextView.setText(currentUser.last_week_purchase_bonus + "%");
                    useCoins.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.tick_green), PorterDuff.Mode.SRC_IN);
                    return;
                }

                if (currentUser.last_week_purchase_bonus == 0) {
                    Toast.makeText(getContext(), getString(R.string.not_enough_bonus), Toast.LENGTH_SHORT).show();
                    return;
                }

                usePurchaseBonus = true;
                updateCoinsAmounts();
                purchaseBonusTextView.setText(0 + "%");
                useCoins.setText(getString(R.string.undo));
                useCoins.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.blue_border_color), PorterDuff.Mode.SRC_IN);


            }
        });

        planAbutton = rootView.findViewById(R.id.planAButton);
        planBbutton = rootView.findViewById(R.id.planBButton);
        planCbutton = rootView.findViewById(R.id.planCButton);

        planAcoins = (PriveTalkTextView) rootView.findViewById(R.id.planAcoins);
        planBcoins = (PriveTalkTextView) rootView.findViewById(R.id.planBCoins);
        planCcoins = (PriveTalkTextView) rootView.findViewById(R.id.planCcoins);

        planAextraCoins = (PriveTalkTextView) rootView.findViewById(R.id.planAextraCoins);
        planBextraCoins = (PriveTalkTextView) rootView.findViewById(R.id.planBextraCoins);
        planCextraCoins = (PriveTalkTextView) rootView.findViewById(R.id.planCextraCoins);

        planAprice = (PriveTalkTextView) rootView.findViewById(R.id.planAprice);
        planBprice = (PriveTalkTextView) rootView.findViewById(R.id.planBprice);
        planCprice = (PriveTalkTextView) rootView.findViewById(R.id.planCPrice);

        purchaseBonusTextView = (PriveTalkTextView) rootView.findViewById(R.id.addCoinsPurchasePercentage);
        coinsBalanceTextView = (PriveTalkTextView) rootView.findViewById(R.id.coinsBalance);
        voucherTextView = (PriveTalkTextView) rootView.findViewById(R.id.voucherCode);
        voucherTextView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.enter_voucher_code));
                builder.setView(R.layout.dialog_voucher_code);
                builder.setPositiveButton(getString(R.string.use), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        androidx.appcompat.app.AlertDialog dialog1 = (androidx.appcompat.app.AlertDialog) dialog;
                        voucherTextView.setText(((EditText) dialog1.findViewById(R.id.voucherCodeTextview)).getText());
                        voucherViewSwitcher.setDisplayedChild(VOUCHER_VIEW_PROGRESSDIALOG);
                        voucherBonus = 0;
                        updateCoinsAmounts();
                        validateVoucher();

                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.create().show();
            }
        });

        planAbutton.setOnTouchListener(getCoinsToucListener);
        planBbutton.setOnTouchListener(getCoinsToucListener);
        planCbutton.setOnTouchListener(getCoinsToucListener);

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void updateCoinsAmounts() {

        int planACoinsAmount = getAmount(coinsPlanA.coins);
        int planBCoinsAmount = getAmount(coinsPlanB.coins);
        int planCCoinsAmount = getAmount(coinsPlanC.coins);

        if (planACoinsAmount == coinsPlanA.coins && planBCoinsAmount == coinsPlanB.coins && planCCoinsAmount == coinsPlanC.coins) {
            planAcoins.setTextColor(Color.BLACK);
            planBcoins.setTextColor(Color.BLACK);
            planCcoins.setTextColor(Color.BLACK);
        } else {
            planAcoins.setTextColor(ContextCompat.getColor(getContext(), R.color.tick_green));
            planBcoins.setTextColor(ContextCompat.getColor(getContext(), R.color.tick_green));
            planCcoins.setTextColor(ContextCompat.getColor(getContext(), R.color.tick_green));
        }

        planAcoins.setText(String.valueOf(planACoinsAmount));
        planBcoins.setText(String.valueOf(planBCoinsAmount));
        planCcoins.setText(String.valueOf(planCCoinsAmount));

        planAcoins.setTag(planACoinsAmount);
        planBcoins.setTag(planBCoinsAmount);
        planCcoins.setTag(planCCoinsAmount);
    }

    private int getAmount(int coins) {

        int purchaseBonusInner = 0;
        if (usePurchaseBonus)
            purchaseBonusInner = (int) ((float) coins * ((float) currentUser.last_week_purchase_bonus / 100f));
        int voucherBonusInner = (int) ((float) coins * ((float) voucherBonus / 100f));

        return coins + purchaseBonusInner + voucherBonusInner;
    }

    FadeOnTouchListener getCoinsToucListener = new FadeOnTouchListener() {
        @Override
        public void onClick(View view, MotionEvent event) {

           if(view.getTag()!=null) {
               final CoinsPlan coinsPlan = (CoinsPlan) view.getTag();
               final String stringie = coinsPlan.id == 1 ? String.valueOf(coinsPlan.extra_coins + (int) (planAcoins.getTag() == null ? coinsPlan.coins : planAcoins.getTag())) :
                       coinsPlan.id == 2 ? String.valueOf(coinsPlan.extra_coins + (int) (planBcoins.getTag() == null ? coinsPlan.coins : planBcoins.getTag())) :
                               String.valueOf(coinsPlan.extra_coins + (int) (planCcoins.getTag() == null ? coinsPlan.coins : planCcoins.getTag()));

               new AlertDialog.Builder(getContext()).setTitle(getString(R.string.you_are_about_to) + stringie + getString(R.string.coins_skett))
                       .setMessage(getString(R.string.are_you_sure_purchase) + stringie + getString(R.string.coins_question))
                       .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                           }
                       }).setPositiveButton(getString(R.string.purchase), new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       if (coinsPlan != null)
                           mHelper.launchPurchaseFlow(getActivity(), coinsPlan.android_product_id, RC_REQUEST, mPurchaseFinishedListener
                                   , CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().userID + "payload");
                       rootView.setDisplayedChild(0);
                   }
               }).create().show();
           }
        }
    };


    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(coinsPlansDownloadedReceiver, new IntentFilter(PriveTalkConstants.BROADCAST_COINS_PLANS_DOWNLOADED));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(recursiveBroadcastReceiver, new IntentFilter(PriveTalkConstants.BROADCAST_RECURSIVE_FAILED));

        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.COINS_PLANS, PriveTalkConstants.BROADCAST_COINS_PLANS_DOWNLOADED, null, new JSONObject());
    }

    @Override
    public void onPause() {
//        mHelper.dispose();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(coinsPlansDownloadedReceiver);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(recursiveBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.add_more_pt_coins);
    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                mHelper.queryInventoryAsync(true, skuDetailses, mGotInventoryListener);
                return;
            }

            for (CoinsPlan coinsPlan : CoinsPlanDatasource.getInstance(getContext()).getCoinPlans()) {
                if (inventory.hasPurchase(coinsPlan.android_product_id)) {
                    Purchase purchase = inventory.getPurchase(coinsPlan.android_product_id);
                    purchaseCoins(purchase);
                }
            }


            Log.d(TAG, "Query inventory was successful.");

            rootView.setDisplayedChild(1);

            if (getActivity() != null)
                loadDataToViews(inventory);

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    private void loadDataToViews(Inventory inventory) {

        SkuDetails skuDetailsA = inventory.getSkuDetails(coinsPlanA.android_product_id);
        SkuDetails skuDetailsB = inventory.getSkuDetails(coinsPlanB.android_product_id);
        SkuDetails skuDetailsC = inventory.getSkuDetails(coinsPlanC.android_product_id);

        //change was made here #893
        updateCoinsAmounts();

        if (isDetached())
            return;

        String free = " " + getString(R.string.free);

        planAextraCoins.setText("+" + String.valueOf(coinsPlanA.extra_coins) + (coinsPlanA.extra_coins != 0 ? free : ""));
        planBextraCoins.setText("+" + String.valueOf(coinsPlanB.extra_coins) + (coinsPlanB.extra_coins != 0 ? free : ""));
        planCextraCoins.setText("+" + String.valueOf(coinsPlanC.extra_coins) + (coinsPlanC.extra_coins != 0 ? free : ""));

        if (coinsPlanA.extra_coins == 0)
            planAextraCoins.setVisibility(View.GONE);


        try {
            planAprice.setText(skuDetailsA.getPrice());
            planBprice.setText(skuDetailsB.getPrice());
            planCprice.setText(skuDetailsC.getPrice());

            purchaseBonusTextView.setText(String.valueOf(currentUser.last_week_purchase_bonus) + "%");
            coinsBalanceTextView.setText(String.format(getString(R.string.coin_balance_2), String.valueOf(currentUser.coins)));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;


            if (result.isFailure()) {
                if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED ||
                        result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED)
                    alert("Error purchasing: " + getString(R.string.user_cancelled));
                else
                    alert("Error purchasing: " + getString(R.string.something_went_wrong));
                return;
            }

            if (!verifyDeveloperPayload(purchase)) {
                alert("Error purchasing. Authenticity verification failed.");
                return;
            }

            if (result.isSuccess())
                purchaseCoins(purchase);

        }
    };

    private void validateVoucher() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("voucher_code", voucherTextView.getText());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Links.FIND_VOUCHER + voucherTextView.getText(), jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());

                voucherViewSwitcher.setDisplayedChild(VOUCHER_VIEW_TICK);
                voucherImageView.setBackgroundResource(R.drawable.green_circle);
                voucherBonus = response.optInt("bonus_percentage");
                updateCoinsAmounts();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                voucherViewSwitcher.setDisplayedChild(VOUCHER_VIEW_TICK);
                voucherImageView.setBackgroundResource(R.drawable.circle_gray);

                if (error.networkResponse != null) {

                    try {
                        JSONObject jsonObject1 = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        alert("Error: " + jsonObject1.optString("detail"));
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
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };


        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(jsonObjectRequest);

    }

    private synchronized void purchaseCoins(final Purchase purchase) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("package_name", purchase.getPackageName());
            jsonObject.put("product_id", purchase.getSku());
            jsonObject.put("token", purchase.getToken());
            jsonObject.put("use_purchase_bonus", usePurchaseBonus);
            jsonObject.put("voucher_code", voucherTextView.getText());
        } catch (Exception ex) {
            ex.printStackTrace();
        }



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Links.BUY_ANDROID_PLAN, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());

                mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        rootView.setDisplayedChild(1);
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error.networkResponse != null) {

                    try {
                        JSONObject jsonObject1 = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                        alert("Error: " + jsonObject1.optString("detail"));
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
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }

        };


        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(jsonObjectRequest);

    }

    boolean verifyDeveloperPayload(Purchase p) {

        String payload = p.getDeveloperPayload();

        if (payload.equals(CurrentUserDetailsDatasource.getInstance(getContext()).getCurrentUserDetails().id + "payload")) {
            return true;
        }

        return false;
    }


}
