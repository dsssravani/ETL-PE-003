package com.example.myapplication.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.myapplication.R;
import com.example.myapplication.utils.PreferencesObserverUtility;

import java.util.List;
import java.util.Set;

public class SearchSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    //OOB constant used as an identifier for the DialogFragments
    private static final String DIALOG_FRAGMENT_TAG =
            "android.support.v7.preference.PreferenceFragment.DIALOG";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Load Preferences from the XML resource
        addPreferencesFromResource(R.xml.preferences);
        //Bind preferences' summary to their value: START
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_publication_type_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_content_type_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_by_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_page_to_display_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_results_per_page_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_reset_settings_key)));
        //Bind preferences' summary to their value: END
    }
    private void bindPreferenceSummaryToValue(Preference preference) {
        if (preference instanceof ListPreference) {
            //Setting summary for ListPreferences
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
        } else if (preference instanceof NumberPickerPreference) {
            //Setting summary for NumberPickerPreferences
            NumberPickerPreference numberPickerPreference = (NumberPickerPreference) preference;
            numberPickerPreference.setSummary(String.valueOf(numberPickerPreference.getValue()));
        }
        //Attaching the OnPreferenceChangeListener to this preference
        preference.setOnPreferenceChangeListener(this);
    }
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        //Evaluating based on the kind of Preference
        if (preference instanceof NumberPickerPreference) {
            //If the preference is based on the NumberPickerPreference
            //then display the corresponding NumberPicker Dialog

            //Retrieving the preference's key
            String preferenceKey = preference.getKey();

            //Declaring the NumberPicker's DialogFragment
            NumberPickerPreferenceDialogFragmentCompat dialogFragment = null;

            if (!anyDialogActive()) {
                //Initializing the NumberPicker DialogFragment based on the Preference Key
                //when no Dialog is found to be active
                if (preferenceKey.equals(getString(R.string.pref_page_to_display_key))) {
                    //Initializing the NumberPicker DialogFragment for "Page To Display" preference setting
                    dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(
                            preferenceKey,
                            getResources().getInteger(R.integer.pref_page_to_display_min_value),
                            PreferenceManager.getDefaultSharedPreferences(requireContext())
                                    .getInt(getString(R.string.pref_page_to_display_max_value_key),
                                            getResources().getInteger(R.integer.pref_page_to_display_default_value))
                    );
                } else if (preferenceKey.equals(getString(R.string.pref_results_per_page_key))) {
                    //Initializing the NumberPicker DialogFragment for "Results per page" preference setting
                    dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(
                            preferenceKey,
                            getResources().getInteger(R.integer.pref_results_per_page_min_value),
                            getResources().getInteger(R.integer.pref_results_per_page_max_value)
                    );
                }
            }

            //Displaying the NumberPicker Dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(requireFragmentManager(), DIALOG_FRAGMENT_TAG);
            }

        } else if (preference instanceof ConfirmationPreference) {
            //If the preference is based on the ConfirmationPreference
            //then display the corresponding Confirmation Dialog

            //Declaring the DialogFragment for Confirmation
            ConfirmationPreferenceDialogFragmentCompat dialogFragment = null;

            if (!anyDialogActive()) {
                //Initializing the ConfirmationPreference DialogFragment when no Dialog is found to be active
                dialogFragment = ConfirmationPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }

            //Displaying the Confirmation Dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(requireFragmentManager(), DIALOG_FRAGMENT_TAG);
            }

        } else {
            //Calling the super when nothing found
            super.onDisplayPreferenceDialog(preference);
        }
    }
    private boolean anyDialogActive() {
        return (requireFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            //For Preferences of type ListPreference
            ListPreference listPreference = (ListPreference) preference;
            int selectedIndex = listPreference.findIndexOfValue(newValue.toString());
            listPreference.setSummary(listPreference.getEntries()[selectedIndex]);
        } else if (preference instanceof NumberPickerPreference) {
            //For Preferences of type NumberPickerPreference
            NumberPickerPreference numberPickerPreference = (NumberPickerPreference) preference;
            numberPickerPreference.setSummary(String.valueOf(newValue));
        } else if (preference instanceof ConfirmationPreference) {
            //For Preferences of type ConfirmationPreference
            boolean confirmationResult = (boolean) newValue;
            if (confirmationResult) {
                //If result is True, then reset all the Preferences to their defaults
                resetAllToDefaults();
            }
            //Returning false to prevent updating the state of this Preference
            return false;
        }
        return true;
    }

    private void resetAllToDefaults() {
        //Retrieving the Preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        //Retrieving the Preferences which are excluded from preparing the Search URL
        List<String> keysExcludedFromSearch = PreferencesObserverUtility.getPreferenceKeysToExclude(requireContext());

        //Iterating over the Preference Keys to reset them to their defaults: START
        Set<String> prefKeySet = sharedPreferences.getAll().keySet();
        for (String prefKeyStr : prefKeySet) {
            if (!keysExcludedFromSearch.contains(prefKeyStr)) {
                //When the Keys are part of the Search URL

                //Switching based on the preference key and setting to their defaults
                if (prefKeyStr.equals(getString(R.string.pref_publication_type_key))) {
                    bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                            getString(R.string.pref_publication_type_default));
                } else if (prefKeyStr.equals(getString(R.string.pref_content_type_key))) {
                    bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                            getString(R.string.pref_content_type_default));
                } else if (prefKeyStr.equals(getString(R.string.pref_sort_by_key))) {
                    bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                            getString(R.string.pref_sort_by_default));
                } else if (prefKeyStr.equals(getString(R.string.pref_page_to_display_key))) {
                    bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                            getResources().getInteger(R.integer.pref_page_to_display_default_value));
                } else if (prefKeyStr.equals(getString(R.string.pref_results_per_page_key))) {
                    bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                            getResources().getInteger(R.integer.pref_results_per_page_default_value));
                }
            }
        }
        //Iterating over the Preference Keys to reset them to their defaults: END

        //Forcibly marking all preferences to their defaults using PreferenceManager
        PreferenceManager.setDefaultValues(requireContext(), R.xml.preferences, true);
    }
   private void bindPreferenceToDefaultValue(Preference preference, Object defaultValue) {
        if (preference instanceof ListPreference) {
            //For Preferences of type ListPreference
            ListPreference listPreference = (ListPreference) preference;
            //Finding the index of the default value
            int selectedIndex = listPreference.findIndexOfValue(defaultValue.toString());
            //Updating the Summary
            listPreference.setSummary(listPreference.getEntries()[selectedIndex]);
            //Setting the Default value
            listPreference.setValue(defaultValue.toString());
        } else if (preference instanceof NumberPickerPreference) {
            //For Preferences of type NumberPickerPreference
            NumberPickerPreference numberPickerPreference = (NumberPickerPreference) preference;
            //Updating the Summary
            numberPickerPreference.setSummary(String.valueOf(defaultValue));
            //Setting the Default value
            numberPickerPreference.setValue((Integer) defaultValue);
        }
    }

}
