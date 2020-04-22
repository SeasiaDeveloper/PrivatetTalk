package com.privetalk.app.utilities;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.percentlayout.widget.PercentLayoutHelper;
import androidx.percentlayout.widget.PercentRelativeLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.ConfigurationScoreDatasource;
import com.privetalk.app.database.objects.ConfigurationScore;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.UserObject;

/**
 * Created by zeniosagapiou on 25/02/16.
 */
public class ProfileAwardsContainer extends FrameLayout {

    public ImageView profileAwardsRowIcon;
    public PriveTalkTextView profileAwardsTitle;
    public View awardsQuestionMark;
    public PriveTalkTextView hiddenDescriptionView;
    public View expandableView;
    public ImageView profileAwardsProgressBar;

    public ViewGroup bottomHiddenViewContainer;
    public View expandableBottomView;

    public PriveTalkTextView progressbarFirstColumnText;
    public PriveTalkTextView progressbarSecondColumnText;
    public PriveTalkTextView progressbarThirdColumnText;
    private View progressbarFirstColumnPercentage;
    private View progressbarSecondColumnPercentage;
    private View progressbarThirdColumnPercentage;

    public PriveTalkTextView profileAwardsFreeCoinsText;
    public PriveTalkTextView freeCoinsFirstcolumn;
    public PriveTalkTextView freeCoinsSecondColumn;
    public PriveTalkTextView freeCoinsThirdColumn;
    private View freeCoinsFirstcolumnPercentage;
    private View freeCoinsSecondColumnPercentage;
    private View freeCoinsThirdColumnPercentage;

    public PriveTalkTextView profileAwardsBonusText;
    public PriveTalkTextView profileAwardsPercentageFirstColumn;
    public PriveTalkTextView profileAwardsPercentageSecondColumn;
    public PriveTalkTextView profileAwardsPercentageThirdColumn;
    private View profileAwardsPercentageFirstColumnPercentage;
    private View profileAwardsPercentageSecondColumnPercentage;
    private View profileAwardsPercentageThirdColumnPercentage;

    public PriveTalkTextView profileAwardsRightCircleText;
    private boolean expanded;
    private PriveTalkTextView profileAwardsText;
    private View awardsPlusSign;


