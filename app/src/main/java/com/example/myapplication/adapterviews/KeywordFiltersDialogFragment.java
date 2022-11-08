package com.example.myapplication.adapterviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.KeywordFiltersAdapter;
import com.example.myapplication.models.KeywordFilter;

import java.util.ArrayList;
public class KeywordFiltersDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener {

    //Constant used for logs
    private static final String LOG_TAG = KeywordFiltersDialogFragment.class.getSimpleName();

    //Constant used as an Identifier for Fragment
    public static final String FRAGMENT_TAG = LOG_TAG;
    //Instance of the DialogFragment
    private static KeywordFiltersDialogFragment mDialogFragment;
    //Instance of the interface to deliver action events
    private OnKeywordFilterSelectedListener mKeywordFilterSelectedListener;
    public static KeywordFiltersDialogFragment newInstance() {
        if (mDialogFragment == null) {
            //Creating a new Instance when not present
            mDialogFragment = new KeywordFiltersDialogFragment();
        }
        //Returning the Instance
        return mDialogFragment;
    }

    //Attaching the context to the fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mKeywordFilterSelectedListener = (OnKeywordFilterSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnKeywordFilterSelectedListener");
        }
    }

    //Attaching the activity to the fragment
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mKeywordFilterSelectedListener = (OnKeywordFilterSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnKeywordFilterSelectedListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Initializing the AlertDialog Builder to build the Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        //Inflating the List View Layout
        //Passing NULL as we are attaching the layout to the Dialog
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.keyword_filter_list_view, null);

        //Retrieving the List View
        ListView listView = rootView.findViewById(R.id.keyword_filters_list_view_id);

        //Initializing ArrayList of KeywordFilter: START
        ArrayList<KeywordFilter> keywordFilterList = new ArrayList<>();
        String[] keywordFilterNameArray = getResources().getStringArray(R.array.keyword_filter_entries);
        String[] keywordFilterDescArray = getResources().getStringArray(R.array.keyword_filter_description);
        String[] keywordFilterValueArray = getResources().getStringArray(R.array.keyword_filter_values);
        int arrayLength = keywordFilterNameArray.length;
        for (int index = 0; index < arrayLength; index++) {
            keywordFilterList.add(new KeywordFilter(keywordFilterNameArray[index], keywordFilterDescArray[index], keywordFilterValueArray[index]));
        }
        //Initializing ArrayList of KeywordFilter: END

        //Initializing the ListView Adapter
        KeywordFiltersAdapter keywordFiltersAdapter = new KeywordFiltersAdapter(getActivity(), R.layout.keyword_filter_list_item, keywordFilterList);

        //Binding the Adapter to ListView
        listView.setAdapter(keywordFiltersAdapter);

        //Setting the OnItemClickListener on ListView
        listView.setOnItemClickListener(this);

        //Setting the inflated view on the Dialog
        dialogBuilder.setView(rootView);

        //Returning the dialog created
        return dialogBuilder.create();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Retrieving the data of selected item view
        KeywordFilter keywordFilterSelected = (KeywordFilter) parent.getItemAtPosition(position);
        //Sending the KeywordFilter's mFilterValue to the Listener to update the Search string
        mKeywordFilterSelectedListener.onKeywordFilterSelected(keywordFilterSelected.getFilterValue());
        //Dismissing the dialog
        dismiss();
    }

    //Called when the Activity is destroyed
    @Override
    public void onDetach() {
        super.onDetach();
        //Clearing the reference to the Activity to avoid leaking
        mKeywordFilterSelectedListener = null;
    }
    public interface OnKeywordFilterSelectedListener {
        void onKeywordFilterSelected(String filterValue);
    }

}