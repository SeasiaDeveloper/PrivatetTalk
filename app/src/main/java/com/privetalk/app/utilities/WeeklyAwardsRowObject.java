package com.privetalk.app.utilities;

/**
 * Created by zeniosagapiou on 15/01/16.
 */
public class WeeklyAwardsRowObject {

    public int imageResourceId;
    public String title;
    public String titleCoins;
    public String titlePercentage;
    public String subtitle;
    public int maximumValue;
    public String firstInfo, secondInfo, thirdInfo;
    public int currentValue;
    public boolean isPercentage;

    public WeeklyAwardsRowObject(int imageResourceId, String title, String subtitle, String titleCoins, String titlePercentage,
                                  int maximumValue , int currentValue, boolean isPercentage){

        this.imageResourceId = imageResourceId;
        this.title = title;
        this.titleCoins = titleCoins;
        this.titlePercentage = titlePercentage;
        this.subtitle = subtitle;
        this.maximumValue = maximumValue;
        this.currentValue = currentValue;
        this.isPercentage = isPercentage;

        firstInfo = String.valueOf(maximumValue / 4);
        secondInfo = String.valueOf(maximumValue /2);
        thirdInfo = String.valueOf((int )(maximumValue * 0.75f));
    }

}
