package com.blackcharm.bottom_nav_pages


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.blackcharm.Common
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.blackcharm.ProfilePage.AlienProfilePageActivity
import com.blackcharm.ProfilePage.MembershipActivity
import com.blackcharm.R
import com.blackcharm.SearchAdapter.SearchAdapter
import com.blackcharm.SearchListener.IFirebaseLoadDone
import com.blackcharm.SearchModel.SearchModel
import com.blackcharm.messages.LatestMessagesActivity
import kotlinx.android.synthetic.main.activity_friendsline.*
import kotlinx.android.synthetic.main.fragment_friends_line.*
import com.blackcharm.common.VerticalViewPager
import com.blackcharm.discovery.Discovery
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.blackcharm.discovery.DiscoveryDetailActivity
import com.blackcharm.discovery.DiscoveryShowAdapter
import com.blackcharm.events.Events
import com.blackcharm.events.EventsDetailActivity
import com.blackcharm.events.EventsShowAdapter
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class FriendsLineFragment : Fragment(), IFirebaseLoadDone  {

    override fun onSearchResaultSuccess(profileList: List<SearchModel>, profileListId: List<String>) {


        Log.d("tester", ""+mcontext+profileList)
        adapter = SearchAdapter(activity!!.applicationContext, profileList, profileListId)
        try {
            friendsline_view_pager_original.adapter = adapter
        }
        catch (e : Exception)
        {

        }

    }


    override fun onSearchResaultFailed(message: String) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show()
    }

    override fun OnSearchPermittedUserSuccess(profileList:List<String>, flag :Int) {
        //Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show()
     //   linear_profile_user.removeAllViews()
        if (flag == 1) {
            val discoveryList: ArrayList<Discovery> = ArrayList()

//            profileList.forEach {
//                var discovery_ref=FirebaseDatabase.getInstance().reference.child("/discoveries/"+it)
            var discovery_ref = FirebaseDatabase.getInstance().reference.child("/discoveries/")

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                    //      linear_profile_user.removeAllViews()
                    for (discoverySnap in dataSnapshot.children) {
                        val discovery_item = discoverySnap.getValue(Discovery::class.java)
                        if (discovery_item!!.draft.equals("false")) {
                            discoveryList.add(discovery_item!!)
                            //       linear_profile_user.addView(addDiscoveryImage(discovery_item))
                        }
                    }

                    discoveryShowAdapter =
                        DiscoveryShowAdapter(activity!!.applicationContext, discoveryList)
                    friendsline_view_pager_original.adapter = discoveryShowAdapter

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // ...
                }
            }
            discovery_ref.addValueEventListener(postListener)
//        }

        }
        else
        {
            val eventsList:ArrayList<Events> = ArrayList()

//                var events_ref=FirebaseDatabase.getInstance().reference.child("/events/"+it)
            var events_ref=FirebaseDatabase.getInstance().reference.child("/events/")

            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI

                 //   linear_profile_user.removeAllViews()
                    for(eventsSnap in dataSnapshot.children) {
                        val events_item = eventsSnap.getValue(Events::class.java)
                        val tsLong : Long = System.currentTimeMillis();
                        try {

                            val event_time :Double = events_item!!.datetime.toDouble()
                            if(events_item!!.draft.equals("false")&&tsLong<=event_time) {
                                eventsList.add(events_item!!)
                                //         linear_profile_user.addView(addeventsImages(events_item))
                            }
                        }catch (e : Exception)
                        {
                            System.err.println(e.message)
                        }
                    }

                    eventsShowAdapter = EventsShowAdapter(activity!!.applicationContext, eventsList)
                    friendsline_view_pager_original.adapter = eventsShowAdapter

                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // ...
                }
            }
            events_ref.addValueEventListener(postListener)

        }
    }