    public ProfileAwardsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ProfileAwardsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        doInflate(null);
    }

    private void doInflate(TypedArray a) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_weekly_awards_copy, ProfileAwardsContainer.this, false);
        addView(view);


        setClipChildren(false);
        setClipToPadding(false);

        hiddenDescriptionView = (PriveTalkTextView) view.findViewById(R.id.descriptionText);
        expandableView = view.findViewById(R.id.upperExpandableView);
        bottomHiddenViewContainer = (ViewGroup) view.findViewById(R.id.awardsFreeCoinsContainer);

        profileAwardsProgressBar = (ImageView) view.findViewById(R.id.profileAwardsPercentageBar);
        profileAwardsRightCircleText = (PriveTalkTextView) view.findViewById(R.id.profileAwardsRightCircleText);

        progressbarFirstColumnText = (PriveTalkTextView) view.findViewById(R.id.progressBarFirstColumnText);
        progressbarSecondColumnText = (PriveTalkTextView) view.findViewById(R.id.progressBarSecondColumnText);
        progressbarThirdColumnText = (PriveTalkTextView) view.findViewById(R.id.progressBarThirdColumnText);

        freeCoinsFirstcolumn = (PriveTalkTextView) view.findViewById(R.id.freeCoinsFirstColumn);
        freeCoinsSecondColumn = (PriveTalkTextView) view.findViewById(R.id.freeCoinsSecondColumn);
        freeCoinsThirdColumn = (PriveTalkTextView) view.findViewById(R.id.freeCoinsThirdColumn);

        profileAwardsPercentageFirstColumn = (PriveTalkTextView) view.findViewById(R.id.profileAwardsPercentageFirstColumn);
        profileAwardsPercentageSecondColumn = (PriveTalkTextView) view.findViewById(R.id.profileAwardsPercentageSecondColumn);
        profileAwardsPercentageThirdColumn = (PriveTalkTextView) view.findViewById(R.id.profileAwardsPercentageThirdColumn);

        profileAwardsTitle = (PriveTalkTextView) view.findViewById(R.id.profileAwardsTitle);
        profileAwardsText = (PriveTalkTextView) view.findViewById(R.id.profileAwardsText);
        profileAwardsRowIcon = (ImageView) view.findViewById(R.id.profileAwardsRowIcon);

        profileAwardsPercentageFirstColumnPercentage = view.findViewById(R.id.profileAwardsPercentageFirstColumnPercentage);
        profileAwardsPercentageSecondColumnPercentage = view.findViewById(R.id.profileAwardsPercentageSecondColumnPercentage);
        profileAwardsPercentageThirdColumnPercentage = view.findViewById(R.id.profileAwardsPercentageThirdColumnPercentage);

        freeCoinsFirstcolumnPercentage = view.findViewById(R.id.freeCoinsFirstColumnPercentage);
        freeCoinsSecondColumnPercentage = view.findViewById(R.id.freeCoinsSecondColumnPercentage);
        freeCoinsThirdColumnPercentage = view.findViewById(R.id.freeCoinsThirdColumnPercentage);

        progressbarFirstColumnPercentage = view.findViewById(R.id.progressBarFirstColumnTextPercentage);
        progressbarSecondColumnPercentage = view.findViewById(R.id.progressBarSecondColumnTextPercentage);
        progressbarThirdColumnPercentage = view.findViewById(R.id.progressBarThirdColumnTextPercentage);

        awardsQuestionMark = view.findViewById(R.id.awardsQuestionmark);

        awardsPlusSign = view.findViewById(R.id.awardsPlusSign);
        awardsPlusSign.setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {
                PriveTalkUtilities.changeFragment(getContext(), true, PriveTalkConstants.BOOST_POPULARITY_FRAGMENT_ID);
            }
        });

        expandableBottomView = view.findViewById(R.id.bottomExpandableView);

        setOnTouchListener(new FadeOnTouchListener() {
            @Override
            public void onClick(View view, MotionEvent event) {

                if (expanded) {


                    expanded = false;

//                    hiddenDescriptionView.setAlpha(0);
                    ObjectAnimator oa = ObjectAnimator.ofFloat(hiddenDescriptionView, View.ALPHA, 0).setDuration(250);

                    final ViewGroup.LayoutParams layoutParams = expandableView.getLayoutParams();
                    final ViewGroup.LayoutParams bottomLayoutParams = expandableBottomView.getLayoutParams();

                    oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        float calculatedSize = hiddenDescriptionView.getMeasuredHeight();
                        float getCalculatedSizeBottom = getContext().getResources().getDimensionPixelSize(R.dimen.hidden_view_height);

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            float animProgress = (float) animation.getAnimatedValue();

                            layoutParams.height = (int) (calculatedSize * (animProgress));
                            bottomLayoutParams.height = (int) (getCalculatedSizeBottom * (animProgress));

                            expandableView.setLayoutParams(layoutParams);
                            expandableBottomView.setLayoutParams(bottomLayoutParams);

                            if (animProgress <= 0) {
                                hiddenDescriptionView.setVisibility(View.GONE);
                                bottomHiddenViewContainer.setVisibility(View.GONE);
                            }

                        }
                    });
                    oa.start();

                } else {
                    expanded = true;

                    hiddenDescriptionView.setVisibility(View.VISIBLE);
                    bottomHiddenViewContainer.setVisibility(View.VISIBLE);

                    int widthMeasureSpec = MeasureSpec.makeMeasureSpec(expandableView.getWidth(), MeasureSpec.EXACTLY);

                    hiddenDescriptionView.measure(widthMeasureSpec, ViewGroup.LayoutParams.WRAP_CONTENT);

                    final ViewGroup.LayoutParams layoutParams = expandableView.getLayoutParams();
                    final ViewGroup.LayoutParams bottomLayoutParams = expandableBottomView.getLayoutParams();

                    hiddenDescriptionView.setAlpha(0);
                    ObjectAnimator oa = ObjectAnimator.ofFloat(hiddenDescriptionView, View.ALPHA, 1).setDuration(250);

                    oa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        float calculatedSize = hiddenDescriptionView.getMeasuredHeight();
                        float getCalculatedSizeBottom = getContext().getResources().getDimensionPixelSize(R.dimen.hidden_view_height);

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {

                            float animProgress = (float) animation.getAnimatedValue();

                            layoutParams.height = (int) (calculatedSize * animProgress);
                            bottomLayoutParams.height = (int) (getCalculatedSizeBottom * animProgress);

                            expandableView.setLayoutParams(layoutParams);
                            expandableBottomView.setLayoutParams(bottomLayoutParams);

                        }
                    });
                    oa.start();
                }
                requestLayout();
            }
        });
    }

