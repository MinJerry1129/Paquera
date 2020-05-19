package com.blackcharm.events;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.blackcharm.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends
    RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View eventsView = inflater.inflate(R.layout.events_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(eventsView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Events events = mEvents.get(position);

        // Set item views based on your views and data model
        TextView titleTextView = viewHolder.titleTextView;
        titleTextView.setText(events.getTitle());
        try {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.valueOf(events.getDatetime()));
            String date = DateFormat.format("EEEE dd LLLL yyyy, hh:mm", cal).toString();
            viewHolder.datetimeTextView.setText(date);
        }
        catch (Exception e)
        {

            viewHolder.datetimeTextView.setText("");
        }
        if (events.getDraft().equalsIgnoreCase("true"))
            viewHolder.events_isDraft.setText("Draft");
        else {
            viewHolder.events_isDraft.setTextColor(Color.parseColor("#2EAACE"));
            viewHolder.events_isDraft.setText("Live");
        }
        ImageView imageView = viewHolder.imageView;
        try {
            Glide.with(imageView).load(events.getImage().getUri())
                    .placeholder(R.drawable.progress_animation)

                    .error(R.drawable.branco).into(imageView);
        }
        catch (Exception e)
        {

        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mEvents.size();
    }

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView titleTextView;
        public TextView datetimeTextView;
        public ImageView imageView;
        public TextView events_isDraft;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.events_title);
            datetimeTextView = (TextView) itemView.findViewById(R.id.events_datetime);
            imageView = (ImageView) itemView.findViewById(R.id.person_photo);
            events_isDraft = (TextView) itemView.findViewById(R.id.events_isDraft);
        }
    }
    private List<Events> mEvents;

    // Pass in the events array into the constructor
    public EventsAdapter(List<Events> events) {
        mEvents = events;
    }
}
