package com.blackcharm.SearchAdapter

import android.content.Context
import com.bumptech.glide.Glide
import com.blackcharm.R
import com.blackcharm.SearchModel.SearchModel
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.profile_list_item.view.*

class SearchAdapterProfile(var context: Context, val profile: SearchModel): Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Glide.with(viewHolder.itemView.image_view).load(profile.profileImageUrl)
            .placeholder(R.drawable.progress_animation)

            .error(R.drawable.branco).into(viewHolder.itemView.image_view)

    }


    override fun getLayout(): Int {
        return R.layout.profile_list_item

    }


}