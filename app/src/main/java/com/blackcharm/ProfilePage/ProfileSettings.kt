package com.blackcharm.ProfilePage

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.R
import com.blackcharm.bottom_nav_pages.ProfileFragment
import com.blackcharm.models.User
import com.blackcharm.registerlogin.TermsScreen
import kotlinx.android.synthetic.main.activity_profile_settings.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProfileSettings : AppCompatActivity() {

    companion object {
        var currentUserProfileSettings: User? = null
        var profileacceptingGuests: String? = null
        var profilemeetup: Boolean? = false
        var profiledate: Boolean? = false
        var checkclickinput: Boolean? = false
        var offerServiseChecked: Boolean? = false
//        var aboutServicesEnabled: Boolean? = false

    }
    val AUTOCOMPLETE_REQUEST_CODE: Int = 1;

    lateinit var toolbarprofilesave: Button
    var membershipStatus: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.profile_settings_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""
        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyChxF3wrDrHLCRsem-L1okrXSmMmgVLUMg");
        }
        var fields: List<Place.Field>  = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        //**********End show back button on top toolbar**********

        toolbarprofilesave = findViewById(R.id.toolbar_profile_save)

        toolbarprofilesave.setOnClickListener {
            saveCurrentProfileUser()
        }

        profile_delete_account_button.setOnClickListener {
            deleteCurrentProfileUserPrompt()
        }

        profile_pics_edit_button.setOnClickListener {
            val intent =  Intent(this, ProfilePicsActivity::class.java)
            startActivity(intent)
        }


        profile_mydiscovery_button.setOnClickListener {
            val intent =  Intent(this, DiscoveryActivity::class.java)
            startActivity(intent)
        }

        profile_events_edit_button.setOnClickListener {
            val intent =  Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }

        profile_homepics_edit_button.setOnClickListener {
            val intent =  Intent(this, ProfileHomePicsActivity::class.java)
            startActivity(intent)
        }

        profile_searchlocation_edittext.setOnClickListener {
            checkclickinput = true
            var intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }
        profile_hometravel_edittext.setOnClickListener {
            checkclickinput = false
            var intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)

                .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }


        fetchCurrentUser()


        profile_checkbox_acceptguests.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                profile_checkbox_acceptguests.setTextColor(getResources().getColor(R.color.LightOrange))
                profile_checkbox_maybeacceptguests.setChecked(false)
                profile_checkbox_notacceptguests.setChecked(false)

                profile_checkbox_acceptguests.setClickable(false)
                profile_checkbox_maybeacceptguests.setClickable(true)
                profile_checkbox_notacceptguests.setClickable(true)

            }
            else {
                profile_checkbox_acceptguests.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        profile_checkbox_maybeacceptguests.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                profile_checkbox_maybeacceptguests.setTextColor(getResources().getColor(R.color.LightOrange))
                profile_checkbox_acceptguests.setChecked(false)
                profile_checkbox_notacceptguests.setChecked(false)

                profile_checkbox_maybeacceptguests.setClickable(false)
                profile_checkbox_acceptguests.setClickable(true)
                profile_checkbox_notacceptguests.setClickable(true)
            }
            else {
                profile_checkbox_maybeacceptguests.setTextColor(getResources().getColor(R.color.CommonBlue))
            }
        }
        profile_checkbox_notacceptguests.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                profile_checkbox_notacceptguests.setTextColor(getResources().getColor(R.color.LightOrange))
                profile_checkbox_acceptguests.setChecked(false)
                profile_checkbox_maybeacceptguests.setChecked(false)

                profile_checkbox_notacceptguests.setClickable(false)
                profile_checkbox_acceptguests.setClickable(true)
                profile_checkbox_maybeacceptguests.setClickable(true)
            }
            else {
                profile_checkbox_notacceptguests.setTextColor(getResources().getColor(R.color.CommonBlue))
            }
        }
        profile_checkbox_meetup.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                profile_checkbox_meetup.setTextColor(getResources().getColor(R.color.LightOrange))
            }
            else {
                profile_checkbox_meetup.setTextColor(getResources().getColor(R.color.CommonBlue))
            }
        }
        profile_checkbox_date.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                profile_checkbox_date.setTextColor(getResources().getColor(R.color.LightOrange))
            }
            else {
                profile_checkbox_date.setTextColor(getResources().getColor(R.color.CommonBlue))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        var mReference = FirebaseDatabase.getInstance().getReference().child("users/" + FirebaseAuth.getInstance().uid);

        mReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var sdf = SimpleDateFormat("yyyy/MM/dd")
                val currentDate = sdf.format(Date());
                println("currnet date " + currentDate)

                var membershipdate : String = p0.child("membershipdate").getValue().toString();
                println("membership date " + membershipdate)
                if(currentDate > membershipdate){
                    membershipStatus = "no"
                }else{
                    membershipStatus = "yes"
                }

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {

            var place: Place = Autocomplete.getPlaceFromIntent(data);
            Log.d("googleplace", "Place: " + place.getAddress());

            var address = getAddress(place)

            if(checkclickinput == true){
                profile_searchlocation_edittext.setText( address.subAdminArea  + ", " + address.countryName)
            }
            else if(checkclickinput == false) {
                profile_hometravel_edittext.setText( address.subAdminArea  + ", " + address.countryName)
            }
//            else if(checkclickinput == false) {
//                profile_hometravel_edittext.setText( if (address.featureName !== null) { address.featureName } else { address.adminArea } + ", " + address.countryName)
//            }



        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            var status: Status = Autocomplete.getStatusFromIntent(data!!);
            Log.d("googleplace", status.getStatusMessage());
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }

    private fun getAddress(place: Place): Address {
        var locate = Locale("pt")
        var geocoder = Geocoder(baseContext, locate)
        var address = if (place.latLng !== null) { geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1) } else { geocoder.getFromLocationName(place.address, 1) }

        return address.get(0)
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUserProfileSettings = p0.getValue(User::class.java)
                Log.d("BottomNav", currentUserProfileSettings?.username)
                profile_name_edittext.setText(currentUserProfileSettings?.name)
                profile_age_edittext.setText(currentUserProfileSettings?.age)
                profile_whatdoyoudo_edittext.setText(currentUserProfileSettings?.occupation)
                profile_email_edittext.setText(currentUserProfileSettings?.email)
                profile_username_edittext.setText(currentUserProfileSettings?.username)
                profile_aboutme_edittext.setText(currentUserProfileSettings?.aboutMe)
                profile_aboutplace_edittext.setText(currentUserProfileSettings?.aboutMyPlace)
                profile_searchlocation_edittext.setText(currentUserProfileSettings?.searchLoc)
                current_location_view.text = currentUserProfileSettings?.currentLoc
                profile_hometravel_edittext.setText(currentUserProfileSettings?.loc)
                profile_aboutservice_edittext.setText(currentUserProfileSettings?.aboutServices)

                var status = p0.child("role").getValue(String::class.java)
                if(status != null){
                    println("show")
                    event_layout.setVisibility(LinearLayout.VISIBLE)
                    profile_mydiscoveries_view.setVisibility(LinearLayout.VISIBLE)
                }else{
                    println("hidden")
                }

                if(ProfileFragment.currentUserProfile?.aboutServicesEnabled == true) {

                    profile_checkbox_offerservicecheckbox.setVisibility(View.VISIBLE)

                }
                else {
                    profile_checkbox_offerservicecheckbox.setVisibility(View.GONE)
                }

                if(ProfileFragment.currentUserProfile?.aboutServicesEnabled == true && ProfileFragment.currentUserProfile?.offerServiseChecked == true) {

                    profile_checkbox_offerservicecheckbox.setChecked(true)
                    profile_aboutservice_edittext.setVisibility(View.VISIBLE)
                    offerServiseChecked = true
                 //   profile_mydiscoveries_view.setVisibility(View.VISIBLE)
                }
                else {
                    profile_checkbox_offerservicecheckbox.setChecked(false)
                    profile_aboutservice_edittext.setVisibility(View.GONE)
                //    profile_mydiscoveries_view.setVisibility(View.GONE)
                    offerServiseChecked = false
                }

                profile_checkbox_offerservicecheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked) {
                        profile_checkbox_offerservicecheckbox.setTextColor(getResources().getColor(R.color.LightOrange))
                        profile_aboutservice_edittext.setVisibility(View.VISIBLE)
                    //    profile_mydiscoveries_view.setVisibility(View.VISIBLE)
                        offerServiseChecked = true

                    }
                    else {
                        profile_checkbox_offerservicecheckbox.setTextColor(getResources().getColor(R.color.CommonBlue))
                        profile_aboutservice_edittext.setVisibility(View.GONE)
                    //    profile_mydiscoveries_view.setVisibility(View.GONE)
                        offerServiseChecked = false
                    }
                }

                if(ProfileFragment.currentUserProfile?.acceptingGuests == "yes") {
                    profile_checkbox_acceptguests.setChecked(true)
                }
                else {
                    profile_checkbox_acceptguests.setChecked(false)
                }

                if(ProfileFragment.currentUserProfile?.acceptingGuests == "maybe") {

                    profile_checkbox_maybeacceptguests.setChecked(true)

                }
                else {
                    profile_checkbox_maybeacceptguests.setChecked(false)
                }

                if(ProfileFragment.currentUserProfile?.acceptingGuests == "no") {

                    profile_checkbox_notacceptguests.setChecked(true)

                }
                else {
                    profile_checkbox_notacceptguests.setChecked(false)
                }

                if(ProfileFragment.currentUserProfile?.meetChecked == true) {

                    profile_checkbox_meetup.setChecked(true)

                }
                else {
                    profile_checkbox_meetup.setChecked(false)
                }
                if(ProfileFragment.currentUserProfile?.dateChecked == true) {

                    profile_checkbox_date.setChecked(true)

                }
                else {
                    profile_checkbox_date.setChecked(false)
                }



            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        val admin_user_ref = FirebaseDatabase.getInstance().getReference("/admin-members/$uid")
        admin_user_ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                System.out.println(p0.toString());
                if(p0.value != null)
                {
                    profile_mydiscoveries_view.setVisibility(View.VISIBLE)
//                    profile_events_view.setVisibility(View.VISIBLE)
                }

            }
        })
    }

    private fun deleteCurrentProfileUserPrompt() {


        val builder = AlertDialog.Builder(this)



        builder.setTitle("Do you realy want to delete your account?")

        builder.setPositiveButton("YES") { dialog, which ->
            deleteCurrentProfileUserSecondPrompt()
        }

        builder.setNeutralButton("No"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()
    }

    private fun deleteCurrentProfileUserSecondPrompt() {



        val builder = AlertDialog.Builder(this)



        builder.setTitle("Are you sure?")

        builder.setMessage("It can be restored by signing in again")

        builder.setPositiveButton("YES") { dialog, which ->


            val uid = FirebaseAuth.getInstance().uid ?: ""
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")


            val map: HashMap<String, Any> = hashMapOf(
                "arcProfileImageUrl" to currentUserProfileSettings!!.profileImageUrl,
                "arcLoc" to currentUserProfileSettings!!.loc,
                "profileImageUrl" to "deleted",
                "loc" to "deleted",
                "wasDeleted" to "bySelf"

            )


            ref.updateChildren(map)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    val intent =  Intent(this, TermsScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    //Do some logging
                }




        }

        builder.setNeutralButton("No"){dialog,which ->
        }

        val dialog: AlertDialog = builder.create()
        //dialog.getWindow().setLayout(600, 400);
        dialog.show()
    }



    private fun saveCurrentProfileUser(){

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")



        if(profile_checkbox_acceptguests.isChecked) {
            profileacceptingGuests = "yes"
        }
        if(profile_checkbox_maybeacceptguests.isChecked){
            profileacceptingGuests = "maybe"
        }
        if(profile_checkbox_notacceptguests.isChecked){
            profileacceptingGuests = "no"
        }

        if(profile_checkbox_meetup.isChecked) {
            profilemeetup = true
        } else {
            profilemeetup = false
        }

        if(profile_checkbox_date.isChecked){
            profiledate = true
        } else {
            profiledate = false
        }

//        profile_name_edittext.toString()
//        profile_age_edittext.toString()
//        profile_whatdoyoudo_edittext.toString()
//        profile_email_edittext.toString()
//        profile_username_edittext.toString()
//        profile_aboutme_edittext.toString()
//        profile_aboutplace_edittext.toString()
//        profile_searchlocation_edittext.toString()
//        current_location_view.toString()
//        profile_hometravel_edittext.toString()

        val map: HashMap<String, Any> = hashMapOf(
            "name" to profile_name_edittext.text.toString(),
            "age"  to profile_age_edittext.text.toString(),
            "occupation" to profile_whatdoyoudo_edittext.text.toString(),
            "email" to profile_email_edittext.text.toString(),
            "username" to profile_username_edittext.text.toString(),
            "aboutMe" to profile_aboutme_edittext.text.toString(),
            "aboutMyPlace" to profile_aboutplace_edittext.text.toString(),
            "searchLoc" to profile_searchlocation_edittext.text.toString(),
            "loc" to profile_hometravel_edittext.text.toString(),
            "acceptingGuests" to profileacceptingGuests.toString(),
            "meetChecked" to profilemeetup!!,
            "dateChecked" to profiledate!!,
            "offerServiseChecked" to offerServiseChecked!!,
            "aboutServices" to profile_aboutservice_edittext.text.toString()



        )


//        val user =
//            User(uid, profile_name_edittext.toString(), profile_username_edittext.toString(), profile_email_edittext.toString(), profileImageUrl, ageedittextregister?.text.toString(), "Programmer", hometraveledittextregister?.text.toString(), "Odessa", false, false, "yes", "", "", "")

        ref.updateChildren(map)
            .addOnSuccessListener {
                Log.d("Register", "Saved user to DB")
                Toast.makeText(applicationContext, "Settings saved", Toast.LENGTH_SHORT).show()
                finish()


            }
            .addOnFailureListener {
                //Do some logging
            }


    }



}
