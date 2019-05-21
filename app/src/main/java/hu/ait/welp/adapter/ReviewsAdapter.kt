package hu.ait.welp.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.welp.CreateReviewActivity
import hu.ait.welp.DisplayReviewsActivity
import hu.ait.welp.R
import hu.ait.welp.data.Review
import kotlinx.android.synthetic.main.review_row.view.*

class ReviewsAdapter(
    private val context: Context,
    private val uId: String
) : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    private var reviewsList = mutableListOf<Review>()
    private var reviewKeys = mutableListOf<String>()

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.review_row, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = reviewsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (authorId, author, name, description, imgUrl, lat, lng, rating) =
            reviewsList[holder.adapterPosition]

        holder.tvName.text = name
        holder.tvDescription.text = description
        holder.tvLocation.text = "${lat}, ${lng}"
        holder.rbRating.rating = rating


        if (uId == authorId) {
            holder.ivDelete.visibility = View.VISIBLE

            holder.ivDelete.setOnClickListener {
                val alert = AlertDialog.Builder(
                    holder.itemView.context
                )
                alert.setTitle("Delete Review")
                alert.setMessage("Are you sure you want to delete this review?")
                alert.setPositiveButton("Yes") { dialog, witch ->

                    removeReview(holder.adapterPosition)

                }
                alert.setNegativeButton("Cancel") { dialog, witch ->
                }
                alert.show()

            }

        } else {
            holder.ivDelete.visibility = View.GONE
        }

        if (imgUrl.isNotEmpty()) {
            holder.ivPhoto.visibility = View.VISIBLE
            Glide.with(context).load(imgUrl).into(holder.ivPhoto)
        } else {
            holder.ivPhoto.visibility = View.GONE
        }

        setAnimation(holder.itemView, position)
    }

    private fun editReview(index: Int, review: Review) {
        var docRef = FirebaseFirestore.getInstance().collection("reviews").document(
            reviewKeys[index]
        )

        docRef.set(review)
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
            val animation = AnimationUtils.loadAnimation(
                context,
                android.R.anim.slide_in_left
            )
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tvName
        val tvLocation: TextView = itemView.tvLocation
        val tvDescription: TextView = itemView.tvDescription
        val rbRating: RatingBar = itemView.rbRating
        val ivPhoto: ImageView = itemView.ivPhoto
        val ivDelete: ImageView = itemView.ivDelete

    }
}
