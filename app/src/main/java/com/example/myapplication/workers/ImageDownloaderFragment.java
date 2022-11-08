package com.example.myapplication.workers;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.cache.BitmapImageCache;
public class ImageDownloaderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Bitmap> {

    //Constant used for logs and Fragment Tag
    private static final String LOG_TAG = ImageDownloaderFragment.class.getSimpleName();

    //Stores the ImageView component that needs to be updated when the Image is downloaded
    private ImageView mImageView;

    //Stores the Image URL whose Image is to be downloaded
    private String mImageURLStr;
    public static ImageDownloaderFragment newInstance(FragmentManager fragmentManager, int tagId) {
        //Creating the Fragment Tag string using the tagId
        String fragmentTagStr = LOG_TAG + "_" + tagId;

        //Retrieving the Fragment from the FragmentManager if existing
        ImageDownloaderFragment imageDownloaderFragment
                = (ImageDownloaderFragment) fragmentManager.findFragmentByTag(fragmentTagStr);
        if (imageDownloaderFragment == null) {
            //When the Fragment is being added for the first time

            //Instantiating the Fragment
            imageDownloaderFragment = new ImageDownloaderFragment();
            //Adding the Fragment to Transaction
            fragmentManager.beginTransaction().add(imageDownloaderFragment, fragmentTagStr).commit();
        }

        //Returning the Fragment instance
        return imageDownloaderFragment;
    }
    public void executeAndUpdate(ImageView imageView, String imageURLStr, int loaderId) {
        //Saving the parameters passed
        mImageView = imageView;
        mImageURLStr = imageURLStr;

        //Looking up for the Image in Memory Cache for the given URL
        Bitmap bitmap = BitmapImageCache.getBitmapFromCache(mImageURLStr);
        if (bitmap != null) {
            //When Bitmap image was present in Memory Cache, update the ImageView
            mImageView.setImageBitmap(bitmap);
        } else {
            //Resetting the ImageView to the default Book Image for lazy loading
            mImageView.setImageResource(R.drawable.ic_book);

            //Starting the download when Bitmap image is not available from Memory Cache: START
            LoaderManager loaderManager = ((FragmentActivity) mImageView.getContext()).getSupportLoaderManager();
            boolean isNewImageURLStr = false; //Boolean to check if we need to restart the loader
            Loader<Bitmap> loader = loaderManager.getLoader(loaderId); //Getting the loader at the loaderId
            if (loader instanceof ImageDownloader) {
                //Validating the loader and casting to ImageDownloader
                ImageDownloader imageDownloader = (ImageDownloader) loader;
                //Checking for inequality of the Image URL with the one from the loader
                isNewImageURLStr = !mImageURLStr.equals(imageDownloader.getImageURLStr());
            }

            if (isNewImageURLStr) {
                //Restarting the Loader when the ImageURL is new
                loaderManager.restartLoader(loaderId, null, this);
            } else {
                //Invoking the Loader AS-IS if the ImageURL is the same
                //or if the Loader is not yet registered with the loaderId passed
                loaderManager.initLoader(loaderId, null, this);
            }
            //Starting the download when Bitmap image is not available from Memory Cache: END
        }
    }
    @NonNull
    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        //Returning an Instance of ImageDownloader to start the Image download
        return new ImageDownloader(mImageView.getContext(), mImageURLStr);
    }
    @Override
    public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap bitmapImage) {
        if (bitmapImage != null && mImageView != null) {
            //Updating the ImageView when the Bitmap is downloaded successfully
            mImageView.setImageBitmap(bitmapImage);
        } else if (mImageView != null) {
            //Resetting the ImageView to the default Book Image when the Bitmap failed to download
            mImageView.setImageResource(R.drawable.ic_book);
        }
    }
     @Override
    public void onLoaderReset(@NonNull Loader<Bitmap> loader) {
        //Resetting the ImageView to the default Book Image
        mImageView.setImageResource(R.drawable.ic_book);
    }

}
