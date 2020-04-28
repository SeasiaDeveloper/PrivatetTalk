package com.privetalk.app.mainflow.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.utilities.CameraPreview;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zeniosagapiou on 22/02/16.
 */
public class CameraViewActivity extends AppCompatActivity {

    private static final int MAXIMUM_PIXELS = 1920;
    static VerificationStage verificationStage;

    enum VerificationStage {INTRO, STEP1, STEP2, STEP3, STEP4}

    public class PhotoHolder {

        private byte[] photoBytes;

        public PhotoHolder(byte[] bytes) {
            photoBytes = bytes;
        }
    }

    private Toolbar toolbar;
    private TextView toolBarTitle;
    private ActionBar actionBar;
    private final static String DEBUG_TAG = "MakePhotoActivity";
    private int cameraId = 0;

    private static ArrayList<PhotoHolder> photosList;

    private BroadcastReceiver captureImageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    captureImage(null);
                }
            });

        }
    };

    private CameraPreview mPreview;
    private Camera mCamera;
    private FrameLayout surfaceView;
    private Camera.PictureCallback jpegCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_view);

        photosList = new ArrayList<>();

        verificationStage = VerificationStage.INTRO;

        toolbar = (Toolbar) findViewById(R.id.cameraToolbar);
        toolBarTitle = (TextView) toolbar.findViewById(R.id.camera_toolbar_title);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setElevation(8);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolBarTitle.setText(getString(R.string.profilePictureVerification));


        surfaceView = (FrameLayout) findViewById(R.id.cameraView);
        jpegCallback = new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {

                photosList.add(new PhotoHolder(processData(data)));

                switch (verificationStage) {
                    case STEP1:
                        verificationStage = VerificationStage.STEP2;
                        if (camera != null)
                            camera.startPreview();
                        break;
                    case STEP2:
                        verificationStage = VerificationStage.STEP3;
                        if (camera != null)
                            camera.startPreview();
                        break;
                    case STEP3:
                        verificationStage = VerificationStage.STEP4;
                        break;
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.cameraFragmentPlaceholder, CameraFragment.newInstance());
                transaction.setCustomAnimations(android.R.anim.fade_in, 0, android.R.anim.fade_in, 0);
                transaction.commit();
            }
        };

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.cameraFragmentPlaceholder, CameraFragment.newInstance());
        transaction.commit();

    }

    private byte[] processData(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        float max = Math.max(width, height);

        if (max > MAXIMUM_PIXELS) {
            if (width > height) {
                float multiplier = 1280f / width;
                width = width * multiplier;
                height = height * multiplier;
            } else {
                float multiplier = 1280f / height;
                width = width * multiplier;
                height = height * multiplier;
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, false);
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 55, stream);
        byte[] flippedImageByteArray = stream.toByteArray();
        bitmap.recycle();
        return flippedImageByteArray;

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCamera == null) {

            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
            } else {
                cameraId = findFrontFacingCamera();
                if (cameraId < 0) {
                    Toast.makeText(this, "No front facing camera found.",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    mCamera = Camera.open(cameraId);
                    mCamera.getParameters().setRotation(90);
                }
                mPreview = new CameraPreview(this, mCamera);
                surfaceView.addView(mPreview);
                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(captureImageReceiver, new IntentFilter("capture-image"));
            }

        }

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1f;
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {

            //              mCamera.setPreviewCallback(null);
            surfaceView.removeView(mPreview);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(captureImageReceiver);
    }

    public void captureImage(View v) {
        try {
            mCamera.takePicture(null, null, jpegCallback);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static class CameraFragment extends Fragment {

        private static final String FRAGMENT_ID = "fragment-id";
        private static final String PUBLISH_TIME_UPDATE = "publishTimeUpdate";

        private static final int CAPTURE__MILLISECONDS = 3000;
        private static final int CAPTURE_INTERVAL = 100;
        private static final int CAPTURE_SECONDS = 3;

        private View rootView;
        private View captureImage;

        private Timer timer;
        private ProgressBar progressBar;
        private TextView timeTextView;
        private MediaPlayer mediaPlayer;
        public SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        private int clikSound;

        public static CameraFragment newInstance() {

            CameraFragment cameraFragment = new CameraFragment();
            return cameraFragment;
        }

        @Nullable
        @Override
        public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

            clikSound = soundPool.load("/system/media/audio/ui/camera_click.ogg", 1);

            switch (verificationStage) {
                case INTRO:
                    rootView = inflater.inflate(R.layout.fragment_profile_picture_verification_intro, container, false);
                    rootView.findViewById(R.id.verificationGoButton).setOnTouchListener(new FadeOnTouchListener() {
                        @Override
                        public void onClick(View view, MotionEvent event) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            verificationStage = VerificationStage.STEP1;
                            transaction.replace(R.id.cameraFragmentPlaceholder, CameraFragment.newInstance());
                            transaction.commit();
                        }
                    });
                    break;
                case STEP1:
                    rootView = inflater.inflate(R.layout.fragment_camera_view_step1, container, false);
                    captureImage = rootView.findViewById(R.id.takePhoto);
                    captureImage.setOnTouchListener(new FadeOnTouchListener() {
                        @Override
                        public void onClick(View view, MotionEvent event) {
                            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent("capture-image"));
                        }
                    });
                    break;
                case STEP2:
                    rootView = inflater.inflate(R.layout.fragment_camera_view_step2, container, false);
                    captureImage = rootView.findViewById(R.id.takePhoto);
                    progressBar = (ProgressBar) rootView.findViewById(R.id.stepTwoProgressBar);
                    timeTextView = (TextView) rootView.findViewById(R.id.stepTwoTimeTextview);
                    timeTextView.setText(CAPTURE_SECONDS + "s");
                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.countdown);
                    break;
                case STEP3:
                    rootView = inflater.inflate(R.layout.fragment_camera_view_step3, container, false);
                    captureImage = rootView.findViewById(R.id.takePhoto);
                    progressBar = (ProgressBar) rootView.findViewById(R.id.stepThreeProgressBar);
                    timeTextView = (TextView) rootView.findViewById(R.id.stepThreeTimeTextview);
                    timeTextView.setText(CAPTURE_SECONDS + "s");
                    mediaPlayer = MediaPlayer.create(getContext(), R.raw.countdown);
                    break;
                case STEP4:
                    rootView = inflater.inflate(R.layout.fragment_camera_view_step4, container, false);
                    rootView.findViewById(R.id.tryAgainButton).setOnTouchListener(new FadeOnTouchListener() {
                        @Override
                        public void onClick(View view, MotionEvent event) {
                            Intent intent = new Intent(getActivity(), CameraViewActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });

                    ViewPager viewpager = (ViewPager) rootView.findViewById(R.id.fullScreenFrontPhotoViewPager);
                    viewpager.setOffscreenPageLimit(3);
                    viewpager.setAdapter(new PagerAdapter() {
                        @Override
                        public int getCount() {
                            return photosList.size();
                        }

                        @Override
                        public Object instantiateItem(ViewGroup container, int position) {

                            ImageView view = (ImageView) inflater.inflate(R.layout.layout_capture_image, container, false);
                            Glide.with(getContext()).load(photosList.get(position).photoBytes).centerCrop().into(view);

                            container.addView(view);

                            return view;
                        }

                        @Override
                        public void destroyItem(ViewGroup container, int position, Object object) {
                            container.removeView((View) object);
                        }

                        @Override
                        public boolean isViewFromObject(View view, Object object) {

                            return view == object;
                        }
                    });
                    rootView.findViewById(R.id.submitButton).setOnTouchListener(new FadeOnTouchListener() {
                        @Override
                        public void onClick(View view, MotionEvent event) {
                            sendPhotosForVerification();
                            getActivity().finish();

//                            Toast.makeText(getContext(), R.string.verification_pending, Toast.LENGTH_SHORT).show();
                        }
                    });

                    Glide.with(getContext()).load(photosList.get(2).photoBytes).into((ImageView) rootView.findViewById(R.id.leftPicture));
                    Glide.with(getContext()).load(photosList.get(0).photoBytes).into((ImageView) rootView.findViewById(R.id.frontPicture));
                    Glide.with(getContext()).load(photosList.get(1).photoBytes).into((ImageView) rootView.findViewById(R.id.rightPicture));

                    break;
                default:
                    return super.onCreateView(inflater, container, savedInstanceState);
            }

            return rootView;
        }

        BroadcastReceiver updateTimeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int progress = intent.getIntExtra(PUBLISH_TIME_UPDATE, 0);
                progressBar.setProgress(progress);
                String string = String.valueOf(CAPTURE_SECONDS - (progress / 1000));
                timeTextView.setText(string + "s");
            }
        };

        @Override
        public void onResume() {
            super.onResume();

            if (progressBar != null) {

                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).registerReceiver(updateTimeReceiver, new IntentFilter(PUBLISH_TIME_UPDATE));

                timer = new Timer();
                timer.schedule(new TimerTask() {

                    private int ellapsedTime;
                    private boolean firstTime = true;
                    boolean broadcastsend;

                    @Override
                    public void run() {

                        if (firstTime) {
                            firstTime = false;
                            if (mediaPlayer == null)
                                mediaPlayer = MediaPlayer.create(getContext(), R.raw.countdown);

                            mediaPlayer.start();
                        }

                        if (ellapsedTime < CAPTURE__MILLISECONDS) {
                            ellapsedTime += CAPTURE_INTERVAL;
                            Intent intent = new Intent(PUBLISH_TIME_UPDATE);
                            intent.putExtra(PUBLISH_TIME_UPDATE, ellapsedTime);
                            LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(intent);

                        } else if (!broadcastsend) {

                            soundPool.play(clikSound, 1f, 1f, 1, 0, 1f);

                            broadcastsend = true;
                            LocalBroadcastManager.getInstance(getContext().getApplicationContext()).sendBroadcast(new Intent("capture-image"));
                        }

                    }
                }, 750, CAPTURE_INTERVAL);

            }
        }

        @Override
        public void onPause() {

            if (progressBar != null)
                LocalBroadcastManager.getInstance(getContext().getApplicationContext()).unregisterReceiver(updateTimeReceiver);

            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            if (mediaPlayer != null && mediaPlayer.isPlaying())
                mediaPlayer.stop();

            super.onPause();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putSerializable(FRAGMENT_ID, verificationStage);
        }

        public void sendPhotosForVerification() {

            Toast.makeText(getContext(), getContext().getString(R.string.uploading_pictures), Toast.LENGTH_LONG).show();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("verified_photo_front", "data:image/jpeg;base64," + Base64.encodeToString(photosList.get(0).photoBytes, Base64.DEFAULT));
                jsonObject.put("verified_photo_left", "data:image/jpeg;base64," + Base64.encodeToString(photosList.get(1).photoBytes, Base64.DEFAULT));
                jsonObject.put("verified_photo_right", "data:image/jpeg;base64," + Base64.encodeToString(photosList.get(2).photoBytes, Base64.DEFAULT));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest sendVerificationPhotosRequest = new JsonObjectRequest
                    (Request.Method.POST, Links.MY_PROFILE, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {


                            Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                            intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.SHOW_UPLOADING_PICTURES_FINISHED);
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);

                            Intent intent1 = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                            intent1.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.VERIFICATION_PENDING);
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent1);

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Intent intent = new Intent(MainActivity.BROADCAST_UTILITY_ACTION);
                            intent.putExtra(PriveTalkConstants.UTILITY_ACTION, PriveTalkConstants.UtilityCommands.SHOW_UPLOADING_PROFILE_PHOTOS_FAILED);
                            LocalBroadcastManager.getInstance(PriveTalkApplication.getInstance()).sendBroadcast(intent);

                            if (error.networkResponse != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
//                        Toast.makeText(mContext, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
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
                    headers.put("AUTHORIZATION", "Token " + CurrentUserDatasource.getInstance(PriveTalkApplication.getInstance()).getCurrentUserInfo().token);
                    headers.put("Accept-Language", String.valueOf(Locale.getDefault()).substring(0, 2));

                    return headers;
                }

            };

            VolleySingleton.getInstance(PriveTalkApplication.getInstance()).addRequest(sendVerificationPhotosRequest);


        }
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}