//    public void setInfoVisibility(boolean visible){
//        awardsQuestionMark.setVisibility(visible ? VISIBLE : INVISIBLE);
//
//    }

    public void assignValues(UserObject user, String type) {
        awardsQuestionMark.setVisibility(INVISIBLE);

        ConfigurationScore configurationScore = ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance()).getConfigurationScore(type);

        PercentRelativeLayout.LayoutParams layoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsProgressBar.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo percentLayoutInfo = layoutParams.getPercentLayoutInfo();
        this.setOnTouchListener(null);

        switch (type) {
            case "popular_score":
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.thisWeekProfileVisits / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.thisWeekProfileVisits));
                profileAwardsTitle.setText(getContext().getString(R.string.popular));
                profileAwardsText.setText(getContext().getString(R.string.visitors));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_popular);
                hiddenDescriptionView.setText(getContext().getString(R.string.howPopularYourprofileIs));
                break;
            case "friendly_score":
                double friendly_score = user.thisWeekReplyPercentage * 100;
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) friendly_score / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf((int) (friendly_score)) + "%");
                profileAwardsTitle.setText(getContext().getString(R.string.friendly));
                profileAwardsText.setText(getContext().getString(R.string.reply));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_friendly);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_often_reply));
                break;
            case "ice_breaker_score":
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.thisWeekConversations / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.thisWeekConversations));
                profileAwardsTitle.setText(getContext().getString(R.string.icebreaker));
                profileAwardsText.setText(getContext().getString(R.string.talks));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_ice_breaker);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_many_times_initiated_chat));
                break;
            case "impression_voter_score":
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.thisWeekVotesCasted / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.thisWeekVotesCasted));
                profileAwardsTitle.setText(getContext().getString(R.string.impression_voter));
                profileAwardsText.setText(getContext().getString(R.string.votes));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_impression_voter);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_many_times_casted_secret_vote));
                break;
            case "angel_score":
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.thisWeekPositiveVotes / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.thisWeekPositiveVotes));
                profileAwardsTitle.setText(getContext().getString(R.string.angel));
                profileAwardsText.setText(getContext().getString(R.string.positive_impressions));
                Glide.with(getContext()).load(R.drawable.angel_icon_new).into(profileAwardsRowIcon);
