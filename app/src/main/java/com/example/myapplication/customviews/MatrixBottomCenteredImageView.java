package com.example.myapplication.customviews;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

public class MatrixBottomCenteredImageView extends AppCompatImageView{
    public  MatrixBottomCenteredImageView (Context context){
        this(context,null);
    }
    public MatrixBottomCenteredImageView(Context context,AttributeSet attrs){
        this(context,attrs,0);
    }
    public MatrixBottomCenteredImageView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }
}
