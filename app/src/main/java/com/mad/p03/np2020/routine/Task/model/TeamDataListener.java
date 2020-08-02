package com.mad.p03.np2020.routine.Task.model;

/**
 *
 * This are events that will be fired / triggered
 * by the parent. This used trigger changes when the
 * the user add email
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 */
public interface TeamDataListener {
    /**
     * WHen the email is added
     * @param email The email that is being added
     */
    void onDataAdd(String email);
}
