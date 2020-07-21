package org.pkbu.Android.ElectronicInvestigationsCRM

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import org.pkbu.Android.ElectronicInvestigationsCRM.model.User
import org.pkbu.Android.ElectronicInvestigationsCRM.viewmodel.MainViewModel
import java.io.ByteArrayOutputStream


class UserInfoDialog : AppCompatDialogFragment() {

    private var listener: UserInfoDialogListener? = null
    private var textUserName: EditText? = null
    private var textUserAgency: EditText? = null
    private var textUserRole: EditText? = null
    private var textUserPhone: EditText? = null
    private var imageViewCreator: ImageView? = null
    private var buCancel: Button? = null
    private var buSave: Button? = null
    private var buLogOut: Button? = null
    private lateinit var mViewModel: MainViewModel
    private lateinit var mUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_info, container)

        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)

        // Bind widgets ////////////////
        textUserName = view.findViewById(R.id.edit_user_display_name)
        textUserAgency = view.findViewById(R.id.edit_user_agency)
        textUserRole = view.findViewById(R.id.edit_user_role)
        textUserPhone = view.findViewById(R.id.edit_user_phone)
        imageViewCreator = view.findViewById(R.id.image_creator_card)
        buCancel = view.findViewById(R.id.bu_user_info_cancel)
        buSave = view.findViewById(R.id.bu_user_info_save)
        buLogOut = view.findViewById(R.id.bu_user_log_out)

        // Create and handle button click handlers ////////////////
        buCancel!!.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        buSave!!.setOnClickListener(View.OnClickListener {
            onButtonSave()
        })

        buLogOut!!.setOnClickListener(View.OnClickListener {
            listener!!.onUserInfoDialogListener(LOG_USER_OUT)
            dismiss()
        })

        imageViewCreator!!.setOnClickListener(View.OnClickListener {
            listener!!.onUserInfoDialogListener(SELECT_PICTURE)
        })

        // Find user account info and populate if available ////////////////
        mViewModel.currentUserLiveData.observe(this, Observer<User?> {user ->
            mUser = User(user!!)
            initUserInfo()
        })

        return view
    }

    private fun initUserInfo() {
        textUserName?.setText(mUser.userName)
        textUserAgency?.setText(mUser.userAgency)
        textUserRole?.setText(mUser.userRole)
        textUserPhone?.setText(mUser.userPhone)
        Picasso.get().load(mUser.userImageUrl).into(imageViewCreator)
    }

    private fun onButtonSave() {
        mUser.userName = textUserName!!.text.toString()
        mUser.userAgency = textUserAgency!!.text.toString()
        mUser.userRole = textUserRole!!.text.toString()
        mUser.userPhone = textUserPhone!!.text.toString()

        if (mUser.userName.isNullOrBlank()) {
            Toast.makeText(context, "User name must not be left blank ",
                Toast.LENGTH_SHORT).show()
        } else {
            // send new info to database:
            mViewModel.updateAllUserInfo(mUser)
            Toast.makeText(context, "User information saved ", Toast.LENGTH_SHORT).show()

            // Refresh current user info in viewmodel:
            mViewModel.getCurrentUser(mViewModel.mAuth.currentUser!!.uid)
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserInfoDialogListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement UserInfoFrag Interfaces")
        }
    }

    fun setImageForUser(pickedImage: Uri) {
        imageViewCreator!!.setImageURI(pickedImage)
        imageViewCreator!!.isDrawingCacheEnabled = true
        imageViewCreator!!.buildDrawingCache()
        val mDrawable: Drawable = imageViewCreator!!.drawable as BitmapDrawable
        var mBitmap: Bitmap = mDrawable.toBitmap()
        val mBaos = ByteArrayOutputStream()

        // reduce file size a tad:
        if (mBitmap.width > 1000 || mBitmap.height > 1000) {
            val scale = 3
            mBitmap = Bitmap.createScaledBitmap(mBitmap, (mBitmap.width / scale).toInt(),
                (mBitmap.height / scale).toInt(), true)
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 65, mBaos)


        val imageDataByteArray = mBaos.toByteArray()
        mViewModel.setImageForUser(mUser.userUid!!, imageDataByteArray)

        Toast.makeText(context, "Uploading image...",
            Toast.LENGTH_SHORT).show()

        // Populate image url if user uploads an image
        mViewModel.userImageUrlLiveData.observe(viewLifecycleOwner, Observer<String?> {imageUrl ->

            if (imageUrl == "error") {
                Toast.makeText(context, "Error uploading image. ",
                    Toast.LENGTH_SHORT).show()
            } else {
                mUser.userImageUrl = imageUrl
                Toast.makeText(context, "Image successfully uploaded. ",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface UserInfoDialogListener {
        fun onUserInfoDialogListener(purpose: String)
    }

    companion object {
        private const val SELECT_PICTURE = "SelectPicture"
        private const val LOG_USER_OUT = "LogUserOut"
        private const val TAG = "UserInfoFrag"
        @JvmStatic

        fun newInstance() : UserInfoDialog {
            return UserInfoDialog()
        }
    }
}
