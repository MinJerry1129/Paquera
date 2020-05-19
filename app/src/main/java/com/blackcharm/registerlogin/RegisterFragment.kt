package com.blackcharm.registerlogin

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.blackcharm.R
import java.util.regex.Pattern


class RegisterFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var nextbuttonregister: Button
    lateinit var selectphotobuttonregister: ImageView
    lateinit var emailedittextregister: TextView
    lateinit var ageedittextregister: TextView
    lateinit var hometraveledittextregister: TextView
    lateinit var passwordedittextregister: TextView
    lateinit var passwordrepeatedittextregister: TextView
    lateinit var nameedittextregister: TextView
    lateinit var registerfemalecheckbox: CheckBox
    lateinit var registermalecheckbox: CheckBox

    var fbId: String? = ""
    var fbImage: String? = null
    var FbToken: AccessToken? = AccessToken.getCurrentAccessToken()
    var aboutthisappregister: String? = ""
    lateinit var yourSex: TextView
    lateinit var registeruploadavatartext: TextView
    lateinit var registernametext: TextView
    lateinit var registeragetext: TextView
    lateinit var registerhometraveltext: TextView
    lateinit var registeremailtext: TextView
    lateinit var registerpasswordtext: TextView
    lateinit var registerrepeatpasswordtext: TextView
    lateinit var registeraboutustext: TextView
    lateinit var registeraboutthisappregister: Spinner
    lateinit var registerfromperson: LinearLayout
    lateinit var registerfrompersoncode: TextView
    lateinit var registerfrompersonname: TextView
    lateinit var registerfrompersoncompany: TextView





    var registerfemale: Boolean = false
    var registermale: Boolean = false

    var nameIsOk = false
    var ageIsOk = false
    var emailIsOk = false
    var locIsOk = false
    var genderIsOk = false
    var passIsOk = false
    var passMatchIsOk = false
    var picIsOc = false
    var referalFromPersonIsOk = true
    var checkclickinput: Boolean? = false
    val AUTOCOMPLETE_REQUEST_CODE: Int = 1;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater?.inflate(R.layout.register_fragment, container, false)
        if (!Places.isInitialized()) {
            Places.initialize(context!!, "AIzaSyChxF3wrDrHLCRsem-L1okrXSmMmgVLUMg");
        }
        var fields: List<Place.Field>  = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);






        nextbuttonregister = view.findViewById(R.id.next_button_register)
        selectphotobuttonregister = view.findViewById(R.id.selectphoto_button_register)
        emailedittextregister = view.findViewById(R.id.email_edittext_register)
        ageedittextregister = view.findViewById(R.id.age_edittext_register)
        hometraveledittextregister = view.findViewById(R.id.hometravel_edittext_register)
        passwordedittextregister = view.findViewById(R.id.password_edittext_register)
        passwordrepeatedittextregister = view.findViewById(R.id.password_repeat_edittext_register2)

        nameedittextregister = view.findViewById(R.id.name_edittext_register)
        registerfemalecheckbox = view.findViewById(R.id.register_female_checkbox)
        registermalecheckbox = view.findViewById(R.id.register_male_checkbox)



        yourSex = view.findViewById(R.id.register_gender_text)
        registernametext = view.findViewById(R.id.register_name_text)
        registeruploadavatartext = view.findViewById(R.id.register_upload_avatar_text)
        registeragetext = view.findViewById(R.id.register_age_text)
        registerhometraveltext = view.findViewById(R.id.register_home_travel_text)
        registeremailtext = view.findViewById(R.id.register_email_text)
        registerpasswordtext = view.findViewById(R.id.register_password_text)
        registerrepeatpasswordtext = view.findViewById(R.id.register_repeatpassword_text)
        registeraboutustext = view.findViewById(R.id.register_aboutus_text)
        registeraboutthisappregister = view.findViewById(R.id.about_this_app_register)
        registerfromperson = view.findViewById(R.id.from_person)
        registerfrompersoncode = view.findViewById(R.id.from_person_code)
        registerfrompersoncompany = view.findViewById(R.id.from_person_company)
        registerfrompersonname = view.findViewById(R.id.from_person_name)




        fbId = getActivity()?.intent?.getStringExtra("FbId")

        if(fbId !=null) {
            nameedittextregister.text = getActivity()?.intent?.getStringExtra("Name")
            emailedittextregister.text = getActivity()?.intent?.getStringExtra("Email")
            fbImage = getActivity()?.intent?.getStringExtra("FbPhoto")
            selectedPhotoUri = Uri.parse(fbImage)

            FbToken = getActivity()?.intent?.getParcelableExtra("FbToken")
            passIsOk = true
            passMatchIsOk = true
            passwordedittextregister.setVisibility(View.GONE)
            passwordrepeatedittextregister.setVisibility(View.GONE)
            registerpasswordtext.setVisibility(View.GONE)
            registerrepeatpasswordtext.setVisibility(View.GONE)


            Glide.with(selectphotobuttonregister).load(fbImage)
                .placeholder(R.drawable.progress_animation)

                .error(R.drawable.branco).into(selectphotobuttonregister);


        }

            hometraveledittextregister.setOnClickListener {
                checkclickinput = true
                var intent: Intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(context!!);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }



        selectphotobuttonregister?.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        registerfemalecheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                registerfemalecheckbox.setTextColor(getResources().getColor(R.color.LightOrange))
                registermalecheckbox.setChecked(false)


                registerfemalecheckbox.setClickable(false)
                registermalecheckbox.setClickable(true)


            }
            else {
                registerfemalecheckbox.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        registermalecheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                registermalecheckbox.setTextColor(getResources().getColor(R.color.LightOrange))
                registerfemalecheckbox.setChecked(false)


                registermalecheckbox.setClickable(false)
                registerfemalecheckbox.setClickable(true)

            }
            else {
                registermalecheckbox.setTextColor(getResources().getColor(R.color.CommonBlue))
            }
        }

        registeraboutthisappregister.setOnItemSelectedListener(this)


        nextbuttonregister?.setOnClickListener {
            //performRegister()

            checkFields()

        }

        return view
    }



    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {

        var menuArray = getResources().getStringArray(R.array.about_arrays)


        aboutthisappregister = menuArray[position].toLowerCase()

        if (aboutthisappregister.equals("from a person")) {
            registerfrompersoncode.text = ""
            registerfrompersonname.text = ""
            registerfrompersoncompany.text = ""
            referalFromPersonIsOk = false
            registerfromperson.visibility = View.VISIBLE
        } else {
            referalFromPersonIsOk = true
            registerfromperson.visibility = View.GONE
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }


    private fun isValidEmail(email:String):Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    private fun checkFields() {
        if (selectedPhotoUri !=null) {
            registeruploadavatartext.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
            picIsOc = true
        } else {
            registeruploadavatartext.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
            picIsOc = false
        }

        if (nameedittextregister?.text.toString() == "" || nameedittextregister?.text.toString() == "DELETED") {
            registernametext.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
            nameIsOk = false
        } else {
            registernametext.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
            nameIsOk = true
        }

        if (ageedittextregister?.text.toString() == "" || ageedittextregister?.text.toString().toInt() < 18 || ageedittextregister?.text.toString().toInt() > 100) {
            registeragetext.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
            ageIsOk = false
        } else {
            registeragetext.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
            ageIsOk = true
        }

        if (isValidEmail(emailedittextregister?.text.toString())) {
            registeremailtext.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
            emailIsOk = true
        } else {
            registeremailtext.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
            emailIsOk = false

        }


        if (passwordedittextregister.getVisibility() == View.VISIBLE) {

            if (passwordedittextregister?.text.length < 6) {
                registerpasswordtext.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
                passIsOk = false
            } else {
                registerpasswordtext.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
                passIsOk = true
            }

            if (passwordedittextregister?.text.toString() == passwordrepeatedittextregister?.text.toString()) {
                passMatchIsOk = true
                registerrepeatpasswordtext.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
            } else {
                passMatchIsOk = false
                registerrepeatpasswordtext.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
            }
        }

        if (!registermalecheckbox.isChecked && !registerfemalecheckbox.isChecked) {
            yourSex.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
            genderIsOk = false
        } else {
            yourSex.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
            genderIsOk = true
        }

        if (hometraveledittextregister?.text.length < 2) {
            registerhometraveltext.setTextColor(ContextCompat.getColor(context!!, R.color.LightOrange))
            locIsOk = false
        } else {
            registerhometraveltext.setTextColor(ContextCompat.getColor(context!!, R.color.commonGrey))
            locIsOk = true
        }

//        Log.d("referalMethod", referalMethod)
//        Log.d("referalCode", referalCode)
        Log.d("referalName", registerfrompersoncode.text.toString())
//        Log.d("referalCompany", referalCompany)

        if (aboutthisappregister == "from a person" && registerfrompersoncode.text.toString() == "" && registerfrompersonname.text.toString() == "" && registerfrompersoncompany.text.toString() == "") {
            referalFromPersonIsOk = false
        } else {
            referalFromPersonIsOk = true
        }


        if (nameIsOk == true && ageIsOk == true && emailIsOk == true && passIsOk == true && passMatchIsOk == true && genderIsOk == true && locIsOk == true && picIsOc == true && referalFromPersonIsOk == true) {

            if(registerfemalecheckbox.isChecked) {
                registerfemale = true
            } else {
                registerfemale = false
            }

            if(registermalecheckbox.isChecked){
                registermale = true
            } else {
                registermale = false
            }
            val intent =  Intent(getActivity(), WelcomeRegisterActivity::class.java)
            intent.putExtra("Name", nameedittextregister?.text.toString())
            intent.putExtra("Email", emailedittextregister?.text.toString())
            intent.putExtra("Photo", selectedPhotoUri)
            intent.putExtra("Age", ageedittextregister?.text.toString())
            intent.putExtra("HomeTravelLoc", hometraveledittextregister?.text.toString())
            intent.putExtra("genderMale", registermale)
            intent.putExtra("genderFemale", registerfemale)
            intent.putExtra("password", passwordedittextregister?.text.toString())
            intent.putExtra("FbToken", FbToken)
            intent.putExtra("referalMethod", aboutthisappregister)
            intent.putExtra("referalCode", registerfrompersoncode?.text.toString())
            intent.putExtra("referalName", registerfrompersonname?.text.toString())
            intent.putExtra("referalCompany", registerfrompersoncompany?.text.toString())
            startActivity(intent)
            //getActivity()?.finish()
        }
        else if (nameIsOk == true && ageIsOk == true && emailIsOk == true && passIsOk == true && passMatchIsOk == false && genderIsOk == true && locIsOk == true && picIsOc == true) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        }
        else if (nameIsOk == true && ageIsOk == true && emailIsOk == true && passIsOk == true && passMatchIsOk == true && genderIsOk == true && locIsOk == true && picIsOc == false) {
            Toast.makeText(context, "The profile picture is mandatory", Toast.LENGTH_SHORT).show()

        }
        else if (referalFromPersonIsOk == false) {
            Toast.makeText(context, "Please fill at least one field about the person that told you about us", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Please check and make sure all the fields are filled correctly", Toast.LENGTH_SHORT).show()
        }

    }



    var selectedPhotoUri: Uri? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && data != null) {
            if(checkclickinput == true) {
                var place: Place = Autocomplete.getPlaceFromIntent(data);
                Log.d("Register", "true")

                var address = getAddress(place)
                println(address.toString())
                hometraveledittextregister?.text = if (address.featureName !== null) { address.featureName } else { address.adminArea } + ", " + address.countryName

                checkclickinput = false
            }
            else {

                selectedPhotoUri = data.data

                Log.d("Register", "Image: ${selectedPhotoUri}")

                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedPhotoUri)


                val bitmapDrawable = BitmapDrawable(bitmap)

                selectphotobuttonregister?.setImageDrawable(bitmapDrawable)
            }

        }
    }

    private fun getAddress(place: Place): Address {
        var locate = Locale("pt")
        var geocoder = Geocoder(context, locate)
        var address = if (place.latLng !== null) { geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1) } else { geocoder.getFromLocationName(place.address, 1) }

        return address.get(0)
    }


