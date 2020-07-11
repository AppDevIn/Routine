package com.mad.p03.np2020.routine.models;

/**
 *
 * Model used to manage the habitGroup
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitGroup {

    //Declare the constants of the database

    /**Used as the name of the table*/
    public static final String TABLE_NAME = "habitGroups";

    /**Used as the primary key for this table*/
    public static final String COLUMN_ID = "_id";
    /**Column of the habit table, Used as the name of the group*/
    public static final String COLUMN_GROUP_NAME = "_name";

    /**
     * The query to create habitGroup table
     */
    public static final String CREATE_GROUPS_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_GROUP_NAME  + " TEXT)";

    /**
     * The query to drop habit table
     */
    public static final String DROP_GROUPS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    private long grp_id;
    private String grp_name;

    /**This method is an empty constructor for habitGroup*/
    public HabitGroup() { }

    /**This method is a constructor for habitGroup*/
    public HabitGroup(String grp_name) {
        this.grp_name = grp_name;
    }

    /**This method is a constructor for habitGroup*/
    public HabitGroup(long grp_id, String grp_name) {
        this.grp_id = grp_id;
        this.grp_name = grp_name;
    }

    /**@return String This return the name of the group*/
    public String getGrp_name() {
        return grp_name;
    }

    /**
     *
     * This method is used to set
     * the name of the group
     *
     * @param grp_name This parameter is used to set
     *              the name of the group
     * */
    public void setGrp_name(String grp_name) {
        this.grp_name = grp_name;
    }

    /**@return long This return the id of the group*/
    public long getGrp_id() {
        return grp_id;
    }

    /**
     *
     * This method is used to set
     * the id of the group
     *
     * @param grp_id This parameter is used to set
     *              the id of the group
     * */
    public void setGrp_id(long grp_id) {
        this.grp_id = grp_id;
    }
}