//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        mcontext = context!!
//        }

    var profileAdapter = GroupAdapter<ViewHolder>()

    lateinit var adapter: SearchAdapter
    lateinit var discoveryShowAdapter: DiscoveryShowAdapter
    lateinit var eventsShowAdapter: EventsShowAdapter
    lateinit var profiles: DatabaseReference
    lateinit var query: Query

    lateinit var iFirebaseLoadDone: IFirebaseLoadDone
    lateinit var searchviewpager: VerticalViewPager
    lateinit var mcontext: Context
    lateinit var metest: TextView
    lateinit var mView: View



    private var searchfilterresult: String? = null

    private var itisadvancedssearch: Boolean? = false
    private var ageFromInput: Int? = 0
    private var ageToInput: Int? = 0
    private var searchName: String? = ""
    private var usernameSearch: String? = ""
    private var searchinput: String? = ""


    private var travelChecked: Boolean? = false
    private var meetChecked: Boolean? = false
    private var dateChecked: Boolean? = false
    private var discoverChecked: Boolean? = false
    private var eventChecked: Boolean? = false

    private var isAllMembersSearching: Boolean? = false
    private var optionChecked: String? = ""
    private var search_loc: String? = ""
    private var searchtype: String? = ""
    private var currentUserId: String? = ""

    private var loaded: Boolean = false
    private var yourBlacklist: ArrayList<String> = ArrayList()

    private var discoveries : MutableList<Discovery> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
        itisadvancedssearch = arguments?.getBoolean("itisadvancedssearch")
        if(itisadvancedssearch == true) {
            ageFromInput = arguments?.getString("ageFromInput")?.toInt()
            ageToInput = arguments?.getString("ageToInput")?.toInt()
            searchName = arguments?.getString("searchName")
            usernameSearch = arguments?.getString("usernameSearch")
            searchinput = arguments?.getString("searchinput")

            Log.d("SEARCHNAME1", ageFromInput.toString())
            Log.d("SEARCHNAME1", ageToInput.toString())
            Log.d("SEARCHNAME1", usernameSearch.toString())
            Log.d("SEARCHNAME1", searchinput.toString())
        }

        travelChecked = arguments?.getBoolean("travelChecked")
        meetChecked = arguments?.getBoolean("meetChecked")
        dateChecked = arguments?.getBoolean("dateChecked")
        discoverChecked = arguments?.getBoolean("discoverChecked")
        optionChecked = arguments?.getString("optionChecked")
        search_loc = arguments?.getString("search_loc")
        searchtype = arguments?.getString("searchtype")


        if (arguments == null)
                loadSearchSettings()

        if(searchtype == "All Members") {
            isAllMembersSearching = true
        }
        else {
            isAllMembersSearching = false
        }



