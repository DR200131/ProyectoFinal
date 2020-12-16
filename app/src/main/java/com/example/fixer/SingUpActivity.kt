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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SingUpActivity : AppCompatActivity() {

    private lateinit var btnR : Button
    private lateinit var edn : EditText
    private lateinit var eda : EditText
    private lateinit var edd : EditText
    private lateinit var ede : EditText
    private lateinit var edp : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        btnR = findViewById(R.id.sign_up_su_btn)
        btnR.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount() {
        edn = findViewById(R.id.full_name_sign_up_frag)
        eda = findViewById(R.id.last_name_sign_up_frag)
        edd = findViewById(R.id.description_sign_up_frag)
        ede = findViewById(R.id.email_sign_up_frag)
        edp = findViewById(R.id.password_sign_up_frag)

        val nombre = edn.text.toString()
        val apellido = eda.text.toString()
        val descripcion = edd.text.toString()
        val email = ede.text.toString()
        val passw = edp.text.toString()

        when{
            TextUtils.isEmpty(nombre) -> Toast.makeText(this, "El campo Nombre es requerido.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(apellido) -> Toast.makeText(this, "El campo Apellido es requerido.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "El campo Correo es requerido.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(passw) -> Toast.makeText(this, "El campo Contraseña es requerido.", Toast.LENGTH_LONG).show()

            else -> {

                val progressDialog = ProgressDialog(this@SingUpActivity)
                progressDialog.setTitle("Registrando")
                progressDialog.setMessage("Por favor espere, esto puede tardar unos minutos.")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, passw)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
                                saveUserInfo(nombre, apellido, descripcion, email, passw, progressDialog)
                            }
                            else
                            {
                                val message = task.exception!!.toString()
                                Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                                mAuth.signOut()
                                progressDialog.dismiss()
                            }
                        }
            }
        }
    }

    private fun saveUserInfo(nombre: String, apellido: String, descripcion: String, email: String, passw: String, progressDialog: ProgressDialog)
    {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Tecnicos")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserId
        userMap["nombre"] = nombre.toLowerCase()
        userMap["apellido"] = apellido.toLowerCase()
        userMap["descripcion"] = descripcion
        userMap["email"] = email
        userMap["contraseña"] = passw
        userMap["validado"] = "No"
        userMap["tecnico"] = "Ninguno"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/fixapp-e1d11.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=87194160-f78c-4340-9d1f-424a16c9bd8d"

        usersRef.child(currentUserId).setValue(userMap)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful)
                {
                    progressDialog.dismiss()
                    Toast.makeText(this, "El usuario ha sido creado correctamente", Toast.LENGTH_LONG).show()



                    val intent = Intent(this@SingUpActivity, MainActivity::class.java)
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