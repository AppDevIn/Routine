package com.mad.p03.np2020.routine.Focus.Interface;

/**
 * Listen to download file
 *
 *  @author Lee Quan Sheng
 *  @since 01-08-2020
 */
public interface FocusDownloadObservable {
    //register the observer with this method
    void registerDownloadObserver(FocusDownloadObserver focusDBObserver);
    //unregister the observer with this method
    void removeDownloadObserver(FocusDownloadObserver focusDBObserver);
    //call this method upon download is completed
    void notifyDownloadComplete();
    void notifyDownloadOngoing();

}