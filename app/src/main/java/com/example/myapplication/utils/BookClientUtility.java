package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.models.BookInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookClientUtility {

    //Constant used for the Book Volumes based calls
    public final static String VOL_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    //Constant used for logs
    private final static String LOG_TAG = BookClientUtility.class.getSimpleName();
    public static List<BookInfo> searchAndExtractVolumes(URL urlObject) {
        //If URL Object is not formed then return as NULL
        if (urlObject == null) {
            return null;
        }

        //Making the HTTP Request to retrieve the JSON Response
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpGetRequest(urlObject);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while closing the URL Stream\n", e);
        }

        //If the response received is null or empty, then return as NULL
        if (jsonResponse == null
                || jsonResponse.trim().length() == 0) {
            return null;
        }

        //Returning the List of {@link BookInfo} objects containing the parsed information
        return extractVolumesFromResponse(jsonResponse);
    }

    private static List<BookInfo> extractVolumesFromResponse(String jsonResponse) {
        //Initializing an ArrayList of BookInfo objects to store the data parsed
        ArrayList<BookInfo> bookInfoList = new ArrayList<>();

        try {
            //Retrieving the root JSON Object
            JSONObject rootJsonObject = new JSONObject(jsonResponse);
            //Retrieving the 'items' JSON Array
            JSONArray itemsJsonArray = rootJsonObject.optJSONArray("items");

            //Returning if the 'items' JSON Array does not exist
            if (itemsJsonArray == null) {
                return bookInfoList;
            }

            //Retrieving the number of Items present
            int noOfItems = itemsJsonArray.length();

            //Iterating over the 'items' JSON Array
            for (int index = 0; index < noOfItems; index++) {
                //Retrieving the current item JSON Object
                JSONObject itemJsonObject = itemsJsonArray.getJSONObject(index);

                //Retrieving the Id of the Book Volume
                String bookId = itemJsonObject.getString("id");

                //Creating a new {@link BookInfo} object to store the parsed data
                BookInfo bookInfo = new BookInfo(bookId);

                //Retrieving the 'volumeInfo' JSON Object
                JSONObject volumeInfoJsonObject = itemJsonObject.getJSONObject("volumeInfo");

                //Parsing and storing the Title
                bookInfo.setTitle(volumeInfoJsonObject.getString("title"));
                //Parsing and storing the SubTitle if any
                bookInfo.setSubTitle(volumeInfoJsonObject.optString("subtitle"));

                //Parsing and storing the Authors list : START
                JSONArray authorsJsonArray = volumeInfoJsonObject.optJSONArray("authors");
                if (authorsJsonArray != null) {
                    //When authors are present
                    int noOfAuthors = authorsJsonArray.length();
                    String[] authors = new String[noOfAuthors];
                    for (int i = 0; i < noOfAuthors; i++) {
                        authors[i] = authorsJsonArray.getString(i);
                    }
                    bookInfo.setAuthors(authors);
                } else {
                    //Storing as null when authors are absent
                    bookInfo.setAuthors(null);
                }
                //Parsing and storing the Authors list : END

                //Parsing and storing the Publisher
                bookInfo.setPublisher(volumeInfoJsonObject.optString("publisher", ""));
                //Parsing and storing the Published date
                bookInfo.setPublishedDateStr(volumeInfoJsonObject.optString("publishedDate", ""));
                //Parsing and storing the Number of Pages
                bookInfo.setPageCount(volumeInfoJsonObject.optInt("pageCount", 1));
                //Parsing and storing the Book Type
                bookInfo.setBookType(volumeInfoJsonObject.getString("printType"));

                //Parsing and storing the Categories list : START
                JSONArray categoriesJsonArray = volumeInfoJsonObject.optJSONArray("categories");
                if (categoriesJsonArray != null) {
                    //When categories are present
                    int noOfCategories = categoriesJsonArray.length();
                    String[] categories = new String[noOfCategories];
                    for (int i = 0; i < noOfCategories; i++) {
                        categories[i] = categoriesJsonArray.getString(i);
                    }
                    bookInfo.setCategories(categories);
                } else {
                    //Storing as null when categories are absent
                    bookInfo.setCategories(null);
                }
                //Parsing and storing the Categories list : END

                //Parsing and storing the ratings
                bookInfo.setBookRatings((float) volumeInfoJsonObject.optDouble("averageRating", 0.0));
                //Parsing and storing the rating count
                bookInfo.setBookRatingCount(volumeInfoJsonObject.optInt("ratingsCount", 0));
                //Parsing and storing the Description
                bookInfo.setDescription(volumeInfoJsonObject.optString("description", ""));

                //Parsing and storing the links to the Images : START
                JSONObject imageLinksJsonObject = volumeInfoJsonObject.optJSONObject("imageLinks");
                if (imageLinksJsonObject != null) {
                    //When Image Links are present

                    //Retrieving all the Image Links
                    String smallThumbnailLink = imageLinksJsonObject.getString("smallThumbnail");
                    String thumbnailLink = imageLinksJsonObject.optString("thumbnail", smallThumbnailLink);
                    String smallImageLink = imageLinksJsonObject.optString("small", thumbnailLink);
                    String mediumImageLink = imageLinksJsonObject.optString("medium", smallImageLink);
                    String largeImageLink = imageLinksJsonObject.optString("large", mediumImageLink);
                    String extraLargeImageLink = imageLinksJsonObject.optString("extraLarge", largeImageLink);
                    //Storing the required Links
                    bookInfo.setImageLinkSmall(smallImageLink);
                    bookInfo.setImageLinkLarge(largeImageLink);
                    bookInfo.setImageLinkExtraLarge(extraLargeImageLink);
                } else {
                    //Storing as Null when Image Links are absent
                    bookInfo.setImageLinkSmall(null);
                    bookInfo.setImageLinkLarge(null);
                    bookInfo.setImageLinkExtraLarge(null);
                }
                //Parsing and storing the links to the Images : END

                //Retrieving the 'saleInfo' JSON Object
                JSONObject saleInfoJsonObject = itemJsonObject.getJSONObject("saleInfo");

                //Parsing and storing the saleability of the book
                bookInfo.setSaleability(saleInfoJsonObject.getString("saleability"));

                //Parsing and storing the List Price : START
                JSONObject listPriceJsonObject = saleInfoJsonObject.optJSONObject("listPrice");
                if (listPriceJsonObject != null) {
                    //Parsing the 'amount' when 'listPrice' JSON Object is present
                    bookInfo.setListPrice(listPriceJsonObject.getDouble("amount"));
                } else {
                    //Setting to 0 when 'listPrice' JSON Object is NOT present
                    bookInfo.setListPrice(0.0);
                }
                //Parsing and storing the List Price : END

                //Parsing and storing the Retail Price : START
                JSONObject retailPriceJsonObject = saleInfoJsonObject.optJSONObject("retailPrice");
                if (retailPriceJsonObject != null) {
                    //Parsing the 'amount' when 'retailPrice' JSON Object is present
                    bookInfo.setRetailPrice(retailPriceJsonObject.getDouble("amount"));
                } else {
                    //Setting to 0 when 'retailPrice' JSON Object is NOT present
                    bookInfo.setRetailPrice(0.0);
                }
                //Parsing and storing the Retail Price : END

                //Parsing and storing the Buy Link
                bookInfo.setBuyLink(saleInfoJsonObject.optString("buyLink", ""));

                //Retrieving the 'accessInfo' JSON Object
                JSONObject accessInfoJsonObject = itemJsonObject.getJSONObject("accessInfo");

                //Parsing and storing the epub sample link
                bookInfo.setEpubLink(accessInfoJsonObject.getJSONObject("epub").optString("acsTokenLink", ""));
                //Parsing and storing the pdf sample link
                bookInfo.setPdfLink(accessInfoJsonObject.getJSONObject("pdf").optString("acsTokenLink", ""));
                //Parsing and storing the web sample link
                bookInfo.setPreviewLink(accessInfoJsonObject.getString("webReaderLink"));
                //Parsing and storing the Sample access view status
                bookInfo.setAccessViewStatus(accessInfoJsonObject.getString("accessViewStatus"));

                //Adding the BookInfo object to the List
                bookInfoList.add(bookInfo);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error occurred while parsing the JSON Response\n", e);
        }

        //Returning the list of {@link BookInfo} objects parsed
        return bookInfoList;
    }
    public static String makeHttpGetRequest(URL urlObject) throws IOException {
        //Declaring the JSON Response String and defaulting to empty string
        String jsonResponse = "";

        //Declaring the URLConnection and InputStream objects
        HttpURLConnection urlConnection = null;
        InputStream urlConnectionInputStream = null;

        try {
            urlConnection = (HttpURLConnection) urlObject.openConnection();
            urlConnection.setReadTimeout(10000); //10 Seconds Read Timeout
            urlConnection.setRequestMethod("GET"); //Request Method set to GET
            urlConnection.connect(); //Establishing connection

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //When the response is OK(200), then read the response
                urlConnectionInputStream = urlConnection.getInputStream();
                jsonResponse = readStream(urlConnectionInputStream);
            } else {
                //When the response is not OK(200), then log the error code
                Log.e(LOG_TAG, "HTTP GET Request failed with the code " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while opening connection to URL\n", e);
        } finally {

            if (urlConnection != null) {
                //Disconnecting in the end if the connection was established
                urlConnection.disconnect();
            }

            if (urlConnectionInputStream != null) {
                //Closing the URL Stream if opened
                urlConnectionInputStream.close();
            }

        }

        //Returning the JSON Response received
        return jsonResponse;
    }
    private static String readStream(InputStream urlConnectionInputStream) {
        //StringBuilder instance to build and store the response read
        StringBuilder responseBuilder = new StringBuilder();

        //Reading the response through BufferedReader
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnectionInputStream));
        try {
            String readStr = "";
            while ((readStr = bufferedReader.readLine()) != null) {
                //When Not Null, appending the line read to the Builder
                responseBuilder.append(readStr);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error occurred while reading the response stream\n", e);
            //resetting the StringBuilder to empty string
            responseBuilder.delete(0, responseBuilder.length());
        }

        //Returning the response read
        return responseBuilder.toString();
    }

}