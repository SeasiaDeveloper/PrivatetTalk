package com.privetalk.app.utilities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;

import com.privetalk.app.R;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.mainflow.fragments.AddPtCoinsFragment;
import com.privetalk.app.mainflow.fragments.BeARoyalUserFragment;
import com.privetalk.app.mainflow.fragments.CommunityFragment;
import com.privetalk.app.mainflow.fragments.FavoritesFragment;
import com.privetalk.app.mainflow.fragments.FlamesIgnitedFragment;
import com.privetalk.app.mainflow.fragments.HotMatchesFragment;
import com.privetalk.app.mainflow.fragments.HotWheelFragment;
import com.privetalk.app.mainflow.fragments.MessagesFragment;
import com.privetalk.app.mainflow.fragments.ProfileFragment;
import com.privetalk.app.mainflow.fragments.ProfileVisitors;
import com.privetalk.app.mainflow.fragments.SettingsFragment;

import java.util.HashMap;

/**
 * Created by zeniosagapiou on 08/01/16.
 */
public class DrawerUtilities {

    public static final String FRAGMENTT_CHANGE = "fragment-to-change";

    public enum DrawerRow {
        PROFILE, LINE1,  COMMUNITY, PROFILE_VISITORS, FAVORITES, LINE2, HOT_WHEEL,MESSAGES,  FLAMES_IGNITED,LINE3,BE_A_ROYAL_USER,
         ADD_PT_COINS, SETTINGS, LOGOUT
    }

/*

    public enum DrawerRow {
        PROFILE, LINE1,  COMMUNITY, MESSAGES, PROFILE_VISITORS, FAVORITES, LINE2, HOT_WHEEL,  FLAMES_IGNITED, HOT_MATCHES,
         LINE3, BE_A_ROYAL_USER, ADD_PT_COINS, SETTINGS, LOGOUT
    }
*/

    public static void handlePositionChanged(AppCompatActivity activity, int position, String titleName) {

        switch(DrawerRow.values()[position]){

            case PROFILE:
                ProfileFragment profileFragment = new ProfileFragment();
                loadFragment(activity, profileFragment, titleName);
                break;
            case COMMUNITY:
                CommunityFragment communityFragment = new CommunityFragment();
                loadFragment(activity, communityFragment, titleName);
                break;
            case HOT_WHEEL:
                HotWheelFragment hotWheelFragment = new HotWheelFragment();
                loadFragment(activity, hotWheelFragment, titleName);
                break;
            case MESSAGES:
                MessagesFragment priveTalkFragment = new MessagesFragment();
                loadFragment(activity, priveTalkFragment, titleName);
                break;
            case PROFILE_VISITORS:
                ProfileVisitors profileVisitors = new ProfileVisitors();
                loadFragment(activity, profileVisitors, titleName);
                break;
            case FLAMES_IGNITED:
               /* HotMatchesFragment hotMatchesFragment = new HotMatchesFragment();
                loadFragment(activity, hotMatchesFragment, titleName);*/

                FlamesIgnitedFragment flamesIgnitedFragment = new FlamesIgnitedFragment();
                loadFragment(activity, flamesIgnitedFragment, titleName);
                break;
           /* case HOT_MATCHES:
                HotMatchesFragment hotMatchesFragment = new HotMatchesFragment();
                loadFragment(activity, hotMatchesFragment, titleName);
                break;*/
            case FAVORITES:
                FavoritesFragment favoritesFragment = new FavoritesFragment();
                loadFragment(activity, favoritesFragment, titleName);
                break;
            case BE_A_ROYAL_USER:
                BeARoyalUserFragment beARoyalUserFragment = new BeARoyalUserFragment();
                loadFragment(activity, beARoyalUserFragment, titleName);
                break;
            case ADD_PT_COINS:
                AddPtCoinsFragment addPtCoinsFragment = new AddPtCoinsFragment();
                loadFragment(activity, addPtCoinsFragment, titleName);
                break;
            case SETTINGS:
                SettingsFragment settingsFragment = new SettingsFragment();
                loadFragment(activity, settingsFragment, titleName);
                break;
            case LOGOUT:
                logOutDialog(activity);
                break;
        }
    }


    private static void logOutDialog(AppCompatActivity activity){
        Intent intent = new Intent(MainActivity.BROADCAST_LOG_OUT);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    private static void loadFragment(AppCompatActivity activity, Fragment fragment, String titleName){

        Bundle bundle = new Bundle();
        bundle.putString(PriveTalkConstants.ACTION_BAR_TITLE, titleName);

        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.setCustomAnimations(0, android.support.v7.appcompat.R.anim.abc_fade_out);
        fragmentTransaction.replace(R.id.fragment_placeholder, fragment, PriveTalkConstants.KEY_CURRENT_MAIN_FRAGMENT);
        fragmentTransaction.commit();
    }


    public static final HashMap<DrawerRow, Integer> DRAWER_RESOURCES = new HashMap<>();
    static {
        {
            DRAWER_RESOURCES.put(DrawerRow.COMMUNITY, R.drawable.community);
            DRAWER_RESOURCES.put(DrawerRow.HOT_WHEEL, R.drawable.menu_hotwheel);
            DRAWER_RESOURCES.put(DrawerRow.MESSAGES, R.drawable.prive_talk);
            DRAWER_RESOURCES.put(DrawerRow.PROFILE_VISITORS, R.drawable.profile_visitors);
            DRAWER_RESOURCES.put(DrawerRow.FLAMES_IGNITED, R.drawable.flames_ignited);
            //DRAWER_RESOURCES.put(DrawerRow.HOT_MATCHES, R.drawable.hot_matches);
            DRAWER_RESOURCES.put(DrawerRow.FAVORITES, R.drawable.favorites);
            DRAWER_RESOURCES.put(DrawerRow.BE_A_ROYAL_USER, R.drawable.is_royal_user);
            DRAWER_RESOURCES.put(DrawerRow.ADD_PT_COINS, R.drawable.pt_coins);
            DRAWER_RESOURCES.put(DrawerRow.SETTINGS, R.drawable.settings);
            DRAWER_RESOURCES.put(DrawerRow.LOGOUT, R.drawable.logout);
        }
    }

    public static int getnumberOfNewItems(DrawerRow drawerRow) {

        switch (drawerRow) {
            case PROFILE_VISITORS:
                return 1;
            case FLAMES_IGNITED:
                return 2;
            default:
                return 0;
        }
    }
}
