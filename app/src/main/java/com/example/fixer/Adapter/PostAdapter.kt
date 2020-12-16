package com.example.fixer.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.fixer.Model.Post
import com.example.fixer.Model.User
import com.example.fixer.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class PostAdapter (private val mContext : Context,
                   private val mPost: List<Post>):RecyclerView.Adapter<PostAdapter.ViewHolder>()
{
    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.getPostimage()).into(holder.postImage)

        publisherInfo(holder.profileimage, holder.nombre, holder.publisher, post.getPublisher())
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var profileimage: CircleImageView
        var postImage: ImageView
        var commentButton: ImageView
        var saveButton: ImageView
        var nombre: TextView
        var publisher: TextView
        var speciality: TextView
        var descripcion: TextView
        var comments: TextView

        init {
            profileimage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)
            saveButton = itemView.findViewById(R.id.post_accept_request_btn)
            nombre = itemView.findViewById(R.id.user_name_post)
            publisher = itemView.findViewById(R.id.publisher)
            speciality = itemView.findViewById(R.id.speciality)
            descripcion = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)
        }
    }

    private fun publisherInfo(profileimage: CircleImageView, nombre: TextView, publisher: TextView, publisheID: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Tecnicos").child(publisheID)

        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val user = p0.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileimage)
                    nombre.setText(user.getNombre())
                    publisher.setText(user.getNombre())
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}