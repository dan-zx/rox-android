package com.grayfox.android.dao;

/**
 * Foursquare Authentication Data Access Object interface. Defines the methods to retrieve and save
 * Foursquare authentication codes.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public interface FoursquareAuthDao {

    /**
     * Fetches the current authentication code.
     * 
     * @return an authentication code.
     */
    String fetchAuthCode();

    /**
     * Saves the current authentication code.
     * 
     * @param authCode an authentication code.
     */
    void saveAuthCode(String authCode);

    /** Deletes the current authentication code. */
    void deleteAuthCode();
}