package com.example.myapplication.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class PreferencesObserverUtility {
    public static List<String> getPreferenceKeysToExclude(Context context) {
        //Initializing an ArrayList of Strings for the Keys to be excluded
        ArrayList<String> keysToExclude = new ArrayList<>();

        //Adding the Preference Keys to be excluded
        keysToExclude.add(context.getString(R.string.pref_page_to_display_max_value_key));
        keysToExclude.add(context.getString(R.string.pref_reset_settings_key));
        keysToExclude.add(context.getString(R.string.pref_last_displayed_page_key));

        //Returning the exclusion list
        return keysToExclude;
    }
    public static void addKeyToExclude(List<String> keysToExclude, String key) {
        //Checking initially if the Key is not empty and the list does NOT contain the Key yet
        if (!TextUtils.isEmpty(key) && !keysToExclude.contains(key)) {
            //Adding to the exclusion list
            keysToExclude.add(key);
        }
    }
    public static void removeKeyToInclude(List<String> keysToExclude, String key) {
        //Checking initially if the Key is not empty and the list does contain the Key
        if (!TextUtils.isEmpty(key) && keysToExclude.contains(key)) {
            //Removing from the exclusion list
            keysToExclude.remove(key);
        }
    }

}