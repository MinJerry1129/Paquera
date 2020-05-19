package com.blackcharm.bottom_nav_pages


import android.app.Activity
import android.content.Context
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
class FriendsLineFragmentEmpty : Fragment() {

    private var myContext = FragmentActivity()



    override fun onAttach(activity: Activity) {
        myContext= activity as FragmentActivity
        super.onAttach(activity);


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)




        var searchlocationedittext: String = ""
        var searchtype: String = ""
        var profilesearchcheckboxtravel: Boolean = false
        var profilesearchcheckboxtomeet: Boolean = false
        var profilesearchcheckboxtodate: Boolean = false
        var profilesearchcheckboxdiscover: Boolean = false
        var profilesearchcheckboxoption: String = "both"

        val sharedPref = activity?.getSharedPreferences("FUN", Context.MODE_PRIVATE)

        val setting_searchinput = sharedPref?.getString("searchinput", "")

        if (!setting_searchinput.equals("")) {
            searchlocationedittext = setting_searchinput.toString()
        }

        sharedPref?.getString("searchtype", "")?.let { searchtype = it }

        sharedPref?.getBoolean("profile_search_checkbox_travel", false)?.let { profilesearchcheckboxtravel = it }
        sharedPref?.getBoolean("profile_search_checkbox_tomeet", false)?.let { profilesearchcheckboxtomeet = it }
        sharedPref?.getBoolean("profile_search_checkbox_todate", false)?.let { profilesearchcheckboxtodate = it }
        sharedPref?.getBoolean("profile_search_checkbox_discover", false)?.let { profilesearchcheckboxdiscover = it }

        sharedPref?.getBoolean("profile_search_checkbox_female", false)?.let { if (it) profilesearchcheckboxoption = "female" }
        sharedPref?.getBoolean("profile_search_checkbox_male", false)?.let { if (it) profilesearchcheckboxoption = "male" }
        sharedPref?.getBoolean("profile_search_checkbox_both", true)?.let { if (it) profilesearchcheckboxoption = "both" }

        val itisadvancedssearch = false;
        val ageFromInput = "";
        val ageToInput = "";
        val searchName = "";
        val usernameSearch = "";
        val searchinput = "";


        val friendsLineFragment = FriendsLineFragment.newInstance(
            itisadvancedssearch,
            ageFromInput,
            ageToInput,
            searchName,
            usernameSearch,
            searchinput,
            profilesearchcheckboxtravel,
            profilesearchcheckboxtomeet,
            profilesearchcheckboxtodate,
            profilesearchcheckboxdiscover,
            profilesearchcheckboxoption,
            searchlocationedittext,
            searchtype
        )
        replaceFragment(friendsLineFragment)
    }



    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = myContext.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.friendslineemptycontainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_line_empty, container, false)
    }




}
