package com.blackcharm.bottom_nav_pages


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.blackcharm.ProfilePage.MembershipActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.blackcharm.ProfilePage.MyLikesActivity
import com.blackcharm.ProfilePage.ProfilePicsActivity
import com.blackcharm.ProfilePage.ProfileSettings

import com.blackcharm.R
import com.blackcharm.models.User
import com.blackcharm.registerlogin.TermsScreen
import kotlinx.android.synthetic.main.fragment_profile_activity.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    companion object {
        var currentUserProfile: User? = null
    }

    lateinit var toolbarsettings: Button
    lateinit var toolbarsignout: Button
    lateinit var profilemylikesbutton: Button
    lateinit var profilemyfriendbutton: Button
    lateinit var profileaddpicsbutton: Button
    lateinit var profilemembershipbutton: Button
    lateinit var profileactivityimage: ImageView
    var currentUserProfileImageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }
    override fun onResume() {
        super.onResume()

        fetchCurrentUser()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_activity, container, false)



        // Inflate the layout for this fragment

        (activity as AppCompatActivity).setSupportActionBar(profile_toolbar)



        toolbarsettings = view.findViewById(R.id.toolbar_settings)
        toolbarsignout = view.findViewById(R.id.toolbar_signout)
        profilemylikesbutton = view.findViewById(R.id.profile_my_likes_button)
        profilemyfriendbutton = view.findViewById(R.id.profile_myfriend_button)
        profileaddpicsbutton = view.findViewById(R.id.profile_add_pics_button)
        profileactivityimage = view.findViewById(R.id.profile_activity_image)
        profilemembershipbutton = view.findViewById(R.id.profile_membership_plan_button)

        profileactivityimage.setOnClickListener {
            onClickImage()
        }



        toolbarsettings.setOnClickListener {
            val intent = Intent(getActivity(), ProfileSettings::class.java)
            startActivity(intent)
        }

        toolbarsignout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent =  Intent(getActivity(), TermsScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        profilemylikesbutton.setOnClickListener {
            val intent =  Intent(getActivity(), MyLikesActivity::class.java)
            startActivity(intent)
        }
        profileaddpicsbutton.setOnClickListener {
            val intent =  Intent(getActivity(), ProfilePicsActivity::class.java)
            startActivity(intent)
        }

        profilemyfriendbutton.setOnClickListener {
            val intent =  Intent(getActivity(), ContactsActivity::class.java)
            startActivity(intent)
        }
        profilemembershipbutton.setOnClickListener{
            val intent =  Intent(getActivity(), MembershipActivity::class.java)
            startActivity(intent)
        }


        fetchCurrentUser()

        return view
    }




    fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUserProfile = p0.getValue(User::class.java)
//                Log.d("BottomNav", currentUserProfile?.username)

                toolbar_profilename.text = currentUserProfile?.name + " " + currentUserProfile?.age

                Glide.with(profile_activity_image).load(currentUserProfile?.profileImageUrl)
                    .placeholder(R.drawable.progress_animation)

                    .error(R.drawable.branco).into(profile_activity_image)

                currentUserProfileImageUrl = Uri.parse(currentUserProfile?.profileImageUrl)

                profile_name.text = currentUserProfile?.name + " " + currentUserProfile?.age

                profile_activity_occupation.text = currentUserProfile?.occupation

                profile_activity_location.text = currentUserProfile?.loc

                profile_activity_current_location.text = currentUserProfile?.currentLoc

                profile_activity_offerservicetext.text = currentUserProfile?.aboutServices
                profile_activity_about_me_text.text = currentUserProfile?.aboutMe
                profile_activity_about_my_place_text.text = currentUserProfile?.aboutMyPlace

                if(currentUserProfile?.aboutServicesEnabled == true && currentUserProfile?.offerServiseChecked == true) {

                    profile_activity_offerservice.setVisibility(View.VISIBLE)
                    profile_activity_offerservicetext.setVisibility(View.VISIBLE)

                }
                else {
                    profile_activity_offerservice.setVisibility(View.GONE)
                    profile_activity_offerservicetext.setVisibility(View.GONE)
                }




                if(currentUserProfile?.meetChecked == true) {

                    profile_activity_meeting.text = "Fazer amizades"
                    profile_activity_meeting.setVisibility(View.VISIBLE)

                }
                else {
                    profile_activity_meeting.setVisibility(View.GONE)
                }
                if(currentUserProfile?.dateChecked == true) {

                    profile_activity_dating.text = "Paquera"
                    profile_activity_dating.setVisibility(View.VISIBLE)

                }
                else {
                    profile_activity_dating.setVisibility(View.GONE)
                }
                if(currentUserProfile?.acceptingGuests == "yes") {

                    profile_activity_guestaccept.text = "Aceitando convidados"
                    profile_activity_guestaccept.setVisibility(View.VISIBLE)

                }
                else if(currentUserProfile?.acceptingGuests == "maybe") {

                    profile_activity_guestaccept.text = "Talvez aceitando convidados"
                    profile_activity_guestaccept.setVisibility(View.VISIBLE)

                }
                else {
                    profile_activity_guestaccept.setVisibility(View.GONE)
                }


            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun onClickImage() {
        val intent = Intent(getActivity(), FullScreenImageActivity::class.java)
        intent.setData(currentUserProfileImageUrl)
        startActivity(intent)
    }


}
