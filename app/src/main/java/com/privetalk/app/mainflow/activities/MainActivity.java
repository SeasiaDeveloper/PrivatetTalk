package com.privetalk.app.mainflow.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.privetalk.app.LauncherActivity;
import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.PriveTalkMapFragment;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.BadgesDatasource;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.CurrentUserProfileSettingsDatasource;
import com.privetalk.app.database.datasource.NotificationsDatasource;
import com.privetalk.app.database.objects.BadgesObject;
import com.privetalk.app.database.objects.ConversationObject;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.CurrentUserPhotoObject;
import com.privetalk.app.database.objects.NotificationObject;
import com.privetalk.app.inappbilling.IabHelper;
import com.privetalk.app.inappbilling.IabResult;
import com.privetalk.app.inappbilling.Inventory;
import com.privetalk.app.inappbilling.Purchase;
import com.privetalk.app.mainflow.adapters.NotificationsAdapter;
import com.privetalk.app.mainflow.fragments.AddMoreCoinsUseFragment;
import com.privetalk.app.mainflow.fragments.BeARoyalUserFragment;
import com.privetalk.app.mainflow.fragments.BoostPopularityFragment;
import com.privetalk.app.mainflow.fragments.ChatFragments;
import com.privetalk.app.mainflow.fragments.CommunityFragment;
import com.privetalk.app.mainflow.fragments.FullScreenMyPicturesFragment;
import com.privetalk.app.mainflow.fragments.FullScreenOtherUsersFragment;
import com.privetalk.app.mainflow.fragments.MessagesFragment;
import com.privetalk.app.mainflow.fragments.OtherUsersProfileInfoFragment;
import com.privetalk.app.mainflow.fragments.ProfileFragment;
import com.privetalk.app.mainflow.fragments.RoyalUserPlansFragment;
import com.privetalk.app.mainflow.fragments.SearchFilterFragment;
import com.privetalk.app.mainflow.fragments.SettingsFragment;
import com.privetalk.app.mainflow.fragments.profile_edit.AboutMeEditFragment;
import com.privetalk.app.mainflow.fragments.profile_edit.PersonalInfoActivitiesPager;
import com.privetalk.app.mainflow.fragments.profile_edit.PersonalInfoEditPagerFragment;
import com.privetalk.app.mainflow.fragments.profile_edit.PersonalInfoEditParentFragment;
import com.privetalk.app.utilities.BitmapUtilities;
import com.privetalk.app.utilities.DrawerUtilities;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.ImageHelper;
import com.privetalk.app.utilities.KeyboardUtilities;
import com.privetalk.app.utilities.Links;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;
import com.privetalk.app.utilities.dialogs.AboutDialog;
import com.privetalk.app.utilities.dialogs.LogOutDialog;
import com.privetalk.app.welcome.WelcomeActivity;
import com.pushbots.push.Pushbots;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zeniosagapiou on 08/01/16.
 */
public class MainActivity extends AppCompatActivity {

    private static final int DRAWER_TOGGLE_ANIMATION_DURATION = 300;
    private static final int SHARE_APP_REQUEST = 5;

    public static final String BROADCAST_CHANGE_DRAWER_FRAGMENT = "broadcast-change-drawer-fragment";
    public static final String BROADCAST_CHANGE_FRAGMENT = "broadcast-change-fragment";
    public static final String FRAGMENT_TO_CHANGE = "fragment-to-change";
    public static final String ADD_TO_BACKSTUCK = "add-to-backstack";
    public static final String BROADCAST_CHANGE_NOTIFICATION_ICON = "broadcast-change-notification-icon";
    public static final String FRAGMENT_VIEWPAGER_POSITION = "fragment-viewpager-position";
    public static final String BROADCAST_UTILITY_ACTION = "utility-action";
    public static final String BROADCAST_LOG_OUT = "log-out-broadcast";

    private View selectedView;
    private RecyclerView drawerView;
    private DrawerAdapter drawerAdapter;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView toolBarTitle;
    private int selectedPosition;
    private String[] drawerRows;
    private Handler mHandler;
    private RelativeLayout dialogLayout;
    private ImageView dialogBackground;
    private View menuIcon;
    private View messageIcon;
    private int unauthorizedReguestsCounter = 0;

    private RecyclerView notificationsRecyclerView;
    private NotificationsAdapter notificationsAdapter;

    private MenuItem notificationIcon;
    private MenuItem newMessageIcon;
    private View notificationsMenu;
    private View sharePriveTalk;
    private View progressView;
    public IabHelper mHelper;
    private View aboutView;
    private boolean activityPaused;
    private AboutDialog aboutDialog;
    private LogOutDialog logoutDialog = null;
    //smallin

    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            if (result.isFailure() || activityPaused) {
                return;
            }

