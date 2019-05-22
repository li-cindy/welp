package hu.ait.welp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import hu.ait.welp.data.Following
import kotlinx.android.synthetic.main.new_following_dialog.view.*
import java.lang.RuntimeException

class FollowingDialog : DialogFragment() {

    interface FollowingHandler{
        fun followingCreated(username: Following)
    }

    private lateinit var followingHandler: FollowingHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is FollowingHandler) {
            followingHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the TodoHandlerInterface")
        }
    }

    private lateinit var etNewUsername: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New item")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_following_dialog, null
        )
        etNewUsername = rootView.etUsername
        builder.setView(rootView)

        val arguments = this.arguments

        builder.setPositiveButton("Follow") {
                dialog, witch -> // empty
        }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etNewUsername.text.isNotEmpty()) {
                val arguments = this.arguments

                handleFollowingCreate()

                dialog.dismiss()
            } else {
                etNewUsername.error = "This field can not be empty"
            }
        }
    }

    private fun handleFollowingCreate() {

        followingHandler.followingCreated(
            Following(
                FirebaseAuth.getInstance().currentUser!!.uid,
                etNewUsername.text.toString(),
                FirebaseAuth.getInstance().currentUser!!.displayName!!
            )
        )
    }
}