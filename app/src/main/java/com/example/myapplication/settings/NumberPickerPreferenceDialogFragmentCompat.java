package com.example.myapplication.settings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.NumberPicker;

import androidx.preference.PreferenceDialogFragmentCompat;

import com.example.myapplication.R;

public class NumberPickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    //Constant used for logs
    private static final String LOG_TAG = NumberPickerPreferenceDialogFragmentCompat.class.getSimpleName();

    //Bundle Key Constants
    private static final String NUMBER_PICKER_MAX_INT_KEY = "numberPicker.maxValue";
    private static final String NUMBER_PICKER_MIN_INT_KEY = "numberPicker.minValue";
    private static final String NUMBER_PICKER_VALUE_INT_KEY = "numberPicker.value";

    //Stores the reference to the NumberPicker in the Dialog
    private NumberPicker mNumberPicker;

    //Saves the value selected by the user, for restoring the state
    private int mNumberPickerValue;

    public static NumberPickerPreferenceDialogFragmentCompat newInstance(String key, int minValue, int maxValue) {
        final NumberPickerPreferenceDialogFragmentCompat dialogFragmentCompat
                = new NumberPickerPreferenceDialogFragmentCompat();

        //Saving the arguments passed, in a Bundle: START
        final Bundle bundle = new Bundle(3);
        bundle.putString(ARG_KEY, key);
        bundle.putInt(NUMBER_PICKER_MAX_INT_KEY, maxValue);
        bundle.putInt(NUMBER_PICKER_MIN_INT_KEY, minValue);
        dialogFragmentCompat.setArguments(bundle);
        //Saving the arguments passed, in a Bundle: END

        //Returning the instance
        return dialogFragmentCompat;
    }

    //Used to restore the preference's state from the Bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //Restoring the previous value of NumberPicker
            mNumberPickerValue = savedInstanceState.getInt(NUMBER_PICKER_VALUE_INT_KEY);
        }
    }

    //Saves the preference's state in the bundle
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //Saving the value of NumberPicker
        outState.putInt(NUMBER_PICKER_VALUE_INT_KEY, mNumberPicker.getValue());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        //Retrieving the NumberPicker from the view
        mNumberPicker = view.findViewById(R.id.pref_number_picker_id);

        //Throwing an exception when no NumberPicker is found
        if (mNumberPicker == null) {
            throw new IllegalStateException("Dialog view must contain a NumberPicker" +
                    " with the id as 'pref_number_picker_id'");
        }

        //Setting the Min and Max values for the NumberPicker
        if (getArguments() != null) {
            mNumberPicker.setMaxValue(getArguments().getInt(NUMBER_PICKER_MAX_INT_KEY));
            mNumberPicker.setMinValue(getArguments().getInt(NUMBER_PICKER_MIN_INT_KEY));
        }

        //Setting the Value of NumberPicker if previously set
        mNumberPicker.setValue(mNumberPickerValue > 0 ? mNumberPickerValue : getNumberPickerPreference().getValue());

        //Wrapping the selector wheel on NumberPicker
        mNumberPicker.setWrapSelectorWheel(true);

    }
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //Saving the selected value on click of positive button

            //Retrieving the value to save
            int selectedValue = mNumberPicker.getValue();

            //Retrieving the reference to NumberPickerPreference
            NumberPickerPreference numberPickerPreference = getNumberPickerPreference();
            //Notifying the OnPreferenceChangeListeners and persisting the value if true
            if (numberPickerPreference.callChangeListener(selectedValue)) {
                //Saving the value
                numberPickerPreference.setValue(selectedValue);
            }
        }
    }
    private NumberPickerPreference getNumberPickerPreference() {
        //Retrieving the NumberPickerPreference
        return (NumberPickerPreference) getPreference();
    }

}