//        Log.d("testingparse", ""+travelChecked)
//        Log.d("testingparse", ""+meetChecked)
//        Log.d("testingparse", ""+dateChecked)
//        Log.d("testingparse", ""+discoverChecked)
//        Log.d("testingparse", ""+search_loc)

    }

    override fun onStart() {
        super.onStart()


    }

    override fun onResume() {
        super.onResume()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }



    private fun loadSearchSettings() {
        val sharedPref = activity?.getSharedPreferences("FUN", Context.MODE_PRIVATE)

        val setting_searchinput = sharedPref?.getString("searchinput", "")

        if (!setting_searchinput.equals("")) {
            search_loc = setting_searchinput
        }

        sharedPref?.getString("searchtype", "")?.let { searchtype = it }

        sharedPref?.getBoolean("profile_search_checkbox_travel", false)?.let { travelChecked = it }
        sharedPref?.getBoolean("profile_search_checkbox_tomeet", false)?.let { meetChecked = it }
        sharedPref?.getBoolean("profile_search_checkbox_todate", false)?.let { dateChecked = it }
        sharedPref?.getBoolean("profile_search_checkbox_discover", false)?.let { discoverChecked = it }

        sharedPref?.getBoolean("profile_search_checkbox_female", false)?.let { if (it) optionChecked = "female" }
        sharedPref?.getBoolean("profile_search_checkbox_male", false)?.let { if (it) optionChecked = "male" }
        sharedPref?.getBoolean("profile_search_checkbox_both", true)?.let { if (it) optionChecked = "both" }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)




        mView = inflater.inflate(R.layout.activity_friendsline, container, false)
        currentUserId = FirebaseAuth.getInstance().uid

        (activity as AppCompatActivity).setSupportActionBar(profile_search_toolbar)

        searchviewpager = mView.findViewById(R.id.friendsline_view_pager_original)

        val loc_txt: TextView = mView.findViewById(R.id.txt_location)

        loc_txt.text = search_loc

        //Init DB

        profiles = FirebaseDatabase.getInstance().getReference("users")
        query = profiles.orderByChild("loc").equalTo(search_loc)

        val ref = FirebaseDatabase.getInstance().getReference("users-notlike/"+ FirebaseAuth.getInstance().uid)

        //Init Event

        iFirebaseLoadDone = this

        mcontext = activity!!.baseContext

        Log.d("testerit", "hello1")

        if(itisadvancedssearch == false) {

            ref.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    loadProfile()
                }

            })
        }
        else {
            loadAdvancedProfile()
        }

        // searchviewpager.setPageTransformer(true, DepthPageTransformer())




        (mView.findViewById(R.id.stay_button) as Button).setOnClickListener {
            travelChecked = true
            meetChecked = false
            dateChecked = false
            discoverChecked = false
            eventChecked = false
            stay_button.setBackgroundResource(R.drawable.rounded_button_pale_grey_selected);
            discover_view_button.setTextColor(Color.parseColor("#ffffff"));
            discover_view_button.setBackgroundResource(R.drawable.rounded_button_orange_yellow);
            event_view_button.setTextColor(Color.parseColor("#ffffff"));
            event_view_button.setBackgroundResource(R.drawable.rounded_button_tealish);

            if(itisadvancedssearch == false) {

                loadProfile()
            }
            else {
                loadAdvancedProfile()
            }
        }

        (mView.findViewById(R.id.discover_view_button) as Button).setOnClickListener {
            discover_view_button.setTextColor(Color.parseColor("#ffa500"));
            discover_view_button.setBackgroundResource(R.drawable.rounded_button_orange_yellow_selected);
            event_view_button.setTextColor(Color.parseColor("#ffffff"));
            event_view_button.setBackgroundResource(R.drawable.rounded_button_tealish);
            stay_button.setBackgroundResource(R.drawable.rounded_button_pale_grey);
            travelChecked = false
            meetChecked = false
            dateChecked = false
            discoverChecked = true
            eventChecked = false
            loadDiscoveries()
        }

        (mView.findViewById(R.id.event_view_button) as Button).setOnClickListener {
            discover_view_button.setTextColor(Color.parseColor("#ffffff"));
            discover_view_button.setBackgroundResource(R.drawable.rounded_button_orange_yellow);
            event_view_button.setTextColor(Color.parseColor("#21bbd7"));
            event_view_button.setBackgroundResource(R.drawable.rounded_button_tealish_selected);
            stay_button.setBackgroundResource(R.drawable.rounded_button_pale_grey);
            travelChecked = false
            meetChecked = false
            dateChecked = false
            discoverChecked = false
            eventChecked = true

            loadEvents()
        }
    

        return mView


    }

    companion object {
//        private val pretravelChecked: String = ""
//        private val premeetChecked: String = ""
//        private val predateChecked: String = ""
//        private val prediscoverChecked: String = ""
//        private val presearch_loc: String = ""

        fun newInstance(itisadvancedssearch: Boolean, ageFromInput: String,ageToInput: String,searchName: String,usernameSearch: String, searchinput: String, profilesearchcheckboxtravel: Boolean, profilesearchcheckboxtomeet: Boolean, profilesearchcheckboxtodate: Boolean, profilesearchcheckboxdiscover: Boolean, profilesearchcheckboxoption: String, searchlocationedittext: String, searchtype: String): FriendsLineFragment {


            val fragment = FriendsLineFragment()
            val args = Bundle()
            args.putBoolean("itisadvancedssearch", itisadvancedssearch)
            args.putString("ageFromInput", ageFromInput)
            args.putString("ageToInput", ageToInput)
            args.putString("searchName", searchName)
            args.putString("usernameSearch", usernameSearch)
            args.putString("searchinput", searchinput)

            args.putBoolean("travelChecked", profilesearchcheckboxtravel)
            args.putBoolean("meetChecked", profilesearchcheckboxtomeet)
            args.putBoolean("dateChecked", profilesearchcheckboxtodate)
            args.putBoolean("discoverChecked", profilesearchcheckboxdiscover)
            args.putString("optionChecked", profilesearchcheckboxoption)
            args.putString("search_loc", searchlocationedittext)
            args.putString("searchtype", searchtype)
            fragment.arguments = args
            //Log.d("testingparse", ""+args)
            return fragment

        }
    }

    fun loadProfile() {

        val ref = FirebaseDatabase.getInstance().getReference("users-notlike/"+ FirebaseAuth.getInstance().uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p1: DatabaseError) {
            }

            override fun onDataChange(p1: DataSnapshot) {
                Log.d("testerit", "hello")
                query.addListenerForSingleValueEvent(object: ValueEventListener {

                    var profiles:MutableList<SearchModel> = ArrayList()
                    var profileids:MutableList<String> = ArrayList()

                    override fun onCancelled(p0: DatabaseError) {
                        iFirebaseLoadDone.onSearchResaultFailed(p0.message)

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        Log.d("test2", p0.toString())

                        var friendslinevalue = p0.value.toString()

//                        if (friendslinevalue.isEmpty() || friendslinevalue == "null" || friendslinevalue == null || friendslinevalue == "") {
//                            val intent = Intent(getActivity(), InviteFriendsActivity::class.java)
//                            startActivity(intent)
//                        }

                        for(profilesSnapShot in p0.children){
                            if(p1.child(profilesSnapShot.key.toString()).value != null){

                            }else{
                                var logicalSearchResultAcceptingGuests = false
                                var travelCoincidence = false
                                var meetCoincidence = false
                                var searchResultMeetChecked = false
                                var searchResultDateChecked = false
                                var offerServiseChecked: Boolean? = false
                                var aboutServicesEnabled: Boolean? = false
                                var dateCoincidence = false
                                var sexCoincidence = false
                                var discoverCoincidence = false

                                var isActive = if(profilesSnapShot.child("isActive").value == null) { false } else { profilesSnapShot.child("isActive").value as Boolean }
                                var disconectTime = if(profilesSnapShot.child("disconectTime").value == null) { -99999999999999 } else { profilesSnapShot.child("disconectTime").value as Long }

                                var allIsOk = false
                                var yourSex = profilesSnapShot.child("yourSex").value
                                var allMembersConcidence = false

                                val searchResultAcceptingGuests = profilesSnapShot.child("acceptingGuests").value
                                searchResultMeetChecked = if(profilesSnapShot.child("meetChecked").value == null) { false } else { profilesSnapShot.child("meetChecked").value as Boolean }
                                searchResultDateChecked = if(profilesSnapShot.child("dateChecked").value == null) { false } else { profilesSnapShot.child("dateChecked").value as Boolean }
                                offerServiseChecked = profilesSnapShot.child("offerServiseChecked").value as Boolean?
                                aboutServicesEnabled = profilesSnapShot.child("aboutServicesEnabled").value as Boolean?

                                if (searchResultAcceptingGuests == "maybe" || searchResultAcceptingGuests == "yes") {
                                    logicalSearchResultAcceptingGuests = true
                                }

                                if (logicalSearchResultAcceptingGuests == true && travelChecked == true) {
                                    travelCoincidence = true
                                }

                                if (searchResultMeetChecked == true && meetChecked == true) {
                                    meetCoincidence = true
                                }
                                if (searchResultDateChecked == true && dateChecked == true) {
                                    dateCoincidence = true
                                }
                                if(optionChecked == "both"){
                                    sexCoincidence = true
                                }
                                else if(optionChecked == yourSex){
                                    sexCoincidence = true
                                }
                                else {
                                    sexCoincidence = false
                                }

                                if(discoverChecked == true) {
                                    if(offerServiseChecked == true && aboutServicesEnabled == true) {
                                        discoverCoincidence = true
                                    }
                                    else {
                                        discoverCoincidence = false
                                    }
                                }
                                else {
                                    discoverCoincidence = false
                                }

                                if(isAllMembersSearching !=true) {
                                    for(userFromFofArray in LatestMessagesActivity.contactlist) {
                                        if (profilesSnapShot.key == userFromFofArray) {
                                            allMembersConcidence = true
                                        }
                                    }
                                }
                                else {
                                    allMembersConcidence = true
                                }



                                val profile = profilesSnapShot.getValue(SearchModel::class.java)

                                if (profile?.email == "clarem@blackcharm.com") {
                                    Log.d("test3", profilesSnapShot.toString())
                                }
                                profile?.key = profilesSnapShot.key
                                val profileid = profilesSnapShot.key





//                    travelChecked = arguments?.getBoolean("travelChecked")
//                    meetChecked = arguments?.getBoolean("meetChecked")
//                    dateChecked = arguments?.getBoolean("dateChecked")
//                    discoverChecked = arguments?.getBoolean("discoverChecked")
//                    search_loc = arguments?.getString("search_loc")

//                    Log.d("testingparse", ""+currentUserId)

                                //Log.d("testingparse1", ""+profile)
                                if(allMembersConcidence == true && (currentUserId != profilesSnapShot.key && (discoverCoincidence == true || (sexCoincidence == true && (travelCoincidence == true || meetCoincidence == true || dateCoincidence == true))))) {
                                    allIsOk = true

                                }
                                if (allIsOk) {

                                    if (disconectTime != -99999999999999) {
                                        if (isActive) {
                                            profile!!.priority = 99999999999999
                                        } else {
                                            profile!!.priority = disconectTime
                                        }
                                    } else {
                                        profile!!.priority = 0
                                    }

                                    profiles.add(profile!!)
                                    profiles.sort()
                                    // profileids.add(profileid!!)

                                }
                            }


                        }

                        try {
                            linear_profile_user.removeAllViews()
                        }
                        catch(e:Exception)
                        {

                        }
                        profiles.forEach {
                            linear_profile_user.addView(addImage(it.key!!, it.profileImageUrl!!))
                        }

//                iFirebaseLoadDone.onSearchResaultSuccess(profiles)
                        iFirebaseLoadDone.onSearchResaultSuccess(profiles, profileids)


                    }

                })
            }

        })



}


    fun loadDiscoveries() {
        val reference = FirebaseDatabase.getInstance().reference
  //      linear_profile_user.removeAllViews()
        var query = reference.child("users")
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            var profileIDs:MutableList<String> = ArrayList()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDone.onSearchResaultFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(profilesSnapShot in p0.children){
                    profileIDs.add(profilesSnapShot.key.toString())
                }
                iFirebaseLoadDone.OnSearchPermittedUserSuccess(profileIDs, 1)
            }
        })

    }


    fun loadEvents() {
        val reference = FirebaseDatabase.getInstance().reference
  //      linear_profile_user.removeAllViews()
        var query = reference.child("users")
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            var profileIDs:MutableList<String> = ArrayList()
            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDone.onSearchResaultFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(profilesSnapShot in p0.children){
                    profileIDs.add(profilesSnapShot.key.toString())
                }
                iFirebaseLoadDone.OnSearchPermittedUserSuccess(profileIDs,2)
            }
        })
    }

    fun addImage(key: String, imagePath: String): CircleImageView {
        var userImage = CircleImageView(context)
        // userImage.tag = key

        val lp = event_view_button.getLayoutParams() as ViewGroup.MarginLayoutParams

        var params = LinearLayout.LayoutParams(lp.width, lp.width)
        params.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
        params.weight = 0.0f
        userImage.visibility = View.VISIBLE
        userImage.layoutParams = params

//        if (Common.getInstance().membership_status == "yes"){
            Picasso.get()
                .load(imagePath)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.branco)
                .into(userImage)
