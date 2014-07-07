package com.vodafone.he.sdk.android;

/**
 * Interface for receiving events about retrieving UserDetails.
 */
public interface UserDetailsCallback extends VodafoneCallback {
    /**
     * Called when UserDetails get updated.
     * @param userDetails current version of UserDetails
     */
    void onUserDetailsUpdated(UserDetails userDetails);

    /**
     * Called when error occurred during retrieving UserDetails.
     * @param ex exception detailing type of error
     */
    void onUserDetailsError(VodafoneException ex);
}