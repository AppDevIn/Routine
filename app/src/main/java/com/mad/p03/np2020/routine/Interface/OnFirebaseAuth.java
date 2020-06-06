package com.mad.p03.np2020.routine.Interface;

/**
 *
 * This is used to be a trigger when it
 * is the firebase is succeed or failed in authenticating the user
 *
 * @author Jeyavishnu
 * @since 06-06-2020
 */
public interface OnFirebaseAuth {

    /**
     * When the user has been successful registered
     */
    void OnSignUpSuccess();

    /**
     * When the user is not registered
     *
     * @param e The error
     */
    void OnSignUpFailure(Exception e);

}
