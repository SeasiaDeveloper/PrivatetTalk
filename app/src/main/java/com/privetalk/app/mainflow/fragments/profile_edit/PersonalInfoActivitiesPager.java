package com.privetalk.app.mainflow.fragments.profile_edit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.privetalk.app.R;
import com.privetalk.app.mainflow.activities.MainActivity;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.NoSwipeViewPager;
import com.privetalk.app.utilities.ViewpagerFragment;

import java.util.HashMap;

/**
 * Created by zeniosagapiou on 01/02/16.
 */
public class PersonalInfoActivitiesPager extends FragmentWithTitle {

    public static final String KEY_FRAGMENT_POSITION = "key-fragment-position";

    public static final int TOTAL_FRAGMENTS = 5;

    public static final int ACTIVITIES = 0;
    public static final int MUSIC = 1;
    public static final int MOVIES = 2;
    public static final int LITERATURE = 3;
    public static final int PLACES = 4;

    private NoSwipeViewPager mViewPager;
    private FragmentAdapter fragmentAdapter;

    private View next, previous, done;
    private View rootView;
    private int currentPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        currentPage = getArguments().getInt(MainActivity.FRAGMENT_VIEWPAGER_POSITION, 0);

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(MainActivity.FRAGMENT_VIEWPAGER_POSITION, 0);
        }

        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView( inflater,  container,  savedInstanceState);

        rootView = inflater.inflate(R.layout.activity_personal_info_edit_viewpager, container, false);

        next = rootView.findViewById(R.id.scrolling_next);
        previous = rootView.findViewById(R.id.scrolling_previous);
        done = rootView.findViewById(R.id.scrolling_done);

        mViewPager = (NoSwipeViewPager) rootView.findViewById(R.id.profileEditViewpager);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setCurrentItem(currentPage);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int newPosition) {

                ViewpagerFragment fragmentToShow = (ViewpagerFragment) fragmentAdapter.getItem(newPosition);
                fragmentToShow.onResumeFragment();

                ViewpagerFragment fragmentToHide = (ViewpagerFragment) fragmentAdapter.getItem(currentPage);
                fragmentToHide.onPauseFragment();

                currentPage = newPosition;

                next.setClickable(true);
                next.setEnabled(true);
                previous.setClickable(true);
                previous.setEnabled(true);

                if (newPosition == 0) {
                    previous.setClickable(false);
                    previous.setEnabled(false);
                } else if (newPosition == mViewPager.getChildCount()) {
                    next.setClickable(false);
                    next.setClickable(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        next.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });
        previous.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });
        done.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                getActivity().onBackPressed();
            }
        });

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                ViewpagerFragment fragmentToShow = (ViewpagerFragment) fragmentAdapter.getItem(currentPage);
                fragmentToShow.onResumeFragment();
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.FRAGMENT_VIEWPAGER_POSITION, mViewPager.getCurrentItem());
    }

    @Override
    public void onPause() {
        super.onPause();

        ViewpagerFragment fragmentToHide =  (ViewpagerFragment)fragmentAdapter.getItem(currentPage);
        fragmentToHide.onPauseFragment();
    }

    private Fragment getFragment(int position) {

        switch (position) {
            case ACTIVITIES:
            case MUSIC:
            case MOVIES:
            case LITERATURE:
            case PLACES:
                return ProfileEditActivitiesListFragment.newInstance(position);
            default:
        }

        return null;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.edit_interests);
    }

    private class FragmentAdapter extends FragmentStatePagerAdapter {

        private HashMap<Integer, Fragment> fragments = new HashMap< >();

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (!fragments.containsKey(position))
                fragments.put(position, getFragment(position));

            return fragments.get(position);
        }

//        @Override
//        public void destroyItem(View container, int position, Object object) {
//            fragments.remove(position);
//            super.destroyItem(container, position, object);
//        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public int getCount() {
            return TOTAL_FRAGMENTS;
        }
    }

}
