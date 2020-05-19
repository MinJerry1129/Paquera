package com.blackcharm.ProfilePage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.blackcharm.Common;
import com.bumptech.glide.Glide;
import com.blackcharm.ImageViewpaperActivity;
import com.blackcharm.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.BlurTransformation;


public class ProfileImageAdapter extends
        RecyclerView.Adapter<ProfileImageAdapter.ViewHolder> {
    Context context;
    @Override
    public ProfileImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View discoveryView = inflater.inflate(R.layout.discovery_image_item,parent, false);

        // Return a new holder instance
        ProfileImageAdapter.ViewHolder viewHolder = new ProfileImageAdapter.ViewHolder(discoveryView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ProfileImageAdapter.ViewHolder viewHolder, int position) {
//        if(Common.getInstance().getMembership_status().equals("no")){
////            Picasso.get().load(mImages.get(position)).transform(new BlurTransformation(context,25,3))
////                    .into(viewHolder.imageView);
//            BlurMaskFilter filter = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);
//            BlurTransformation _transform = new BlurTransformation(context,25,3);
//            Picasso.get().load(mImages.get(position)).transform(_transform)
//                    .into(viewHolder.imageView);
//            Log.d("blureimage","adfadfad");
////            Glide.with(viewHolder.imageView).load(R.drawable.blur)
////                    .placeholder(R.drawable.progress_animation)
////                    .error(R.drawable.branco).into(viewHolder.imageView);
//
//        }
//        else{
//            Glide.with(viewHolder.imageView).load(mImages.get(position))
//                    .placeholder(R.drawable.progress_animation)
//                    .error(R.drawable.branco).into(viewHolder.imageView);
            Picasso.get().load(mImages.get(position))
                    .into(viewHolder.imageView);
//        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent fullScreenIntent =
//                        new Intent(v.getContext(), FullScreenImageActivity.class);
//                fullScreenIntent.setData(Uri.parse(mImages.get(position)));
//                context.startActivity(fullScreenIntent);
//                if(Common.getInstance().getMembership_status().equals("yes")){
                    Intent fullScreenIntent =
                            new Intent(v.getContext(), ImageViewpaperActivity.class);
                    fullScreenIntent.putStringArrayListExtra("urls", mImages);
                    context.startActivity(fullScreenIntent);
//                }else{
//                    Intent intent=new Intent(v.getContext(), MembershipActivity.class);
//                    context.startActivity(intent);
//                }
            }
        });
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
    private ArrayList<String> mImages;

    // Pass in the discovery array into the constructor
    public ProfileImageAdapter(ArrayList<String> images) {
        mImages = images;
    }
}