//    private fun performRegister() {
//        val email = emailedittextregister?.text.toString()
//        val password = passwordedittextregister?.text.toString()
//
//        if(email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(getActivity(), "Please enter email and password", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        Log.d("RegisterLoginActivityMain", "Email is: " + email)
//        Log.d("RegisterLoginActivityMain", "Password: $password")
//
//        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener {
//                if (!it.isSuccessful) return@addOnCompleteListener
//
//                //else if success
//
//                Log.d("Main", "Success register: ${it.result?.user?.uid}")
//
//                uploadImageToFirebaseStorage()
//            }
//            .addOnFailureListener {
//                // Log.d("Main", "Failed to create user: ${it.message}")
//                Toast.makeText(getActivity(), "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

//    private fun uploadImageToFirebaseStorage() {
//        Log.d("Main", "Trying to upload image")
//        if (selectedPhotoUri == null) return
//
//        val filename = UUID.randomUUID().toString()
//        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
//
//        Log.d("Main", "Uploading")
//
//        ref.putFile(selectedPhotoUri!!)
//            .addOnSuccessListener {
//                Log.d("Register", "Succesfully uploaded image: ${it.metadata?.path}")
//
//                ref.downloadUrl.addOnSuccessListener {
//                    //Log.d("RegisterLoginActivityMain", "File Location: $it")
//
//                    saveUserToFirebaseDatabase(it.toString())
//
//                }
//            }
//            .addOnFailureListener {
//                //Do some logging
//            }
//    }

//    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
//        val uid = FirebaseAuth.getInstance().uid ?: ""
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//
//        val user =
//            User(uid, nameedittextregister?.text.toString(), "", emailedittextregister.toString(), profileImageUrl, ageedittextregister?.text.toString(), "Programmer", hometraveledittextregister?.text.toString(), "Odessa", false, false, "yes", "", "", "")
//
//        ref.setValue(user)
//            .addOnSuccessListener {
//                Log.d("Register", "Saved user to DB")
//
//                val intent = Intent(getActivity(), LatestMessagesActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//            }
//            .addOnFailureListener {
//                //Do some logging
//            }
//
//    }
}

//
//class User(val uid: String, val username: String, val profileImageUrl: String) {
//    constructor() : this("", "", "")
//}
