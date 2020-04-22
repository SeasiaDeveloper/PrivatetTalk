package com.privetalk.app.mainflow.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.adapters.PagerCarouselAdapter;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.MyViewPager;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zachariashad on 21/01/16.
 */
public class BoostPopularityFragment extends FragmentWithTitle {

    private MyViewPager mViewPager;
    private int screenWidth;
    private View rootView;
    private PagerCarouselAdapter mPagerAdapter;
    private JsonObjectRequest getBalanceRequest;
    private JsonObjectRequest getPossibleMathcesRequest;
    private JsonObjectRequest promoteThisPicRequest;
    private PriveTalkTextView coinsBalanceTextView;
    private PriveTalkTextView matchesCount;
    private PriveTalkTextView matchGenre;
    private PriveTalkTextView promotePicture;
    private CurrentUser currentUser;
    private View addMoreCoins;
    private BroadcastReceiver photosDownloaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPagerAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PriveTalkUtilities.startDownloadWithPaging(Request.Method.GET, Links.PHOTOS, PriveTalkConstants.BROADCAST_PHOTOS_DOWNLOADED, null, new JSONObject());
        currentUser = CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_boost_popularity, container, false);

        initViews();

        getBalance();

        getPossibleMatches();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        return rootView;
    }

    private void initViews() {

        promotePicture = (PriveTalkTextView) rootView.findViewById(R.id.promotePicture);
        matchGenre = (PriveTalkTextView) rootView.findViewById(R.id.matchGenre);
        matchesCount = (PriveTalkTextView) rootView.findViewById(R.id.matchesCount);
        coinsBalanceTextView = (PriveTalkTextView) rootView.findViewById(R.id.textMyCoins);
        mViewPager = (MyViewPager) rootView.findViewById(R.id.boostPopularityProfiles);
        addMoreCoins = rootView.findViewById(R.id.addMoreCoins);
        mViewPager.setOverScrollMode(ViewPager.SCROLL_STATE_DRAGGING);

        addMoreCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ADD_MORE_PT_COINS_FRAGMENT);
                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
            }
        });

        mViewPager.post(new Runnable() {
            @Override
            public void run() {

                mViewPager.setOffscreenPageLimit(100);
                mViewPager.setPageMargin(-mViewPager.getWidth() + (screenWidth / 3)); //set margin so 3 items are visible to user
                mViewPager.setAdapter(mPagerAdapter = new PagerCarouselAdapter(getContext(), LayoutInflater.from(getContext()), mViewPager.getHeight()));

            }
        });

        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(final View page, float position) {

                CircleImageView imageView = (CircleImageView) page.findViewById(R.id.boostProfilePicture);
                RelativeLayout parentLayout = (RelativeLayout) page.findViewById(R.id.viewPagerCenter);
                position = (position > 2.0) ? 2.0f : position;

                imageView.setScaleX((float) (1 - Math.abs(position * 0.8)));
                imageView.setScaleY((float) (1 - Math.abs(position * 0.8)));
                imageView.setAlpha((float) (1 - 3 * Math.pow(position, 2)));

                if (position <= 0.15 && position >= -0.15) {
                    mViewPager.setFrontView((Integer) imageView.getTag(R.id.position_tag));
                }

                if (position > 0)
                    parentLayout.setTranslationX((float) (-(3 * Math.pow(position, 2)) * screenWidth / 4));
                else
                    parentLayout.setTranslationX((float) ((3 * Math.pow(position, 2)) * screenWidth / 4));
            }
        });

        matchGenre.setText((currentUser.lookingFor == 0) ? getString(R.string.people) : (currentUser.lookingFor == 1) ? getString(R.string.men) : getString(R.string.women));
        promotePicture.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                if (mPagerAdapter.getPictureId(mViewPager.getCurrentItem()) != -1) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.promote_photo)
                            .setMessage(R.string.promote_cost)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    promoteThisPicture(mPagerAdapter.getPictureId(mViewPager.getCurrentItem()));
                                }
                            }).setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            }
        });


    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.boost_your_popularity);
    }


    @Override
    public void onPause() {
        if (getBalanceRequest != null)
            getBalanceRequest.cancel();
        if (getPossibleMathcesRequest != null)
            getPossibleMathcesRequest.cancel();
        if (promoteThisPicRequest != null)
            promoteThisPicRequest.cancel();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(photosDownloaded, new IntentFilter(PriveTalkConstants.BROADCAST_PHOTOS_DOWNLOADED));
        super.onPause();
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(photosDownloaded);
        super.onResume();
    }

    private void getBalance() {
        getBalanceRequest = new JsonObjectRequest(Request.Method.GET, Links.COINS_BALANCE, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                coinsBalanceTextView.setText(String.valueOf(response.optInt("coins") + "{b}c{/b}"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                showErrorDialog();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null)
                    System.out.println("Get coins balance Server Response: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                return headers;
            }
        };

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getBalanceRequest);

    }

    private void getPossibleMatches() {

        getPossibleMathcesRequest = new JsonObjectRequest(Request.Method.GET, Links.POSSIBLE_MATCHES, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response.optBoolean("success"))
                    matchesCount.setText("{b}" + String.valueOf(response.optInt("possible_matches")) + "{/b}");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                showErrorDialog();
            }
        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null)
                    System.out.println("Get coins balance Server Response: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));


                return headers;
            }
        };


        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(getPossibleMathcesRequest);
    }


    private void promoteThisPicture(int pictureID) {


        promoteThisPicRequest = new JsonObjectRequest(Request.Method.POST, Links.PROMOTE_PICTURE + String.valueOf(pictureID), new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                System.out.println("Promote Picture Response: " + response);
                coinsBalanceTextView.setText(String.valueOf(response.optInt("coins") + "{b}c{/b}"));
                if (getContext() != null)
                    Toast.makeText(getContext(), "Picture Succesfully promoted", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    json = new String(response.data);
                    switch (response.statusCode) {
                        case 402:
                            json = trimMessage(json, "error_message");
                            if (json != null)
                                Toast.makeText(PriveTalkApplication.getInstance(), json, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(PriveTalkApplication.getInstance(), "Error while promoting photo", Toast.LENGTH_SHORT).show();
                }
            }
        }

        )

        {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                if (response != null)
                    System.out.println("Get coins balance Server Response: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                return headers;
            }
        }

        ;

        VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(promoteThisPicRequest);

    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }


//    private void showErrorDialog() {
//        new AlertDialog.Builder(getContext()).setTitle("Error while loading your information")
//                .setMessage("Please try again later")
//                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        getActivity().onBackPressed();
//                    }
//                }).create().show();
//    }

}
