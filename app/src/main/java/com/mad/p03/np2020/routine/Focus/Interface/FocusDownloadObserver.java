package com.mad.p03.np2020.routine.Focus.Interface;

/**
 * Custom interface to listen for file changes
 *
 *  @author Lee Quan Sheng
 *  @since 01-08-2020
 */
public interface FocusDownloadObserver {
    void onFileGoing();

    void onFileComplete();
}