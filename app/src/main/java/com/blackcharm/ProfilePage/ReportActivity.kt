package com.blackcharm.ProfilePage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.blackcharm.R
import kotlinx.android.synthetic.main.activity_report.*

class ReportActivity : AppCompatActivity() {

//    companion object {
//
//
//
//    }

    lateinit var reportcheckboxinnmessages: CheckBox
    lateinit var reportcheckboxinnphotos: CheckBox
    lateinit var reportcheckboxfakeprofile: CheckBox
    lateinit var reportcheckboxspam: CheckBox
    lateinit var reportcheckboxcommprofile: CheckBox
    lateinit var reportcheckboxscam: CheckBox
    lateinit var reportcheckboxother: CheckBox
    lateinit var reportmessageedittext: TextView
    lateinit var reportsendbutton: Button
    var reportReason: String? = ""
    var reportMethod: String? = ""
    var reportUserId: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        //**********Show back button on top toolbar**********

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.profile_settings_toolbar);
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        //**********End show back button on top toolbar**********

        reportcheckboxinnmessages = findViewById(R.id.report_checkbox_inn_messages)
        reportcheckboxinnphotos = findViewById(R.id.report_checkbox_inn_photos)
        reportcheckboxfakeprofile = findViewById(R.id.report_checkbox_fake_profile)
        reportcheckboxspam = findViewById(R.id.report_checkbox_spam)
        reportcheckboxcommprofile = findViewById(R.id.report_checkbox_comm_profile)
        reportcheckboxscam = findViewById(R.id.report_checkbox_scam)
        reportcheckboxother = findViewById(R.id.report_checkbox_other)
        reportmessageedittext = findViewById(R.id.report_message_edittext)
        reportsendbutton = findViewById(R.id.report_send_button)

        reportMethod = intent.extras.getString("Method")
        reportUserId = intent.extras.getString("ReportUserId")

        reportcheckboxinnmessages.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                reportcheckboxinnmessages.setTextColor(getResources().getColor(R.color.LightOrange))
                reportcheckboxinnphotos.setChecked(false)
                reportcheckboxfakeprofile.setChecked(false)
                reportcheckboxspam.setChecked(false)
                reportcheckboxcommprofile.setChecked(false)
                reportcheckboxscam.setChecked(false)
                reportcheckboxother.setChecked(false)

                reportcheckboxinnmessages.setClickable(false)
                reportcheckboxinnphotos.setClickable(true)
                reportcheckboxfakeprofile.setClickable(true)
                reportcheckboxspam.setClickable(true)
                reportcheckboxcommprofile.setClickable(true)
                reportcheckboxscam.setClickable(true)
                reportcheckboxother.setClickable(true)

                reportReason = "Innapropriate messages"

            }
            else {
                reportcheckboxinnmessages.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        reportcheckboxinnphotos.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                reportcheckboxinnphotos.setTextColor(getResources().getColor(R.color.LightOrange))
                reportcheckboxinnmessages.setChecked(false)
                reportcheckboxfakeprofile.setChecked(false)
                reportcheckboxspam.setChecked(false)
                reportcheckboxcommprofile.setChecked(false)
                reportcheckboxscam.setChecked(false)
                reportcheckboxother.setChecked(false)

                reportcheckboxinnphotos.setClickable(false)
                reportcheckboxinnmessages.setClickable(true)
                reportcheckboxfakeprofile.setClickable(true)
                reportcheckboxspam.setClickable(true)
                reportcheckboxcommprofile.setClickable(true)
                reportcheckboxscam.setClickable(true)
                reportcheckboxother.setClickable(true)

                reportReason = "Innapropriate photos"

            }
            else {
                reportcheckboxinnphotos.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        reportcheckboxfakeprofile.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                reportcheckboxfakeprofile.setTextColor(getResources().getColor(R.color.LightOrange))
                reportcheckboxinnmessages.setChecked(false)
                reportcheckboxinnphotos.setChecked(false)
                reportcheckboxspam.setChecked(false)
                reportcheckboxcommprofile.setChecked(false)
                reportcheckboxscam.setChecked(false)
                reportcheckboxother.setChecked(false)

                reportcheckboxfakeprofile.setClickable(false)
                reportcheckboxinnmessages.setClickable(true)
                reportcheckboxinnphotos.setClickable(true)
                reportcheckboxspam.setClickable(true)
                reportcheckboxcommprofile.setClickable(true)
                reportcheckboxscam.setClickable(true)
                reportcheckboxother.setClickable(true)

                reportReason = "Fake profile"

            }
            else {
                reportcheckboxfakeprofile.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        reportcheckboxspam.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                reportcheckboxspam.setTextColor(getResources().getColor(R.color.LightOrange))
                reportcheckboxinnmessages.setChecked(false)
                reportcheckboxinnphotos.setChecked(false)
                reportcheckboxfakeprofile.setChecked(false)
                reportcheckboxcommprofile.setChecked(false)
                reportcheckboxscam.setChecked(false)
                reportcheckboxother.setChecked(false)

                reportcheckboxspam.setClickable(false)
                reportcheckboxinnmessages.setClickable(true)
                reportcheckboxinnphotos.setClickable(true)
                reportcheckboxfakeprofile.setClickable(true)
                reportcheckboxcommprofile.setClickable(true)
                reportcheckboxscam.setClickable(true)
                reportcheckboxother.setClickable(true)

                reportReason = "Spam"

            }
            else {
                reportcheckboxspam.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        reportcheckboxcommprofile.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                reportcheckboxcommprofile.setTextColor(getResources().getColor(R.color.LightOrange))
                reportcheckboxinnmessages.setChecked(false)
                reportcheckboxinnphotos.setChecked(false)
                reportcheckboxfakeprofile.setChecked(false)
                reportcheckboxspam.setChecked(false)
                reportcheckboxscam.setChecked(false)
                reportcheckboxother.setChecked(false)

                reportcheckboxcommprofile.setClickable(false)
                reportcheckboxinnmessages.setClickable(true)
                reportcheckboxinnphotos.setClickable(true)
                reportcheckboxfakeprofile.setClickable(true)
                reportcheckboxspam.setClickable(true)
                reportcheckboxscam.setClickable(true)
                reportcheckboxother.setClickable(true)

                reportReason = "Commercial profile"

            }
            else {
                reportcheckboxcommprofile.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        reportcheckboxscam.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                reportcheckboxscam.setTextColor(getResources().getColor(R.color.LightOrange))
                reportcheckboxinnmessages.setChecked(false)
                reportcheckboxinnphotos.setChecked(false)
                reportcheckboxfakeprofile.setChecked(false)
                reportcheckboxspam.setChecked(false)
                reportcheckboxcommprofile.setChecked(false)
                reportcheckboxother.setChecked(false)

                reportcheckboxscam.setClickable(false)
                reportcheckboxinnmessages.setClickable(true)
                reportcheckboxinnphotos.setClickable(true)
                reportcheckboxfakeprofile.setClickable(true)
                reportcheckboxspam.setClickable(true)
                reportcheckboxcommprofile.setClickable(true)
                reportcheckboxother.setClickable(true)

                reportReason = "Scam"

            }
            else {
                reportcheckboxscam.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }
        reportcheckboxother.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                reportcheckboxother.setTextColor(getResources().getColor(R.color.LightOrange))
                reportcheckboxinnmessages.setChecked(false)
                reportcheckboxinnphotos.setChecked(false)
                reportcheckboxfakeprofile.setChecked(false)
                reportcheckboxspam.setChecked(false)
                reportcheckboxcommprofile.setChecked(false)
                reportcheckboxscam.setChecked(false)

                reportcheckboxother.setClickable(false)
                reportcheckboxinnmessages.setClickable(true)
                reportcheckboxinnphotos.setClickable(true)
                reportcheckboxfakeprofile.setClickable(true)
                reportcheckboxspam.setClickable(true)
                reportcheckboxcommprofile.setClickable(true)
                reportcheckboxscam.setClickable(true)

                reportReason = "Other"

            }
            else {
                reportcheckboxother.setTextColor(getResources().getColor(R.color.CommonBlue))

            }
        }

        reportsendbutton.setOnClickListener {
            sentReport()
        }



    }

    private fun sentReport() {
        if (reportmessageedittext.text.length > 0) {
            var uid = FirebaseAuth.getInstance().uid
            var timestamp = System.currentTimeMillis()/1000
            val map: HashMap<String, Any?> = hashMapOf(
                "timestamp" to timestamp,
                "reportReason" to reportReason.toString(),
                "reportMessage" to reportmessageedittext.text.toString(),
                "reportMethod" to reportMethod.toString()
            )

//            if (reportMethod == "message") {
//                values["messageId"] = reportMessageId
//            }
            var ref = FirebaseDatabase.getInstance().getReference("users-reports").child(reportUserId!!).child(uid!!)
            ref.updateChildren(map)
            val builder = AlertDialog.Builder(this)

            builder.setTitle("O relatório foi enviado")

            builder.setNeutralButton("OK"){dialog,which ->
                finish()
            }

            val dialog: AlertDialog = builder.create()
            //dialog.getWindow().setLayout(600, 400);
            dialog.show()
            report_message_title.setTextColor(getResources().getColor(R.color.commonGrey))

        } else {
            val builder = AlertDialog.Builder(this)

            builder.setTitle("Por favor, selecione um dos motivos")
            builder.setNeutralButton("OK"){dialog,which ->
            }
            val dialog: AlertDialog = builder.create()
            //dialog.getWindow().setLayout(600, 400);
            dialog.show()

            report_message_title.setTextColor(getResources().getColor(R.color.LightOrange))
        }
    }


}
