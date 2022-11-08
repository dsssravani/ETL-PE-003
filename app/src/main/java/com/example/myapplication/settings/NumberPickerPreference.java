package com.example.myapplication.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.preference.DialogPreference;
import android.util.AttributeSet;

import com.example.myapplication.R;

public class NumberPickerPreference extends DialogPreference {

    //Constant used for logs
    private static final String LOG_TAG = NumberPickerPreference.class.getSimpleName();
    //Constant used as the Default fallback value when the preference value is not available
    private static final int FALLBACK_VALUE = 0;
    //Stores the reference to the Number Picker Layout Resource
    private final int mDialogLayoutResourceId = R.layout.pref_number_picker_dialog;
    //Stores the value of the Number Picked by the User
    private int mValue;

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        //Delegate to other constructor
        this(context, attrs, defStyleAttr, 0);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        //Delegate to other constructor
        //Use the dialogPreferenceStyle as the default style
        this(context, attrs, androidx.preference.R.attr.dialogPreferenceStyle);
    }

    public NumberPickerPreference(Context context) {
        //Delegate to other constructor
        this(context, null);
    }
    public int getValue() {
        return mValue;
    }

    public void setValue(int numberPickedValue) {
        this.mValue = numberPickedValue;

        //Saving the value to the SharedPreferences
        persistInt(mValue);
    }
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, FALLBACK_VALUE);
    }
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(FALLBACK_VALUE) : (int) defaultValue);
    }
    @Override
    public int getDialogLayoutResource() {
        return mDialogLayoutResourceId;
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
        savedState.stateValue = getValue();

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

        //Casting the saved state to the custom BaseSavedState
        SavedState savedState = (SavedState) state;
        //Passing the super state to the superclass
        super.onRestoreInstanceState(savedState.getSuperState());

        //Restoring the state to the member to reflect the current state
        setValue(savedState.stateValue);
    }
    private static class SavedState extends BaseSavedState {
        //Standard Parcelable CREATOR object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Creator<SavedState>() {

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
        int stateValue;

        public SavedState(Parcel source) {
            super(source);
            //Get the current preference value
            stateValue = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            //Write the preference value
            dest.writeInt(stateValue);
        }
    }

}

