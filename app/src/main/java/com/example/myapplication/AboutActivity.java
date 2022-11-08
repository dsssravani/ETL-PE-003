package com.example.myapplication;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity
        implements View.OnClickListener {

    //Constant used for Logs
    private static final String LOG_TAG = AboutActivity.class.getSimpleName();

    //Method invoked by the system to create and setup the layout 'R.layout.activity_about'
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Displaying the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Finding the Title TextView
        TextView titleTextView = findViewById(R.id.abt_title_text_id);
        //Setting its Font
        titleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/coprgtb.ttf"));

        //Retrieving the font to be used for the content text
        Typeface contentTypeface = Typeface.createFromAsset(getAssets(), "fonts/quintessential_regular.ttf");

        //Finding the TextView for the first line of content
        TextView firstLineTextView = findViewById(R.id.abt_text_1_id);
        //Generating the Html Text
        Spanned htmlSpannedText;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlSpannedText = Html.fromHtml(getString(R.string.abt_content_textline_2), Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmlSpannedText = Html.fromHtml(getString(R.string.abt_content_textline_2));
        }
        //Setting the above Html Text
        firstLineTextView.setText(htmlSpannedText);
        //Setting the font
        firstLineTextView.setTypeface(contentTypeface);

        //Finding the TextView for the second line of content
        TextView secondLineTextView = findViewById(R.id.abt_text_2_id);
        //Setting the Font
        secondLineTextView.setTypeface(contentTypeface);

        //Finding the Click Views
        ImageButton googleBrandingButton = findViewById(R.id.abt_google_branding_image_id);
        ImageView udacityImageView = findViewById(R.id.abt_udacity_image_id);
        ImageView githubImageView = findViewById(R.id.abt_github_image_id);
        ImageView linkedinImageView = findViewById(R.id.abt_linkedin_image_id);

        //Registering Listener on Click Views
        googleBrandingButton.setOnClickListener(this);
        udacityImageView.setOnClickListener(this);
        githubImageView.setOnClickListener(this);
        linkedinImageView.setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Executing based on MenuItem's Id
        switch (item.getItemId()) {
            case android.R.id.home:
                //Handling the action bar's home/up button
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onClick(View view) {
        //Executing action based on View's id
        switch (view.getId()) {
            case R.id.abt_google_branding_image_id:
                //For the "powered by Google" Button
                //Opens the webpage for the Google Books API
                openLink(getString(R.string.google_books_api_link));
                break;
        }
    }
   private void openLink(String urlString) {
        //Parsing the URL
        Uri webPageUri = Uri.parse(urlString);
        //Creating an ACTION_VIEW Intent with this URI
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPageUri);
        //Checking if there is any Activity that handles the Intent
        if (webIntent.resolveActivity(getPackageManager()) != null) {
            //Launching the corresponding Activity and passing it the Intent
            startActivity(webIntent);
        }
    }

}