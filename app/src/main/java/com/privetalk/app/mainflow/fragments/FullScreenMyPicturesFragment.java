package com.privetalk.app.mainflow.fragments;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.percentlayout.widget.PercentRelativeLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.isseiaoki.simplecropview.CropImageView;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.objects.CurrentUserPhotoObject;
import com.privetalk.app.mainflow.activities.CameraViewActivity;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.FragmentWithTitle;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.SmoothScrollLinearLayoutManager;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by zachariashad on 15/02/16.
 */
public class FullScreenMyPicturesFragment extends FragmentWithTitle implements Animation.AnimationListener {

    //views
    private View oldRootView;
    private ViewPager fullScreenPicturesViewPager;
    private LinearLayout.LayoutParams smallPictureRecyclerViewParams;
    private RecyclerView smallPicturePreviewRecyclerView;
    private SmoothScrollLinearLayoutManager mSmoothScrollLayoutManager;
    private View removePictureBucket;
    private ImageView makeProfilePicture;
    private View shadowView;
    private TextView tapToProfileMessage;
    private ImageView tempMovingImageView;
    private View addProfilePictureView;
    private View verifiedPicture;
    private Animation animFadein, animFadeout;
    //adapters
    private HorizontalRecyclerAdapter smallPicturesPreviewAdapter;
    private FullScreenPicturesPagerAdapter mPagerAdapter;

    private boolean isTouchFree = true;
    private Animation bucketAnimationIn;
    private Animation bucketAnimationOut;
    private Handler mHanlder;
    private PercentRelativeLayout.LayoutParams movingImageParams;
    private Boolean isExecutedFirst = false;

    //data
    private List<CurrentUserPhotoObject> currentUserPhotoObjects;

    private boolean isProfilePicture = false,verified_picture=false;

    private BroadcastReceiver profilePictureChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getBooleanExtra(PriveTalkConstants.KEY_FAIL, false))
                return;
