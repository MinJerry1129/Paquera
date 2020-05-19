package com.blackcharm.discovery;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.blackcharm.R;

import java.util.List;

public class DiscoveryAdapter extends
    RecyclerView.Adapter<DiscoveryAdapter.ViewHolder> {

    @Override
    public DiscoveryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View discoveryView = inflater.inflate(R.layout.discovery_row, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(discoveryView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(DiscoveryAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Discovery discovery = mDiscoveries.get(position);

        // Set item views based on your views and data model
        TextView titleTextView = viewHolder.titleTextView;
        titleTextView.setText(discovery.getTitle());
        TextView subtitleTextView = viewHolder.subtitleTextView;
        subtitleTextView.setText(discovery.getSubtitle());

        if (discovery.getDraft().equalsIgnoreCase("true"))
            viewHolder.discover_isDraft.setText("Draft");
        else {
            viewHolder.discover_isDraft.setTextColor(Color.parseColor("#2EAACE"));
            viewHolder.discover_isDraft.setText("Live");
        }
        try {
            ImageView imageView = viewHolder.imageView;
            Glide.with(imageView).load(discovery.getImages().get(0).getUri())
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
        return mDiscoveries.size();
    }

// Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView titleTextView;
        public TextView subtitleTextView;
        public ImageView imageView;
        public TextView discover_isDraft;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.discovery_title);
            subtitleTextView = (TextView) itemView.findViewById(R.id.discovery_subtitle);
            discover_isDraft = (TextView) itemView.findViewById(R.id.discover_isDraft);
            imageView = (ImageView) itemView.findViewById(R.id.person_photo);
        }
    }
    private List<Discovery> mDiscoveries;

    // Pass in the discovery array into the constructor
    public DiscoveryAdapter(List<Discovery> discoveries) {
        mDiscoveries = discoveries;
    }
}
