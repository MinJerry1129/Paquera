package com.blackcharm.discovery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.blackcharm.R;

import java.util.List;

public class DiscoveryImageAdapter  extends
        RecyclerView.Adapter<DiscoveryImageAdapter.ViewHolder> {

    @Override
    public DiscoveryImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View discoveryView = inflater.inflate(R.layout.discovery_image_item, parent, false);

        // Return a new holder instance
        DiscoveryImageAdapter.ViewHolder viewHolder = new DiscoveryImageAdapter.ViewHolder(discoveryView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(DiscoveryImageAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Glide.with(viewHolder.imageView).load(mImages.get(position).uri)
                    .placeholder(R.drawable.progress_animation)

                .error(R.drawable.branco).into(viewHolder.imageView);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mImages.size();
    }

    // Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView imageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.discovery_image_view);
        }
    }
    private List<DiscoveryImage> mImages;

    // Pass in the discovery array into the constructor
    public DiscoveryImageAdapter(List<DiscoveryImage> images) {
        mImages = images;
    }
}
