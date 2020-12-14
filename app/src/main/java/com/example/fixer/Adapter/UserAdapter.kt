package com.example.fixer.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.fixer.Fragments.ProfileFragment
import com.example.fixer.Model.User
import com.example.fixer.R
import com.example.fixer.R.id.Fragment_container
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (private var mContext : Context,
                    private var mUser: List<User>,
                    private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]

        holder.nombreTextView.text = user.getNombre()
        holder.tecnicoTextView.text = user.getTecnico()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.imageProfile)

        holder.itemView.setOnClickListener { View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUid())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(Fragment_container, ProfileFragment()).commit()
        } }
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var nombreTextView: TextView = itemView.findViewById(R.id.user_name_search)
        var tecnicoTextView: TextView = itemView.findViewById(R.id.speciality_name_search)
        var imageProfile: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var requestButton: Button = itemView.findViewById(R.id.request_btn_search)
    }

}
