package com.privetalk.app.mainflow.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;

public class AddPtCoinsFragment extends FragmentWithTitle {

    String title;
    private View rootView;
    private View addMore;
    private PriveTalkTextView priveTalkTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(PriveTalkConstants.ACTION_BAR_TITLE);
        } else {
            title = getArguments().getString(PriveTalkConstants.ACTION_BAR_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_privetalk_coins, container, false);
        addMore = rootView.findViewById(R.id.addMoreCoins);
        priveTalkTextView = (PriveTalkTextView) rootView.findViewById(R.id.coinsBalance);
        String balance = getString(R.string.coin_balance);

        Log.d("testing50", " coins balance " + balance);

        priveTalkTextView.setText(String.valueOf(balance.format(balance, CurrentUserDatasource.getInstance(getContext()).getCurrentUserInfo().coins)));

        addMore.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.ADD_MORE_PT_COINS_FRAGMENT);
                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PriveTalkConstants.ACTION_BAR_TITLE, title);
    }

    @Override
    protected String getActionBarTitle() {
        return title;
    }
}
