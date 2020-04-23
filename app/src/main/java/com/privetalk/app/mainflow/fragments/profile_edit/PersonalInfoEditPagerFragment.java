package com.privetalk.app.mainflow.fragments.profile_edit;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
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
 * Created by zeniosagapiou on 20/01/16.
 */
public class PersonalInfoEditPagerFragment extends FragmentWithTitle {

    public final  String TAG = "PagerFragment";
    public static final String KEY_FRAGMENT_POSITION = "key-fragment-position";

    public static final int TOTAL_FRAGMENTS = 13;
   /* public static final int NAME = 0;
    public static final int AGE = 1;
    public static final int LOCATION = 2;*/

    public static final int RELATIONSHIP = 0;// ['0' or '1' or '2' or '3' or '4'],
    public static final int SEXUALITY = 1; // ['0' or '1' or '2' or '3'],
    public static final int HEIGHT = 2; // INTEGER
    public static final int WEIGHT = 3;// INTEGER
    public static final int BODY_TYPE = 4; //  ['0' or '1' or '2' or '3' or '4' or '5'],
    public static final int HAIR = 5; //  ['0' or '1' or '2' or '3' or '4' or '5' or '6' or '7' or '8' or '9' or '10' or '11'],
    public static final int EYES = 6; //  ['0' or '1' or '2' or '3' or '4' or '5' or '6'],
    public static final int SMOKING = 7; //  ['0' or '1' or '2' or '3' or '4'],
    public static final int DRINKING = 8; // ['0' or '1' or '2' or '3' or '4'],
    public static final int EDUCATION = 9; // ['0' or '1' or '2' or '3' or '4' or '5' or '6' or '7' or '8'],
    public static final int WORK = 10;// string
    public static final int LANGUAGES = 11; // COMPLEX CHOICE
    public static final int RELIGION = 12;// COMPLEX CHOICE

    private NoSwipeViewPager mViewPager;
    private FragmentAdapter fragmentAdapter;

    private View next, previous, done;
    private View rootView;
    private int currentPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: " + currentPage);

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentPage = savedInstanceState.getInt(MainActivity.FRAGMENT_VIEWPAGER_POSITION, 0);
        } else {
            currentPage = getArguments().getInt(MainActivity.FRAGMENT_VIEWPAGER_POSITION, 0);
        }


        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: " + currentPage);
        super.onCreateView(inflater, container, savedInstanceState);

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

                if (currentPage == 0) {
                    previous.setClickable(false);
                    previous.setEnabled(false);
                } else if (currentPage == mViewPager.getChildCount()) {
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

        return rootView;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume: " + currentPage);
        super.onResume();

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                ViewpagerFragment fragmentToShow = (ViewpagerFragment) fragmentAdapter.getItem(currentPage);
                fragmentToShow.onResumeFragment();
            }
        });
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: " + currentPage);

        ViewpagerFragment fragmentToHide =  (ViewpagerFragment)fragmentAdapter.getItem(currentPage);
        fragmentToHide.onPauseFragment();

        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + currentPage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.FRAGMENT_VIEWPAGER_POSITION, currentPage);
        Log.d(TAG, "onSaveInstanceState: " + currentPage);
    }

    private Fragment getFragment(int position) {

        switch (position) {
           /* case NAME:
            case LOCATION:
                return ProfileEditSimpleFragment.newInstance(position);*/
            case WORK:
                return WorkFragment.newInstance(position);
          /*  case AGE:
                return ProfileEditDatePickerFragment.newInstance(position);*/
            case RELATIONSHIP:
            case SEXUALITY:
            case BODY_TYPE:
            case HAIR:
            case EYES:
            case SMOKING:
            case DRINKING:
            case EDUCATION:
                return ProfileEditSimpleListFragment.newInstance(position, PersonalInfoEditParentFragment.PARENT_TYPE_PROFILE_EDIT);
            case HEIGHT:
            case WEIGHT:
                return ProfileSpinnerFragment.newInstance(position);
            case LANGUAGES:
                return ProfileEditComplexListFragment.newInstance(position);
            case RELIGION:
                return ReligionComplexList.newInstance(position);
            default:
        }

        return null;
    }

    @Override
    protected String getActionBarTitle() {
        return null;
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

            return  fragments.get(position);
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