//            verifiedPicture.setVisibility(View.GONE);

            currentUserPhotoObjects.clear();
            currentUserPhotoObjects.addAll(CurrentUserPhotosDatasource.getInstance(getContext()).getCurrentUserPhotos());
            if (currentUserPhotoObjects.size() <= 0) {
                getActivity().onBackPressed();
                return;
            }

            mPagerAdapter = new FullScreenPicturesPagerAdapter();
            fullScreenPicturesViewPager.setAdapter(mPagerAdapter);

            //show changes on UI (verified picture label visibility)
            //   if (currentUserPhotoObjects.size() > 0)
            //     verifiedPicture.setVisibility(currentUserPhotoObjects.get(0).verified_photo ? View.VISIBLE : View.GONE);


            if (currentUserPhotoObjects.size() > 0) {
                if (currentUserPhotoObjects.get(0).verified_photo) {
                    verifiedPicture.setVisibility(View.VISIBLE);
                    animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                    verifiedPicture.startAnimation(animFadein);
                    // animFadein.setAnimationListener(this);
                } else {
                    if (verifiedPicture.getVisibility() == View.VISIBLE) {
                        animFadeout = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                        verifiedPicture.startAnimation(animFadeout);
                        verifiedPicture.setVisibility(View.GONE);
                    }
                }
            }


            setThumbsRecyclerWidth();
            smallPicturesPreviewAdapter = new HorizontalRecyclerAdapter();
            smallPicturePreviewRecyclerView.setAdapter(smallPicturesPreviewAdapter);

            if (currentUserPhotoObjects.get(fullScreenPicturesViewPager.getCurrentItem()).is_main_profile_photo) {
                makeProfilePicture.setImageResource(R.drawable.make_profile_true);
                isProfilePicture = true;
            } else {
                makeProfilePicture.setImageResource(R.drawable.make_profile_false);
                isProfilePicture = false;
                tapToProfileMessage.setVisibility(View.GONE);
            }
        }
    };
    private ViewSwitcher rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHanlder = new Handler();

        currentUserPhotoObjects = CurrentUserPhotosDatasource.getInstance(getContext()).getCurrentUserPhotos();
    }


    @Override
    protected String getActionBarTitle() {
        return null;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = (ViewSwitcher) inflater.inflate(R.layout.fragment_full_screen_pictures, container, false);

        oldRootView = rootView.findViewById(R.id.oldRootView);

        initViews();

        return rootView;
    }


    private void initViews() {

        verifiedPicture = rootView.findViewById(R.id.verifiedPicture);

        addProfilePictureView = oldRootView.findViewById(R.id.addProfilePicture);

        addProfilePictureView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                EasyImage.openChooserWithGallery(FullScreenMyPicturesFragment.this, getString(R.string.select_photo), 0);
            }
        });

        fullScreenPicturesViewPager = (ViewPager) oldRootView.findViewById(R.id.fullScreenPicturePreviewViewPager);
        smallPicturePreviewRecyclerView = (RecyclerView) oldRootView.findViewById(R.id.smallPicturePreviewRecyclerView);
        tapToProfileMessage = oldRootView.findViewById(R.id.profile_message);
        smallPictureRecyclerViewParams = (LinearLayout.LayoutParams) smallPicturePreviewRecyclerView.getLayoutParams();
        makeProfilePicture = (ImageView) oldRootView.findViewById(R.id.makeProfilePicture);

        makeProfilePicture.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                if (isProfilePicture) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(getString(R.string.already_profile));
                    builder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // makeProfilePicture();
                        }
                    });
                    builder.create().show();

                }


                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(getString(R.string.make_as_profile_message)); //make_as_profile
                    builder.setTitle(getString(R.string.please_confirm));
                    builder.setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //add verification code
                            makeProfilePicture();
                            Intent intent = new Intent(getActivity(), CameraViewActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no_string), null);
                    builder.create().show();

                }

            }
        });

        removePictureBucket = oldRootView.findViewById(R.id.removePicture);
        removePictureBucket.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                if (currentUserPhotoObjects.get(fullScreenPicturesViewPager.getCurrentItem()).verified_photo) {
                    builder.setTitle(R.string.warning_);
                    builder.setMessage(getString(R.string.profile_photo_delete));
                    builder.setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deleteProfilePhoto(currentUserPhotoObjects.get(fullScreenPicturesViewPager.getCurrentItem()));

                        }
                    });
                    builder.setNegativeButton(getString(R.string.no_string), null);
                    builder.create().show();
                } else {
                    builder.setMessage(getString(R.string.delete_picture));
                    builder.setTitle(getString(R.string.confirm_deletion));
                    builder.setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deleteProfilePhoto(currentUserPhotoObjects.get(fullScreenPicturesViewPager.getCurrentItem()));

                        }
                    });
                    builder.setNegativeButton(getString(R.string.no_string), null);
                    builder.create().show();
                }
            }
        });

        shadowView = oldRootView.findViewById(R.id.shadowViewFullScreenPicturesFragment);
        bucketAnimationIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_in_2);
        bucketAnimationOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_out_2);

        fullScreenPicturesViewPager.setAdapter(mPagerAdapter = new FullScreenPicturesPagerAdapter());

        mSmoothScrollLayoutManager = new SmoothScrollLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mSmoothScrollLayoutManager.setMillisecondsPerCebntimeter(500f);

        smallPicturePreviewRecyclerView.setLayoutManager(mSmoothScrollLayoutManager);
        setThumbsRecyclerWidth();
        smallPicturePreviewRecyclerView.setAdapter(smallPicturesPreviewAdapter = new HorizontalRecyclerAdapter());


        //   if (currentUserPhotoObjects.size() > 0)
        //     verifiedPicture.setVisibility(currentUserPhotoObjects.get(0).verified_photo ? View.VISIBLE : View.GONE);

        if (currentUserPhotoObjects.size() > 0) {

            if (currentUserPhotoObjects.get(0).verified_photo) {
                verifiedPicture.setVisibility(View.VISIBLE);
                animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                verifiedPicture.startAnimation(animFadein);
                // animFadein.setAnimationListener(this);
            } else {
                if (verifiedPicture.getVisibility() == View.VISIBLE) {
                    animFadeout = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                    verifiedPicture.startAnimation(animFadeout);
                    verifiedPicture.setVisibility(View.GONE);
                }
            }
        }

        if (currentUserPhotoObjects.size() > 0) {
            if (currentUserPhotoObjects.get(0).is_main_profile_photo) {
                tapToProfileMessage.setVisibility(View.GONE);
            } else {
                if (!isExecutedFirst) {
                    tapToProfileMessage.setVisibility(View.VISIBLE);
                    isExecutedFirst = true;
                } else {
                    tapToProfileMessage.setVisibility(View.GONE);
                }
            }
        }


        fullScreenPicturesViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                smallPicturePreviewRecyclerView.smoothScrollToPosition(position);

                if (currentUserPhotoObjects.get(position).is_main_profile_photo) {
                    makeProfilePicture.setImageResource(R.drawable.make_profile_true);
                    tapToProfileMessage.setVisibility(View.GONE);
                    isProfilePicture = true;
                } else {
                    makeProfilePicture.setImageResource(R.drawable.make_profile_false);
                    isProfilePicture = false;
                    if (!isExecutedFirst) {
                        tapToProfileMessage.setVisibility(View.VISIBLE);
                        isExecutedFirst = true;
                    } else {
                        tapToProfileMessage.setVisibility(View.GONE);
                    }

                }

                smallPicturesPreviewAdapter.setSelected(position);

                // verifiedPicture.setVisibility(currentUserPhotoObjects.get(position).verified_photo ? View.VISIBLE : View.GONE);

                if (currentUserPhotoObjects.get(position).verified_photo) {
                    verifiedPicture.setVisibility(View.VISIBLE);
                    animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
                    verifiedPicture.startAnimation(animFadein);
                    // animFadein.setAnimationListener(this);
                } else {
                    if (verifiedPicture.getVisibility() == View.VISIBLE) {
                        animFadeout = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
                        verifiedPicture.startAnimation(animFadeout);
                        verifiedPicture.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (currentUserPhotoObjects.get(fullScreenPicturesViewPager.getCurrentItem()).is_main_profile_photo) {
            makeProfilePicture.setImageResource(R.drawable.make_profile_true);
            isProfilePicture = true;
        } else {
            makeProfilePicture.setImageResource(R.drawable.make_profile_false);
            isProfilePicture = false;
        }
    }


    private void setThumbsRecyclerWidth() {
        int thumbWidth = getResources().getDimensionPixelSize(R.dimen.full_screen_thumbs_width);

        int count = currentUserPhotoObjects.size();

        if (count == 0)
            smallPictureRecyclerViewParams.width = 0;
        else if (count == 1)
            smallPictureRecyclerViewParams.width = thumbWidth;
        else if (count == 2)
            smallPictureRecyclerViewParams.width = 2 * thumbWidth;
        else
            smallPictureRecyclerViewParams.width = 3 * thumbWidth;

        smallPicturePreviewRecyclerView.setLayoutParams(smallPictureRecyclerViewParams);
    }


    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(profilePictureChanged, new IntentFilter(PriveTalkConstants.BROADCAST_NEW_PHOTO_UPLOADED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext().getApplicationContext()).unregisterReceiver(profilePictureChanged);
    }

    private void makeProfilePicture() {

        if (currentUserPhotoObjects != null && currentUserPhotoObjects.size() > 0) {

            CurrentUserPhotoObject currentUserPhotoObject = currentUserPhotoObjects.get(fullScreenPicturesViewPager.getCurrentItem());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("pk", currentUserPhotoObject.id);
//                jsonObject.put("photo", currentUserPhotoObject.photo);
//                jsonObject.put("square_photo", currentUserPhotoObject.square_photo);
                jsonObject.put("is_main_profile_photo", true);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

          JsonObjectRequest makeProfileRequest = new JsonObjectRequest(Request.Method.PATCH, Links.PHOTOS + currentUserPhotoObject.id + "/", jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    CurrentUserPhotoObject previousProfilePhoto = CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).getProfilePhoto();
                    if (previousProfilePhoto != null) {
                        previousProfilePhoto.is_main_profile_photo = false;

                        CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).savePhoto(previousProfilePhoto);
                    }

                    CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).savePhoto(new CurrentUserPhotoObject(response));

                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_NEW_PHOTO_UPLOADED));

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

            VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(makeProfileRequest);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animFadein) {
            Toast.makeText(getActivity(), "dfd", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    private class FullScreenPicturesPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return currentUserPhotoObjects.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View convertView = LayoutInflater.from(container.getContext()).inflate(R.layout.row_full_screen_pictures_pager, container, false);

            final ImageViewTouch image = (ImageViewTouch) convertView.findViewById(R.id.fullScreenImage);
            image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

            Glide.with(getContext()).load(currentUserPhotoObjects.get(position).photo).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    image.setImageBitmap(resource);
                }
            });


            container.addView(convertView);

            return convertView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    private class HorizontalRecyclerAdapter extends RecyclerView.Adapter<HorizontalRecyclerAdapter.ViewHolder> {

        private int selection = 0;
        private int previousSelection = -1;
        private int borderNormal;
        private int borderBold;


        private HorizontalRecyclerAdapter() {
            borderNormal = getResources().getDimensionPixelSize(R.dimen.border_width_normal);
            borderBold = getResources().getDimensionPixelSize(R.dimen.border_width_bold);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fullscreen_my_profile_small, parent, false);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullScreenPicturesViewPager.setCurrentItem((int) v.getTag(R.id.position_tag));
                }
            });
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.itemView.setTag(R.id.position_tag, position);
            holder.imageView.setBorderWidth(borderNormal);

            Glide.with(getContext())
                    .load(currentUserPhotoObjects.get(position).square_thumb)
                    .error(R.drawable.dummy_img)
                    .into((ImageView) holder.itemView.findViewById(R.id.userPictureSmallPreview));

            if (position == selection)
                holder.imageView.setBorderWidth(borderBold);

        }

        public void setSelected(int selection) {
            this.previousSelection = this.selection;
            this.selection = selection;
            notifyDataSetChanged();


        }

        @Override
        public int getItemCount() {
            return currentUserPhotoObjects.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public CircleImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (CircleImageView) itemView.findViewById(R.id.userPictureSmallPreview);
            }
        }
    }

