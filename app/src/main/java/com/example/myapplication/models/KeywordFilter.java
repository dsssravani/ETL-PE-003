package com.example.myapplication.models;

public class KeywordFilter {

    //Stores the Search Keyword filter's Name
    private String mFilterName;

    //Stores the Search Keyword filter's Description
    private String mFilterDesc;

    //Stores the actual value of the Search Keyword Filter
    private String mFilterValue;

    public KeywordFilter(String filterName, String filterDesc, String filterValue) {
        this.mFilterName = filterName;
        this.mFilterDesc = filterDesc;
        this.mFilterValue = filterValue;
    }
    public String getFilterName() {
        return mFilterName;
    }
    public String getFilterDesc() {
        return mFilterDesc;
    }
    public String getFilterValue() {
        return mFilterValue;
    }

}