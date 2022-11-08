package com.example.myapplication.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapterviews.RecyclerViewFragment;

import java.util.Set;

public class DisplayPagerAdapter extends FragmentStatePagerAdapter {

    public static final int TOTAL_VIEW_COUNT = 2;
    private Context mContext;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();
    private FragmentManager mFragmentManager;
    /**constructor**/
    public DisplayPagerAdapter(FragmentManager fm,Context context){
        super(fm);
        this.mFragmentManager = fm;
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0 :
                return RecyclerViewFragment.newInstance(RecyclerViewFragment.LIST_MODE);
            case 1:
                return RecyclerViewFragment.newInstance(RecyclerViewFragment.GRID_MODE);
            default:
                return null;
        }
    }
    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    //Unregisters the Fragment when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.delete(position);
        super.destroyItem(container, position, object);
    }
    @Nullable
    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
        if (state != null) {
            //When the state is present
            Bundle bundle = (Bundle) state;
            //Setting the ClassLoader passed, onto the Bundle
            bundle.setClassLoader(loader);

            //Retrieving the keys used in Bundle
            Set<String> keyStringSet = bundle.keySet();
            //Iterating over the Keys to find the Fragments
            for (String keyString : keyStringSet) {
                if (keyString.startsWith("f")) {
                    //Fragment keys starts with 'f' followed by its position index
                    int position = Integer.parseInt(keyString.substring(1));
                    //Getting the Fragment from the Bundle using the Key through the FragmentManager
                    Fragment fragment = mFragmentManager.getFragment(bundle, keyString);
                    if (fragment != null) {
                        //If Fragment is valid, then update the Sparse Array of Registered Fragments
                        mRegisteredFragments.put(position, fragment);
                    }
                }
            }
        }
    }
    @Override
    public int getCount() {
        return TOTAL_VIEW_COUNT;
    }
    @NonNull
    public View getTabView(int position) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.icon_tab_layout_template, null);
        ImageView iconImageView = rootView.findViewById(R.id.tab_image_icon_id);
        TextView tabTextView = rootView.findViewById(R.id.tab_text_id);
        switch (position) {
            case 0: //For List View
                iconImageView.setImageResource(R.drawable.list_tab_icon_state_list);
                tabTextView.setText(mContext.getString(R.string.list_tab_title));
                break;
            case 1: //For Grid View
                iconImageView.setImageResource(R.drawable.grid_tab_icon_state_list);
                tabTextView.setText(mContext.getString(R.string.grid_tab_title));
                break;
        }
        return rootView;
    }

}
