package com.blackcharm.bottom_nav_pages


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.blackcharm.R
import com.blackcharm.SearchListener.IFirebaseLoadDone
import com.blackcharm.SearchModel.SearchModel
import com.blackcharm.messages.LatestMessagesActivity
import info.androidhive.fontawesome.FontTextView
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*

class SearchFragment : Fragment(), IFirebaseLoadDone {

    override fun onSearchResaultSuccess(profileList: List<SearchModel>, dataid: List<String>) {

    }

    override fun onSearchResaultFailed(message: String) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show()
    }

    override fun OnSearchPermittedUserSuccess(profileList:List<String>, flag :Int) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show()
    }

    private var myContext = FragmentActivity()

    var profilesearchcheckboxtravel: Boolean = true
    var profilesearchcheckboxtomeet: Boolean = false
    var profilesearchcheckboxtodate: Boolean = true

    var profilesearchcheckboxdiscover: Boolean = false

    var profilesearchcheckboxoption: String = ""
    var searchlocationedittext: String = ""
    lateinit var searchinput: TextView

    var profilesearchcheckboxfemalevalue: Boolean = false
    var profilesearchcheckboxmalevalue: Boolean = false
    var profilesearchcheckboxbothvalue: Boolean = true
    var profilesearchcheckboxagevalue: Boolean = false

    // All members... Contacts Only
    var searchtype: String = "All Members"

    val AUTOCOMPLETE_REQUEST_CODE: Int = 1;

    override fun onAttach(activity: Activity) {
        myContext = activity as FragmentActivity
        super.onAttach(activity);


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    override fun onResume() {
        super.onResume()

        loadSearchSettings(view!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        loadElements(view)

        return view


    }

    fun loadElements(view: View) {
        if (!Places.isInitialized()) {
            Places.initialize(context!!, "AIzaSyChxF3wrDrHLCRsem-L1okrXSmMmgVLUMg");
        }

        var fields: List<Place.Field> = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        
        searchinput = (view.findViewById(R.id.search_location_edittext) as TextView)

        (view.findViewById(R.id.search_allmembers_button) as Button).setOnClickListener {
            setButtonSearchType("All Members", view)
        }

        (view.findViewById(R.id.search_contactsonly_button) as Button).setOnClickListener {
            setButtonSearchType("Contacts Only", view)
        }

        (view.findViewById(R.id.searchmembersbtn) as Button).setOnClickListener {

            val fragmentTransaction = myContext.supportFragmentManager.beginTransaction()

            fragmentTransaction.replace(R.id.searchemptycontainer, AdvancedSearch())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        searchinput.setOnClickListener {
            var intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            ).build(context!!);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }

        (view.findViewById(R.id.travel_accommodation) as LinearLayout).setOnClickListener { checkView ->
            if (profilesearchcheckboxtravel) {
                travel_accommodation_icon.setTextColor(getResources().getColor(R.color.battleship_grey))
                travel_accommodation_text.setTextColor(getResources().getColor(R.color.battleship_grey))
                profilesearchcheckboxtravel = false
            } else  {
                travel_accommodation_icon.setTextColor(getResources().getColor(R.color.orange_yellow))
                travel_accommodation_text.setTextColor(getResources().getColor(R.color.orange_yellow))
                profilesearchcheckboxtravel = true
            }
        }

        (view.findViewById(R.id.to_meet_people) as LinearLayout).setOnClickListener {  checkView ->
            if (profilesearchcheckboxtomeet) {
                to_meet_people_icon.setTextColor(getResources().getColor(R.color.battleship_grey))
                to_meet_people_text.setTextColor(getResources().getColor(R.color.battleship_grey))
                profilesearchcheckboxtomeet = false
            } else  {
                to_meet_people_icon.setTextColor(getResources().getColor(R.color.orange_yellow))
                to_meet_people_text.setTextColor(getResources().getColor(R.color.orange_yellow))
                profilesearchcheckboxtomeet = true
            }
        }

        (view.findViewById(R.id.to_date) as LinearLayout).setOnClickListener {  checkView ->
            if (profilesearchcheckboxtodate) {
                to_date_icon.setTextColor(getResources().getColor(R.color.battleship_grey))
                to_date_text.setTextColor(getResources().getColor(R.color.battleship_grey))
                profilesearchcheckboxtodate = false
            } else  {
                to_date_icon.setTextColor(getResources().getColor(R.color.orange_yellow))
                to_date_text.setTextColor(getResources().getColor(R.color.orange_yellow))
                profilesearchcheckboxtodate = true
            }
        }

        // checkboxes

        (view.findViewById(R.id.female) as LinearLayout).setOnClickListener {  checkView ->
            female_check.setTextColor(getResources().getColor(R.color.orange_yellow))
            female_text.setTextColor(getResources().getColor(R.color.orange_yellow))

            male_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            male_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            both_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            both_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            age_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            age_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            profilesearchcheckboxfemalevalue = true
            profilesearchcheckboxmalevalue = false
            profilesearchcheckboxbothvalue = false
            profilesearchcheckboxagevalue = false
        }

        (view.findViewById(R.id.male) as LinearLayout).setOnClickListener {  checkView ->
            female_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            female_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            male_check.setTextColor(getResources().getColor(R.color.orange_yellow))
            male_text.setTextColor(getResources().getColor(R.color.orange_yellow))

            both_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            both_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            age_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            age_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            profilesearchcheckboxfemalevalue = false
            profilesearchcheckboxmalevalue = true
            profilesearchcheckboxbothvalue = false
            profilesearchcheckboxagevalue = false
        }

        (view.findViewById(R.id.both) as LinearLayout).setOnClickListener {  checkView ->
            female_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            female_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            male_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            male_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            both_check.setTextColor(getResources().getColor(R.color.orange_yellow))
            both_text.setTextColor(getResources().getColor(R.color.orange_yellow))

            age_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            age_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            profilesearchcheckboxfemalevalue = false
            profilesearchcheckboxmalevalue = false
            profilesearchcheckboxbothvalue = true
            profilesearchcheckboxagevalue = false
        }

        (view.findViewById(R.id.age) as LinearLayout).setOnClickListener {  checkView ->
            female_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            female_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            male_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            male_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            both_check.setTextColor(getResources().getColor(R.color.battleship_grey))
            both_text.setTextColor(getResources().getColor(R.color.battleship_grey))

            age_check.setTextColor(getResources().getColor(R.color.orange_yellow))
            age_text.setTextColor(getResources().getColor(R.color.orange_yellow))

            profilesearchcheckboxfemalevalue = false
            profilesearchcheckboxmalevalue = false
            profilesearchcheckboxbothvalue = false
            profilesearchcheckboxagevalue = true
        }

        (view.findViewById(R.id.searchgobutton) as Button).setOnClickListener {

            saveSearchSettings(view)

            if(!profilesearchcheckboxdiscover && !profilesearchcheckboxtodate && !profilesearchcheckboxtomeet  && !profilesearchcheckboxtravel){
                val builder = AlertDialog.Builder(context!!)
                builder.setTitle("Para prosseguir")
                builder.setMessage("Escolha pelo menos uma opção")

                builder.setNeutralButton("OK"){dialog,which ->
                }

                val dialog: AlertDialog = builder.create()
                dialog.show()

            }
            else {

                var searchfilter: MutableMap<String, String> = mutableMapOf()
                Log.d("testerit", "clicked")

                if (profilesearchcheckboxfemalevalue) {
                    profilesearchcheckboxoption = "female"
                }
                if (profilesearchcheckboxmalevalue) {
                    profilesearchcheckboxoption = "male"
                }
                if (profilesearchcheckboxbothvalue) {
                    profilesearchcheckboxoption = "both"
                }
                if (profilesearchcheckboxagevalue) {
                    profilesearchcheckboxoption = "age"
                }

                searchlocationedittext = search_location_edittext.text.toString()
                val itisadvancedssearch = false;
                val ageFromInput = "";
                val ageToInput = "";
                val searchName = "";
                val usernameSearch = "";

                val friendsLineFragment = FriendsLineFragment.newInstance(
                    itisadvancedssearch,
                    ageFromInput,
                    ageToInput,
                    searchName,
                    usernameSearch,
                    searchinput.text.toString(),
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


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {

            var place: Place = Autocomplete.getPlaceFromIntent(data)
            Log.d("googleplace", "Place: " + place.getAddress())

            var address = getAddress(place)
            Log.d("googleplace", "address: " + address.toString())

            searchinput?.text = address.subAdminArea  + ", " + address.countryName


//            searchinput.text = place.getAddress()
        }

    }

    private fun getAddress(place: Place): Address {
        var locate = Locale("pt")
        var geocoder = Geocoder(myContext, locate)
        var address = if (place.latLng !== null) {
            geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1)
        } else {
            geocoder.getFromLocationName(place.address, 1)
        }

        return address.get(0)
    }

    private fun replaceFragment(fragment: Fragment) {


        val fragmentTransaction = myContext.supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.friendslineemptycontainer, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        (activity as LatestMessagesActivity).getPager().setCurrentItem(0)


    }

    fun loadSearchSettings(view: View) {
        val sharedPref = activity?.getSharedPreferences("FUN", Context.MODE_PRIVATE)

        val setting_searchinput = sharedPref?.getString("searchinput", "")

        if (!setting_searchinput.equals("")) {
            if (searchinput.text.equals(""))
                searchinput.text = setting_searchinput
        }

        sharedPref?.getString("searchtype", "")?.let { setButtonSearchType(it, view) }

        sharedPref?.getBoolean("profile_search_checkbox_travel", true)?.let { profilesearchcheckboxtravel = it }
        sharedPref?.getBoolean("profile_search_checkbox_tomeet", false)?.let { profilesearchcheckboxtomeet = it }
        sharedPref?.getBoolean("profile_search_checkbox_todate", true)?.let { profilesearchcheckboxtodate = it }
        sharedPref?.getBoolean("profile_search_checkbox_discover", false)?.let { profilesearchcheckboxdiscover = it }

        sharedPref?.getBoolean("profile_search_checkbox_female", false)?.let { profilesearchcheckboxfemalevalue = it }
        sharedPref?.getBoolean("profile_search_checkbox_male", false)?.let { profilesearchcheckboxmalevalue = it }
        sharedPref?.getBoolean("profile_search_checkbox_both", true)?.let { profilesearchcheckboxbothvalue = it }
        sharedPref?.getBoolean("profile_search_checkbox_age", false)?.let { profilesearchcheckboxagevalue = it }



        (view.findViewById(R.id.travel_accommodation_icon) as FontTextView).setTextColor(getResources().getColor(R.color.battleship_grey))
        (view.findViewById(R.id.travel_accommodation_text) as TextView).setTextColor(getResources().getColor(R.color.battleship_grey))

        (view.findViewById(R.id.to_meet_people_icon) as FontTextView).setTextColor(getResources().getColor(R.color.battleship_grey))
        (view.findViewById(R.id.to_meet_people_text) as TextView).setTextColor(getResources().getColor(R.color.battleship_grey))

        (view.findViewById(R.id.to_date_icon) as FontTextView).setTextColor(getResources().getColor(R.color.battleship_grey))
        (view.findViewById(R.id.to_date_text) as TextView).setTextColor(getResources().getColor(R.color.battleship_grey))

        if (profilesearchcheckboxtravel) {
            (view.findViewById(R.id.travel_accommodation_icon) as FontTextView).setTextColor(getResources().getColor(R.color.orange_yellow))
            (view.findViewById(R.id.travel_accommodation_text) as TextView).setTextColor(getResources().getColor(R.color.orange_yellow))
        }

        if (profilesearchcheckboxtomeet) {
            (view.findViewById(R.id.to_meet_people_icon) as FontTextView).setTextColor(getResources().getColor(R.color.orange_yellow))
            (view.findViewById(R.id.to_meet_people_text) as TextView).setTextColor(getResources().getColor(R.color.orange_yellow))
        }

        if (profilesearchcheckboxtodate) {
            (view.findViewById(R.id.to_date_icon) as FontTextView).setTextColor(getResources().getColor(R.color.orange_yellow))
            (view.findViewById(R.id.to_date_text) as TextView).setTextColor(getResources().getColor(R.color.orange_yellow))
        }


        (view.findViewById(R.id.female_check) as FontTextView).setTextColor(getResources().getColor(R.color.battleship_grey))
        (view.findViewById(R.id.female_text) as TextView).setTextColor(getResources().getColor(R.color.battleship_grey))

        (view.findViewById(R.id.male_check) as FontTextView).setTextColor(getResources().getColor(R.color.battleship_grey))
        (view.findViewById(R.id.male_text) as TextView).setTextColor(getResources().getColor(R.color.battleship_grey))

        (view.findViewById(R.id.both_check) as FontTextView).setTextColor(getResources().getColor(R.color.battleship_grey))
        (view.findViewById(R.id.both_text) as TextView).setTextColor(getResources().getColor(R.color.battleship_grey))

        (view.findViewById(R.id.age_check) as FontTextView).setTextColor(getResources().getColor(R.color.battleship_grey))
        (view.findViewById(R.id.age_text) as TextView).setTextColor(getResources().getColor(R.color.battleship_grey))

        if (profilesearchcheckboxfemalevalue) {
            (view.findViewById(R.id.female_check) as FontTextView).setTextColor(getResources().getColor(R.color.orange_yellow))
            (view.findViewById(R.id.female_text) as TextView).setTextColor(getResources().getColor(R.color.orange_yellow))
        }

        if (profilesearchcheckboxmalevalue) {
            (view.findViewById(R.id.male_check) as FontTextView).setTextColor(getResources().getColor(R.color.orange_yellow))
            (view.findViewById(R.id.male_text) as TextView).setTextColor(getResources().getColor(R.color.orange_yellow))
        }

        if (profilesearchcheckboxbothvalue) {
            (view.findViewById(R.id.both_check) as FontTextView).setTextColor(getResources().getColor(R.color.orange_yellow))
            (view.findViewById(R.id.both_text) as TextView).setTextColor(getResources().getColor(R.color.orange_yellow))
        }

        if (profilesearchcheckboxagevalue) {
            (view.findViewById(R.id.age_check) as FontTextView).setTextColor(getResources().getColor(R.color.orange_yellow))
            (view.findViewById(R.id.age_text) as TextView).setTextColor(getResources().getColor(R.color.orange_yellow))
        }

    }

    private fun saveSearchSettings(view: View) {
        val sharedPref = activity?.getSharedPreferences("FUN", Context.MODE_PRIVATE)
        with(sharedPref!!.edit()) {
            putString("searchinput", searchinput.text.toString())
            putString("searchtype", searchtype)

            putBoolean("profile_search_checkbox_travel", profilesearchcheckboxtravel)
            putBoolean("profile_search_checkbox_tomeet", profilesearchcheckboxtomeet)
            putBoolean("profile_search_checkbox_todate", profilesearchcheckboxtodate)
            putBoolean("profile_search_checkbox_discover",profilesearchcheckboxdiscover)

            putBoolean("profile_search_checkbox_female", profilesearchcheckboxfemalevalue)
            putBoolean("profile_search_checkbox_male", profilesearchcheckboxmalevalue)
            putBoolean("profile_search_checkbox_both", profilesearchcheckboxbothvalue)
            putBoolean("profile_search_checkbox_age", profilesearchcheckboxagevalue)

            commit()
        }
    }

    private fun setButtonSearchType(searchtypeVal: String, view: View) {

        if (searchtypeVal == "All Members") {
            searchtype = searchtypeVal

            (view.findViewById(R.id.search_allmembers_button) as Button).setBackgroundResource(R.drawable.rounded_button_orange_yellow)
            (view.findViewById(R.id.search_contactsonly_button) as Button).setBackgroundResource(R.drawable.rounded_button_greyish)
            // allmembersbutton.setBackgroundTintList(context!!.getResources().getColorStateList(R.color.LightOrange))
            // contactsonlybutton.setBackgroundTintList(context!!.getResources().getColorStateList(R.color.commonGrey))
        }

        if (searchtypeVal == "Contacts Only") {
            searchtype = searchtypeVal

            (view.findViewById(R.id.search_allmembers_button) as Button).setBackgroundResource(R.drawable.rounded_button_greyish)
            (view.findViewById(R.id.search_contactsonly_button) as Button).setBackgroundResource(R.drawable.rounded_button_orange_yellow)
            // allmembersbutton.setBackgroundTintList(context!!.getResources().getColorStateList(R.color.commonGrey))
            // contactsonlybutton.setBackgroundTintList(context!!.getResources().getColorStateList(R.color.LightOrange))
        }
    }


}
