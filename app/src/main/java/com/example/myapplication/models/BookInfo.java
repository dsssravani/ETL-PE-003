package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.example.myapplication.utils.BooksDiffUtility;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookInfo implements Parcelable {
    public static final Creator<BookInfo> CREATOR = new Creator<BookInfo>() {
        @Override
        public BookInfo createFromParcel(Parcel in) {
            return new BookInfo(in);
        }
        @Override
        public BookInfo[] newArray(int size) {
            return new BookInfo[size];
        }
    };
    //Stores the id of the Book (for comparison purpose)
    private String mBookId;
    //Stores the Title of the Book
    private String mTitle;
    //Stores the SubTitle of the Book if any
    private String mSubTitle;
    //Stores the List of Authors for the Book
    private String[] mAuthors;
    //Stores the Publisher of the Book
    private String mPublisher;
    //Stores the Published Date of the Book
    private String mPublishedDateStr;
    //Stores the Number of Pages in the Book
    private int mPageCount;
    //Stores whether the Book is a Magazine or a Book
    private String mBookType;
    //Stores the list of Categories that the Book belongs to
    private String[] mCategories;
    //Stores the Book ratings
    private float mBookRatings;
    //Stores the Count of Ratings the Book has received
    private int mBookRatingCount;
    //Stores whether the book is saleable or not for the user
    private String mSaleability;
    //Stores the List price of the book
    private double mListPrice;
    //Stores the Retail price of the book
    private double mRetailPrice;
    //Stores the Description of the book
    private String mDescription;
    //Stores the link to Small Image of the Book
    private String mImageLinkSmall;
    //Stores the link to Large Image of the Book
    private String mImageLinkLarge;
    //Stores the link to Extra Large Image of the Book
    private String mImageLinkExtraLarge;
    //Stores the accessibility status details of the book
    private String mAccessViewStatus;
    //Stores the link to Sample epub download of the Book
    private String mEpubLink;
    //Stores the link to Sample pdf download of the Book
    private String mPdfLink;
    //Stores the link to web preview if available or the info page
    private String mPreviewLink;
    //Stores the link to buying page of the book
    private String mBuyLink;
    public BookInfo(String bookId) {
        mBookId = bookId;
    }
    protected BookInfo(Parcel in) {
        mBookId = in.readString();
        mTitle = in.readString();
        mSubTitle = in.readString();
        mAuthors = in.createStringArray();
        mPublisher = in.readString();
        mPublishedDateStr = in.readString();
        mPageCount = in.readInt();
        mBookType = in.readString();
        mCategories = in.createStringArray();
        mBookRatings = in.readFloat();
        mBookRatingCount = in.readInt();
        mSaleability = in.readString();
        mListPrice = in.readDouble();
        mRetailPrice = in.readDouble();
        mDescription = in.readString();
        mImageLinkSmall = in.readString();
        mImageLinkLarge = in.readString();
        mImageLinkExtraLarge = in.readString();
        mAccessViewStatus = in.readString();
        mEpubLink = in.readString();
        mPdfLink = in.readString();
        mPreviewLink = in.readString();
        mBuyLink = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mBookId);
        dest.writeString(mTitle);
        dest.writeString(mSubTitle);
        dest.writeStringArray(mAuthors);
        dest.writeString(mPublisher);
        dest.writeString(mPublishedDateStr);
        dest.writeInt(mPageCount);
        dest.writeString(mBookType);
        dest.writeStringArray(mCategories);
        dest.writeFloat(mBookRatings);
        dest.writeInt(mBookRatingCount);
        dest.writeString(mSaleability);
        dest.writeDouble(mListPrice);
        dest.writeDouble(mRetailPrice);
        dest.writeString(mDescription);
        dest.writeString(mImageLinkSmall);
        dest.writeString(mImageLinkLarge);
        dest.writeString(mImageLinkExtraLarge);
        dest.writeString(mAccessViewStatus);
        dest.writeString(mEpubLink);
        dest.writeString(mPdfLink);
        dest.writeString(mPreviewLink);
        dest.writeString(mBuyLink);
    }
    @Override
    public int describeContents() {
        return 0; //Indicating with no mask
    }
    public void setAuthors(String[] authors) {
        this.mAuthors = authors;
    }
    public void setPublisher(String publisher) {
        this.mPublisher = publisher;
    }
    public void setPublishedDateStr(String publishedDateStr) {
        this.mPublishedDateStr = publishedDateStr;
    }
    public void setCategories(String[] categories) {
        this.mCategories = categories;
    }
    public void setSaleability(String saleability) {
        this.mSaleability = saleability;
    }
    public void setDescription(String description) {
        this.mDescription = description;
    }
    public void setImageLinkSmall(String imageLinkSmall) {
        this.mImageLinkSmall = imageLinkSmall;
    }
    public void setImageLinkLarge(String imageLinkLarge) {
        this.mImageLinkLarge = imageLinkLarge;
    }
    public void setImageLinkExtraLarge(String imageLinkExtraLarge) {
        this.mImageLinkExtraLarge = imageLinkExtraLarge;
    }
    public void setAccessViewStatus(String accessViewStatus) {
        this.mAccessViewStatus = accessViewStatus;
    }
    public String getTitle() {

        if (!TextUtils.isEmpty(mSubTitle)
                && !mTitle.contains(mSubTitle)) {
            //Combining SubTitle when not empty and the Title does not contain the same
            return (mTitle + ": " + mSubTitle);
        }

        //Returning the Title only when Subtitle is not present
        return mTitle;
    }
    public void setTitle(String title) {
        this.mTitle = title;
    }
    public String getSubTitle() {
        return mSubTitle;
    }
    public void setSubTitle(String subTitle) {
        this.mSubTitle = subTitle;
    }
    public String getAuthors(String fallback) {
        int countOfAuthors = (null == mAuthors ? 0 : mAuthors.length);

        if (countOfAuthors == 1) {
            //Returning the first Author if only one is present
            return TextUtils.isEmpty(mAuthors[0]) ? fallback : mAuthors[0];
        } else if (countOfAuthors > 1) {
            //Concatenating all the Authors and returning the same
            return TextUtils.join(", ", mAuthors);
        }

        //Returning the default fallback string when no Authors are present
        return fallback;
    }
    public String getPublisher(String fallback) {
        return TextUtils.isEmpty(mPublisher) ? fallback : mPublisher;
    }
    public String getPublishedDateInMediumFormat(String fallback) throws ParseException {
        if (!TextUtils.isEmpty(mPublishedDateStr)) {
            //Splitting the date to check for the existence of Year, Month and Date
            String[] dateSplits = mPublishedDateStr.split("-");

            int noOfDateParts = dateSplits.length;

            //Returning the formatted date based on what is present in the date received
            switch (noOfDateParts) {
                case 1: //When only the Year part is present
                    return mPublishedDateStr; //Returning the Year AS-IS
                case 2: //When only the Year + Month part is present
                    Date yearMonthDate = (new SimpleDateFormat("yyyy-MM", Locale.getDefault())).parse(mPublishedDateStr);
                    //Returning the published date in the format like 'Jan, 2017'
                    return (new SimpleDateFormat("MMM, yyyy", Locale.getDefault())).format(yearMonthDate);
                case 3: //When Complete date is present
                    Date parsedDate = (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).parse(mPublishedDateStr);
                    //Returning the published date in the format like 'Jan 01, 2017'
                    return DateFormat.getDateInstance(DateFormat.MEDIUM).format(parsedDate);
                default:
                    //Returning default fallback string when no date is available
                    return fallback;
            }
        }

        //Returning default fallback string when no date is available
        return fallback;
    }
    public String getPublishedDateInLongFormat(String fallback) throws ParseException {
        if (!TextUtils.isEmpty(mPublishedDateStr)) {
            //Splitting the date to check for the existence of Year, Month and Date
            String[] dateSplits = mPublishedDateStr.split("-");

            int noOfDateParts = dateSplits.length;

            //Returning the formatted date based on what is present in the date received
            switch (noOfDateParts) {
                case 1: //When only the Year part is present
                    return mPublishedDateStr; //Returning the Year AS-IS
                case 2: //When only the Year + Month part is present
                    Date yearMonthDate = (new SimpleDateFormat("yyyy-MM", Locale.getDefault())).parse(mPublishedDateStr);
                    //Returning the published date in the format like 'January, 2017'
                    return (new SimpleDateFormat("MMMM, yyyy", Locale.getDefault())).format(yearMonthDate);
                case 3: //When Complete date is present
                    Date parsedDate = (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).parse(mPublishedDateStr);
                    //Returning the published date in the format like 'January 01, 2017'
                    return DateFormat.getDateInstance(DateFormat.LONG).format(parsedDate);
                default:
                    //Returning default fallback string when no date is available
                    return fallback;
            }
        }

        //Returning default fallback string when no date is available
        return fallback;
    }
    public int getPageCount() {
        return mPageCount;
    }
    public void setPageCount(int pageCount) {
        this.mPageCount = pageCount;
    }
    public String getBookType() {
        //Capitalizing only the first letter and returning the modified string
        return mBookType.substring(0, 1) + mBookType.substring(1).toLowerCase();
    }
    public void setBookType(String bookType) {
        this.mBookType = bookType;
    }
    public String getCategories(String fallback) {
        int countOfCategories = (null == mCategories ? 0 : mCategories.length);

        if (countOfCategories == 1) {
            //Returning the first category if only one is present
            return TextUtils.isEmpty(mCategories[0]) ? fallback : mCategories[0];
        } else if (countOfCategories > 1) {
            //Concatenating all the categories and returning the same
            return TextUtils.join(" / ", mCategories);
        }

        //Returning the default fallback string when no categories are present
        return fallback;
    }
    public float getBookRatings() {
        return mBookRatings;
    }
    public void setBookRatings(float bookRatings) {
        this.mBookRatings = bookRatings;
    }
    public String getBookRatingCount() {
        return String.valueOf(mBookRatingCount);
    }
    public void setBookRatingCount(int bookRatingCount) {
        this.mBookRatingCount = bookRatingCount;
    }
    public boolean isForSale() {
        boolean forSale = false;

        if (mSaleability.equals("FOR_SALE")) {
            forSale = true;
        } else if (mSaleability.equals("NOT_FOR_SALE")) {
            forSale = false;
        }

        return forSale;
    }
    public String getListPrice() {
        return NumberFormat.getCurrencyInstance().format(mListPrice);
    }
    public void setListPrice(double listPrice) {
        this.mListPrice = listPrice;
    }
    public String getRetailPrice() {
        return NumberFormat.getCurrencyInstance().format(mRetailPrice);
    }
    public void setRetailPrice(double retailPrice) {
        this.mRetailPrice = retailPrice;
    }
    public boolean isDiscounted() {
        //Returning True when the Retail Price is less, indicating that it is discounted
        return mRetailPrice < mListPrice;
    }
    public String getDescription(String fallback) {
        return TextUtils.isEmpty(mDescription) ? fallback : mDescription;
    }
    public String getImageLinkForItemInfo() {
        return mImageLinkSmall;
    }
    public String getImageLinkForDetailInfo() {
        return mImageLinkLarge;
    }
    public String getImageLinkForBookImageInfo() {
        return mImageLinkExtraLarge;
    }
    public String getEpubLink() {
        return mEpubLink;
    }
    public void setEpubLink(String epubLink) {
        this.mEpubLink = epubLink;
    }
    public String getPdfLink() {
        return mPdfLink;
    }
    public void setPdfLink(String pdfLink) {
        this.mPdfLink = pdfLink;
    }
    public String getPreviewLink() {
        return mPreviewLink;
    }
    public void setPreviewLink(String previewLink) {
        this.mPreviewLink = previewLink;
    }
    public String getBookId() {
        return mBookId;
    }
    public boolean isSampleAvailable() {
        boolean isSamplePresent = false;

        if (mAccessViewStatus.equals("SAMPLE")) {
            //Sample is present when Access View Status says "SAMPLE"
            isSamplePresent = true;
        } else if (mAccessViewStatus.equals("NONE")) {
            //Sample is NOT present when Access View Status says "NONE"
            isSamplePresent = false;
        }

        //Returning the Sample availability status evaluated
        return isSamplePresent;
    }
    public String getBuyLink() {
        return mBuyLink;
    }
    public void setBuyLink(String buyLink) {
        this.mBuyLink = buyLink;
    }

}
