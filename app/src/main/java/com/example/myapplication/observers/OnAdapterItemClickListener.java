package com.example.myapplication.observers;

import com.example.myapplication.adapterviews.RecyclerViewFragment;
import com.example.myapplication.models.BookInfo;

public interface OnAdapterItemClickListener {
    void onItemClick(BookInfo itemBookInfo);

}