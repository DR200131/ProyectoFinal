package com.example.fixer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fixer.Model.User
import com.example.fixer.R.*
import com.example.fixer.R.id.save_infor_profile_btn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var lobtn : Button
    private lateinit var sibtn : ImageView
    private lateinit var imgPro : CircleImageView
    private lateinit var etN: EditText
    private lateinit var etA: EditText
    private lateinit var etD: EditText
    private lateinit var etE: EditText
    private lateinit var etP: EditText
    private var checker = ""

    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        imgPro = findViewById(R.id.profile_image_view_profile_frag)
        etN = findViewById(R.id.full_name_profile_frag)
        etA = findViewById(R.id.last_name_profile_frag)
        etD = findViewById(R.id.description_profile_frag)
        etE = findViewById(R.id.email_profile_frag)
        etP = findViewById(R.id.password_profile_frag)

        lobtn = findViewById(id.logout_btn)
        lobtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SingInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        userInfo()

        sibtn = findViewById(save_infor_profile_btn)
        sibtn.setOnClickListener {
            if(checker == "clicked")
            {

            }
            else
            {
                updateUserInfoOnly()
            }
        }
    }

    private fun updateUserInfoOnly() {

        if (etN.text.toString() == ""){
            Toast.makeText(this, "Por favor introduzca al menos un nombre", Toast.LENGTH_LONG).show()
        }
        else if(etA.text.toString() == ""){
            Toast.makeText(this, "Por favor introduzca al menos un apellido", Toast.LENGTH_LONG).show()
        }
        else if(etE.text.toString() == ""){
            Toast.makeText(this, "Por favor introduzca un correo electronico", Toast.LENGTH_LONG).show()
        }
        else if(etP.text.toString() == ""){
            Toast.makeText(this, "Por favor introduzca un contraseña", Toast.LENGTH_LONG).show()
        }
        else{
            val usersRef = FirebaseDatabase.getInstance().reference.child("Tecnicos").child(firebaseUser.uid)

            val userMap = HashMap<String, Any>()
            userMap["nombre"] = etN.text.toString().toLowerCase()
            userMap["apellido"] = etA.text.toString().toLowerCase()
            userMap["descripcion"] = etD.text.toString()
            userMap["email"] = etE.text.toString()
            userMap["contraseña"] = etP.text.toString()

            usersRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this, "El usuario ha sido actualizado correctamente", Toast.LENGTH_LONG).show()

            val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Tecnicos").child(firebaseUser.uid)

        usersRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(drawable.profile).into(imgPro)

                    etN.setText(user!!.getNombre())
                    etA.setText(user!!.getApellido())
                    etD.setText(user!!.getDescripcion())
                    etE.setText(user!!.getEmail())
                    etP.setText(user!!.getContraseña())
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

