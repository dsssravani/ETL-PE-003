package com.example.myapplication.settings;

import android.os.Bundle;
import android.view.View;

import androidx.preference.PreferenceDialogFragmentCompat;

public class ConfirmationPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    //Constant used for logs
    private static final String LOG_TAG = ConfirmationPreferenceDialogFragmentCompat.class.getSimpleName();
    public static ConfirmationPreferenceDialogFragmentCompat newInstance(String key) {
        ConfirmationPreferenceDialogFragmentCompat dialogFragmentCompat
                = new ConfirmationPreferenceDialogFragmentCompat();

        //Saving the Arguments in a Bundle: START
        Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        dialogFragmentCompat.setArguments(bundle);
        //Saving the Arguments in a Bundle: END

        //Returning the instance
        return dialogFragmentCompat;
    }
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //Saving the confirmation result on click of positive button

            //Retrieving the reference to ConfirmationPreference
            ConfirmationPreference confirmationPreference = getConfirmationPreference();
            //Notifying the OnPreferenceChangeListeners and persisting the value if True
            if (confirmationPreference.callChangeListener(positiveResult)) {
                //Saving the value
                confirmationPreference.setValue(positiveResult);
            }
        }
    }
    private ConfirmationPreference getConfirmationPreference() {
        return (ConfirmationPreference) getPreference();
    }

}
