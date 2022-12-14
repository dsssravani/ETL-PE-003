package com.example.myapplication.adapterviews;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.BookDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.RecyclerGridAdapter;
import com.example.myapplication.adapters.RecyclerListAdapter;
import com.example.myapplication.models.BookInfo;
import com.example.myapplication.observers.BaseRecyclerViewScrollListener;
import com.example.myapplication.observers.OnAdapterItemClickListener;
import com.example.myapplication.observers.OnAdapterItemDataSwapListener;
import com.example.myapplication.observers.OnPagerFragmentVerticalScrollListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
public class RecyclerViewFragment extends Fragment
        implements OnAdapterItemClickListener {

    //Annotation constants that define the possible values for LayoutMode
    public static final int LIST_MODE = 0;
    public static final int GRID_MODE = 1;
    //Constant used for logs
    private static final String LOG_TAG = RecyclerViewFragment.class.getSimpleName();
    //Bundle Constant used for storing the argument passed during initialization
    private static final String LAYOUT_MODE_INT_KEY = "layoutMode.Value";
    //Stores the reference to the RecyclerView inflated
    private RecyclerView mRecyclerView;
    //Stores the reference to the Listener OnPagerFragmentVerticalScrollListener
    private OnPagerFragmentVerticalScrollListener mListener;
    public static RecyclerViewFragment newInstance(@LayoutMode int layoutMode) {
        RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();

        //Saving the arguments passed, in a Bundle: START
        final Bundle bundle = new Bundle(1);
        bundle.putInt(LAYOUT_MODE_INT_KEY, layoutMode);
        recyclerViewFragment.setArguments(bundle);
        //Saving the arguments passed, in a Bundle: END
        //Returning the instance
        return recyclerViewFragment;
    }
    public void setOnPagerFragmentVerticalScrollListener(OnPagerFragmentVerticalScrollListener listener) {
        mListener = listener;
    }
    public void clearOnPagerFragmentVerticalScrollListener() {
        mListener = null;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflating the RecyclerView layout ('R.layout.recycler_layout_view')
        View rootView = inflater.inflate(R.layout.recycler_layout_view, container, false);

        //Retrieving the RecyclerView component
        mRecyclerView = rootView.findViewById(R.id.recycler_layout_id);

        if (getArguments() != null) {
            //Retrieving the Layout mode passed
            int layoutMode = getArguments().getInt(LAYOUT_MODE_INT_KEY);

            //Setting the Layout Manager, Adapter and Decoration based on the Layout Mode passed
            if (layoutMode == LIST_MODE) {
                //When the Layout mode passed is for Vertical List View
                setLayoutForListView();
            } else if (layoutMode == GRID_MODE) {
                //When the Layout mode passed is for Vertical Grid View
                setLayoutForGridView();
            }
        }

        //Registering the scroll listener on RecyclerView
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener());

        //Returning the inflated layout
        return rootView;
    }
    private void setLayoutForGridView() {
        //Initializing the StaggeredGridLayoutManager with Vertical Orientation and spanning two columns
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        //Setting the LayoutManager on the RecyclerView
        mRecyclerView.setLayoutManager(gridLayoutManager);

        //Initializing an empty ArrayList of BookInfo Objects as the dataset for the Adapter
        ArrayList<BookInfo> bookInfoList = new ArrayList<>();

        //Initializing the Adapter for the Grid view
        RecyclerGridAdapter recyclerGridAdapter = new RecyclerGridAdapter(requireContext(), R.layout.books_grid_item, bookInfoList);

        //Registering the OnAdapterItemClickListener on the Adapter
        recyclerGridAdapter.setOnAdapterItemClickListener(this);

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(recyclerGridAdapter);

        //Setting the Book Shelf Item Decoration for Grid View
        BookShelfItemDecoration bookShelfItemDecoration = new BookShelfItemDecoration(
                ContextCompat.getDrawable(requireContext(), R.drawable.book_shelf_no_base),
                getResources().getDimensionPixelSize(R.dimen.grid_item_bottom_decoration_offset)
        );
        mRecyclerView.addItemDecoration(bookShelfItemDecoration);
    }
    private void setLayoutForListView() {
        //Initializing the LinearLayoutManager with Vertical Orientation and start to end layout direction
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        //Setting the LayoutManager on the RecyclerView
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Initializing an empty ArrayList of BookInfo Objects as the dataset for the Adapter
        ArrayList<BookInfo> bookInfoList = new ArrayList<>();

        //Initializing the Adapter for the List view
        RecyclerListAdapter recyclerListAdapter = new RecyclerListAdapter(requireContext(), R.layout.books_list_item, bookInfoList);

        //Registering the OnAdapterItemClickListener on the Adapter
        recyclerListAdapter.setOnAdapterItemClickListener(this);

        //Setting the Adapter on the RecyclerView
        mRecyclerView.setAdapter(recyclerListAdapter);

        //Setting the Book Shelf Item Decoration for List View
        BookShelfItemDecoration bookShelfItemDecoration = new BookShelfItemDecoration(
                ContextCompat.getDrawable(requireContext(), R.drawable.book_shelf_no_base),
                getResources().getDimensionPixelSize(R.dimen.list_item_bottom_decoration_offset)
        );
        mRecyclerView.addItemDecoration(bookShelfItemDecoration);

    }
    public int getFirstVisibleItemPosition() {
        //Retrieving the Layout Manager of the RecyclerView
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //When the Layout Manager is an instance of LinearLayoutManager

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            //First, retrieving the top completely visible item position
            int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the top partially visible item position
                return linearLayoutManager.findFirstVisibleItemPosition();
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //When the Layout Manager is an instance of StaggeredGridLayoutManager

            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            //First, retrieving the top completely visible item position
            int position = gridLayoutManager.findFirstCompletelyVisibleItemPositions(null)[0];
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the top partially visible item position
                return gridLayoutManager.findFirstVisibleItemPositions(null)[0];
            }
        }

        //On all else, returning -1 (RecyclerView.NO_POSITION)
        return RecyclerView.NO_POSITION;
    }
    public void scrollToItemPosition(int position, boolean scrollImmediate) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (position > RecyclerView.NO_POSITION) {
            //Scrolling to the item position passed when valid
            if (scrollImmediate) {
                //Scrolling to the item position immediately
                layoutManager.scrollToPosition(position);
            } else {
                //Scrolling to the item position naturally with default animation
                layoutManager.smoothScrollToPosition(mRecyclerView, null, position);
            }
        }
    }
    public void registerItemDataSwapListener(@Nullable OnAdapterItemDataSwapListener itemDataSwapListener, int position) {
        //Retrieving the RecyclerView's Adapter
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        //Registering the listener on the corresponding adapter based on the Fragment position passed
        if (position == LIST_MODE) {
            //When the Fragment position is of the LIST_MODE Fragment

            RecyclerListAdapter listAdapter = (RecyclerListAdapter) adapter;
            listAdapter.setOnAdapterItemDataSwapListener(itemDataSwapListener);
        } else if (position == GRID_MODE) {
            //When the Fragment position is of the GRID_MODE Fragment

            RecyclerGridAdapter gridAdapter = (RecyclerGridAdapter) adapter;
            gridAdapter.setOnAdapterItemDataSwapListener(itemDataSwapListener);
        }
    }
    public void clearItemDataSwapListener(int position) {
        //Propagating the call to #registerItemDataSwapListener with null to unregister
        registerItemDataSwapListener(null, position);
    }
    public void loadNewData(List<BookInfo> bookInfos) {
        Log.d(LOG_TAG, "loadNewData: Started");
        //Calling the Adapter's method to Load the new data
        swapAdapterData(bookInfos);
    }
    private void swapAdapterData(@NonNull List<BookInfo> bookInfos) {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        //Calling the respective method from the Adapter to load & display the new data accordingly
        if (adapter instanceof RecyclerListAdapter) {
            Log.d(LOG_TAG, "swapAdapterData: Started for List");
            RecyclerListAdapter listAdapter = (RecyclerListAdapter) adapter;
            listAdapter.swapItemData(bookInfos);
        } else if (adapter instanceof RecyclerGridAdapter) {
            Log.d(LOG_TAG, "swapAdapterData: Started for Grid");
            RecyclerGridAdapter gridAdapter = (RecyclerGridAdapter) adapter;
            gridAdapter.swapItemData(bookInfos);
        }
    }

    /**
     * Method to clear the data from the Adapter
     */
    public void clearData() {
        //Creating an Empty List of BookInfo objects to clear the content in the Adapter
        List<BookInfo> bookInfos = new ArrayList<>();
        //Calling the Adapter's method to clear the data
        swapAdapterData(bookInfos);
    }
    @Override
    public void onItemClick(BookInfo itemBookInfo) {
        //Passing the selected Item's data as an Intent to the BookDetailActivity
        Intent itemIntent = new Intent(getActivity(), BookDetailActivity.class);
        itemIntent.putExtra(BookDetailActivity.BOOK_INFO_ITEM_STR_KEY, itemBookInfo);
        startActivity(itemIntent);
    }

    //Defining the LayoutMode IntDef annotation with Retention only at SOURCE
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LIST_MODE, GRID_MODE})
    @interface LayoutMode {
    }
    private class BookShelfItemDecoration extends RecyclerView.ItemDecoration {

        //Drawable resource of Book Shelf used as decoration
        private Drawable mItemDrawable;
        //Offset correction for the decoration
        private int mOffsetFromImage;
        BookShelfItemDecoration(Drawable itemDrawable, int offsetFromImage) {
            mItemDrawable = itemDrawable;
            mOffsetFromImage = offsetFromImage;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            int totalItems = parent.getAdapter().getItemCount();
            if (parent.getChildAdapterPosition(view) == totalItems - 1) {

                outRect.bottom = mItemDrawable.getIntrinsicHeight() * 2;

            } else {
                outRect.bottom = mItemDrawable.getIntrinsicHeight() - mOffsetFromImage;
            }

        }
        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            //Retrieving the Child View Count
            int childCount = parent.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View childView = parent.getChildAt(index);
                int positionLeft = childView.getLeft();
                int positionRight = childView.getRight();
                //Applying the offset correction so that the Drawable decoration sits below the book image
                int positionTop = childView.getBottom() - mOffsetFromImage;
                int positionBottom = positionTop + mItemDrawable.getIntrinsicHeight();

                //Applying the bounds on the Drawable decoration
                mItemDrawable.setBounds(positionLeft, positionTop, positionRight, positionBottom);
                //Drawing the decoration on the canvas
                mItemDrawable.draw(canvas);
            }
        }
    }
    private class RecyclerViewScrollListener extends BaseRecyclerViewScrollListener {
        public void onBottomReached(int verticalScrollAmount) {
            //Propagating the call to the listener OnPagerFragmentVerticalScrollListener
            mListener.onBottomReached(verticalScrollAmount);
        }
    }
}

