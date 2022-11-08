package com.example.myapplication.utils;

import androidx.recyclerview.widget.DiffUtil;

import com.example.myapplication.models.BookInfo;

import java.util.List;

public class BooksDiffUtility extends DiffUtil.Callback {

    //Stores the current List of BookInfo objects to be compared
    private List<BookInfo> mOldBookInfoList;

    //Stores the New List of BookInfo objects to be compared
    private List<BookInfo> mNewBookInfoList;
    public BooksDiffUtility(List<BookInfo> oldBookInfos, List<BookInfo> newBookInfos) {
        mOldBookInfoList = oldBookInfos;
        mNewBookInfoList = newBookInfos;
    }

    @Override
    public int getOldListSize() {
        return mOldBookInfoList.size();
    }
    @Override
    public int getNewListSize() {
        return mNewBookInfoList.size();
    }
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //Returning the result of the comparison of Book Id
        return mOldBookInfoList.get(oldItemPosition).getBookId().equals(
                mNewBookInfoList.get(newItemPosition).getBookId()
        );
    }
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        //Returning the result of the comparison of Book Titles only as we assume
        //that there will be no change in a subset of the fields representing the BookInfo
        return mOldBookInfoList.get(oldItemPosition).getTitle().equals(
                mNewBookInfoList.get(newItemPosition).getTitle()
        );
    }

}
