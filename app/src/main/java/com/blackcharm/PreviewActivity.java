package com.blackcharm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackcharm.discovery.Discovery;
import com.blackcharm.discovery.DiscoveryDetailActivity;
import com.blackcharm.events.Events;
import com.blackcharm.events.EventsDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

import info.androidhive.fontawesome.FontDrawable;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.preview_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        int type = (int) getIntent().getSerializableExtra("type");

        if(type==1) {
            Discovery discovery_data = (Discovery) getIntent().getSerializableExtra("discovery_data");

            ImageView profilesearchimage = findViewById(R.id.profile_search_image);
            Button friendslinedetailbutton = findViewById(R.id.friendsline_profile_button);
            Button friendslinelikebutton = findViewById(R.id.friendsline_contact_button);

//            FontDrawable drawable = new FontDrawable(this, R.string.fa_heart_solid, true, false);
//            drawable.setTextColor(this.getColor(R.color.orange_yellow));
//            drawable.setTextSize(65F);
//            friendslinelikebutton.setBackground(drawable);

            LinearLayout discover_information_view = findViewById(R.id.discover_information_view);
            discover_information_view.setVisibility(View.VISIBLE);
            TextView mainview_discover_title = findViewById(R.id.mainview_discover_title);
            mainview_discover_title.setText(discovery_data.getTitle());
            TextView mainview_discover_subtitle = findViewById(R.id.mainview_discover_subtitle);
            mainview_discover_subtitle.setText(discovery_data.getSubtitle());

            try {

                Picasso
                        .get()
                        .load(discovery_data.getImages().get(0).getUri())
                        .placeholder(R.drawable.progress_animation)

                        .error(R.drawable.branco)
                        .into(profilesearchimage);
            }catch (Exception e)
            {

            }


            // profilesearchlocation.text = data[position].loc

            friendslinedetailbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DiscoveryDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("discovery_data", discovery_data);

                    startActivity(intent);
                }
            });
        }
        else if (type==2)
        {
            Events event_data = (Events) getIntent().getSerializableExtra("event_data");
            ImageView profilesearchimage = findViewById(R.id.profile_search_image);
            Button friendslinedetailbutton = findViewById(R.id.friendsline_profile_button);
            Button friendslinelikebutton = findViewById(R.id.friendsline_contact_button) ;

//            FontDrawable drawable = new FontDrawable(this, R.string.fa_heart_solid, true, false);
//            drawable.setTextColor(this.getColor(R.color.orange_yellow));
//            drawable.setTextSize(65F);
//
//            friendslinelikebutton.setBackground(drawable);

            LinearLayout events_information_view = findViewById(R.id.events_information_view);
            events_information_view.setVisibility(View.VISIBLE);

            TextView mainview_events_organisation = findViewById(R.id.mainview_events_organisation);
            mainview_events_organisation.setText(event_data.getOrganise());
            TextView mainview_events_datetime = findViewById(R.id.mainview_events_datetime);
            try {
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(java.lang.Long.valueOf(event_data.getDatetime()));
                String date = DateFormat.format("EEEE dd LLLL yyyy, hh:mm", cal).toString();
                mainview_events_datetime.setText(date);
            }catch (Exception e)
            {
                mainview_events_datetime.setText("");
            }
            TextView mainview_events_title = findViewById(R.id.mainview_events_title);
            mainview_events_title.setText(event_data.getTitle());
            TextView mainview_events_location = findViewById(R.id.mainview_events_location);
            mainview_events_location.setText( event_data.getLocation());
//        profilesearchname.text = data[position].title
//        profilesearchoccupation.text = data[position].
            try {
                Picasso
                .get()
                .load(event_data.getImage().getUri())
                .placeholder(R.drawable.progress_animation)

                .error(R.drawable.branco)
                .into(profilesearchimage);
            }
            catch (Exception e)
            {

            }
//

            // profilesearchlocation.text = data[position].loc

            friendslinedetailbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EventsDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            val row = item as LatestMessageRow
                    intent.putExtra("events_data", event_data);

                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
