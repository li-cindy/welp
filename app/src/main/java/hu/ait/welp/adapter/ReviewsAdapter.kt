package hu.ait.welp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.welp.R
import hu.ait.welp.data.Review
import kotlinx.android.synthetic.main.review_row.view.*

class ReviewsAdapter(
    private val context: Context,
    private val uId: String) : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    private var reviewsList = mutableListOf<Review>()
    private var reviewKeys = mutableListOf<String>()

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.review_row, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() =  reviewsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, description, imgUrl, location, rating) =
            reviewsList[holder.adapterPosition]

        holder.tvName.text = name
        holder.tvDescription.text = description
//        holder.tvLocation.text = location
        holder.tvRating.text = String.format("%s/10", rating.toString())


//        TODO: delete review
//        if (uId == authorId) {
//            holder.btnDeletePost.visibility = View.VISIBLE
//
//            holder.btnDeletePost.setOnClickListener {
//                removePost(holder.adapterPosition)
//            }
//        } else {
//            holder.btnDeletePost.visibility = View.GONE
//        }

//        TODO: add image
//        if (imgUrl.isNotEmpty()) {
//            holder.ivPhoto.visibility = View.VISIBLE
//            Glide.with(context).load(imgUrl).into(holder.ivPhoto)
//        } else {
//            holder.ivPhoto.visibility = View.GONE
//        }


        setAnimation(holder.itemView, position)
    }

    fun addReview(review: Review, key: String) {
        reviewsList.add(review)
        reviewKeys.add(key)
        notifyDataSetChanged()
    }

    private fun removeReview(index: Int) {
        FirebaseFirestore.getInstance().collection("reviews").document(
            reviewKeys[index]
        ).delete()

        reviewsList.removeAt(index)
        reviewKeys.removeAt(index)
        notifyItemRemoved(index)
    }

    fun removeReviewByKey(key: String) {
        val index = reviewKeys.indexOf(key)
        if (index != -1) {
            reviewsList.removeAt(index)
            reviewKeys.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tvName
        val tvDescription: TextView = itemView.tvDescription
        val tvRating: TextView = itemView.tvRating

    }
}
