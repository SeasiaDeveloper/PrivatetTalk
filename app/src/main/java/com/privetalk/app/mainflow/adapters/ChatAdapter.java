package com.privetalk.app.mainflow.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.privetalk.app.R;
import com.privetalk.app.database.datasource.ConversationsDatasource;
import com.privetalk.app.database.datasource.CurrentUserDatasource;
import com.privetalk.app.database.datasource.CurrentUserPhotosDatasource;
import com.privetalk.app.database.datasource.MessageDatasource;
import com.privetalk.app.database.objects.ConversationObject;
import com.privetalk.app.database.objects.CurrentUser;
import com.privetalk.app.database.objects.CurrentUserPhotoObject;
import com.privetalk.app.database.objects.MessageObject;
import com.privetalk.app.database.objects.UserObject;
import com.privetalk.app.mainflow.activities.FullScreenPicutreActivity;
import com.privetalk.app.mainflow.fragments.ChatFragments;
import com.privetalk.app.utilities.FadeOnTouchListener;
import com.privetalk.app.utilities.MyRoundedCornersTransformation;
import com.privetalk.app.utilities.PriveTalkConstants;
import com.privetalk.app.utilities.PriveTalkTextView;
import com.privetalk.app.utilities.PriveTalkUtilities;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_LAST_ROW = -10;
    private static final int VIEW_TYPE_COMMON_INTERESTS = -20;
    private static final int COMMON_INTERESTS = 0;

    public int commonFriendsCount;
    public int commonInterestsCount;

    public List<MessageObject> messageObjects;
    private int numberOfMessagesSent;
    private RecyclerView chatRecyclerView;
    private Activity mActivity;
    private Fragment mFragment;
    private int partnerID;
    private ConversationObject conversationObject;
    private CurrentUser currentUser;
    private CurrentUserPhotoObject profilePhoto;
    private UserObject otherUserObject;
    private String impressHer;
    private String impressHim;
    private String impress;
    public boolean canRefreshData;
    public long timeDifference;

    public ChatAdapter(RecyclerView chatRecyclerView, Fragment fragment, int partnerID) {

        this.chatRecyclerView = chatRecyclerView;
        this.mActivity = fragment.getActivity();
        mFragment = fragment;
        this.partnerID = partnerID;

        timeDifference = PriveTalkUtilities.getGlobalTimeDifference();

        currentUser = CurrentUserDatasource.getInstance(mActivity).getCurrentUserInfo();
        conversationObject = ConversationsDatasource.getInstance(mActivity).getConversationByUserId(partnerID);

        if (conversationObject == null) {
            conversationObject = new ConversationObject();
            conversationObject.partnerID = partnerID;
        }

       // profilePhoto = CurrentUserPhotosDatasource.getInstance(mActivity).getProfilePhoto();
        if (CurrentUserPhotosDatasource.getInstance(mActivity).checkProfilePic(mActivity)!=null) {
            profilePhoto = CurrentUserPhotosDatasource.getInstance(mActivity).checkProfilePic(mActivity);
        }

        messageObjects = MessageDatasource.getInstance(mActivity).getMessagesWithUserId(conversationObject.partnerID);
        numberOfMessagesSent = MessageDatasource.getInstance(mActivity).numberOfMessagesSent(conversationObject.partnerID);

        canRefreshData = true;

        impress = mActivity.getString(R.string.impress_with_gift);
        impressHim = mActivity.getString(R.string.impress_him_with_gift);
        impressHer = mActivity.getString(R.string.impress_her_with_gift);

    }

    public void refreshData(boolean viewNewest) {
        messageObjects.clear();
        List<MessageObject> messageObjectsCopyList = MessageDatasource.getInstance(mActivity).getMessagesWithUserId(partnerID);
        if (messageObjectsCopyList.size() != messageObjects.size()) {
            messageObjects.addAll(MessageDatasource.getInstance(mActivity).getMessagesWithUserId(partnerID));
            numberOfMessagesSent = MessageDatasource.getInstance(mActivity).numberOfMessagesSent(conversationObject.partnerID);
            notifyDataSetChanged();
        }
        if (viewNewest) chatRecyclerView.scrollToPosition(getItemCount() - 1);
    }

    public void setOtherUserObject(UserObject otherUserObject) {
        this.otherUserObject = otherUserObject;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEWTYPE_LAST_ROW) {
            return new MakePriorityMessageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_be_priority_message, parent, false));
        } else if (viewType == conversationObject.partnerID) {
            OtherUserHolder holder = new OtherUserHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chat_sender, parent, false));

            holder.profilePicture.setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(PriveTalkConstants.KEY_OTHER_USER_ID, conversationObject.partnerID);
                    PriveTalkUtilities.changeFragment(mActivity, true, PriveTalkConstants.OTHER_USER_PROFILE_INFO, bundle);
                }
            });
            return holder;
        } else if (viewType == currentUser.userID) {
            CurrentUserHolder holder = new CurrentUserHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_chat_receiver, parent, false));
            return holder;
        } else if (viewType == VIEW_TYPE_COMMON_INTERESTS)
            return new ViewHolder3(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_common_interests, parent, false));

        return new OtherUserHolder(new View(mActivity));

    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder3) {
            ViewHolder3 holder3 = (ViewHolder3) holder;
            holder3.commonFriendsCountOnly.setVisibility(commonFriendsCount == 0 ? View.INVISIBLE : View.VISIBLE);
            holder3.commonInterestCountOnly.setVisibility(commonInterestsCount == 0 ? View.INVISIBLE : View.VISIBLE);
            holder3.commonFriendsCountOnly.setText(String.valueOf(commonFriendsCount));
            holder3.commonInterestCountOnly.setText(String.valueOf(commonInterestsCount));
            holder3.commonInterestCountText.setText(String.valueOf(commonInterestsCount) + " " + mActivity.getString(R.string.common_interests));
            holder3.commonFriendsCountText.setText(String.valueOf(commonFriendsCount) + " " + mActivity.getString(R.string.common_facebook_friends));
            holder3.impessWithGiftText.setText(otherUserObject == null ? impress : otherUserObject.gender.value.equals("1") ? impressHim : impressHer);
            holder3.space.setVisibility(messageObjects.size() > 0 ? View.GONE : View.VISIBLE);
        } else {
            position = position - 1;

            if (holder instanceof MakePriorityMessageViewHolder) {
                MakePriorityMessageViewHolder holder1 = (MakePriorityMessageViewHolder) holder;
                holder1.itemView.setOnTouchListener(new FadeOnTouchListener() {
                    @Override
                    public void onClick(View view, MotionEvent event) {
                        PriveTalkUtilities.changeFragment(mActivity, true, PriveTalkConstants.ROYAL_USER_BENEFITS_FRAGMENT_ID);
                    }
                });
            } else if (holder instanceof OtherUserHolder) {

                OtherUserHolder partnerMessageHolder = (OtherUserHolder) holder;

                Glide.with(mActivity)
                        .load(conversationObject.partnerAvatarImg)
                        .error(R.drawable.dummy_img)
                        .bitmapTransform(new CropCircleTransformation(mActivity))
                        .into(partnerMessageHolder.profilePicture);

                ViewGroup.LayoutParams layoutParams = partnerMessageHolder.chatSenderMessageLayout.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                partnerMessageHolder.messagePicture.setOnTouchListener(null);

                partnerMessageHolder.handler.removeCallbacksAndMessages(null);

                long remainingTimeCopy = messageObjects.get(position).expiresOn == 0 ?
                        (System.currentTimeMillis() - timeDifference + messageObjects.get(position).lifespan * 1000) : messageObjects.get(position).expiresOn;

                if (remainingTimeCopy > 0)
                    ((OtherUserHolder) holder).messageTimePassed.setText(MessageObject.getExpirationTime(remainingTimeCopy - (System.currentTimeMillis() - timeDifference)));

                partnerMessageHolder.handler.post(new MessagesRunnable(holder, remainingTimeCopy));

                partnerMessageHolder.royalMessageIcon.setVisibility(conversationObject.isSenderRoyal ? View.VISIBLE : View.GONE);
                partnerMessageHolder.underSpace.setVisibility((position + 1 == messageObjects.size()) ? View.GONE : View.GONE);


                switch (messageObjects.get(position).mtype) {
                    case MessageObject.TYPE_MESSAGE:
                        partnerMessageHolder.txtMessage.setVisibility(View.VISIBLE);
                        partnerMessageHolder.messagePicture.setVisibility(View.GONE);
                        partnerMessageHolder.txtMessage.setText(messageObjects.get(position).data);
                        break;

                    case MessageObject.TYPE_IMAGE:
                        partnerMessageHolder.messagePicture.setImageDrawable(null);
                        partnerMessageHolder.txtMessage.setVisibility(View.GONE);
                        partnerMessageHolder.messagePicture.setVisibility(View.VISIBLE);
                        layoutParams.width = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);
                        layoutParams.height = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);

                        Glide.with(mActivity)
                                .load(messageObjects.get(position).attachmentData)
                                .error(R.drawable.dummy_img)
                                .transform(new MyRoundedCornersTransformation(mActivity, 20, 12))
                                .into(partnerMessageHolder.messagePicture);

                        partnerMessageHolder.messagePicture.setTag(R.id.position_tag, position);
                        partnerMessageHolder.messagePicture.setOnTouchListener(new FadeOnTouchListener() {

                            @Override
                            public void onClick(View view, MotionEvent event) {
                                Intent intent1 = new Intent(mActivity, FullScreenPicutreActivity.class);
                                intent1.putExtra(PriveTalkConstants.KEY_MESSAGE, messageObjects.get((int) view.getTag(R.id.position_tag)));
                                intent1.putExtra(PriveTalkConstants.PARTNER_ID, conversationObject.partnerID);
                                mFragment.startActivityForResult(intent1, ChatFragments.FULL_SCREEN_REQUEST);

                            }
                        });
                        break;

                    case MessageObject.TYPE_GIFT:
                        partnerMessageHolder.messagePicture.setImageDrawable(null);
                        partnerMessageHolder.txtMessage.setVisibility(View.GONE);
                        partnerMessageHolder.messagePicture.setVisibility(View.VISIBLE);
                        layoutParams.width = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);
                        layoutParams.height = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);
                        if (messageObjects.get(position).attachmentData != null && !messageObjects.get(position).attachmentData.isEmpty()) {
                            Glide.with(mActivity).load(messageObjects.get(position).attachmentData).bitmapTransform(new RoundedCornersTransformation(mActivity, 20, 0)).into(partnerMessageHolder.messagePicture);
                        }
                        break;
                }
                partnerMessageHolder.chatSenderMessageLayout.setLayoutParams(layoutParams);

            } else if (holder instanceof CurrentUserHolder) {


                CurrentUserHolder myMessageHolder = ((CurrentUserHolder) holder);

//                if (profilePhoto != null)
                    Glide.with(mActivity)
                            .load(profilePhoto == null ? R.drawable.dummy_img : profilePhoto.square_thumb)
                            .error(R.drawable.dummy_img)
                            .bitmapTransform(new CropCircleTransformation(mActivity))
                            .into(myMessageHolder.profilePicture);

                ViewGroup.LayoutParams layoutParams = myMessageHolder.chatSenderMessageLayout.getLayoutParams();
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;


                if (messageObjects.get(position).expiresOn > 0)
                    ((CurrentUserHolder) holder).messageTimePassed.setText(MessageObject.getExpirationTime(messageObjects.get(position).expiresOn - (System.currentTimeMillis() - timeDifference)));

                myMessageHolder.handler.removeCallbacksAndMessages(null);
                myMessageHolder.handler.post(new MessagesRunnable(holder, messageObjects.get(position).expiresOn));

                myMessageHolder.royalMessageIcon.setVisibility(currentUser.royal_user ? View.VISIBLE : View.GONE);
                myMessageHolder.messagePicture.setOnTouchListener(null);

                myMessageHolder.underSpace.setVisibility((position + 1 == messageObjects.size()) ? View.GONE : View.GONE);

                switch (messageObjects.get(position).mtype) {

                    case MessageObject.TYPE_MESSAGE:
                        myMessageHolder.txtMessage.setVisibility(View.VISIBLE);
                        myMessageHolder.messagePicture.setVisibility(View.GONE);
                        myMessageHolder.txtMessage.setText(messageObjects.get(position).data);
                        break;

                    case MessageObject.TYPE_IMAGE:
                        myMessageHolder.messagePicture.setImageDrawable(null);
                        myMessageHolder.txtMessage.setVisibility(View.GONE);
                        myMessageHolder.messagePicture.setVisibility(View.VISIBLE);
                        layoutParams.width = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);
                        layoutParams.height = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);

                        if (messageObjects.get(position).attachmentData != null && !messageObjects.get(position).attachmentData.isEmpty()) {
                            Glide.with(mActivity)
                                    .load(messageObjects.get(position).attachmentData)
                                    .error(R.drawable.dummy_img)
                                    .transform(new MyRoundedCornersTransformation(mActivity, 20, 12))
                                    .into(myMessageHolder.messagePicture);
                        }
                        myMessageHolder.messagePicture.setTag(R.id.position_tag, position);
                        myMessageHolder.messagePicture.setOnTouchListener(new FadeOnTouchListener() {

                            @Override
                            public void onClick(View view, MotionEvent event) {
                                Intent intent1 = new Intent(mActivity, FullScreenPicutreActivity.class);
                                intent1.putExtra(PriveTalkConstants.KEY_MESSAGE, messageObjects.get((int) view.getTag(R.id.position_tag)));
                                intent1.putExtra(PriveTalkConstants.PARTNER_ID, conversationObject.partnerID);
                                mFragment.startActivityForResult(intent1, ChatFragments.FULL_SCREEN_REQUEST);

                            }
                        });
                        break;

                    case MessageObject.TYPE_GIFT:
                        myMessageHolder.messagePicture.setImageDrawable(null);
                        myMessageHolder.txtMessage.setVisibility(View.GONE);
                        myMessageHolder.messagePicture.setVisibility(View.VISIBLE);
                        layoutParams.width = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);
                        layoutParams.height = mActivity.getResources().getDimensionPixelSize(R.dimen.chat_photo);
                        if (messageObjects.get(position).attachmentData != null && !messageObjects.get(position).attachmentData.isEmpty()) {
                            Glide.with(mActivity).load(messageObjects.get(position).attachmentData).bitmapTransform(new RoundedCornersTransformation(mActivity, 20, 0)).into(myMessageHolder.messagePicture);
                        }
                        break;
                }

                myMessageHolder.chatSenderMessageLayout.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return
                position == COMMON_INTERESTS ? VIEW_TYPE_COMMON_INTERESTS :

                        !currentUser.royal_user && position == messageObjects.size() + 1 && numberOfMessagesSent > 0 && numberOfMessagesSent < 2 ? VIEWTYPE_LAST_ROW :

                                messageObjects.get(position - 1).senderID;
    }

    @Override
    public int getItemCount() {
        return !currentUser.royal_user && numberOfMessagesSent > 0 && numberOfMessagesSent < 2 ? messageObjects.size() + 2 : messageObjects.size() + 1;
    }

    public class OtherUserHolder extends RecyclerView.ViewHolder {
        private final View rootView;
        private final ImageView profilePicture;
        private final ImageView messagePicture;
        private final PriveTalkTextView txtMessage;
        private final PriveTalkTextView messageTimePassed;
        private final android.os.Handler handler;
        private final View royalMessageIcon;
        private final View chatSenderMessageLayout;
        private final View underSpace;

        public OtherUserHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.messagePicture = (ImageView) rootView.findViewById(R.id.chatImageMessage);
            this.profilePicture = (ImageView) rootView.findViewById(R.id.chatSenderProfilePicture);
            this.txtMessage = (PriveTalkTextView) rootView.findViewById(R.id.chatTextMessage);
            this.messageTimePassed = (PriveTalkTextView) rootView.findViewById(R.id.messageTimePassed);
            this.handler = new android.os.Handler();
            this.chatSenderMessageLayout = rootView.findViewById(R.id.chatSenderMessageLayout);
            this.royalMessageIcon = rootView.findViewById(R.id.royalMessageIcon);
            this.underSpace = rootView.findViewById(R.id.underSpace);
        }
    }


    public class CurrentUserHolder extends RecyclerView.ViewHolder {

        private final View chatSenderMessageLayout;
        private final View rootView;
        private final ImageView profilePicture;
        private final ImageView messagePicture;
        private final PriveTalkTextView txtMessage;
        private final PriveTalkTextView messageTimePassed;
        private final android.os.Handler handler;
        private final View royalMessageIcon;
        private final View underSpace;

        public CurrentUserHolder(View itemView) {
            super(itemView);
            this.rootView = itemView;
            this.messagePicture = (ImageView) rootView.findViewById(R.id.chatImageMessage);
            this.profilePicture = (ImageView) rootView.findViewById(R.id.chatReceiverProfilePicture);
            this.txtMessage = (PriveTalkTextView) rootView.findViewById(R.id.chatTextMessage);
            this.messageTimePassed = (PriveTalkTextView) rootView.findViewById(R.id.messageTimePassed);
            this.handler = new android.os.Handler();
            this.chatSenderMessageLayout = rootView.findViewById(R.id.chatSenderMessageLayout);
            this.royalMessageIcon = rootView.findViewById(R.id.royalMessageIcon);
            this.underSpace = rootView.findViewById(R.id.underSpace);
        }
    }


    public class ViewHolder3 extends RecyclerView.ViewHolder {
        public PriveTalkTextView commonFriendsCountOnly;
        public PriveTalkTextView commonInterestCountOnly;
        public PriveTalkTextView commonFriendsCountText;
        public PriveTalkTextView commonInterestCountText;
        public PriveTalkTextView impessWithGiftText;
        public Space space;

        public ViewHolder3(View v) {
            super(v);
            commonFriendsCountOnly = (PriveTalkTextView) v.findViewById(R.id.commonFriendsCount);
            commonInterestCountOnly = (PriveTalkTextView) v.findViewById(R.id.commonInterestsCount);
            commonFriendsCountText = (PriveTalkTextView) v.findViewById(R.id.commonFriendsText);
            commonInterestCountText = (PriveTalkTextView) v.findViewById(R.id.commonInterestText);
            impessWithGiftText = (PriveTalkTextView) v.findViewById(R.id.impressWithGiftText);
            space = (Space) v.findViewById(R.id.space);

            v.findViewById(R.id.iceBrakerGift).setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Intent intent = new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER);
                    intent.putExtra(PriveTalkConstants.KEY_EVENT_TYPE, ChatFragments.SHOW_GIFTS);
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
                }
            });

            v.findViewById(R.id.chatViewCommonInterestsButton).setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Intent intent = new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER);
                    intent.putExtra(PriveTalkConstants.KEY_EVENT_TYPE, ChatFragments.COMMON_INTEREST_DIALOG);
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
                }
            });

            v.findViewById(R.id.chatViewCommonFriendsButton).setOnTouchListener(new FadeOnTouchListener() {
                @Override
                public void onClick(View view, MotionEvent event) {
                    Intent intent = new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER);
                    intent.putExtra(PriveTalkConstants.KEY_EVENT_TYPE, ChatFragments.COMMON_FRIENDS_DIALOG);
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
                }
            });

        }
    }


    private class MakePriorityMessageViewHolder extends RecyclerView.ViewHolder {
        public MakePriorityMessageViewHolder(View itemView) {
            super(itemView);
        }
    }


    public class MessagesRunnable implements Runnable {

        Handler handler;
        PriveTalkTextView textView;
        long remainingTime;

        public MessagesRunnable(RecyclerView.ViewHolder holder, long expirtationTime) {

            if (holder instanceof OtherUserHolder) {
                OtherUserHolder holder1 = (OtherUserHolder) holder;
                handler = holder1.handler;
                textView = holder1.messageTimePassed;
                remainingTime = expirtationTime;
            } else if (holder instanceof CurrentUserHolder) {
                CurrentUserHolder holder1 = (CurrentUserHolder) holder;
                handler = holder1.handler;
                textView = holder1.messageTimePassed;
                remainingTime = expirtationTime;
            }

        }

        @Override
        public void run() {

            if (remainingTime == 0) {
                textView.setVisibility(View.GONE);
                return;
            }
            if ((remainingTime - (System.currentTimeMillis() - timeDifference)) <= 0) {
                Intent intent = new Intent(ChatFragments.CHAT_HANDLE_EVENTS_RECEIVER);
                intent.putExtra("event_type", ChatFragments.MESSAGE_EXPIRED);
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
            } else {
                textView.setVisibility(View.VISIBLE);
                textView.setText(MessageObject.getExpirationTime(remainingTime - (System.currentTimeMillis() - timeDifference)));
                handler.postDelayed(this, 1000);
            }

        }
    }
}
