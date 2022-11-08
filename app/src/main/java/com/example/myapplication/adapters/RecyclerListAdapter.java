package com.example.myapplication.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.models.BookInfo;
import com.example.myapplication.observers.OnAdapterItemClickListener;
import com.example.myapplication.observers.OnAdapterItemDataSwapListener;
import com.example.myapplication.utils.TextAppearanceUtility;
import com.example.myapplication.workers.BooksDiffLoader;
import com.example.myapplication.workers.ImageDownloaderFragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ViewHolder>
        implements LoaderManager.LoaderCallbacks<DiffUtil.DiffResult> {

    //Constant used for logs
    private static final String LOG_TAG = RecyclerListAdapter.class.getSimpleName();

    //Constants used for the Diff Loader's Bundle arguments
    private static final String OLD_LIST_STR_KEY = "BookInfo.Old";
    private static final String NEW_LIST_STR_KEY = "BookInfo.New";

    //Stores the layout resource of the list item that needs to be inflated manually
    private int mLayoutRes;

    //Stores the reference to the Context
    private Context mContext;

    //Stores the List of BookInfo objects which is the Dataset of the Adapter
    private List<BookInfo> mBookInfoList;

    //Stores a reference to the font face to be used for the Book Title
    private Typeface mTitleTextTypeface;

    //Stores the reference to the Listener OnAdapterItemClickListener
    private OnAdapterItemClickListener mItemClickListener;

    //Stores the reference to the Listener OnAdapterItemDataSwapListener
    private OnAdapterItemDataSwapListener mItemDataSwapListener;
    public RecyclerListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<BookInfo> bookInfos) {
        mContext = context;
        mLayoutRes = resource;
        mBookInfoList = bookInfos;

        //Saving an instance of the font face for the Book Title
        mTitleTextTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/garamond_bold.ttf");
    }
    private Context getContext() {
        return mContext;
    }
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mItemClickListener = listener;
    }
    public void setOnAdapterItemDataSwapListener(OnAdapterItemDataSwapListener listener) {
        mItemDataSwapListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflating the item Layout view
        //Passing False as we are attaching the View ourselves
        View itemView = LayoutInflater.from(getContext()).inflate(mLayoutRes, parent, false);

        //Instantiating the ViewHolder to initialize the reference to the view components in the item layout
        //and returning the same
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //Retrieving the BookInfo object at the current item position
        BookInfo bookInfo = mBookInfoList.get(position);

        //Retrieving the Context
        Context context = getContext();

        //Populating the data onto the Template View using the BookInfo object: START

        //Updating the Book Image if link is present
        String imageURLStr = bookInfo.getImageLinkForItemInfo();
        if (!TextUtils.isEmpty(imageURLStr)) {
            //Loading the Image when link is present
            ImageDownloaderFragment
                    .newInstance(((FragmentActivity) getContext()).getSupportFragmentManager(), position)
                    .executeAndUpdate(viewHolder.bookImageView,
                            imageURLStr,
                            position);
        } else {
            //Resetting to the default Book image when link is absent
            viewHolder.bookImageView.setImageResource(R.drawable.ic_book);
        }

        //Updating the Title
        viewHolder.titleTextView.setText(bookInfo.getTitle());
        //Setting the Title font
        viewHolder.titleTextView.setTypeface(mTitleTextTypeface);

        //Updating the Authors
        viewHolder.authorTextView.setText(bookInfo.getAuthors(context.getString(R.string.no_authors_found_default_text)));

        //Updating the Publisher
        viewHolder.publisherTextView.setText(bookInfo.getPublisher(context.getString(R.string.no_publisher_found_default_text)));

        //Updating the Published Date : START
        String publishedDateStr = "";
        try {
            publishedDateStr = bookInfo.getPublishedDateInMediumFormat(context.getString(R.string.no_published_date_default_text));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error occurred while parsing and formatting the Published date", e);
        } finally {
            viewHolder.publishedDateTextView.setText(publishedDateStr);
        }
        //Updating the Published Date : END

        //Updating the Count of Pages and the Book Type
        int pageCount = bookInfo.getPageCount();
        String bookTypeStr = bookInfo.getBookType();
        viewHolder.pagesTextView.setText(context.getString(R.string.pages_book_type, pageCount, bookTypeStr));

        //Updating the Categories
        viewHolder.categoriesTextView.setText(bookInfo.getCategories(context.getString(R.string.no_categories_found_default_text)));

        //Updating the Book's Ratings
        viewHolder.bookRatingBarView.setRating(bookInfo.getBookRatings());

        //Updating the Count of Ratings
        viewHolder.ratingCountTextView.setText(bookInfo.getBookRatingCount());

        //Updating the Price of the Book : START
        //Ensuring that the view components are visible
        if (viewHolder.listPriceTextView.getVisibility() == View.GONE) {
            viewHolder.listPriceTextView.setVisibility(View.VISIBLE);
        }
        if (viewHolder.retailPriceTextView.getVisibility() == View.GONE) {
            viewHolder.retailPriceTextView.setVisibility(View.VISIBLE);
        }

        if (bookInfo.isForSale()) {
            //When the book is FOR SALE in the user's locale, then the prices will be available
            if (bookInfo.isDiscounted()) {
                //When the price is discounted

                //Setting the List Price with the Strikethrough Text
                TextAppearanceUtility.setStrikethroughText(viewHolder.listPriceTextView, bookInfo.getListPrice());
                //Setting the Retail Price
                viewHolder.retailPriceTextView.setText(bookInfo.getRetailPrice());
            } else {
                //When both the List price and Retail price are same

                //Hiding the List price field as there is no discount to be shown
                viewHolder.listPriceTextView.setVisibility(View.GONE);

                //Setting the Retail Price
                viewHolder.retailPriceTextView.setText(bookInfo.getRetailPrice());
            }
        } else {
            //Updating with a default message when the book is NOT FOR SALE

            //Hiding the Retail Price field as the book is NOT FOR SALE
            viewHolder.retailPriceTextView.setVisibility(View.GONE);

            //Setting the List Price field with the default message
            viewHolder.listPriceTextView.setText(context.getString(R.string.not_for_sale_price_text));
        }
        //Updating the Price of the Book : END

        //Populating the data onto the Template View using the BookInfo object: END
    }
    @Override
    public int getItemCount() {
        return mBookInfoList.size();
    }
    public void swapItemData(@NonNull List<BookInfo> newBookInfos) {
        //Loading the List of BookInfo objects as Bundle arguments to be passed to a Loader
        Bundle args = new Bundle();
        args.putParcelableArrayList(OLD_LIST_STR_KEY, (ArrayList<? extends Parcelable>) mBookInfoList);
        args.putParcelableArrayList(NEW_LIST_STR_KEY, (ArrayList<? extends Parcelable>) newBookInfos);
        //Initiating a loader to execute the difference computation in a background thread
        ((FragmentActivity) getContext()).getSupportLoaderManager().restartLoader(BooksDiffLoader.BOOK_DIFF_LOADER, args, this);
    }
    private void doSwapItemData(DiffUtil.DiffResult diffResult, @NonNull List<BookInfo> newBookInfos) {
        Log.d(LOG_TAG, "doSwapItemData: Started");
        //Clearing the Adapter's data to load the new list of BookInfo objects
        mBookInfoList.clear();
        mBookInfoList.addAll(newBookInfos);

        //Informing the adapter about the changes required, so that it triggers the notify accordingly
        diffResult.dispatchUpdatesTo(this);

        //Dispatching the Item Data Swap event to the listener
        mItemDataSwapListener.onItemDataSwapped();
    }
    @Override
    public Loader<DiffUtil.DiffResult> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case BooksDiffLoader.BOOK_DIFF_LOADER:
                //Preparing the Diff Loader and returning the instance
                List<BookInfo> oldBookInfoList = args.getParcelableArrayList(OLD_LIST_STR_KEY);
                List<BookInfo> newBookInfoList = args.getParcelableArrayList(NEW_LIST_STR_KEY);
                return new BooksDiffLoader(getContext(), oldBookInfoList, newBookInfoList);
            default:
                return null;
        }
    }
    @Override
    public void onLoadFinished(@NonNull Loader<DiffUtil.DiffResult> loader, DiffUtil.DiffResult diffResult) {
        if (diffResult != null) {
            //When there is a result of the difference computation
            switch (loader.getId()) {
                case BooksDiffLoader.BOOK_DIFF_LOADER:
                    //Update the New Data to the Adapter and notify the changes in the data
                    doSwapItemData(diffResult, ((BooksDiffLoader) loader).getNewBookInfoList());
                    break;
            }
        }
    }
    @Override
    public void onLoaderReset(@NonNull Loader<DiffUtil.DiffResult> loader) {
        //No-op, just invalidating the loader
        loader = null;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        //Declaring the View components of the template item view
        private ImageView bookImageView;
        private TextView titleTextView;
        private TextView authorTextView;
        private TextView publisherTextView;
        private TextView publishedDateTextView;
        private TextView pagesTextView;
        private TextView categoriesTextView;
        private RatingBar bookRatingBarView;
        private TextView ratingCountTextView;
        private TextView listPriceTextView;
        private TextView retailPriceTextView;
        ViewHolder(final View itemView) {
            super(itemView);

            //Doing the view lookup for each of the item layout view's components
            bookImageView = itemView.findViewById(R.id.list_item_image_id);
            titleTextView = itemView.findViewById(R.id.list_item_title_text_id);
            authorTextView = itemView.findViewById(R.id.list_item_author_text_id);
            publisherTextView = itemView.findViewById(R.id.list_item_publ_text_id);
            publishedDateTextView = itemView.findViewById(R.id.list_item_publ_date_text_id);
            pagesTextView = itemView.findViewById(R.id.list_item_pages_text_id);
            categoriesTextView = itemView.findViewById(R.id.list_item_categories_text_id);
            bookRatingBarView = itemView.findViewById(R.id.list_item_ratingbar_id);
            ratingCountTextView = itemView.findViewById(R.id.list_item_rating_count_text_id);
            listPriceTextView = itemView.findViewById(R.id.list_item_list_price_text_id);
            retailPriceTextView = itemView.findViewById(R.id.list_item_retail_price_text_id);

            //Setting the Click Listener on the Item View
            itemView.setOnClickListener(this);
        }
       @Override
        public void onClick(View v) {
            //Retrieving the position of the item view clicked
            int adapterPosition = getAdapterPosition();
            if (adapterPosition > RecyclerView.NO_POSITION) {
                //Verifying the validity of the position before proceeding

                //Propagating the call to the Listener with the selected Item's data
                mItemClickListener.onItemClick(mBookInfoList.get(adapterPosition));
            }
        }
    }
}
