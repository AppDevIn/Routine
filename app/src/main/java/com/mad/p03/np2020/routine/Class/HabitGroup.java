package com.mad.p03.np2020.routine.Class;

public class HabitGroup {

    public static final String TABLE_NAME = "habitGroups";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GROUP_NAME = "_name";

    public static final String CREATE_GROUPS_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_GROUP_NAME  + " TEXT)";

    public static final String DROP_GROUPS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    private long grp_id;
    private String grp_name;

    public HabitGroup(String grp_name) {
        this.grp_name = grp_name;
    }

    public HabitGroup(long grp_id, String grp_name) {
        this.grp_id = grp_id;
        this.grp_name = grp_name;
    }

    public HabitGroup() {

    }

    public String getGrp_name() {
        return grp_name;
    }

    public void setGrp_name(String grp_name) {
        this.grp_name = grp_name;
    }

    public long getGrp_id() {
        return grp_id;
    }

    public void setGrp_id(long grp_id) {
        this.grp_id = grp_id;
    }
}
