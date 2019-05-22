package hu.ait.welp.adapter

import android.content.Context

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.ait.welp.*
import hu.ait.welp.data.Following
import kotlinx.android.synthetic.main.activity_following.*
import kotlinx.android.synthetic.main.following_row.view.*

class FollowingAdapter(
    private val context: Context,
    private val uId: String
) : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    var listFollowing = mutableListOf<Following>()
    var followingKeys = mutableListOf<String>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val followingRowView = LayoutInflater.from(context).inflate(
            R.layout.following_row, viewGroup, false
        )

        return ViewHolder(followingRowView)
    }

    override fun getItemCount(): Int {
        return listFollowing.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val (usersId, followUsername) = listFollowing[holder.adapterPosition]

        holder.tvUsername.text = followUsername

        holder.tvUsername.setOnClickListener {
            val intentDetails = Intent(context, DisplayReviewsActivity::class.java)
            intentDetails.putExtra("KEY_USERNAME", followUsername)
            context.startActivity(intentDetails)
        }

        if (uId == usersId) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener {
                removeFollowing(holder.adapterPosition)
            }
        } else {
            holder.btnDelete.visibility = View.GONE
        }

    }

    fun addFollowing(following: Following, key: String) {
        listFollowing.add(following)
        followingKeys.add(key)
        notifyDataSetChanged()
    }

    private fun removeFollowing(index: Int) {
        FirebaseFirestore.getInstance().collection("following").document(
            followingKeys[index]
        ).delete()

        listFollowing.removeAt(index)
        followingKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removeFollowingByKey(key: String) {
        val index = followingKeys.indexOf(key)
        if (index != -1) {
            listFollowing.removeAt(index)
            followingKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(FollowingView: View) : RecyclerView.ViewHolder(FollowingView){
        val tvUsername: TextView = FollowingView.tvUsername
        val btnDelete: Button = FollowingView.btnDelete
    }
}