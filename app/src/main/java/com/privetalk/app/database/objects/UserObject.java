package com.privetalk.app.database.objects;

import com.privetalk.app.PriveTalkApplication;
import com.privetalk.app.database.PriveTalkTables;
import com.privetalk.app.database.datasource.AttributesDatasource;
import com.privetalk.app.utilities.PriveTalkConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zachariashad on 22/02/16.
 */
public class UserObject implements Serializable {

    public int userID;
    public String name = "";
    public AttributesObject gender = new AttributesObject();
    public AttributesObject lookingFor = new AttributesObject();
    public long birthday;
    public int age;
    public String location = "";
    public boolean isOnline;
    public boolean photosVerified;
    public boolean socialVerified;
    public boolean royalUser;
    public String mainProfilePhoto = "";
    public Info info = new Info();
    public int thisWeekProfileVisits;
    public int thisWeekConversations;
    public double thisWeekReplyPercentage;
    public double lastWeekReplyPercentage;
    public int thisWeekVotesCasted;
    public int lastWeekVotesCasted;
    public int thisWeekPositiveVotes;
    public int lastWeekPositiveVotes;
    public int lastWeekPurchaseBonus;
    public int lastWeekFreeCoins;
    public String fbID = "";
    public String fbName = "";
    public boolean isFavorite;
    public ArrayList<LanguageObject> languagesList = new ArrayList<>();
    public FaithObject faith = new FaithObject();
    public ArrayList<ProfilePhoto> profilePhotosList = new ArrayList<>();
    public RealLocation realLocation = new RealLocation();
    public float hotnessPercentage;


    public HashMap<String, ArrayList<InterestObject>> interests = new HashMap<>();


    public UserObject() {
    }

    public UserObject(JSONObject obj) {
        this.languagesList = new ArrayList<>();
        this.profilePhotosList = new ArrayList<>();
        this.name = obj.optString("name");
        this.gender = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"), String.valueOf(obj.optInt("gender")));
        this.lookingFor = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("gender"), String.valueOf(obj.optInt("looking_for")));

        try {
            this.birthday = PriveTalkConstants.BIRTHDAY_FORMAT.parse(obj.optString("birthday")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        this.fbID = obj.optString("fb_id");
        this.age = obj.optInt("age");
        this.location = obj.optString("location");
        this.isOnline = obj.optBoolean("is_online");
        this.photosVerified = obj.optBoolean("photos_verified");
        this.socialVerified = obj.optBoolean("social_verified");
        this.royalUser = obj.optBoolean("royal_user");
        this.mainProfilePhoto = obj.optString("main_profile_photo");
        this.isFavorite = obj.optBoolean("is_favorite");

        JSONObject infoObj = obj.optJSONObject("info");
        if (infoObj != null)
            this.info = new Info(infoObj);


        JSONObject locationObj = obj.optJSONObject("real_location");
        if (locationObj != null)
            realLocation = new RealLocation(locationObj);

        this.thisWeekProfileVisits = obj.optInt("this_week_profile_visits");
        this.thisWeekConversations = obj.optInt("this_week_conversations");
        this.thisWeekReplyPercentage = obj.optDouble("this_week_reply_percentage");
        this.lastWeekReplyPercentage = obj.optDouble("last_week_reply_percentage");
        this.thisWeekVotesCasted = obj.optInt("this_week_votes_casted");
        this.lastWeekVotesCasted = obj.optInt("last_week_votes_casted");
        this.thisWeekPositiveVotes = obj.optInt("this_week_positive_votes");
        this.lastWeekPositiveVotes = obj.optInt("last_week_positives_votes");
        this.lastWeekPurchaseBonus = obj.optInt("last_week_purchase_bonus");
        this.lastWeekFreeCoins = obj.optInt("last_week_free_coins");
        this.fbID = obj.optString("fb_id");
        this.fbName = obj.optString("fb_username");

        String hotnessPec = obj.optString("hotness_percentage");
        this.hotnessPercentage = Float.parseFloat(hotnessPec.isEmpty() ? "0" : hotnessPec);

        JSONArray languagesArray = obj.optJSONArray("languages");

        for (int i = 0; i < languagesArray.length(); i++)
            this.languagesList.add(new LanguageObject(languagesArray.optJSONObject(i)));

        JSONObject faithObj = obj.optJSONObject("faith");
        if (faithObj != null)
            this.faith = new FaithObject(faithObj);

        this.interests = InterestObject.getInterestsListFromServer(obj.optJSONArray("interests"), false);

        JSONArray profilePhotosArray = obj.optJSONArray("profile_photos");

        if (obj.optJSONObject("main_profile_photo") != null)
            this.profilePhotosList.add(new ProfilePhoto(obj.optJSONObject("main_profile_photo")));

        int mainPicId = this.profilePhotosList.size() > 0 ? this.profilePhotosList.get(0).photoID : -1;

        for (int i = 0; i < profilePhotosArray.length(); i++) {
            ProfilePhoto photo = new ProfilePhoto(profilePhotosArray.optJSONObject(i));
            if (photo.photoID != mainPicId)
                this.profilePhotosList.add(photo);
        }
    }


    public class Info {
        public Info() {
        }

        public Info(JSONObject infoObj) {
            relationshipStatus = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("relationship_status"), String.valueOf(infoObj.optInt("relationship_status")));
            sexualityStatus = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("sexuality_status"), String.valueOf(infoObj.optInt("sexuality_status")));
            drinkinStatus = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("drinking_status"), String.valueOf(infoObj.optInt("drinking_status")));
            educationStatus = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("education_status"), String.valueOf(infoObj.optInt("education_status")));
            zodiac = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("zodiac"), String.valueOf(infoObj.optInt("zodiac")));
            height = infoObj.optInt("height");
            weight = infoObj.optInt("weight");
            smokingStatus = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("smoking_status"), String.valueOf(infoObj.optInt("smoking_status")));
            bodyType = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("body_type"), String.valueOf(infoObj.optInt("body_type")));
            hairColor = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("hair_color"), String.valueOf(infoObj.optInt("hair_color")));
            eyesColor = AttributesDatasource.getInstance(PriveTalkApplication.getInstance()).getSpecificAttribute(PriveTalkTables.AttributesTables.getTableString("eyes_color"), String.valueOf(infoObj.optInt("eyes_color")));
            work = infoObj.optString("work");
            if (work == null)
                work = "";
            about = infoObj.optString("about");
            if (about == null)
                about = "";
        }

        public AttributesObject relationshipStatus;
        public AttributesObject sexualityStatus;
        public AttributesObject smokingStatus;
        public AttributesObject drinkinStatus;
        public AttributesObject educationStatus;
        public AttributesObject zodiac;
        public int height;
        public int weight;
        public AttributesObject bodyType;
        public AttributesObject hairColor;
        public AttributesObject eyesColor;
        public String work;
        public String about;
    }


}
