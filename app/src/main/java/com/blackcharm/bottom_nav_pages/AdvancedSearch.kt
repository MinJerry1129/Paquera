package com.blackcharm.bottom_nav_pages

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.blackcharm.R
import com.blackcharm.messages.LatestMessagesActivity
import java.util.*

class AdvancedSearch : Fragment() {

    lateinit var ageFromInput: TextView
    lateinit var ageToInput: TextView
    lateinit var searchName: TextView
    lateinit var usernameSearch: TextView
    lateinit var advanced_searchinput:TextView

    lateinit var searchgobutton: Button
    lateinit var toolbarbackbutton: Button
    private var myContext = FragmentActivity()

    var ageFrom = 0
    var ageTo = 0

    val AUTOCOMPLETE_REQUEST_CODE: Int = 1;


    override fun onAttach(activity: Activity) {
        myContext= activity as FragmentActivity
        super.onAttach(activity);


    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.activity_advanced_search, container, false)
        if (!Places.isInitialized()) {
            Places.initialize(context!!, "AIzaSyChxF3wrDrHLCRsem-L1okrXSmMmgVLUMg");
        }
        var fields: List<Place.Field>  = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);


        val itisadvancedssearch = true
//**********End show back button on top toolbar**********

        toolbarbackbutton = view.findViewById(R.id.toolbar_back_button)

        toolbarbackbutton.setOnClickListener {
            val fragmentTransaction = myContext.supportFragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.searchemptycontainer, SearchFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        ageFromInput = view.findViewById(R.id.search_age_from_text);
        ageToInput = view.findViewById(R.id.search_age_to_text);
        searchName = view.findViewById(R.id.search_name_text);
        usernameSearch = view.findViewById(R.id.search_username_text);
        searchgobutton = view.findViewById(R.id.search_go_button)
        advanced_searchinput = view.findViewById(R.id.search_location_text)


        ageFromInput.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                compareSearchMemberData()
            }
        }
        ageToInput.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                compareSearchMemberData()
            }
        }


        advanced_searchinput.setOnClickListener {
            var intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(context!!);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }


        searchgobutton.setOnClickListener {

            val profilesearchcheckboxtravel = false
            val profilesearchcheckboxtomeet = false
            val profilesearchcheckboxtodate = false
            val profilesearchcheckboxdiscover = false
            val profilesearchcheckboxoption = ""
            val searchlocationedittext = ""
            val searchtype = ""


            val friendsLineFragment = FriendsLineFragment.newInstance(
                itisadvancedssearch,
                ageFromInput.text.toString(),
                ageToInput.text.toString(),
                searchName.text.toString(),
                usernameSearch.text.toString(),
                advanced_searchinput.text.toString(),
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

        return view

    }

    private fun replaceFragment(fragment: Fragment){



        val fragmentTransaction = myContext.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.friendslineemptycontainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        (activity as LatestMessagesActivity).getPager().setCurrentItem(1)


    }



    fun compareSearchMemberData(){

        if (ageFromInput.text.toString().length in 1..4) {

            ageFrom = ageFromInput.text.toString().toInt()

            if (ageFrom < 18) {

                ageFromInput.setText("18");

            }  else if (ageFrom > 100) {

                ageFromInput.setText("100");

            }

        }  else {
            ageFromInput.setText("18");

        }

        if (ageToInput.text.toString().length in 1..4) {
            ageTo = ageToInput.text.toString().toInt()

            if (ageTo < 18) {

                ageToInput.setText("18");

            } else if (ageFrom > ageTo) {

                ageToInput.setText(ageFrom.toString())

            } else if (ageTo > 100) {

                ageToInput.setText("100");

            }

        } else {

            ageToInput.setText("100");
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {

//            var place: Place = Autocomplete.getPlaceFromIntent(data)
//            Log.d("googleplace", "Place: " + place.getAddress())
//
//            var address = getAddress(place)
//            Log.d("googleplace", "Place: " + address)

            var place: Place = Autocomplete.getPlaceFromIntent(data);
            Log.d("Register", "true")

            var address = getAddress(place)
            println(address.toString())

            advanced_searchinput.text = address.subAdminArea  + ", " + address.countryName
//            advanced_searchinput.text =  place.getAddress()

        } /*else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                var status: Status? = data?.let { Autocomplete.getStatusFromIntent(it) }!!;
                Log.d("googleplace", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }*/

    }

    private fun getAddress(place: Place): Address {
        var locate = Locale("pt")
        var geocoder = Geocoder(myContext, locate)
        var address = if (place.latLng !== null) { geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1) } else { geocoder.getFromLocationName(place.address, 1) }

        return address.get(0)
    }




}