//        }else{
//            Picasso.get()
//                .load(imagePath)
//                .placeholder(R.drawable.progress_animation)
//                .transform(BlurTransformation(context,25,3))
//                .error(R.drawable.branco)
//                .into(userImage)
//        }



        userImage.setOnClickListener {
//            if (Common.getInstance().membership_status == "yes"){
                val intent = Intent(context, AlienProfilePageActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val row = item as LatestMessageRow
                intent.putExtra("profileuid", key)

                context!!.startActivity(intent)
//            }else{
//                val intent = Intent(context, MembershipActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context!!.startActivity(intent)
//            }

        }

        return userImage
    }



    fun searchNameIsNotEmpty(): Boolean {
        var notEmprty = false

        if (searchName!!.isNotEmpty()) {
            notEmprty = true
        }
        return notEmprty
    }

    fun usernameIsNotEmpty(): Boolean {
        var notEmprty = false
        if (usernameSearch!!.isNotEmpty()) {
            notEmprty = true
        }
        return notEmprty
    }

    fun loadAdvancedProfile() {



        var blacklistRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId!!)

        blacklistRef.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                iFirebaseLoadDone.onSearchResaultFailed(p0.message)

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (blockedUser in p0.children) {
                    yourBlacklist.add(blockedUser.key.toString())
                }


                profiles = FirebaseDatabase.getInstance().getReference("users")
                if (searchinput!!.isNotEmpty()) {
                    query = profiles.orderByChild("loc").equalTo(searchinput)
                } else if (searchNameIsNotEmpty() && usernameIsNotEmpty() == false) {
//                    query = profiles.orderByChild("searchName").startAt(searchName?.toUpperCase())
                    query = profiles.orderByChild("searchName").startAt(searchName?.toUpperCase()).endAt(searchName?.toUpperCase()+"\uf8ff")

                } else if (usernameIsNotEmpty()) {
                    query = profiles.orderByChild("username").equalTo(usernameSearch)
                } else {
                    query = profiles
                }

                query.addListenerForSingleValueEvent(object : ValueEventListener {


                    var profiles: MutableList<SearchModel> = ArrayList()
                    var profileids: MutableList<String> = ArrayList()


                    override fun onCancelled(p0: DatabaseError) {
                        iFirebaseLoadDone.onSearchResaultFailed(p0.message)


                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        var friendslinevalue = p0.value.toString()

                        if (friendslinevalue.isEmpty() || friendslinevalue == "null" || friendslinevalue == null || friendslinevalue == "") {
                            val intent = Intent(getActivity(), InviteFriendsActivity::class.java)
                            startActivity(intent)

                        }


                        for (profilesSnapShot in p0.children) {

//                    var logicalSearchResultAcceptingGuests = false
//                    var travelCoincidence = false
//                    var meetCoincidence = false
//                    var searchResultMeetChecked = false
//                    var searchResultDateChecked = false
//                    var offerServiseChecked: Boolean? = false
//                    var aboutServicesEnabled: Boolean? = false
//                    var dateCoincidence = false
//                    var sexCoincidence = false
//                    var discoverCoincidence = false
                            var deletedUser = profilesSnapShot.child("wasDeleted").value.toString()
                            var userId = profilesSnapShot.key

                            var allIsOk = false
                            var userNotDeleted = false
                            var isInBlacklist = false

                            if (deletedUser != "bySelf" && deletedUser != "byAdministration") {
                                userNotDeleted = true
                            }


                            var searchResultAge = profilesSnapShot.child("age").value.toString()

                            for (blockedItem in yourBlacklist) {
                                if (userId == blockedItem) {
                                    isInBlacklist = true

                                }
                            }



                            if (searchResultAge.isNotEmpty() && searchResultAge != "null" && searchResultAge != null && searchResultAge != "" ) {


                                var searchResultAgeInt: Int = Integer.parseInt(searchResultAge)

                                if (userNotDeleted && isInBlacklist == false && currentUserId != profilesSnapShot.key) {

                                    if (ageFromInput!! <= searchResultAgeInt!! && ageToInput!! >= searchResultAgeInt!!) {
                                        allIsOk = true

                                    }

                                }
                            }


                            var isActive = if (profilesSnapShot.child("isActive").value == null) {
                                true
                            } else {
                                profilesSnapShot.child("isActive").value as Boolean
                            }
                            var disconectTime = if (profilesSnapShot.child("disconectTime").value == null) {
                                -99999999999999
                            } else {
                                profilesSnapShot.child("disconectTime").value as Long
                            }


                            val profile = profilesSnapShot.getValue(SearchModel::class.java)
                            profile?.key = profilesSnapShot.key
                            val profileid = profilesSnapShot.key

                            Log.d("allisfine", "trying");

                            if (allIsOk) {
                                Log.d("allisfine", allIsOk.toString());

                                if (disconectTime != -99999999999999) {
                                    if (isActive) {
                                        profile!!.priority = 99999999999999
                                    } else {
                                        profile!!.priority = disconectTime
                                    }
                                } else {
                                    profile!!.priority = 0
                                }

                                profiles.add(profile!!)
                                profiles.sort()

                            }
                        }
                        try {
                            linear_profile_user.removeAllViews()
                        }catch (e:Exception)
                        {

                        }
                        profiles.forEach {
                            linear_profile_user.addView(addImage(it.key!!, it.profileImageUrl!!))
                        }

                        iFirebaseLoadDone.onSearchResaultSuccess(profiles, profileids)


                    }

                })
            }
        })


    }

    fun addDiscoveryImage(item: Discovery): CircleImageView {
        var userImage = CircleImageView(context)
        // userImage.tag = key

        val lp = event_view_button.getLayoutParams() as ViewGroup.MarginLayoutParams

        var params = LinearLayout.LayoutParams(lp.width, lp.width)
        params.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
        params.weight = 0.0f
        userImage.visibility = View.VISIBLE
        userImage.layoutParams = params

        Glide.with(this)
            .load(item.images.get(0).uri)
            .into(userImage)

        userImage.setOnClickListener {
            val intent = Intent(context, DiscoveryDetailActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val row = item as LatestMessageRow
            intent.putExtra("discovery_data", item)
            context!!.startActivity(intent)
        }

        return userImage
    }

    fun addeventsImages(item: Events): CircleImageView {
        var userImage = CircleImageView(context)
        // userImage.tag = key

        val lp = event_view_button.getLayoutParams() as ViewGroup.MarginLayoutParams

        var params = LinearLayout.LayoutParams(lp.width, lp.width)
        params.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin)
        params.weight = 0.0f
        userImage.visibility = View.VISIBLE
        userImage.layoutParams = params

        Glide.with(this)
            .load(item.image.uri)
            .into(userImage)

        userImage.setOnClickListener {
            val intent = Intent(context, EventsDetailActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val row = item as LatestMessageRow
            intent.putExtra("events_data", item)

            context!!.startActivity(intent)
        }

        return userImage
    }
}
