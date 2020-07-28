package com.mad.p03.np2020.routine.models;

import java.io.File;
import java.util.HashMap;

public class Achievement {


    public static final String TABLE_NAME = "achievement_task";

    /**
     * Primary key of the table
     */
    public static final String COLUMN_TASK_ID = "TASK_NAME";

    /**
     * Column name of the table
     */
    public static final String COLUMN_HOURS = "REQUIREMENT";
    public static final String COLUMN_FILENAME = "BADGE_FILENAME";
    public static final String COLUMN_TYPE = "TASK_TYPE";
    public static final String COLUMN_URL = "BADGE_URL";



    /**
     * The query needed to create a sql database
     * for the Task
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_TASK_ID + " TEXT,"
                    + COLUMN_HOURS + " INTEGER,"
                    + COLUMN_FILENAME + " TEXT, "
                    + COLUMN_TYPE + " TEXT,"
                    + COLUMN_URL + " TEXT,"
                    + "PRIMARY KEY ( " + COLUMN_TASK_ID + "," + COLUMN_TYPE + ")" + ")";

    public static final String SQL_DELETE_ENTRIES_ACHIEVEMENT =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    public Achievement(String stageNo, int requirement, String filename, int typeAchievement, String badgeUrl) {
        this.stageNo = stageNo;
        this.requirement = requirement;
        this.filename = filename;
        this.typeAchievement = typeAchievement;
        this.badgeUrl = badgeUrl;
    }

    public Achievement() {
    }

    public static String getAchievementName(int type){
        HashMap<Integer, String> achievementType = new HashMap<>();

        achievementType.put(0, "Hours");
        achievementType.put(1, "Cycle");
        achievementType.put(2, "Consecutive");

        return achievementType.get(type);
    }

    public String stageNo;

    public String getStageNo() {
        return stageNo;
    }

    public void setStageNo(String stageNo) {
        this.stageNo = stageNo;
    }

    public String getBadgeUrl() {
        return badgeUrl;
    }

    public void setBadgeUrl(String badgeUrl) {
        this.badgeUrl = badgeUrl;
    }

    public String badgeUrl;

    public int getRequirement() {
        return requirement;
    }

    public void setRequirement(int requirement) {
        this.requirement = requirement;
    }

    public int requirement;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String filename;

    public int getTypeAchievement() {
        return typeAchievement;
    }

    public void setTypeAchievement(int typeAchievement) {
        this.typeAchievement = typeAchievement;
    }

    public int typeAchievement;

    public File pathImg;

    public File getPathImg() {
        return pathImg;
    }

    public void setPathImg(File pathImg) {
        this.pathImg = pathImg;
    }
}
