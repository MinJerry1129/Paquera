package com.blackcharm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;

import java.util.ArrayList;

public class ImageViewpaperActivity extends AppCompatActivity {
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;
    ArrayList<String> mUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewpaper);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.imageViewPaper);

        mUrls =(ArrayList<String>) this.getIntent().getSerializableExtra("urls");


        pagerAdapter = new ScreenSlidePagerAdapter(this,mUrls);
        mPager.setAdapter(pagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends PagerAdapter {
        Context context;
        ArrayList<String> images;
        LayoutInflater layoutInflater;


        public ScreenSlidePagerAdapter(Context context, ArrayList<String> images) {
            this.context = context;
            this.images = images;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.fragment_screen_slide_page, container, false);

            PhotoView imageView = (PhotoView) itemView.findViewById(R.id.imageContent);

            Glide.with(imageView).load(images.get(position))
                    .placeholder(R.drawable.progress_animation)

                    .error(R.drawable.branco).into(imageView);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

    }
}
