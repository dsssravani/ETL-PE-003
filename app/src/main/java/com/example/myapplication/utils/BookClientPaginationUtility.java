package com.example.myapplication.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class BookClientPaginationUtility {

    //Constant used for logs
    private static final String LOG_TAG = BookClientPaginationUtility.class.getSimpleName();
    public static int getLastPageIndex(URL urlObject, int itemsPerPage,
                                       int startPageIndex, int lastPageIndex) {

        //If URL Object is not formed then return the same lastPageIndex
        if (urlObject == null) {
            return lastPageIndex;
        }

        //Returning the calculated value of the last page index
        return getCalculatedLastPageIndex(urlObject, itemsPerPage, startPageIndex, lastPageIndex);
    }
    private static int getCalculatedLastPageIndex(URL urlObject, int itemsPerPage,
                                                  int startPageIndex, int lastPageIndex) {

        //Making the HTTP Request to retrieve the JSON Response
        String jsonResponse = "";
        try {
            jsonResponse = BookClientUtility.makeHttpGetRequest(urlObject);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while closing the URL Stream\n", e);
        }

        //If the response received is null or empty, then return the value of lastPageIndex
        if (jsonResponse == null
                || jsonResponse.trim().length() == 0) {
            return lastPageIndex;
        }

        try {
            //Retrieving the root JSON Object
            JSONObject rootJsonObject = new JSONObject(jsonResponse);
            //Checking for any error received
            JSONObject errorJsonObject = rootJsonObject.optJSONObject("error");
            //Returning the value of lastPageIndex if error was returned
            if (errorJsonObject != null) {
                return lastPageIndex;
            }

            //Retrieving the count of total items found from the current response if present
            int totalItemsFound = rootJsonObject.optInt("totalItems");
            if (totalItemsFound > 0) {
                //Retrieving the 'items' JSON Array if present
                JSONArray itemsJsonArray = rootJsonObject.optJSONArray("items");

                if (itemsJsonArray != null) {
                    //When items are returned

                    //Retrieving the number of Items present
                    int noOfItems = itemsJsonArray.length();

                    if (noOfItems > 0) {
                        //When some items are found in the response

                        //Calculating the Number of pages from the total number of items found
                        //based on the max results per page setting
                        int noOfPagesLeft = (int) Math.floor((float) (totalItemsFound - noOfItems) / (float) itemsPerPage);
                        //Returning with the lastPageIndex evaluated as current startPageIndex + noOfPagesLeft
                        return (startPageIndex + noOfPagesLeft);

                    } else {
                        //Returning the lastPageIndex evaluated when there are no actual items found in the response
                        return lastPageIndex;
                    }

                } else {
                    //Returning the lastPageIndex evaluated when there are no actual items found in the response
                    return lastPageIndex;
                }

            } else {
                //Returning the lastPageIndex evaluated when there are no 'totalItems' found in the response
                return lastPageIndex;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error occurred while parsing the JSON Response\n", e);
        }

        //Returning the evaluated lastPageIndex for the search query
        return lastPageIndex;
    }

}