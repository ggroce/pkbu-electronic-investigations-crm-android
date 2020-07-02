package org.pkbu.pkbu_androidcasemaintenance

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProviders
import org.pkbu.pkbu_androidcasemaintenance.model.CaseFile
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import org.pkbu.pkbu_androidcasemaintenance.model.User
import org.pkbu.pkbu_androidcasemaintenance.utils.DateUtils
import org.pkbu.pkbu_androidcasemaintenance.viewmodel.MainViewModel
import java.io.ByteArrayOutputStream
import java.util.*

class CaseFileEdit : Fragment() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mCaseFile: CaseFile
    private var listener: CaseFileEditListener? = null
    private lateinit var mUser: User

    private var mDatePicker: DatePickerDialog.OnDateSetListener? = null
    private var mCalendar: Calendar = Calendar.getInstance()
    private var textCaseDate: EditText? = null
    private var textCaseNumber: EditText? = null
    private var textCaseTitle: EditText? = null
    private var textOriginatingAgency: EditText? = null
    private var textCaseAgent: EditText? = null
    private var textCaseAddress: EditText? = null
    private var textCaseCity: EditText? = null
    private var textCaseStatus: EditText? = null
    private var imageViewCaseBannerImage: ImageView? = null
    private var buCancel: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_case_file_edit, container, false)
        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)

        // Bind widgets ////////////////
        textCaseDate = view.findViewById(R.id.edit_evidence_found_by)
        textCaseDate!!.showSoftInputOnFocus = false //prevents keyboard from appearing
        textCaseNumber = view.findViewById(R.id.edit_evidence_descriptor)
        textCaseTitle = view.findViewById(R.id.edit_evidence_make)
        textOriginatingAgency = view.findViewById(R.id.edit_evidence_model)
        textCaseAgent = view.findViewById(R.id.edit_evidence_serial_number)
        textCaseAddress = view.findViewById(R.id.edit_evidence_storage_size)
        textCaseCity = view.findViewById(R.id.edit_evidence_processing_notes)
        textCaseStatus = view.findViewById(R.id.edit_evidence_account_info)
        imageViewCaseBannerImage = view.findViewById(R.id.case_image_of_interest)
        buCancel = view.findViewById(R.id.bu_case_cancel)

        // Create and handle datepicker for case date ////////////////
        textCaseDate!!.setOnClickListener(View.OnClickListener {
            // Set todays date into date picker:
            var year: Int = mCalendar.get(Calendar.YEAR)
            var month: Int = mCalendar.get(Calendar.MONTH)
            var day: Int = mCalendar.get(Calendar.DAY_OF_MONTH)

            var dialog: DatePickerDialog = DatePickerDialog(requireContext(),
                android.R.style.Theme_Holo_Light_Dialog, mDatePicker, year, month, day)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        })

        // Get selected information from date picker, place into date edit text field:
        mDatePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, month)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            textCaseDate!!.setText(DateUtils.dateFormat.format(mCalendar.time))
        }

        buCancel!!.setOnClickListener(View.OnClickListener {
            listener!!.onCaseFileEditListener(CLOSE_EDITING)
        })

        imageViewCaseBannerImage!!.setOnClickListener(View.OnClickListener {
            listener!!.onCaseFileEditListener(SELECT_PICTURE)
        })

        mViewModel.getCaseFileByCaseId(mViewModel.getCaseIdForEdit())
        // Find case file data and populate if available ////////////////
        mViewModel.caseFileLiveData.observe(viewLifecycleOwner, Observer<CaseFile?> {caseFile ->
            mCaseFile = CaseFile(caseFile!!)
            initCaseData()
        })

        return view
    }

    private fun initCaseData() {
        textCaseDate!!.setText(DateUtils.dateFormat.format(mCaseFile.caseDate!!))
        textCaseNumber!!.setText(mCaseFile.caseNumber)
        textCaseTitle!!.setText(mCaseFile.caseTitle)
        textOriginatingAgency!!.setText(mCaseFile.originatingAgency)
        textCaseAgent!!.setText(mCaseFile.caseAgent)
        textCaseAddress!!.setText(mCaseFile.caseAddress)
        textCaseCity!!.setText(mCaseFile.caseCity)
        textCaseStatus!!.setText(mCaseFile.caseStatus)

        mCaseFile.caseImageUrl?.let { Picasso.get().load(mCaseFile.caseImageUrl).fit().centerCrop()
            .placeholder(R.drawable.anim_loading_images).into(imageViewCaseBannerImage) }


        mViewModel.currentUserLiveData.observe(viewLifecycleOwner, Observer<User?> { user ->
            mUser = User(user!!)
            mCaseFile.caseCreatorImageUrl = mUser.userImageUrl
            mCaseFile.caseAgent = mUser.userName
            mCaseFile.originatingAgency = mUser.userAgency
        })
    }

    fun saveCaseData() {
        if (textCaseDate!!.text.isNullOrBlank() || textCaseAddress!!.text.isNullOrBlank() ||
            textCaseCity!!.text.isNullOrBlank()
        ) {
            Toast.makeText(
                context, "Case date, Address, and City fields require input",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            mCaseFile.caseDate = DateUtils.dateFormat.parse(textCaseDate!!.text.toString())
            mCaseFile.caseNumber = textCaseNumber!!.text.toString()
            mCaseFile.caseTitle = textCaseTitle!!.text.toString()
            mCaseFile.originatingAgency = textOriginatingAgency!!.text.toString()
            mCaseFile.caseAgent = textCaseAgent!!.text.toString()
            mCaseFile.caseAddress = textCaseAddress!!.text.toString()
            mCaseFile.caseCity = textCaseCity!!.text.toString()
            mCaseFile.caseStatus = textCaseStatus!!.text.toString()

            // send new info to database:
            mViewModel.updateCaseFile(mCaseFile)

            if (mViewModel.mAuth.currentUser!!.uid == mCaseFile.caseCreatorUid) {
                Toast.makeText(context, "Case file saving", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "A user may only edit their own case files",
                    Toast.LENGTH_SHORT).show()
            }

            listener!!.onCaseFileEditListener(CLOSE_EDITING)
        }
    }

    fun deleteCaseFileByCaseId() {
        val dialogClickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        if(mUser.userUid == mCaseFile.caseCreatorUid) {
                            mViewModel.deleteCaseFileByCaseId(mCaseFile.caseId!!)
                            Toast.makeText(context, "Case deleted. ",
                                Toast.LENGTH_SHORT).show()
                            listener!!.onCaseFileEditListener(CLOSE_EDITING)
                        } else {
                            Toast.makeText(context, "A user may only delete their " +
                                    "own case files",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage("You are about to delete this case, are you sure?")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CaseFileEditListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement CaseFileEditFrag Interfaces")
        }
    }

    fun setImageForCaseFile(pickedImage: Uri) {
        imageViewCaseBannerImage!!.setImageURI(pickedImage)
        imageViewCaseBannerImage!!.isDrawingCacheEnabled = true
        imageViewCaseBannerImage!!.buildDrawingCache()
        val mDrawable: Drawable = imageViewCaseBannerImage!!.drawable as BitmapDrawable
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
        mViewModel.setImageForCaseFile(mCaseFile.caseId!!, imageDataByteArray)

        Toast.makeText(context, "Please wait while image is uploaded...",
            Toast.LENGTH_SHORT).show()

        // Populate image url if user uploads an image
        mViewModel.caseFileImageUrlLiveData.observe(viewLifecycleOwner, Observer<String?> {imageUrl ->

            if (imageUrl == "error") {
                Toast.makeText(context, "Error uploading image. ",
                    Toast.LENGTH_SHORT).show()
            } else {
                mCaseFile.caseImageUrl = imageUrl
                Toast.makeText(context, "Image successfully uploaded. ",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface CaseFileEditListener {
        fun onCaseFileEditListener(purpose: String)
    }

    companion object {
        private const val CLOSE_EDITING = "CloseEditing"
        private const val SELECT_PICTURE = "SelectPicture"

        @JvmStatic
        fun newInstance() : CaseFileEdit {
            return CaseFileEdit()
        }
    }
}
