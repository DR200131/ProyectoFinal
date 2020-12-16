package com.example.fixer

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {

    private lateinit var ivS : ImageView
    private lateinit var ivP : ImageView
    private lateinit var etD : EditText

    private var myUrl: String = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        etD = findViewById(R.id.description_post)
        ivP = findViewById(R.id.image_post)

        ivS = findViewById(R.id.save_new_post_btn)
        ivS.setOnClickListener { uploadImage() }

        CropImage.activity()
            .setAspectRatio(2,1)
            .start(this@AddPostActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
        {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            ivP.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {
        when{
            TextUtils.isEmpty(etD.toString()) -> Toast.makeText(this, "Por favor introduzca una descripción.", Toast.LENGTH_LONG).show()
            imageUri != null -> Toast.makeText(this, "Por favor seleccione una imagen primero.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Añadiendo publicación")
                progressDialog.setMessage("Por favor espere, estamos añadiendo su publicación.")
                progressDialog.show()

                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{task ->
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

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key

                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["descripcion"] = etD.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUrl

                        ref.child(postId).updateChildren(postMap).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "La publicación ha sido añadida correctamente.", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@AddPostActivity, MainActivity::class.java)
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
                    })
            }
        }
    }
}