//                profileAwardsRowIcon.setImageResource(R.drawable.angel_icon);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_many_times_received_positive_vote));
                break;
        }

        if (configurationScore == null)
            return;

        progressbarFirstColumnText.setText(String.valueOf(configurationScore.benchmark1_value));
        progressbarSecondColumnText.setText(String.valueOf(configurationScore.benchmark2_value));
        progressbarThirdColumnText.setText(String.valueOf(configurationScore.benchmark3_value));

        freeCoinsFirstcolumn.setText(String.valueOf(configurationScore.benchmark1_coins) + "c");
        freeCoinsSecondColumn.setText(String.valueOf(configurationScore.benchmark2_coins) + "c");
        freeCoinsThirdColumn.setText(String.valueOf(configurationScore.benchmark3_coins) + "c");

        profileAwardsPercentageFirstColumn.setText(String.valueOf(configurationScore.benchmark1_purchase_bonus) + "%");
        profileAwardsPercentageSecondColumn.setText(String.valueOf(configurationScore.benchmark2_purchase_bonus) + "%");
        profileAwardsPercentageThirdColumn.setText(String.valueOf(configurationScore.benchmark3_purchase_bonus) + "%");

        float firstColumngPercentage = (float) configurationScore.benchmark1_value / (float) configurationScore.max;
        float secondColumnPercentage = (float) configurationScore.benchmark2_value / (float) configurationScore.max;
        float thirdColumnPercentage = (float) configurationScore.benchmark3_value / (float) configurationScore.max;

        PercentRelativeLayout.LayoutParams profileAwardsPercentageFirstColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsPercentageFirstColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo profileAwardsPercentageFirstColumnPercentagePercentLayoutInfo = profileAwardsPercentageFirstColumnPercentageLayoutParams.getPercentLayoutInfo();
        profileAwardsPercentageFirstColumnPercentagePercentLayoutInfo.widthPercent = firstColumngPercentage;

        PercentRelativeLayout.LayoutParams profileAwardsPercentageSecondColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsPercentageSecondColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo profileAwardsPercentageSecondColumnPercentagePercentLayoutInfo = profileAwardsPercentageSecondColumnPercentageLayoutParams.getPercentLayoutInfo();
        profileAwardsPercentageSecondColumnPercentagePercentLayoutInfo.widthPercent = secondColumnPercentage;

        PercentRelativeLayout.LayoutParams profileAwardsPercentageThirdColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsPercentageThirdColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo profileAwardsPercentageThirdColumnPercentagePercentLayoutInfo = profileAwardsPercentageThirdColumnPercentageLayoutParams.getPercentLayoutInfo();
        profileAwardsPercentageThirdColumnPercentagePercentLayoutInfo.widthPercent = thirdColumnPercentage;

        PercentRelativeLayout.LayoutParams freeCoinsFirstcolumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) freeCoinsFirstcolumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo freeCoinsFirstcolumnPercentagePercentLayoutInfo = freeCoinsFirstcolumnPercentageLayoutParams.getPercentLayoutInfo();
        freeCoinsFirstcolumnPercentagePercentLayoutInfo.widthPercent = firstColumngPercentage;

        PercentRelativeLayout.LayoutParams freeCoinsSecondColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) freeCoinsSecondColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo freeCoinsSecondColumnPercentagePercentLayoutInfo = freeCoinsSecondColumnPercentageLayoutParams.getPercentLayoutInfo();
        freeCoinsSecondColumnPercentagePercentLayoutInfo.widthPercent = secondColumnPercentage;

        PercentRelativeLayout.LayoutParams freeCoinsThirdColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) freeCoinsThirdColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo freeCoinsThirdColumnPercentagePercentLayoutInfo = freeCoinsThirdColumnPercentageLayoutParams.getPercentLayoutInfo();
        freeCoinsThirdColumnPercentagePercentLayoutInfo.widthPercent = thirdColumnPercentage;

        PercentRelativeLayout.LayoutParams progressbarFirstColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) progressbarFirstColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo progressbarFirstColumnPercentagePercentLayoutInfo = progressbarFirstColumnPercentageLayoutParams.getPercentLayoutInfo();
        progressbarFirstColumnPercentagePercentLayoutInfo.widthPercent = firstColumngPercentage;

        PercentRelativeLayout.LayoutParams progressbarSecondColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) progressbarSecondColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo progressbarSecondColumnPercentagePercentLayoutInfo = progressbarSecondColumnPercentageLayoutParams.getPercentLayoutInfo();
        progressbarSecondColumnPercentagePercentLayoutInfo.widthPercent = secondColumnPercentage;

        PercentRelativeLayout.LayoutParams progressbarThirdColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) progressbarThirdColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo progressbarThirdColumnPercentagePercentLayoutInfo = progressbarThirdColumnPercentageLayoutParams.getPercentLayoutInfo();
        progressbarThirdColumnPercentagePercentLayoutInfo.widthPercent = thirdColumnPercentage;
    }

    public void assignValues(CurrentUser user, String type) {

        awardsQuestionMark.setVisibility(VISIBLE);
        ConfigurationScore configurationScore = ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance()).getConfigurationScore(type);

        PercentRelativeLayout.LayoutParams layoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsProgressBar.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo percentLayoutInfo = layoutParams.getPercentLayoutInfo();

        switch (type) {
            case ConfigurationScoreDatasource.POPULAR:
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.this_week_profile_visits / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.this_week_profile_visits));
                profileAwardsTitle.setText(getContext().getString(R.string.popular) + ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance())
                        .getScrorePercentages(ConfigurationScoreDatasource.POPULAR, 0f, user.this_week_profile_visits));
                profileAwardsText.setText(getContext().getString(R.string.visitors));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_popular);
                awardsPlusSign.setVisibility(View.VISIBLE);
                hiddenDescriptionView.setText(getContext().getString(R.string.howPopularYourprofileIs));
                break;
            case ConfigurationScoreDatasource.FRIENDLY:
                double friendly_score = user.this_week_reply_percentage * 100;
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) friendly_score / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf((int) (friendly_score) + "%"));
                profileAwardsTitle.setText(getContext().getString(R.string.friendly)
                        + ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance())
                        .getScrorePercentages(ConfigurationScoreDatasource.FRIENDLY, (float)user.this_week_reply_percentage, 0));
                profileAwardsText.setText(getContext().getString(R.string.reply));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_friendly);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_often_reply));
                break;
            case ConfigurationScoreDatasource.ICE_BREAKER:
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.this_week_conversations / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.this_week_conversations));
                profileAwardsTitle.setText(getContext().getString(R.string.icebreaker)
                        + ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance())
                        .getScrorePercentages(ConfigurationScoreDatasource.ICE_BREAKER, 0f, user.this_week_conversations));
                profileAwardsText.setText(getContext().getString(R.string.talks));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_ice_breaker);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_many_times_initiated_chat));
                break;
            case ConfigurationScoreDatasource.IMPRESSION:
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.this_week_positive_votes / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.this_week_positive_votes));
                profileAwardsTitle.setText(getContext().getString(R.string.impression_voter)
                        + ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance())
                        .getScrorePercentages(ConfigurationScoreDatasource.IMPRESSION, 0f, user.this_week_votes_casted));
                profileAwardsText.setText(getContext().getString(R.string.votes));
                profileAwardsRowIcon.setImageResource(R.drawable.profile_weekly_awards_impression_voter);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_many_times_casted_secret_vote));
                break;
            case ConfigurationScoreDatasource.ANGEL:
                if (configurationScore != null)
                    percentLayoutInfo.widthPercent = (float) user.this_week_positive_votes / (float) configurationScore.max;
                profileAwardsRightCircleText.setText(String.valueOf(user.this_week_positive_votes));
                profileAwardsTitle.setText(getContext().getString(R.string.angel)
                        + ConfigurationScoreDatasource.getInstance(PriveTalkApplication.getInstance())
                        .getScrorePercentages(ConfigurationScoreDatasource.ANGEL, 0f, user.this_week_positive_votes));
                profileAwardsText.setText(getContext().getString(R.string.positive_impressions));
                Glide.with(getContext()).load(R.drawable.angel_icon_new).into(profileAwardsRowIcon);
