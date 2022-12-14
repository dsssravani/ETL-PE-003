package com.example.myapplication.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.DialogPreference;
import android.util.AttributeSet;

import com.example.myapplication.R;

public class ConfirmationPreference extends DialogPreference {

    //Constant used for logs
    private static final String LOG_TAG = ConfirmationPreference.class.getSimpleName();

    //Stores the confirmation value given by the user
    private boolean mPositiveConfirmationResult;

    //Constant used as the Default fallback value when the preference value is not available
    private boolean FALLBACK_VALUE = false;

    public ConfirmationPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //Propagating the call to super
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ConfirmationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        //Delegating to other constructor
        this(context, attrs, defStyleAttr, 0);
    }

    public ConfirmationPreference(Context context, AttributeSet attrs) {
        //Delegating to other constructor
        //Using the dialogPreferenceStyle as the default dialog style
        this(context, attrs, androidx.preference.R.attr.dialogPreferenceStyle);
    }

    public ConfirmationPreference(Context context) {
        //Delegating to other constructor
        this(context, null);
    }
    public boolean getValue() {
        return mPositiveConfirmationResult;
    }
    public void setValue(boolean confirmationResult) {
        this.mPositiveConfirmationResult = confirmationResult;

        //Saving the value to the SharedPreferences
        persistBoolean(mPositiveConfirmationResult);
    }
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getBoolean(index, FALLBACK_VALUE);
    }
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedBoolean(FALLBACK_VALUE) : (boolean) defaultValue);
    }
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        //Checking if the preference state is persisted
        if (isPersistent()) {
            //No need to save the state as it is persistent
            return superState;
        }

        //Creating an instance of a custom BaseSavedState
        SavedState savedState = new SavedState(superState);
        //Saving the state with the current preference value
        savedState.confirmationResult = getValue();

        //Returning the saved state
        return savedState;
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //Checking whether the state was saved in the onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            //Calling to the superclass when the state was never saved
            super.onRestoreInstanceState(state);
            return;
        }

        //Casting the saved state to custom BaseSavedState
        SavedState savedState = (SavedState) state;
        //Passing the super state to super class
        super.onRestoreInstanceState(savedState.getSuperState());

        //Restoring the saved state to the member to reflect the current state
        setValue(savedState.confirmationResult);
    }
   private static class SavedState extends BaseSavedState {

        //Standard Parcelable CREATOR object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                //Returning the instance of this class
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        //Holds the setting's value for persisting the preference value
        boolean confirmationResult;

        public SavedState(Parcel source) {
            super(source);
            //Reading and saving the Preference value
            confirmationResult = source.readInt() == 1;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            //Write the Preference value
            dest.writeInt(confirmationResult ? 1 : 0);
        }

    }

}