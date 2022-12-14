package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.example.myapplication.cache.BitmapImageCache;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtility {

    //Constant used for logs
    private static final String LOG_TAG = ImageUtility.class.getSimpleName();

    public static Bitmap downloadFromURL(String imageURLStr) {
        //Creating the URL
        URL imageURL = createURL(imageURLStr);

        //Returning NULL when the URL of the Image URL string is not prepared due to some failure
        if (imageURL == null) {
            return null;
        }

        //Declaring a bitmap to store the image downloaded
        Bitmap bitmap = null;

        //Declaring the HTTP Connection
        HttpURLConnection urlConnection = null;
        //Declaring the HTTP Input Stream
        InputStream imageInputStream = null;

        try {
            urlConnection = (HttpURLConnection) imageURL.openConnection();
            urlConnection.setConnectTimeout(10000); //10seconds as Connection Timeout
            urlConnection.connect(); //Connecting to the Image URL

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //When the Response code is OK(200), then download and construct the Bitmap
                imageInputStream = urlConnection.getInputStream();
                bitmap = getSampledBitmapImage(imageInputStream);
            } else {
                //When the response is not OK(200), then log the error code
                Log.e(LOG_TAG, "HTTP Request to Image URL failed with the code " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while opening connection to the Image URL\n", e);
        } finally {
            if (urlConnection != null) {
                //Disconnecting in the end if the connection was established
                urlConnection.disconnect();
            }

            if (imageInputStream != null) {
                //Closing the Image Input Stream if opened
                try {
                    imageInputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error occurred while closing the Image Input Stream\n", e);
                }
            }

            //Adding the Bitmap to Memory Cache if generated
            if (bitmap != null) {
                BitmapImageCache.addBitmapToCache(imageURLStr, bitmap);
            }
        }

        //Returning the Bitmap Image downloaded
        return bitmap;
    }
    private static Bitmap getSampledBitmapImage(InputStream imageInputStream) {
        //Retrieving the Image Byte Array for downsampling
        byte[] imageByteArray = getImageByteArrayForProcessing(imageInputStream);

        //Returning NULL when the Image Byte array was not generated due to some failure
        if (imageByteArray == null) {
            return null;
        }

        //Retrieving the Down Sampling factor to downsample the image if necessary
        int downScalingFactorSize = getDownSamplingFactor(imageByteArray);

        //Returning the Down Sampled Bitmap Image
        return getDownSampledImage(imageByteArray, downScalingFactorSize);
    }
    private static Bitmap getDownSampledImage(byte[] imageByteArray, int downScalingFactorSize) {
        //Rendering the Downscaled Bitmap image through the BitmapFactory Options
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inPreferQualityOverSpeed = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmapOptions.inPremultiplied = false;
            bitmapOptions.inScaled = false;
        }

        //Setting the Down Scaling factor determined
        bitmapOptions.inSampleSize = downScalingFactorSize;

        //Returning the Down Scaled Image
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, bitmapOptions);
    }
    private static int getDownSamplingFactor(byte[] imageByteArray) {
        //Rendering only the Image Bounds through the BitmapFactory Options
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        bitmapOptions.inPreferQualityOverSpeed = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmapOptions.inPremultiplied = false;
            bitmapOptions.inScaled = false;
        }

        //Decoding the Image Bounds
        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, bitmapOptions);

        //Setting the required dimensions of the Image to (300 x 400)
        int reqdWidth = 300;
        int reqdHeight = 400;

        //Retrieving the Raw dimensions of the current Image decoded through BitmapFactory Options
        int rawWidth = bitmapOptions.outWidth;
        int rawHeight = bitmapOptions.outHeight;

        //Deriving the Scaling factor to downsample the image : START
        //Starting with the Downsampling factor of 1
        int downScalingFactorSize = 1;

        if (rawWidth > reqdWidth || rawHeight > reqdHeight) {
            //Calculating half raw dimensions
            int halfWidth = rawWidth / 2;
            int halfHeight = rawHeight / 2;

            //Calculating the Down Scaling factor
            while ((halfWidth / downScalingFactorSize) >= reqdWidth
                    && (halfHeight / downScalingFactorSize) >= reqdHeight) {
                downScalingFactorSize *= 2;
            }

        }
        //Deriving the Scaling factor to downsample the image : END

        //Returning the Downsampling factor calculated
        return downScalingFactorSize;
    }
    private static byte[] getImageByteArrayForProcessing(InputStream imageInputStream) {
        //Creating a Buffered Input Stream of the Image Stream
        BufferedInputStream bufferedImageInputStream = new BufferedInputStream(imageInputStream);

        //Initializing a ByteArrayOutputStream to save the Image in a Byte Array for downsampling
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        //Declaring Byte Array with a buffer size to buffer the content read
        byte[] byteBuff = new byte[4096];
        int bytesRead; //Stores the Bytes read

        //Declaring the Byte Array to be returned for processing
        byte[] imageByteArray;

        //Writing the Bytes read to the ByteArrayOutputStream
        try {
            while ((bytesRead = bufferedImageInputStream.read(byteBuff, 0, byteBuff.length)) > 0) {
                byteArrayOutputStream.write(byteBuff, 0, bytesRead);
            }

            //Converting the bytes read to a Byte Array for image downsampling
            imageByteArray = byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while reading the Image Stream\n", e);
            return null; //Returning Null on Error

        } finally {
            //Closing the ByteArrayOutputStream when done
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error occurred while closing the ByteArrayOutputStream after writing the image to a Byte array\n", e);
            }

            //Closing the BufferedInputStream
            try {
                bufferedImageInputStream.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error occurred while closing the BufferedInputStream of the Image InputStream\n", e);
            }
        }

        //Returning the Image Byte Array prepared for processing
        return imageByteArray;
    }
    private static URL createURL(String imageURLStr) {
        //Declaring the URL for Image
        URL imageURL = null;

        //Returning Null when the Image URL String is empty
        if (TextUtils.isEmpty(imageURLStr)) {
            return null;
        }

        //Preparing the URL
        try {
            imageURL = new URL(imageURLStr);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error occurred while creating the URL from Image URL String\n", e);
        }

        //Returning the URL formed
        return imageURL;
    }

}