//                profileAwardsRowIcon.setImageResource(R.drawable.angel_icon_new);
                hiddenDescriptionView.setText(getContext().getString(R.string.how_many_times_received_positive_vote));
                break;
        }

        if (configurationScore == null) {
            return;
        }

        progressbarFirstColumnText.setText(String.valueOf(configurationScore.benchmark1_value));
        progressbarSecondColumnText.setText(String.valueOf(configurationScore.benchmark2_value));
        progressbarThirdColumnText.setText(String.valueOf(configurationScore.benchmark3_value));

        freeCoinsFirstcolumn.setText(String.valueOf(configurationScore.benchmark1_coins) + "c");
        freeCoinsSecondColumn.setText(String.valueOf(configurationScore.benchmark2_coins) + "c");
        freeCoinsThirdColumn.setText(String.valueOf(configurationScore.benchmark3_coins) + "c");

        profileAwardsPercentageFirstColumn.setText(String.valueOf(configurationScore.benchmark1_purchase_bonus) + "%");
        profileAwardsPercentageSecondColumn.setText(String.valueOf(configurationScore.benchmark2_purchase_bonus) + "%");
        profileAwardsPercentageThirdColumn.setText(String.valueOf(configurationScore.benchmark3_purchase_bonus) + "%");


        float firstColumngPercentage = (float) configurationScore.benchmark1_value / (float) configurationScore.max;
        float secondColumnPercentage = (float) configurationScore.benchmark2_value / (float) configurationScore.max;
        float thirdColumnPercentage = (float) configurationScore.benchmark3_value / (float) configurationScore.max;

        PercentRelativeLayout.LayoutParams profileAwardsPercentageFirstColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsPercentageFirstColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo profileAwardsPercentageFirstColumnPercentagePercentLayoutInfo = profileAwardsPercentageFirstColumnPercentageLayoutParams.getPercentLayoutInfo();
        profileAwardsPercentageFirstColumnPercentagePercentLayoutInfo.widthPercent = firstColumngPercentage;

        PercentRelativeLayout.LayoutParams profileAwardsPercentageSecondColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsPercentageSecondColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo profileAwardsPercentageSecondColumnPercentagePercentLayoutInfo = profileAwardsPercentageSecondColumnPercentageLayoutParams.getPercentLayoutInfo();
        profileAwardsPercentageSecondColumnPercentagePercentLayoutInfo.widthPercent = secondColumnPercentage;

        PercentRelativeLayout.LayoutParams profileAwardsPercentageThirdColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) profileAwardsPercentageThirdColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo profileAwardsPercentageThirdColumnPercentagePercentLayoutInfo = profileAwardsPercentageThirdColumnPercentageLayoutParams.getPercentLayoutInfo();
        profileAwardsPercentageThirdColumnPercentagePercentLayoutInfo.widthPercent = thirdColumnPercentage;

        PercentRelativeLayout.LayoutParams freeCoinsFirstcolumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) freeCoinsFirstcolumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo freeCoinsFirstcolumnPercentagePercentLayoutInfo = freeCoinsFirstcolumnPercentageLayoutParams.getPercentLayoutInfo();
        freeCoinsFirstcolumnPercentagePercentLayoutInfo.widthPercent = firstColumngPercentage;

        PercentRelativeLayout.LayoutParams freeCoinsSecondColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) freeCoinsSecondColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo freeCoinsSecondColumnPercentagePercentLayoutInfo = freeCoinsSecondColumnPercentageLayoutParams.getPercentLayoutInfo();
        freeCoinsSecondColumnPercentagePercentLayoutInfo.widthPercent = secondColumnPercentage;

        PercentRelativeLayout.LayoutParams freeCoinsThirdColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) freeCoinsThirdColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo freeCoinsThirdColumnPercentagePercentLayoutInfo = freeCoinsThirdColumnPercentageLayoutParams.getPercentLayoutInfo();
        freeCoinsThirdColumnPercentagePercentLayoutInfo.widthPercent = thirdColumnPercentage;

        PercentRelativeLayout.LayoutParams progressbarFirstColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) progressbarFirstColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo progressbarFirstColumnPercentagePercentLayoutInfo = progressbarFirstColumnPercentageLayoutParams.getPercentLayoutInfo();
        progressbarFirstColumnPercentagePercentLayoutInfo.widthPercent = firstColumngPercentage;

        PercentRelativeLayout.LayoutParams progressbarSecondColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) progressbarSecondColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo progressbarSecondColumnPercentagePercentLayoutInfo = progressbarSecondColumnPercentageLayoutParams.getPercentLayoutInfo();
        progressbarSecondColumnPercentagePercentLayoutInfo.widthPercent = secondColumnPercentage;

        PercentRelativeLayout.LayoutParams progressbarThirdColumnPercentageLayoutParams = (PercentRelativeLayout.LayoutParams) progressbarThirdColumnPercentage.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo progressbarThirdColumnPercentagePercentLayoutInfo = progressbarThirdColumnPercentageLayoutParams.getPercentLayoutInfo();
        progressbarThirdColumnPercentagePercentLayoutInfo.widthPercent = thirdColumnPercentage;
    }


}
