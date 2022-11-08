package com.example.myapplication.workers;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.DiffUtil;

import com.example.myapplication.models.BookInfo;
import com.example.myapplication.utils.BooksDiffUtility;

import java.util.List;
public class BooksDiffLoader extends AsyncTaskLoader<DiffUtil.DiffResult> {

    //Integer Constant used for the Diff Loader
    public final static int BOOK_DIFF_LOADER = 101;

    //Stores the current List of BookInfo objects to be compared
    private List<BookInfo> mOldBookInfoList;

    //Stores the New List of BookInfo objects to be compared
    private List<BookInfo> mNewBookInfoList;

    //Stores the result of the difference computation between the
    //current and new List of BookInfo Objects
    private DiffUtil.DiffResult mDiffResult;
    public BooksDiffLoader(Context context, List<BookInfo> oldBookInfos, List<BookInfo> newBookInfos) {
        super(context);
        mOldBookInfoList = oldBookInfos;
        mNewBookInfoList = newBookInfos;
    }
    @Override
    public DiffUtil.DiffResult loadInBackground() {
        //Instantiating the DiffUtil for difference computation
        BooksDiffUtility booksDiffUtility = new BooksDiffUtility(mOldBookInfoList, mNewBookInfoList);
        //Computing the Difference and returning the result
        return DiffUtil.calculateDiff(booksDiffUtility);
    }
    @Override
    public void deliverResult(DiffUtil.DiffResult diffResult) {
        if (isReset()) {
            //Ignoring the result as the loader is already reset
            diffResult = null;
            //Returning when the loader is already reset
            return;
        }

        //Saving a reference to the old data as we are about to deliver the result
        DiffUtil.DiffResult oldDiffResult = mDiffResult;
        mDiffResult = diffResult;

        if (isStarted()) {
            //Delivering the result when the loader is started
            super.deliverResult(diffResult);
        }

        //Invalidating the old result as it is not required anymore
        if (oldDiffResult != null && oldDiffResult != mDiffResult) {
            oldDiffResult = null;
        }
    }
    @Override
    protected void onStartLoading() {
        if (mDiffResult != null) {
            //Deliver the result immediately if already present
            deliverResult(mDiffResult);
        }

        if (takeContentChanged() || mDiffResult == null) {
            //Force a new load when the data is not yet retrieved
            //or the content has changed
            forceLoad();
        }
    }
    @Override
    protected void onStopLoading() {
        //Canceling the load if any as the loader has entered Stopped state
        cancelLoad();
    }

    @Override
    protected void onReset() {
        //Ensuring the loader has stopped
        onStopLoading();

        //Releasing the resources associated with the loader
        releaseResources();
    }
    @Override
    public void onCanceled(DiffUtil.DiffResult data) {
        //Canceling any asynchronous load
        super.onCanceled(data);

        //Releasing the resources associated with the loader, as the loader is canceled
        releaseResources();
    }
    private void releaseResources() {
        //Invalidating the Loader data
        if (mOldBookInfoList != null) {
            mOldBookInfoList = null;
        }

        if (mNewBookInfoList != null) {
            mNewBookInfoList = null;
        }

        if (mDiffResult != null) {
            mDiffResult = null;
        }

    }
    public List<BookInfo> getNewBookInfoList() {
        return mNewBookInfoList;
    }

}