            for (String plan : PriveTalkConstants.PLANS) {
                if (inv.hasPurchase(plan)) {
                    Purchase purchase = inv.getPurchase(plan);
                    if (purchase == null)
                        return;

                    String payload = String.valueOf(CurrentUserDatasource.getInstance(MainActivity.this).getCurrentUserInfo().userID) + "payload";

                    if (purchase.getDeveloperPayload().equals(payload)) {
                        switch (purchase.getPurchaseState()) {
                            case Purchase.CANCELED:
                                break;
                            case Purchase.PURCHASED:
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("package_name", purchase.getPackageName());
                                    jsonObject.put("subscription_id", purchase.getSku());
                                    jsonObject.put("token", purchase.getToken());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                PriveTalkUtilities.postJsonObject(Links.SUBSCRIBE, jsonObject);
                                break;
                            case Purchase.REFUNDED:
                                break;
                        }
                    }

                }
            }

        }
    };


    private BroadcastReceiver utilitiesReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            PriveTalkConstants.UtilityCommands utilityCode = (PriveTalkConstants.UtilityCommands) intent.getSerializableExtra(PriveTalkConstants.UTILITY_ACTION);

            switch (utilityCode) {
                case CLAIM_COINS:
                    checkIfRewardClaimed();
                    break;
                case VERIFICATION_PENDING:

                    PriveTalkUtilities.getUserInfoFromServer();

                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(getString(R.string.verification_pending))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    break;
                case SHOW_UPLOADING_PICTURE_FINISHED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.picture_uploaded), Snackbar.LENGTH_LONG).show();
                    break;
                case SHOW_UPLOADING_PICTURE_TOAST:
                    Toast.makeText(MainActivity.this, getString(R.string.uploading_picture), Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_UPLOADING_PROFILE_PHOTO_FAILED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.uploading_picture_failed), Snackbar.LENGTH_LONG).show();
                    break;
                case SHOW_UPLOADING_PROFILE_PHOTOS_FAILED:
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(getString(R.string.uploading_profile_pictures_failed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    break;
                case SHOW_UPLOADING_PICTURES_FINISHED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.profile_pictures_uploaded), Snackbar.LENGTH_LONG).show();
                    break;
                case CURRENT_USER_SAVED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.save_used_completed), Snackbar.LENGTH_LONG).show();
                    progressView.setVisibility(View.GONE);
                    PersonalInfoEditParentFragment.infoChanged = false;
                    AboutMeEditFragment.infochanged = false;
                    onBackPressed();
                    break;
                case CURRENT_USER_SAVE_FAILED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.failed_to_save_user), Snackbar.LENGTH_LONG).show();
                    progressView.setVisibility(View.GONE);
                    break;
                case USER_BLOCKED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.user_blocked), Snackbar.LENGTH_LONG).show();
                    progressView.setVisibility(View.GONE);
                    break;
                case USER_REPORTED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.user_reported), Snackbar.LENGTH_LONG).show();
                    progressView.setVisibility(View.GONE);
                    break;
                case BLOCK_FAILED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.block_failed), Snackbar.LENGTH_LONG).show();
                    progressView.setVisibility(View.GONE);
                    break;
                case REPORT_FAILED:
                    Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), getString(R.string.report_failed), Snackbar.LENGTH_LONG).show();
                    progressView.setVisibility(View.GONE);
                    break;
                case SHOW_VISIBILITY_CHANGED:
                    if (CurrentUserDatasource.getInstance(context).getCurrentUserInfo().hidden_mode_on)
                        Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), R.string.hidden_mode_on, Snackbar.LENGTH_LONG).show();
                    else
                        Snackbar.make(findViewById(R.id.mainActivityCoordinatorLayout), R.string.hedden_mode_off, Snackbar.LENGTH_LONG).show();
                    progressView.setVisibility(View.GONE);
                    break;
                case NOTIFICATIONS_UPADATED:
                    notificationsAdapter.refreshDataset();
                    setMenuIcon(NotificationsDatasource.getInstance(MainActivity.this).getUnreadNotificationsCount());
                    break;
                case SHOW_MY_PROFILE:
                    onBackPressed();
                    DrawerUtilities.handlePositionChanged(MainActivity.this, DrawerUtilities.DrawerRow.PROFILE.ordinal(), drawerRows[DrawerUtilities.DrawerRow.PROFILE.ordinal()]);
                    break;
                case MENU_BADGES_RECEIVED:
                    drawerAdapter.notifyDataSetChanged();

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
                    if (!newMessageIcon.isVisible() && (fragment instanceof MessagesFragment || fragment instanceof ChatFragments))
                        return;
                    newMessageIcon.setVisible(BadgesDatasource.getInstance(context).getBadges().messagesBadge != 0);
                    break;
                case ANAUTHORIZED_SESSION:
                    unauthorizedReguestsCounter++;
                    if (unauthorizedReguestsCounter >= 3) {
                        PriveTalkUtilities.clearAllSavedData();
                        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                        finish();
                    }
                    break;
            }

        }
    };


    private BroadcastReceiver profilePhotoUploadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(PriveTalkConstants.KEY_FAIL, false))
                return;
            if (drawerAdapter != null)
                drawerAdapter.notifyDataSetChanged();
        }
    };


    private BroadcastReceiver changeNotificationIconToText = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (activityPaused || newMessageIcon == null) return;
            if (intent.getBooleanExtra("toText", false)) {
                notificationIcon.setIcon(null);
                notificationIcon.setTitle(getString(R.string.save));
                notificationIcon.setTitleCondensed(getString(R.string.save));
            } else {
                notificationIcon.setIcon(R.drawable.notification_icon);
                notificationIcon.setTitle("");
            }
        }
    };


    private BroadcastReceiver changeTitlebaTitleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newTitle = intent.getStringExtra(PriveTalkConstants.ACTION_BAR_TITLE);
            if (newTitle == null) return;
            if (!toolBarTitle.getText().equals(newTitle)) {
                toolBarTitle.setText(newTitle);
            }
        }
    };


    private BroadcastReceiver logOut = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //add a new Relative Layout for the dialog
            dialogLayout = new RelativeLayout(MainActivity.this);
            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialogLayout.setLayoutParams(params);
            mDrawerLayout.addView(dialogLayout);
            dialogLayout.bringToFront();

            dialogLayout.setClickable(true);

            dialogBackground = new ImageView(MainActivity.this);
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialogBackground.setLayoutParams(params1);
            dialogLayout.addView(dialogBackground);

            //create bitmap from screenshot, blur image and set in background
            dialogBackground.setImageBitmap(ImageHelper.blurBitmap(getApplicationContext(), BitmapUtilities.getBitmapFromView(findViewById(android.R.id.content)), 25.f));

            logoutDialog = new LogOutDialog(MainActivity.this, dialogLayout, getLayoutInflater());
            logoutDialog.setYesButtonListener(new LogOutDialog.ButtonYesListener() {
                @Override
                public void onYesPressed() {
                    Pushbots.sharedInstance().unRegister();
                    PriveTalkUtilities.clearAllSavedData();
                    Intent intent1 = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent1);
                    finish();
                }
            });
            logoutDialog.show();
        }
    };

    private BroadcastReceiver switchFragmentOnMenuProgramatically = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int whichFragment = intent.getIntExtra(DrawerUtilities.FRAGMENTT_CHANGE, -1);

            if (whichFragment != -1)
                showDrawerFragment(whichFragment);

        }
    };

    private String TAG = "MainActivity";
    private BroadcastReceiver configurationsScoresDownloadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PriveTalkUtilities.getUserInfoFromServer();
        }
    };

    private BroadcastReceiver profilePhotosReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (drawerAdapter != null)
                drawerAdapter.notifyDataSetChanged();
        }
    };

    private View navigationDrawerParent;
    private boolean pushFound;
    private Handler setAliasHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PriveTalkUtilities.getAllInterests();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);
        Configuration c = getResources().getConfiguration();
        if (c.fontScale > 1f) c.fontScale = 1f;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer);
        drawerRows = getResources().getStringArray(R.array.drawer_rows);
        initIabHelber();
        toolbar = (Toolbar) findViewById(R.id.mToolbar);
        toolBarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setElevation(8);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
      //  toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        //toolbar.getNavigationIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        toolBarTitle.setText(drawerRows[DrawerUtilities.DrawerRow.COMMUNITY.ordinal()]);

        progressView = findViewById(R.id.fullScreenProgressBar);
        navigationDrawerParent = findViewById(R.id.navigationDrawerParent);
        navigationDrawerParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });

        notificationsMenu = findViewById(R.id.notificationsMenu);
        sharePriveTalk = findViewById(R.id.sharePriveTalk);

        sharePriveTalk.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.sharePrive(MainActivity.this, SHARE_APP_REQUEST);
            }
        });

        aboutView = findViewById(R.id.aboutMeButton);
        aboutView.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                aboutDialog = new AboutDialog(MainActivity.this, mDrawerLayout, getLayoutInflater());
                aboutDialog.show();
            }
        });

        setupDrawer();
        setupNotificationsDrawer();
        mHandler = new Handler();

        //check if profile settings are not loaded then fetch from server
        if (CurrentUserProfileSettingsDatasource.getInstance(this).getCurrentUserProfileSettings() == null) {
            PriveTalkUtilities.getCurrentUserProfileSettings();
        }

        if (!Pushbots.sharedInstance().isInitialized()) Pushbots.sharedInstance().init(this);
        PriveTalkUtilities.badgesRequest(false, null);
        if (!checkForPush()) {
            if (savedInstanceState != null) {
                selectedPosition = savedInstanceState.getInt("selectedPosition");
            } else {
                selectedPosition = DrawerUtilities.DrawerRow.COMMUNITY.ordinal();
                DrawerUtilities.handlePositionChanged(MainActivity.this, DrawerUtilities.DrawerRow.COMMUNITY.ordinal(), drawerRows[DrawerUtilities.DrawerRow.COMMUNITY.ordinal()]);
            }
        }

        checkIfRewardClaimed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        notificationIcon = menu.findItem(R.id.action_notifications);
        newMessageIcon = menu.findItem(R.id.action_messages);
        newMessageIcon.setVisible(BadgesDatasource.getInstance(MainActivity.this).getBadges().messagesBadge != 0);
        setMessageIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications:
                if (item.getIcon() == null) {
                    progressView.setVisibility(View.VISIBLE);
                    if (getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder) instanceof PersonalInfoEditParentFragment)
                        PriveTalkUtilities.saveCurrentUserData(PersonalInfoEditParentFragment.currentUser.getJsonForServer());
                    else if (getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder) instanceof AboutMeEditFragment) {
                        JSONObject info = new JSONObject();
                        JSONObject aboutObject = new JSONObject();
                        try {
                            info.put("about", AboutMeEditFragment.currentValue);
                            aboutObject.put("info", info);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        PriveTalkUtilities.saveCurrentUserData(aboutObject);
                    }

                } else if (mDrawerLayout.isDrawerOpen(notificationsMenu)) {
                    mDrawerLayout.closeDrawer(notificationsMenu);
                } else {
                    mDrawerLayout.openDrawer(notificationsMenu);
                }
                return true;
            case R.id.home:
                onBackPressed();
                return true;
            case R.id.action_messages:

                DrawerUtilities.handlePositionChanged(this, DrawerUtilities.DrawerRow.MESSAGES.ordinal(), DrawerUtilities.DrawerRow.MESSAGES.toString());

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkForPush() {
        String pushData = getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getString(PriveTalkConstants.KEY_PUSH_DATA, "");

        pushFound = false;

        if (!pushData.isEmpty()) {

            try {
                final JSONObject obj = new JSONObject(pushData);

                String type = obj.optString("type");

                switch (type) {
                    case NotificationObject.TYPE_MSG: {
                        DrawerUtilities.handlePositionChanged(MainActivity.this, DrawerUtilities.DrawerRow.COMMUNITY.ordinal(), drawerRows[DrawerUtilities.DrawerRow.COMMUNITY.ordinal()]);
                        pushFound = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ConversationObject conversationObject = new ConversationObject();
                                String sender = obj.optString("sender");

                                conversationObject.partnerID = (sender != null && !sender.equals("null")) ? Integer.parseInt(sender) : 0;

                                Intent intent = new Intent(MainActivity.BROADCAST_CHANGE_FRAGMENT);
                                intent.putExtra(MainActivity.FRAGMENT_TO_CHANGE, PriveTalkConstants.CHAT_FRAGMENT);
                                intent.putExtra(MainActivity.ADD_TO_BACKSTUCK, true);
                                Bundle bundle2 = new Bundle();
                                bundle2.putSerializable("partnerObject", conversationObject);
                                intent.putExtra("bundle", bundle2);
                                changeFragmentAboveMenu(intent);

                            }
                        }, 100);
                    }
                    break;

                    case NotificationObject.TYPE_PROFILE_VISITOR: {
                        pushFound = true;
                        selectedPosition = DrawerUtilities.DrawerRow.PROFILE_VISITORS.ordinal();
                        DrawerUtilities.handlePositionChanged(MainActivity.this, selectedPosition,
                                drawerRows[DrawerUtilities.DrawerRow.PROFILE_VISITORS.ordinal()]);
                        toolBarTitle.setText(drawerRows[DrawerUtilities.DrawerRow.PROFILE_VISITORS.ordinal()]);
                    }
                    break;

                    case NotificationObject.TYPE_FLAME_IGNITED: {
                        pushFound = true;
                        selectedPosition = DrawerUtilities.DrawerRow.FLAMES_IGNITED.ordinal();
                        DrawerUtilities.handlePositionChanged(MainActivity.this,
                                DrawerUtilities.DrawerRow.FLAMES_IGNITED.ordinal(),
                                drawerRows[DrawerUtilities.DrawerRow.FLAMES_IGNITED.ordinal()]);
                        toolBarTitle.setText(drawerRows[DrawerUtilities.DrawerRow.FLAMES_IGNITED.ordinal()]);
                    }
                    break;

                  /*  case NotificationObject.TYPE_HOTMATCH: {
                        pushFound = true;
                        selectedPosition = DrawerUtilities.DrawerRow.HOT_MATCHES.ordinal();
                        DrawerUtilities.handlePositionChanged(MainActivity.this,
                                DrawerUtilities.DrawerRow.HOT_MATCHES.ordinal(),
                                drawerRows[DrawerUtilities.DrawerRow.HOT_MATCHES.ordinal()]);
                        toolBarTitle.setText(drawerRows[DrawerUtilities.DrawerRow.HOT_MATCHES.ordinal()]);
                    }
                    break;*/

                    case NotificationObject.TYPE_FAVORITE: {
                        pushFound = true;
                        selectedPosition = DrawerUtilities.DrawerRow.FAVORITES.ordinal();
                        DrawerUtilities.handlePositionChanged(MainActivity.this, DrawerUtilities.DrawerRow.FAVORITES.ordinal(),
                                drawerRows[DrawerUtilities.DrawerRow.FAVORITES.ordinal()]);
                        toolBarTitle.setText(drawerRows[DrawerUtilities.DrawerRow.FAVORITES.ordinal()]);

                    }
                    break;
                }

                drawerAdapter.notifyDataSetChanged();

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).edit().putString(PriveTalkConstants.KEY_PUSH_DATA, "").apply();
        return pushFound;
    }


    @Override
    protected void onResume() {
        super.onResume();
        activityPaused = false;

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = 1f;
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());


        if (drawerView != null)
            drawerView.bringToFront();

        if (!PriveTalkUtilities.isNetworkConnected())
            showNoNetworkDialog();

        PriveTalkUtilities.sendLocationToServer();
        PriveTalkUtilities.getAttributes();
        PriveTalkUtilities.postJsonObject(Links.GO_ONLINE);
        PriveTalkUtilities.getGlobalTimeFromServer();


        if (activityPaused) {
            notificationsAdapter.refreshDataset();
            setMenuIcon(NotificationsDatasource.getInstance(MainActivity.this).getUnreadNotificationsCount());

            if (newMessageIcon != null)
                newMessageIcon.setVisible(BadgesDatasource.getInstance(MainActivity.this).getBadges().messagesBadge != 0);
        }

        sendOnlineNotification();
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    private void sendOnlineNotification() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PriveTalkUtilities.postJsonObject(Links.GO_ONLINE);
                sendOnlineNotification();
            }
        }, 180000);
    }


    @Override
    protected void onPause() {

        if (setAliasHandler != null)
            setAliasHandler.removeCallbacksAndMessages(null);

        notificationsAdapter.onActivityPaused(this);

        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        PriveTalkUtilities.postJsonObject(Links.GO_OFFLINE);
        activityPaused = true;
    }

    private void setupNotificationsDrawer() {
        notificationsRecyclerView = (RecyclerView) findViewById(R.id.notificationsRecyclerView);
        notificationsAdapter = new NotificationsAdapter(this);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationsRecyclerView.setAdapter(notificationsAdapter);
        PriveTalkUtilities.getNotifications();
        setMenuIcon(NotificationsDatasource.getInstance(this).getUnreadNotificationsCount());
    }

    private void setupDrawer() {

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.common_open_on_phone,
                R.string.back
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                invalidateOptionsMenu();
                syncState();

                if (view.equals(notificationsMenu)) {
                    setMenuIcon(NotificationsDatasource.getInstance(MainActivity.this).getUnreadNotificationsCount());
                }
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                invalidateOptionsMenu();
                syncState();

                if (drawerView.equals(notificationsMenu)) {
                    notificationsAdapter.markAllNotificationsAsRed();
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawerView.equals(notificationsMenu))
                    return;

                super.onDrawerSlide(drawerView, slideOffset);
            }

        };
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        drawerView = (RecyclerView) findViewById(R.id.navigatorRecyclerView);
        drawerView.setLayoutManager(new LinearLayoutManager(this));

        OnDrawerSelectedPositionChangedListener onDrawerSelectedPositionChangedListener = new OnDrawerSelectedPositionChangedListener() {
            @Override
            public void onPositionChanged(final int newPosition, int previousPosition) {
                if (newPosition != previousPosition) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DrawerUtilities.handlePositionChanged(MainActivity.this, newPosition, drawerRows[newPosition]);
                            drawerView.scrollToPosition(newPosition);
                        }
                    }, DRAWER_TOGGLE_ANIMATION_DURATION);
                } else if (newPosition == drawerRows.length - 1) {
                    DrawerUtilities.handlePositionChanged(MainActivity.this, newPosition, drawerRows[newPosition]);
                    drawerView.scrollToPosition(newPosition);
                }
            }
        };
        drawerView.setAdapter(drawerAdapter = new DrawerAdapter(onDrawerSelectedPositionChangedListener));
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private OnDrawerSelectedPositionChangedListener onDrawerSelectedPositionChangedListener;

        public DrawerAdapter(OnDrawerSelectedPositionChangedListener onDrawerSelectedPositionChangedListener) {
            this.onDrawerSelectedPositionChangedListener = onDrawerSelectedPositionChangedListener;
            selectedPosition = DrawerUtilities.DrawerRow.COMMUNITY.ordinal();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (DrawerUtilities.DrawerRow.values()[viewType]) {

                case PROFILE:
                    return new HeaderViewHolder(getLayoutInflater().inflate(R.layout.drawer_header, parent, false));
                case LINE1:
                case LINE2:
                case LINE3:
                    return new LineViewHolder(getLayoutInflater().inflate(R.layout.row_line, parent, false));
                default:
                    return new RowViewHolder(getLayoutInflater().inflate(R.layout.drawer_row, parent, false));
            }

        }


        @Override
        public int getItemViewType(int position) {

            return DrawerUtilities.DrawerRow.values()[position].ordinal();
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            switch (DrawerUtilities.DrawerRow.values()[position]) {
                case PROFILE:
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    ImageView profilePic = (ImageView) headerViewHolder.itemView.findViewById(R.id.profilePic);

                    CurrentUserPhotoObject profilePhoto = CurrentUserPhotosDatasource.getInstance(profilePic.getContext()).getProfilePhoto();

                    if (profilePhoto != null)
                        Glide.with(profilePic.getContext()).load(profilePhoto.square_thumb).error(R.drawable.dummy_img).into(profilePic);

                    TextView drawerUserName = (TextView) headerViewHolder.itemView.findViewById(R.id.drawerUserName);
                    CurrentUser currentUser = CurrentUserDatasource.getInstance(getApplicationContext()).getCurrentUserInfo();
                    if (currentUser == null)
                        return;
                    drawerUserName.setText(currentUser.name);

                    headerViewHolder.itemView.setOnTouchListener(new MyOnTouchListener(headerViewHolder, position));
                    headerViewHolder.itemView.setSelected(selectedPosition == position);

                    if (headerViewHolder.itemView.isSelected()) {
                        selectedView = headerViewHolder.itemView;
                        headerViewHolder.itemView.setBackgroundResource(R.color.drawer_selected);
                    } else {
                        headerViewHolder.itemView.setBackgroundResource(android.R.color.transparent);
                    }
                    break;
                case LINE1:
                case LINE2:
                case LINE3:
                    break;
                default:
                    final RowViewHolder rowViewHolder = (RowViewHolder) holder;

                    BadgesObject badgesObject = BadgesDatasource.getInstance(MainActivity.this).getBadges();

                    if (badgesObject != null) {
                        if (DrawerUtilities.DrawerRow.values()[position] == DrawerUtilities.DrawerRow.MESSAGES) {
                            rowViewHolder.drawerNumberOfNewContainer.setVisibility(badgesObject.messagesBadge == 0 ? View.GONE : View.VISIBLE);
                            rowViewHolder.drawerNumberOfNewsTextView.setText(String.valueOf(badgesObject.messagesBadge));
                        } else if (DrawerUtilities.DrawerRow.values()[position] == DrawerUtilities.DrawerRow.PROFILE_VISITORS) {
                            rowViewHolder.drawerNumberOfNewContainer.setVisibility(badgesObject.visitorsBadge == 0 ? View.GONE : View.VISIBLE);
                            rowViewHolder.drawerNumberOfNewsTextView.setText(String.valueOf(badgesObject.visitorsBadge));
                        } else if (DrawerUtilities.DrawerRow.values()[position] == DrawerUtilities.DrawerRow.FLAMES_IGNITED) {
                            rowViewHolder.drawerNumberOfNewContainer.setVisibility(badgesObject.flamesBadge == 0 ? View.GONE : View.VISIBLE);
                            rowViewHolder.drawerNumberOfNewsTextView.setText(String.valueOf(badgesObject.flamesBadge));
                        }/* else if (DrawerUtilities.DrawerRow.values()[position] == DrawerUtilities.DrawerRow.HOT_MATCHES) {
                            rowViewHolder.drawerNumberOfNewContainer.setVisibility(badgesObject.matchesBadge == 0 ? View.GONE : View.VISIBLE);
                            rowViewHolder.drawerNumberOfNewsTextView.setText(String.valueOf(badgesObject.matchesBadge));
                        } */else if (DrawerUtilities.DrawerRow.values()[position] == DrawerUtilities.DrawerRow.FAVORITES) {
                            rowViewHolder.drawerNumberOfNewContainer.setVisibility(badgesObject.favouritedBadge == 0 ? View.GONE : View.VISIBLE);
                            rowViewHolder.drawerNumberOfNewsTextView.setText(String.valueOf(badgesObject.favouritedBadge));
                        } else {
                            rowViewHolder.drawerNumberOfNewContainer.setVisibility(View.GONE);
                        }
                    }

                    rowViewHolder.drawerRowIcon.setImageResource(DrawerUtilities.DRAWER_RESOURCES.get(DrawerUtilities.DrawerRow.values()[position]));
                    rowViewHolder.drawerRowTextView.setText(drawerRows[position]);
                    rowViewHolder.itemView.setSelected(selectedPosition == position);

                    if (rowViewHolder.itemView.isSelected()) {
                        selectedView = rowViewHolder.itemView;
                        rowViewHolder.itemView.setBackgroundResource(R.color.drawer_selected);
                    } else {
                        rowViewHolder.itemView.setBackgroundResource(android.R.color.transparent);
                    }
                    rowViewHolder.itemView.setOnTouchListener(new MyOnTouchListener(rowViewHolder, position));

            }
        }

        @Override
        public int getItemCount() {
            return drawerRows.length;
        }

        private class RowViewHolder extends RecyclerView.ViewHolder {

            public View drawerNumberOfNewContainer;
            public TextView drawerNumberOfNewsTextView;
            public ImageView drawerRowIcon;
            public TextView drawerRowTextView;

            public RowViewHolder(View itemView) {
                super(itemView);
                drawerNumberOfNewContainer = itemView.findViewById(R.id.drawerNumberOfNewContainer);
                drawerNumberOfNewsTextView = (TextView) itemView.findViewById(R.id.drawerNumberOfNewsTextView);
                drawerRowIcon = (ImageView) itemView.findViewById(R.id.drawerRowIcon);
                drawerRowTextView = (TextView) itemView.findViewById(R.id.drawerRowTextView);
            }
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {

            public HeaderViewHolder(android.view.View itemView) {
                super(itemView);
            }
        }

        private class LineViewHolder extends RecyclerView.ViewHolder {

            public LineViewHolder(android.view.View itemView) {
                super(itemView);
            }
        }

        private class MyOnTouchListener implements android.view.View.OnTouchListener {

            RecyclerView.ViewHolder viewHolder;
            private int position;

            public MyOnTouchListener(RecyclerView.ViewHolder viewHolder, int position) {
                this.viewHolder = viewHolder;
                this.position = position;
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (MotionEvent.ACTION_DOWN == event.getAction()) {

                    viewHolder.itemView.setBackgroundResource(R.color.drawer_selected);

                } else if (MotionEvent.ACTION_UP == event.getAction()) {

                    if (position != selectedPosition) {
                        selectedView.setBackgroundResource(android.R.color.transparent);
                    }

                    final int previousPosition = selectedPosition;
                    selectedPosition = position;
                    selectedView = v;
                    notifyItemChanged(selectedPosition);

                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            mDrawerLayout.closeDrawers();
                            onDrawerSelectedPositionChangedListener.onPositionChanged(selectedPosition, previousPosition);
                        }
                    });

                } else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
                    if (position != selectedPosition)
                        viewHolder.itemView.setBackgroundResource(android.R.color.transparent);
                }

                return true;
            }
        }
    }

    public interface OnDrawerSelectedPositionChangedListener {
        void onPositionChanged(int newPosition, int previousPosition);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedPosition", selectedPosition);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(changeFragmentAboveMenu, new IntentFilter(BROADCAST_CHANGE_FRAGMENT));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(changeTitlebaTitleReceiver, new IntentFilter(PriveTalkConstants.CHANGE_ACTIONBAR_TITLE));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(switchFragmentOnMenuProgramatically, new IntentFilter(BROADCAST_CHANGE_DRAWER_FRAGMENT));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(logOut, new IntentFilter(BROADCAST_LOG_OUT));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(profilePhotoUploadedReceiver, new IntentFilter(PriveTalkConstants.BROADCAST_NEW_PHOTO_UPLOADED));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(utilitiesReceiver, new IntentFilter((BROADCAST_UTILITY_ACTION)));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(changeNotificationIconToText, new IntentFilter((BROADCAST_CHANGE_NOTIFICATION_ICON)));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(configurationsScoresDownloadedReceiver, new IntentFilter(PriveTalkConstants.BROADCAST_CONFIGURATIONS_SCORES_DOWNLOADED));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(profilePhotosReceiver, new IntentFilter(PriveTalkConstants.BROADCAST_PHOTOS_DOWNLOADED));


    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(profilePhotosReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(configurationsScoresDownloadedReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(changeFragmentAboveMenu);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(switchFragmentOnMenuProgramatically);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(changeTitlebaTitleReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(logOut);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(profilePhotoUploadedReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(utilitiesReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(changeNotificationIconToText);

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    private BroadcastReceiver changeFragmentAboveMenu = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            changeFragmentAboveMenu(intent);

        }
    };

    private void changeFragmentAboveMenu(Intent intent) {
        setDrawerState(false);

        int fragmentId = intent.getIntExtra(FRAGMENT_TO_CHANGE, -1);

        if (fragmentId >= 0) {

            Fragment fragment = null;

            switch (fragmentId) {
                case PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID:
                    fragment = new BoostPopularityFragment();
                    break;
                case PriveTalkConstants.PERSONALEDIT_FRAGMENT_ID:
                    fragment = new PersonalInfoEditParentFragment();
                    break;
                case PriveTalkConstants.PERSONALEDIT_INFO_PAGER_FRAGMENTPARENT_ID:
                    fragment = new PersonalInfoEditPagerFragment();
                    break;
                case PriveTalkConstants.SEARCH_FILTER_FRAGMENT_ID:
                    fragment = new SearchFilterFragment();
                    break;
                case PriveTalkConstants.ROYAL_USER_PLANS_FRAGMENT_ID:
                    fragment = new RoyalUserPlansFragment();
                    break;
                case PriveTalkConstants.ADD_MORE_PT_COINS_FRAGMENT:
                    fragment = new AddMoreCoinsUseFragment();
                    break;
                case PriveTalkConstants.ADD_MORE_COINS_USE_FRAGMENT:
                    fragment = new AddMoreCoinsUseFragment();
                    break;
                case PriveTalkConstants.PERSONALEDIT_ACTIVITIES_PAGER_FRAGMENTPARENT_ID:
                    fragment = new PersonalInfoActivitiesPager();
                    break;
                case PriveTalkConstants.CHAT_FRAGMENT:
                    fragment = new ChatFragments();
                    if (getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder) instanceof ChatFragments) {
                        getSupportFragmentManager().popBackStack();
                    }
                    break;
                case PriveTalkConstants.FULL_SCREEN_PICTURES_FRAGMENT:
                    fragment = new FullScreenMyPicturesFragment();
                    break;
                case PriveTalkConstants.OTHER_USER_PROFILE_INFO:
                    fragment = new OtherUsersProfileInfoFragment();
                    break;
                case PriveTalkConstants.OTHER_USER_FULL_SCREEN_PHOTOS:
                    fragment = new FullScreenOtherUsersFragment();
                    break;
                case PriveTalkConstants.ROYAL_USER_BENEFITS_FRAGMENT_ID:
                    fragment = new BeARoyalUserFragment();
                    break;
                case PriveTalkConstants.ABOUT_ME_FRAGMENT_ID:
                    fragment = new AboutMeEditFragment();
                    break;
                case PriveTalkConstants.MAP_FRAGMENT_ID:
                    fragment = new PriveTalkMapFragment();
                    break;
                case PriveTalkConstants.SETTINGS_FRAGMENT:
                    fragment = new SettingsFragment();
                    break;
                case PriveTalkConstants.PROFILE_FRAGMENT:
                    fragment = new ProfileFragment();
                    break;
                default:
                    fragment = new Fragment();
            }

            Bundle bundle = intent.getExtras();

            for (String key : bundle.keySet()) {
            }

            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (intent.getBooleanExtra(ADD_TO_BACKSTUCK, false))
                fragmentTransaction.addToBackStack(null);

            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

            fragmentTransaction.replace(R.id.fragment_placeholder, fragment);
            fragmentTransaction.commit();

        }
    }

    @Override
    public void onBackPressed() {

        if (aboutDialog != null && aboutDialog.isVisible) {
            aboutDialog.dismiss();
            return;
        }

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder) instanceof PersonalInfoEditParentFragment
                || getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder) instanceof AboutMeEditFragment) {

            if (PersonalInfoEditParentFragment.infoChanged || AboutMeEditFragment.infochanged) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.are_you_sure));
                builder.setMessage(getString(R.string.will_be_lost));
                builder.setPositiveButton(getString(R.string.yes_string), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                            setDrawerState(true);
                        }
                        MainActivity.super.onBackPressed();
                    }
                });
                builder.setNegativeButton(getString(R.string.no_string), null);
                builder.create().show();
                return;
            }
        }

        if (logoutDialog != null) {
            logoutDialog.dismiss();
            logoutDialog = null;
            return;
        }
        KeyboardUtilities.closeKeyboard(this, mDrawerLayout);
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            handleBackPress();
        } else if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            setDrawerState(getSupportFragmentManager().getBackStackEntryCount() == 1);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void handleBackPress() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PriveTalkConstants.KEY_CURRENT_MAIN_FRAGMENT);

        if (fragment instanceof CommunityFragment)
            super.onBackPressed();
        else
            showDrawerFragment(DrawerUtilities.DrawerRow.COMMUNITY.ordinal());
    }


    private void showDrawerFragment(final int whichFragment) {
        drawerView.scrollToPosition(whichFragment);
        drawerView.post(new Runnable() {
            @Override
            public void run() {
                drawerView.findViewHolderForLayoutPosition(whichFragment).itemView.setBackgroundResource(R.color.drawer_selected);

                if (whichFragment != selectedPosition) {
                    selectedView.setBackgroundResource(android.R.color.transparent);
                }

                selectedPosition = whichFragment;
                selectedView = drawerView.findViewHolderForAdapterPosition(whichFragment).itemView;
                drawerAdapter.notifyItemChanged(selectedPosition);

                drawerView.findViewHolderForAdapterPosition(whichFragment).itemView.post(new Runnable() {
                    @Override
                    public void run() {
                        mDrawerLayout.closeDrawers();
                        DrawerUtilities.handlePositionChanged(MainActivity.this, selectedPosition, drawerRows[selectedPosition]);

                    }
                });
            }
        });
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
        if (keyCode == KeyEvent.KEYCODE_BACK && (f instanceof ChatFragments)) {
            Intent intent = new Intent(ChatFragments.BACK_BUTTON_PRESSED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            return false;
        }
//        else if (keyCode == KeyEvent.KEYCODE_BACK && (f instanceof SettingsFragment)) {
//            Intent intent = new Intent(SettingsFragment.SETTINGS_FRAGMENT_BACK_PRESSED);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//            return false;
//        }
        return super.onKeyUp(keyCode, event);
    }


    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.syncState();

        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            actionBarDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.syncState();
        }
    }


    private void setMenuIcon(int count) {

        menuIcon = getLayoutInflater().inflate(R.layout.menu_icon, null);
        if (count > 0) {
            ((PriveTalkTextView) menuIcon.findViewById(R.id.notificationCount)).setText(String.valueOf(count));
            menuIcon.findViewById(R.id.notificationCount).setVisibility(View.VISIBLE);
        } else
            menuIcon.findViewById(R.id.notificationCount).setVisibility(View.GONE);

        menuIcon.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDrawerLayout.addView(menuIcon);
        menuIcon.setVisibility(View.INVISIBLE);

        menuIcon.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = new BitmapDrawable(getResources(), BitmapUtilities.getFixedSizeBitmapFromView(menuIcon,
                        getResources().getDimensionPixelSize(R.dimen.menu_icon), getResources().getDimensionPixelSize(R.dimen.menu_icon)));

                if (notificationIcon != null)
                    notificationIcon.setIcon(d);

                mDrawerLayout.removeView(menuIcon);

            }
        });
    }


    private void showNoNetworkDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(R.string.please_check_your_internet_connection)
                .setCancelable(false)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!PriveTalkUtilities.isNetworkConnected())
                            showNoNetworkDialog();
                        else {
                            startActivity(new Intent(MainActivity.this, LauncherActivity.class));
                            finish();
                        }
                    }
                }).show();
    }

    private void setMessageIcon() {
        messageIcon = getLayoutInflater().inflate(R.layout.new_message_icon, null);

        messageIcon.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDrawerLayout.addView(messageIcon);
        messageIcon.setVisibility(View.INVISIBLE);

        menuIcon.post(new Runnable() {
            @Override
            public void run() {
                Drawable d = new BitmapDrawable(getResources(),
                        BitmapUtilities.getFixedSizeBitmapFromView(messageIcon, getResources().getDimensionPixelSize(R.dimen.menu_icon), getResources().getDimensionPixelSize(R.dimen.menu_icon)));
                if (newMessageIcon != null) newMessageIcon.setIcon(d);
                mDrawerLayout.removeView(messageIcon);
            }
        });
    }


    //if reward for sharing already claimed hide views (coin icon & claim text)
    private void checkIfRewardClaimed() {

        Log.d("testing50", "checkIfRewardClaimed " + getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PriveTalkConstants.CLAIM_REWARD, false));

        if (getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PriveTalkConstants.CLAIM_REWARD, false)) {
            findViewById(R.id.earnPoints).setVisibility(View.GONE);
            findViewById(R.id.earnPointsImg).setVisibility(View.GONE);
        } else {
            findViewById(R.id.earnPoints).setVisibility(View.VISIBLE);
            findViewById(R.id.earnPointsImg).setVisibility(View.VISIBLE);
        }
    }


    //    //initialize in app purchases to read user status
    private void initIabHelber() {
        mHelper = new IabHelper(PriveTalkApplication.getInstance(), PriveTalkConstants.base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);

        if (mHelper.mSetupDone) {
            mHelper.queryInventoryAsync(true, PriveTalkConstants.PLANS, mGotInventoryListener);
        } else {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess() || mHelper == null) {
                        return;
                    }
                    mHelper.queryInventoryAsync(true, PriveTalkConstants.PLANS, mGotInventoryListener);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHARE_APP_REQUEST) {
            if (!getSharedPreferences(PriveTalkConstants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PriveTalkConstants.CLAIM_REWARD, false)) {
                PriveTalkUtilities.postJsonObject(Links.CLAIM_REWARD);
            }
        } else if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
        super.onDestroy();
    }

}
