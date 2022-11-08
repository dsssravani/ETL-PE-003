package com.example.myapplication.observers;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class BaseRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    //Constant used for logs
    private static final String LOG_TAG = BaseRecyclerViewScrollListener.class.getSimpleName();

    //Flag to keep a tab on the scroll, whether it has reached the bottom or not
    //to avoid invoking callbacks multiple times
    private boolean mIsScrolledToBottomEnd = false;

    public BaseRecyclerViewScrollListener() {
    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        if (dy != 0) {
            //Scanning for vertical scrolls only

            //Retrieving the current total items in the RecyclerView
            int totalItems = recyclerView.getAdapter().getItemCount();
            //Retrieving the current last seen item position
            int lastItemPosition = getLastVisibleItemPosition(recyclerView.getLayoutManager());

            if (dy > 0) {
                //Scanning when scrolling to the bottom

                //Checking if the last seen item was one of the last three items
                if (lastItemPosition >= (totalItems - 4) && !mIsScrolledToBottomEnd) {
                    //If not scrolled to bottom before and the screen has reached the last three items

                    //Updating the scrolled to bottom state as True
                    mIsScrolledToBottomEnd = true;
                    //Signalling that the last item view has been reached
                    onBottomReached(dy);

                } else if (lastItemPosition < (totalItems - 4) && mIsScrolledToBottomEnd) {
                    //If scrolled to bottom before, but had been reset to the 0th item
                    //then the scrolled to bottom state should be reset to False
                    mIsScrolledToBottomEnd = false;
                }

            } else {
                //Scanning when scrolling to the top

                if (lastItemPosition < (totalItems - 4) && mIsScrolledToBottomEnd) {
                    //If scrolled to bottom before and now scrolling away from the bottom
                    //and away from the last three items

                    //Updating the scrolled to bottom state as False
                    mIsScrolledToBottomEnd = false;
                    //Signalling that the scroll has moved away from the last item view
                    onBottomReached(dy);
                }
            }
        }
    }
    public abstract void onBottomReached(int verticalScrollAmount);
    private int getLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            //When the Layout Manager is an instance of LinearLayoutManager

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            //First, retrieving the last completely visible item position
            int position = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the last partially visible item position
                return linearLayoutManager.findLastVisibleItemPosition();
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //When the Layout Manager is an instance of StaggeredGridLayoutManager

            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int noOfGridColumns = gridLayoutManager.getSpanCount();
            //First, retrieving the last completely visible item position
            int position = gridLayoutManager.findLastCompletelyVisibleItemPositions(null)[noOfGridColumns - 1];
            //Checking the validity of the above position
            if (position > RecyclerView.NO_POSITION) {
                return position; //Returning the same if valid
            } else {
                //Else, returning the last partially visible item position
                return gridLayoutManager.findLastVisibleItemPositions(null)[noOfGridColumns - 1];
            }
        }

        //On all else, returning -1 (RecyclerView.NO_POSITION)
        return RecyclerView.NO_POSITION;
    }
}