package com.blackcharm.bottom_nav_pages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blackcharm.R
import com.blackcharm.SearchListener.IFirebaseLoadDone
import com.blackcharm.SearchModel.SearchModel

class SearchActivity: AppCompatActivity(), IFirebaseLoadDone {
    override fun onSearchResaultSuccess(profileList: List<SearchModel>, dataid: List<String>) {

    }

    override fun onSearchResaultFailed(message: String) {

    }

    override fun OnSearchPermittedUserSuccess(profileList:List<String>, flag :Int) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search)

        Log.d("HELLO", "hello")



//
//        val fragment = FriendsLineFragment()
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.search_view_pager, fragment)
//        transaction.commit()


    }






}