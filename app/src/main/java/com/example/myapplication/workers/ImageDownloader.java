package com.example.myapplication.workers;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.loader.content.AsyncTaskLoader;

import com.example.myapplication.utils.ImageUtility;
import com.example.myapplication.utils.NetworkUtility;

public class ImageDownloader extends AsyncTaskLoader<Bitmap> {

    //Stores the Image URL String from which the Image needs to be downloaded
    private String mImageURLStr;

    //Stores the Bitmap Downloaded
    private Bitmap mDownloadedBitmap;
    public ImageDownloader(Context context, String imageURLStr) {
        super(context);
        mImageURLStr = imageURLStr;
    }
    @Override
    public Bitmap loadInBackground() {
        //Proceeding to download when the Internet Connectivity is established
        if (NetworkUtility.isNetworkConnected(getContext())) {
            //Downloading the Image from URL and returning the Bitmap
            Bitmap downloadedBitmap = ImageUtility.downloadFromURL(mImageURLStr);
            if (downloadedBitmap != null) {
                //Uploading the Bitmap to GPU for caching in background thread (for faster loads)
                downloadedBitmap.prepareToDraw();
            }
            return downloadedBitmap; //Returning the Bitmap downloaded
        }

        //For all else, returning null
        return null;
    }
    @Override
    public void deliverResult(Bitmap newBitmap) {
        if (isReset()) {
            //Ignoring the result if the loader is already reset
            newBitmap = null;
            //Returning when the loader is already reset
            return;
        }

        //Storing a reference to the old Bitmap as we are about to deliver the result
        Bitmap oldBitmap = mDownloadedBitmap;
        mDownloadedBitmap = newBitmap;

        if (isStarted()) {
            //Delivering the result when the loader is started
            super.deliverResult(newBitmap);
        }

        //invalidating the old bitmap as it is not required anymore
        if (oldBitmap != null && oldBitmap != newBitmap) {
            oldBitmap = null;
        }

    }
    @Override
    protected void onStartLoading() {
        if (mDownloadedBitmap != null) {
            //Deliver the result immediately if the Bitmap is already downloaded
            deliverResult(mDownloadedBitmap);
        }

        if (takeContentChanged() || mDownloadedBitmap == null) {
            //Force a new Load when the Bitmap Image is not yet downloaded
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

        //Releasing the resources associated with the Loader
        releaseResources();
    }
    @Override
    public void onCanceled(Bitmap data) {
        //Canceling any asynchronous load
        super.onCanceled(data);

        //Releasing the resources associated with the Loader, as the Loader is canceled
        releaseResources();
    }
    private void releaseResources() {
        //Invalidating the Loader data
        if (mDownloadedBitmap != null) {
            mDownloadedBitmap = null;
        }
    }
    public String getImageURLStr() {
        return mImageURLStr;
    }

}
