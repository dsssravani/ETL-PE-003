package com.example.myapplication.utils;

import android.content.Context;
import androidx.annotation.ColorInt;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

public class TextAppearanceUtility {
    //Constant used for logs
    private static final String LOG_TAG = TextAppearanceUtility.class.getSimpleName();
    public static void setStrikethroughText(TextView textView, String textToSet, String textToStrike) {
        //Initializing a SpannableStringBuilder to create a Strikethrough text
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        //Appending the Text to be set
        spannableStringBuilder.append(textToSet);

        //Using the android.text.style.StrikethroughSpan for the Strikethrough text appearance
        //Determining the Span length based on the Text values passed
        if (textToSet.equals(textToStrike)) {
            //When the Text to be set is same as the Strikethrough text
            spannableStringBuilder.setSpan(new StrikethroughSpan(), 0, textToStrike.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            //When the Strikethrough text is a subset of the Text to be set
            int startIndex = TextUtils.indexOf(textToSet, textToStrike);
            int endIndex = startIndex + textToStrike.length();
            spannableStringBuilder.setSpan(new StrikethroughSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //Setting the Spannable Text on TextView with the SPANNABLE Buffer type,
        //for later modification on spannable if required
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }
    public static void setStrikethroughText(TextView textView, String textToSet) {
        //Propagating the call to the actual method with the parameter 'textToStrike' having
        //the value defaulted to 'textToSet'
        setStrikethroughText(textView, textToSet, textToSet);
    }
    public static void modifyTextColor(TextView textView, String textToColor, @ColorInt int color) {
        //Retrieving the Text from TextView as a Spannable Text
        Spannable spannable = (Spannable) textView.getText();

        //Setting the ForegroundColorSpan to color the part of the Text in the color passed
        int startIndex = TextUtils.indexOf(spannable, textToColor);
        int endIndex = startIndex + textToColor.length();
        spannable.setSpan(
                new ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }
    public static void modifyTextSize(TextView textView, String textToSet, String textToResize, float resizeFactor) {
        //Initializing a SpannableStringBuilder to build the text
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        //Appending the Text to be set
        spannableStringBuilder.append(textToSet);

        //Resizing the Text 'textToResize' to the relative size mentioned by the factor 'resizeFactor'
        int startIndex = TextUtils.indexOf(spannableStringBuilder, textToResize);
        int endIndex = startIndex + textToResize.length();
        spannableStringBuilder.setSpan(
                new RelativeSizeSpan(resizeFactor),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        //Setting the Spannable Text on TextView with the NORMAL Buffer type,
        //for later modification of Text if required
        textView.setText(spannableStringBuilder, TextView.BufferType.NORMAL);
    }
   public static void replaceTextWithImage(Context context, TextView textView) {
        //Retrieving the Text from TextView as a Spannable Text
        Spannable spannable = (Spannable) textView.getText();

        //To keep track of the last character index scanned
        int previousEndIndex = 0;
        //Ensuring that we are using the App's Context
        context = context.getApplicationContext();
        //Read-Only text for iteration
        final String textContentStr = textView.getText().toString();

        do {
            //Retrieving the start and end Index of the identifiers embedded in the TextView's Text,
            //starting with the index of the last scanned character
            int startIndex = TextUtils.indexOf(textContentStr, '[', previousEndIndex);
            int endIndex = TextUtils.indexOf(textContentStr, ']', previousEndIndex);

            if (startIndex > -1 && endIndex > -1) {
                //When the drawable identifier string is present

                //Retrieving the drawable identifier string embedded in the TextView's Text
                String drawableString = TextUtils.substring(textContentStr, startIndex + 1, endIndex);

                //Retrieving the identifier from the current drawable package
                int drawableResId = context.getResources().getIdentifier(drawableString, "drawable", context.getPackageName());
                if (drawableResId == 0) {
                    //If the drawable resource was not found in the current package
                    //then look up the android source package
                    drawableResId = context.getResources().getIdentifier(drawableString, "drawable", "android");
                }

                //When the identifier of the drawable is retrieved
                if (drawableResId > 0) {
                    //Creating an ImageSpan for the drawable found
                    ImageSpan imageSpan = new ImageSpan(context, drawableResId, ImageSpan.ALIGN_BOTTOM);
                    //Updating the ImageSpan onto the spannable at the position of the identified part of the text
                    spannable.setSpan(
                            imageSpan, startIndex, endIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }

                //Updating the previous end index with the current end index for the next iteration
                previousEndIndex = endIndex + 1;

            } else {
                //Break out when the drawable identifier string is no longer present
                break;
            }

        }
        while (true); //Repeats till entire Text is scanned once for the drawable identifier strings
    }

}
