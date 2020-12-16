package com.example.fixer

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.fixer.Model.User
import com.example.fixer.R.*
import com.example.fixer.R.id.save_infor_profile_btn
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
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
    private lateinit var tvI: TextView

    private var myUrl: String = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null
    private var checker = ""

    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        imgPro = findViewById(R.id.profile_image_view_profile_frag)
        etN = findViewById(R.id.full_name_profile_frag)
        etA = findViewById(R.id.last_name_profile_frag)
        etD = findViewById(R.id.description_profile_frag)
        etE = findViewById(R.id.email_profile_frag)
        etP = findViewById(R.id.password_profile_frag)
        tvI = findViewById(R.id.change_image_text_btn)

        lobtn = findViewById(id.logout_btn)
        lobtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SingInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        tvI.setOnClickListener {

            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        sibtn = findViewById(save_infor_profile_btn)
        sibtn.setOnClickListener {
            if(checker == "clicked")
            {
                uploadImageAndUpdateInfo()
            }
            else
            {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            imgPro.setImageURI(imageUri)
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
            val usersRef = FirebaseDatabase.getInstance().reference.child("Tecnicos")

            val userMap = HashMap<String, Any>()
            userMap["nombre"] = etN.text.toString().toLowerCase()
            userMap["apellido"] = etA.text.toString().toLowerCase()
            userMap["descripcion"] = etD.text.toString()
            userMap["email"] = etE.text.toString()
            userMap["contraseña"] = etP.text.toString()

            usersRef.child(firebaseUser.uid).updateChildren(userMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "El usuario ha sido actualizado correctamente", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this, "Error ${task.exception}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Tecnicos").child(firebaseUser.uid)

        usersRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(drawable.profile).into(imgPro)

                    etN.setText(user.getNombre())
                    etA.setText(user.getApellido())
                    etD.setText(user.getDescripcion())
                    etE.setText(user.getEmail())
                    etP.setText(user.getContraseña())
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun uploadImageAndUpdateInfo() {
        when{
            TextUtils.isEmpty(etN.toString()) -> Toast.makeText(this, "Por favor introduzca un nombre.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(etA.toString()) -> Toast.makeText(this, "Por favor introduzca un apellido.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(etE.toString()) -> Toast.makeText(this, "Por favor introduzca un email.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(etP.toString()) -> Toast.makeText(this, "Por favor introduzca una contraseña.", Toast.LENGTH_LONG).show()
            imageUri != null -> Toast.makeText(this, "Por favor seleccione una imagen primero.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Actualizando")
                progressDialog.setMessage("Por favor espere, estamos actualiando su información.")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if(!task.isSuccessful){
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri>{ task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Tecnicos")

                        val userMap = HashMap<String, Any>()
                        userMap["nombre"] = etN.text.toString().toLowerCase()
                        userMap["apellido"] = etA.text.toString().toLowerCase()
                        userMap["descripcion"] = etD.text.toString()
                        userMap["email"] = etE.text.toString()
                        userMap["contraseña"] = etP.text.toString()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "El usuario ha sido actualizado correctamente", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                progressDialog.dismiss()
                            }
                            else {
                                Toast.makeText(this, "Error ${task.exception}", Toast.LENGTH_LONG).show()
                                progressDialog.dismiss()
                            }
                        }
                    }
                    else{
                        progressDialog.dismiss()
                        Toast.makeText(this, "Error ${task.exception}", Toast.LENGTH_LONG).show()
                    }
                } )
            }
        }

    }
}

