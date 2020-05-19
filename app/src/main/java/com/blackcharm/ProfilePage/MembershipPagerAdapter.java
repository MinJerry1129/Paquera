package com.blackcharm.ProfilePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.blackcharm.R;

import java.util.ArrayList;

public class MembershipPagerAdapter extends LoopingPagerAdapter<Integer> {
    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.item_membershippager, container, false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        convertView.findViewById(R.id.image).setBackgroundResource(getBackgroundColor(listPosition));
    }
    public MembershipPagerAdapter(Context context, ArrayList<Integer> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }
    private int getBackgroundColor (int number) {
        switch (number) {
            case 0:
                return R.drawable.member1;
            case 1:
                return R.drawable.member2;
            case 2:
                return R.drawable.member3;
            case 3:
                return R.drawable.member4;
            default:
                return R.drawable.member1;
        }
    }
}
