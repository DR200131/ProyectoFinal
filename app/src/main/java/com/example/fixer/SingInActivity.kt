package com.example.fixer

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SingInActivity : AppCompatActivity() {

    private lateinit var btnSU : Button
    private lateinit var btnSI : Button
    private lateinit var ede : EditText
    private lateinit var edp : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)

        btnSI = findViewById(R.id.login_btn)
        btnSI.setOnClickListener {
            loginUser();
        }

        btnSU = findViewById(R.id.sign_up_btn)
        btnSU.setOnClickListener {
            startActivity(Intent(this, SingUpActivity()::class.java))
        }
    }

    private fun loginUser() {
        ede = findViewById(R.id.email_login)
        edp = findViewById(R.id.password_login)

        val email = ede.text.toString()
        val passw = edp.text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(this, "El campo Correo es requerido.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(passw) -> Toast.makeText(this, "El campo Contraseña es requerido.", Toast.LENGTH_LONG).show()

            else ->{
                val progressDialog = ProgressDialog(this@SingInActivity)
                progressDialog.setTitle("Ingresando")
                progressDialog.setMessage("Por favor espere, esto puede tardar unos minutos.")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, passw).addOnCompleteListener {task ->
                    if (task.isSuccessful)
                    {
                        progressDialog.dismiss()
                        val intent = Intent(this@SingInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this@SingInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}