package com.blackcharm.bottom_nav_pages


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity

import com.blackcharm.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SearchFragmentEmpty : Fragment() {

    private var myContext = FragmentActivity()



    override fun onAttach(activity: Activity) {
        myContext= activity as FragmentActivity
        super.onAttach(activity);


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)

        replaceFragment(SearchFragment())
    }

   fun onResume1() {
        super.onResume()
        replaceFragment(SearchFragment())
    }



    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = myContext.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.searchemptycontainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_empty, container, false)
    }




}
