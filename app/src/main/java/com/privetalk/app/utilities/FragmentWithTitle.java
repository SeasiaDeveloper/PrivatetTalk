package com.privetalk.app.utilities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.privetalk.app.PriveTalkApplication;

/**
 * Created by zeniosagapiou on 29/01/16.
 */
public abstract class FragmentWithTitle extends Fragment {


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            Intent intent = new Intent(PriveTalkConstants.CHANGE_ACTIONBAR_TITLE);
            intent.putExtra(PriveTalkConstants.ACTION_BAR_TITLE, getActionBarTitle());
            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcastSync(intent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint()){
            Intent intent = new Intent(PriveTalkConstants.CHANGE_ACTIONBAR_TITLE);
            intent.putExtra(PriveTalkConstants.ACTION_BAR_TITLE, getActionBarTitle());
            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcastSync(intent);
        }
    }

    protected abstract String getActionBarTitle();
}