//    private LongPressAndClickListener getLongPressAndClickListener() {
//
//        return new LongPressAndClickListener(mHanlder) {
//            private float initialY = 0;
//            private float intitialX = 0;
//            float DY;
//            float DX;
//            float dy;
//            float dx;
//            float myY;
//            float myX;
//
//            @Override
//            public void OnClick(View v) {
//                fullScreenPicturesViewPager.setCurrentItem((int) v.getTag(R.id.position_tag));
//            }
//
//            @Override
//            public void OnLongPress(View v, float downX, float downY) {
//                v.setTag(R.id.touch_tag, isTouchFree);
//
//                if (isTouchFree)
//                    isTouchFree = false;
//
//                if ((boolean) v.getTag(R.id.touch_tag)) {
//
//                    smallPicturePreviewRecyclerView.setEnabled(false);
//                    smallPicturePreviewRecyclerView.setClickable(false);
//                    mSmoothScrollLayoutManager.setCanScroll(false);
//
//                    //set shadowView visible
//                    shadowView.setVisibility(View.VISIBLE);
//
//                    //slide in animation bucket
//                    removePictureBucket.setVisibility(View.VISIBLE);
//                    removePictureBucket.setAnimation(bucketAnimationIn);
//
//                    //create new ImageView and set layout params from selected view
//                    tempMovingImageView = new ImageView(getContext());
//
//                    movingImageParams = new PercentRelativeLayout.LayoutParams(v.getWidth(), v.getHeight());
//                    movingImageParams.leftMargin = v.getLeft() + smallPicturePreviewRecyclerView.getLeft();
//                    movingImageParams.topMargin = v.getTop();
//
//                    tempMovingImageView.setLayoutParams(movingImageParams);
//
//                    //create a new bitmap from selected view and set on temp moving view
//                    tempMovingImageView.setImageBitmap(BitmapFromView.getBitmapFromView(v));
//
//                    //add view to oldRootView and bring to front
//                    ((PercentRelativeLayout) oldRootView).addView(tempMovingImageView);
//                    tempMovingImageView.bringToFront();
//
//                    //make initial view invisible
//                    v.setVisibility(View.INVISIBLE);
//
//                    //after view is created(post) calculate distance from target view
//                    tempMovingImageView.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            //calculate DX & DY distances from the new created image view to the target view (bucket)
//                            initialY = tempMovingImageView.getY() + tempMovingImageView.getHeight() / 2;
//                            intitialX = tempMovingImageView.getX() + tempMovingImageView.getWidth() / 2;
//                            targetPositionY = removePictureBucket.getTop() + removePictureBucket.getHeight() / 2;
//                            targetPositionX = removePictureBucket.getLeft() + removePictureBucket.getWidth() / 2;
//
//
//                            if (targetPositionY > initialY)
//                                DY = targetPositionY - initialY;
//                            else
//                                DY = initialY - targetPositionY;
//
//                            if (targetPositionX > intitialX)
//                                DX = targetPositionX - intitialX;
//                            else
//                                DX = intitialX - targetPositionX;
//
//                            //pithagorio - calculate distance
//                            initialDistanceFromCarbageBucket = (float) Math.sqrt((DY * DY) + (DX * DX));
//                        }
//                    });
//
//                    //vibrate for 200ms
//                    Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
//                    vibe.vibrate(200);
//
//                }
//
//            }
//
//            @Override
//            public void OnRelease(final View v, float downX, float downY) {
//                if ((boolean) v.getTag(R.id.touch_tag)) {
//
//                    if (canDelete) {
//                        ((RelativeLayout) oldRootView).removeView(tempMovingImageView);
//
//                        deleteProfilePhoto(currentUserPhotoObjects.get((int) v.getTag(R.id.position_tag)));
////                        profilePictures.remove((int) v.getTag(R.id.position_tag));
////                        smallPicturesPreviewAdapter.notifyItemRemoved((int) v.getTag(R.id.position_tag));
////                        smallPicturesPreviewAdapter.notifyItemRangeChanged((int) v.getTag(R.id.position_tag), profilePictures.size());
////
////                        Toast.makeText(getContext(), getString(R.string.deleted_match), Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        tempMovingImageView.animate().translationX(0).translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(250);
//
//                        mHanlder.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                tempMovingImageView.setVisibility(View.GONE);
//                                ((RelativeLayout) oldRootView).removeView(tempMovingImageView);
//                                v.setVisibility(View.VISIBLE);
//
//                            }
//                        }, 250);
//                    }
//
//                    smallPicturePreviewRecyclerView.setEnabled(true);
//                    smallPicturePreviewRecyclerView.setClickable(true);
//                    mSmoothScrollLayoutManager.setCanScroll(true);
//
//                    //slide out animation bucket
//                    removePictureBucket.setAnimation(bucketAnimationOut);
//                    removePictureBucket.setVisibility(View.GONE);
//
//                    //remove shadowView
//                    shadowView.setVisibility(View.INVISIBLE);
//
//                    isTouchFree = true;
//
//                }
//            }
//
//            @Override
//            public void OnMove(View v, float downX, float downY, float deltaX, float deltaY) {
//                if ((boolean) v.getTag(R.id.touch_tag)) {
//                    tempMovingImageView.setTranslationY(deltaY);
//                    tempMovingImageView.setTranslationX(deltaX);
//
//                    //get current center X & Y of my view
//                    myY = tempMovingImageView.getY() + tempMovingImageView.getHeight() / 2;
//                    myX = tempMovingImageView.getX() + tempMovingImageView.getWidth() / 2;
//
//                    //check if view is left/right or bottom/top related to target and do calculations
//                    if (targetPositionY > myY)
//                        dy = targetPositionY - myY;
//                    else
//                        dy = myY - targetPositionY;
//
//                    if (targetPositionX > myX)
//                        dx = targetPositionX - myX;
//                    else
//                        dx = myX - targetPositionX;
//
//                    //ipologizo arxiki apostasi kai apostasi kathos metakinite. meta diero
//                    float hypotunousa = (float) Math.sqrt((dy * dy) + (dx * dx));
//
//                        /*
//                        move value is used to find how close the view is to the target:
//                        value >= 1 => view is at it's initial place or more far away
//                        value < 1 closer to target
//                         */
//                    moveValue = hypotunousa / initialDistanceFromCarbageBucket;
//
//                    //if view x and y matches bucket height and width then canDelete = true
//                    canDelete = ((myX >= removePictureBucket.getLeft() && myX <= removePictureBucket.getRight())
//                            && (myY >= removePictureBucket.getTop() && myY <= removePictureBucket.getBottom()));
//
//                    if (moveValue > 1)
//                        moveValue = 1;
//                    else if (moveValue < 0.4)
//                        moveValue = 0.4f;
//
//
//                    tempMovingImageView.setAlpha(moveValue);
//
//                }
//
//            }
//        };
//    }

    private void deleteProfilePhoto(CurrentUserPhotoObject currentUserPhotoObject) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, Links.PHOTOS + currentUserPhotoObject.id + "/",

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        CurrentUserPhotoObject currentUserPhotoObject1 = new CurrentUserPhotoObject(response);
                        CurrentUserPhotosDatasource.getInstance(PriveTalkApplication.getInstance()).deletePhoto(currentUserPhotoObject1);

                        LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED));

                        //change profile picture in drawer menu
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(PriveTalkConstants.BROADCAST_NEW_PHOTO_UPLOADED));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {


                } else {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                try {

                    final View cropDoneView = rootView.findViewById(R.id.cropDone);
                    View cropCancelledView = rootView.findViewById(R.id.cropCancel);
                    final CropImageView cropImageView = (CropImageView) rootView.findViewById(R.id.cropImageView);

                    // cropImageView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                    //cropImageView.setCustomRatio(1,1);

                    cropImageView.setGuideShowMode(CropImageView.ShowMode.NOT_SHOW);
                    cropImageView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);

                    rootView.findViewById(R.id.rotateRight).setOnTouchListener(new FadeOnTouchListener() {
                        @Override
                        public void onClick(View view, MotionEvent event) {
                            cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                        }
                    });

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

                    if (options.outHeight > PriveTalkConstants.PICTURE_MAXIMUM_THRESHOLD || options.outWidth > PriveTalkConstants.PICTURE_MAXIMUM_THRESHOLD) {
                        float ratio = (float) options.outHeight / (float) options.outWidth;

                        if (options.outHeight > options.outWidth) {
                            options.outHeight = (int) (((float) PriveTalkConstants.PICTURE_MAXIMUM_THRESHOLD / (float) options.outHeight) * (float) options.outHeight);
                            options.outWidth = (int) ((float) options.outHeight / ratio);
                        } else {
                            options.outWidth = (int) (((float) PriveTalkConstants.PICTURE_MAXIMUM_THRESHOLD / (float) options.outWidth) * (float) options.outWidth);
                            options.outHeight = (int) ((float) options.outWidth * ratio);
                        }
                    }

                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    bitmap = Bitmap.createScaledBitmap(bitmap, options.outWidth, options.outHeight, false);

                    cropImageView.setImageBitmap(bitmap);
                    cropDoneView.setOnTouchListener(new FadeOnTouchListener() {
                        @Override
                        public void onClick(View view, MotionEvent event) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(getString(R.string.profile_photo));
                            builder.setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    rootView.showPrevious();
                                    PriveTalkUtilities.uploadProfilePicture(cropImageView.getImageBitmap(), cropImageView.getCroppedBitmap(), true);
                                }
                            });
                            builder.setNegativeButton(getString(R.string.no_string), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    rootView.showPrevious();
                                    PriveTalkUtilities.uploadProfilePicture(cropImageView.getImageBitmap(), cropImageView.getCroppedBitmap(), false);
                                }
                            });
                            builder.create().show();
                        }
                    });
                    cropCancelledView.setOnTouchListener(new FadeOnTouchListener() {
                        @Override
                        public void onClick(View view, MotionEvent event) {
                            rootView.showPrevious();
                        }
                    });

                    rootView.showNext();

                } catch (Exception ex) {
                    ex.printStackTrace();

                    Toast.makeText(getContext(), getContext().getString(R.string.loading_picture_failed), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
