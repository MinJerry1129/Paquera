package com.blackcharm.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.blackcharm.R
import com.blackcharm.messages.LatestMessagesActivity
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import android.app.AlertDialog


class LoginFragment : Fragment() {

    lateinit var loginbuttonlogin: Button
    lateinit var loginbuttonforgotpassword: Button
    lateinit var emailedittextlogin: TextView
    lateinit var passwordedittextlogin: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        loginbuttonlogin = view.findViewById(R.id.login_button_login)
        loginbuttonforgotpassword = view.findViewById(R.id.login_button_forgot)
        emailedittextlogin = view.findViewById(R.id.email_edittext_login)
        passwordedittextlogin = view.findViewById(R.id.password_edittext_login)

        loginbuttonlogin.setOnClickListener {

            val email = emailedittextlogin.text.toString()
            val password = passwordedittextlogin.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            if (emailedittextlogin?.text.toString() != "" && emailedittextlogin?.text.toString() !="") {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Toast.makeText(getActivity(), "Login ou senha incorretos.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            var token: String? = null
                            val intent = Intent(getActivity(), LatestMessagesActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            getActivity()?.finish()



                        }
                    }
            }
            else {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }
        }

        loginbuttonforgotpassword.setOnClickListener {
            val builder = AlertDialog.Builder(getActivity())
            builder.setTitle("Redefina sua senha")

            builder.setMessage("Digite o endereço de e-mail com o qual você se inscreveu. Se você se inscreveu no Facebook, digite o email que você usa no Facebook e crie uma senha.")

            val input = EditText(getActivity())

            input.inputType = InputType.TYPE_CLASS_TEXT
            input.hint = "Insira seu email aqui ..."
            builder.setView(input)

            builder.setPositiveButton("Redefinir",
                DialogInterface.OnClickListener { dialog, which ->
                    var email = input.text.toString()

                    if (email == "") {
                        Toast.makeText(context, "Digite o endereço de e-mail ...", Toast.LENGTH_LONG).show()
                    } else {

                        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                            if (!it.isSuccessful) {

                                Toast.makeText(
                                    getActivity(), if (it.exception!!.message == null) {
                                        it.exception!!.message
                                    } else {
                                        "Verifique seu e-mail..."
                                    }, Toast.LENGTH_LONG
                                ).show()

                                dialog.dismiss()
                            } else {
                                Toast.makeText(
                                    getActivity(), "Verifique seu e-mail para redefinir sua senha ...", Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                })
            builder.setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }
        return view



    }


}
