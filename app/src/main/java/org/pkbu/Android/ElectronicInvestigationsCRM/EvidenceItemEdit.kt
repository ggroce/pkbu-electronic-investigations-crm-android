package org.pkbu.Android.ElectronicInvestigationsCRM


import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import org.pkbu.Android.ElectronicInvestigationsCRM.model.CaseFile
import org.pkbu.Android.ElectronicInvestigationsCRM.model.EvidenceItem
import org.pkbu.Android.ElectronicInvestigationsCRM.model.User
import org.pkbu.Android.ElectronicInvestigationsCRM.viewmodel.MainViewModel
import java.io.ByteArrayOutputStream

class EvidenceItemEdit : Fragment() {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mEvidenceItem: EvidenceItem
    private lateinit var mCaseFile: CaseFile
    private var listener: EvidenceItemEditListener? = null

    private var textEvidenceLastEditedBy: TextView? = null
    private var textEvidenceFoundBy: EditText? = null
    private var textEvidenceDescriptor: EditText? = null
    private var textEvidenceMake: EditText? = null
    private var textEvidenceModel: EditText? = null
    private var textEvidenceSerialNumber: EditText? = null
    private var textEvidenceStorageSize: EditText? = null
    private var textEvidenceProcessingNotes: EditText? = null
    private var textEvidenceAccountInfo: EditText? = null
    private var textEvidenceSuspectedOwner: EditText? = null
    private var textEvidenceFoundLocation: EditText? = null

    private var spinnerEvidenceEntryComplete: Spinner? = null
    private var spinnerEvidenceEntryType: Spinner? = null
    private var spinnerEvidenceLocked: Spinner? = null
    private var spinnerEvidenceRelevancy: Spinner? = null
    private var spinnerEvidenceStatus: Spinner? = null

    private var spinnerEvidenceEntryCompleteAdapter: ArrayAdapter<CharSequence>? = null
    private var spinnerEvidenceEntryTypeAdapter: ArrayAdapter<CharSequence>? = null
    private var spinnerEvidenceLockedAdapter: ArrayAdapter<CharSequence>? = null
    private var spinnerEvidenceRelevancyAdapter: ArrayAdapter<CharSequence>? = null
    private var spinnerEvidenceStatusAdapter: ArrayAdapter<CharSequence>? = null

    private var imageViewEvidenceFront: ImageView? = null
    private var imageViewEvidenceBack: ImageView? = null
    private var imageViewEvidenceDetail1: ImageView? = null
    private var imageViewEvidenceDetail2: ImageView? = null

    private var buCancelTop: Button? = null
    private var buCancelBottom: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_evidence_item_edit, container, false)
        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)

        val nestedScrollView: NestedScrollView = view.findViewById(R.id.evidence_item_edit)
        val runnable = Runnable { nestedScrollView.fullScroll(ScrollView.FOCUS_UP) }
        nestedScrollView.post(runnable)

        // Bind widgets ////////////////
        textEvidenceLastEditedBy = view.findViewById(R.id.textView_last_edited_by)
        textEvidenceFoundBy = view.findViewById(R.id.edit_evidence_found_by)
        textEvidenceDescriptor = view.findViewById(R.id.edit_evidence_descriptor)
        textEvidenceMake = view.findViewById(R.id.edit_evidence_make)
        textEvidenceModel = view.findViewById(R.id.edit_evidence_model)
        textEvidenceSerialNumber = view.findViewById(R.id.edit_evidence_serial_number)
        textEvidenceStorageSize = view.findViewById(R.id.edit_evidence_storage_size)
        textEvidenceProcessingNotes = view.findViewById(R.id.edit_evidence_processing_notes)
        textEvidenceAccountInfo = view.findViewById(R.id.edit_evidence_account_info)
        textEvidenceSuspectedOwner = view.findViewById(R.id.edit_evidence_suspected_owner)
        textEvidenceFoundLocation = view.findViewById(R.id.edit_evidence_found_location)
        imageViewEvidenceFront = view.findViewById(R.id.image_evidence_front)
        imageViewEvidenceBack = view.findViewById(R.id.image_evidence_back)
        imageViewEvidenceDetail1 = view.findViewById(R.id.image_evidence_detail_1)
        imageViewEvidenceDetail2 = view.findViewById(R.id.image_evidence_detail_2)

        // Setup spinners ////////////////
        spinnerEvidenceEntryComplete = view.findViewById(R.id.spinner_entry_complete)
        spinnerEvidenceEntryCompleteAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.evidence_complete, android.R.layout.simple_spinner_item)
        spinnerEvidenceEntryCompleteAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEvidenceEntryComplete!!.adapter = spinnerEvidenceEntryCompleteAdapter

        spinnerEvidenceEntryType = view.findViewById(R.id.spinner_evidence_type)
        spinnerEvidenceEntryTypeAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.evidence_type, android.R.layout.simple_spinner_item)
        spinnerEvidenceEntryTypeAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEvidenceEntryType!!.adapter = spinnerEvidenceEntryTypeAdapter

        spinnerEvidenceLocked = view.findViewById(R.id.spinner_evidence_locked)
        spinnerEvidenceLockedAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.evidence_locked, android.R.layout.simple_spinner_item)
        spinnerEvidenceLockedAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEvidenceLocked!!.adapter = spinnerEvidenceLockedAdapter

        spinnerEvidenceRelevancy = view.findViewById(R.id.spinner_evidence_relevancy)
        spinnerEvidenceRelevancyAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.evidence_relevancy, android.R.layout.simple_spinner_item)
        spinnerEvidenceRelevancyAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEvidenceRelevancy!!.adapter = spinnerEvidenceRelevancyAdapter

        spinnerEvidenceStatus = view.findViewById(R.id.spinner_evidence_status)
        spinnerEvidenceStatusAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.evidence_status, android.R.layout.simple_spinner_item)
        spinnerEvidenceStatusAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEvidenceStatus!!.adapter = spinnerEvidenceStatusAdapter

        // Setup on click listeners for all image views ////////////////
        imageViewEvidenceFront!!.setOnClickListener(View.OnClickListener {
            listener!!.onEvidenceItemEditListener(PICK_IMAGE_FOR_EVIDENCE_FRONT.toString())
        })

        imageViewEvidenceBack!!.setOnClickListener(View.OnClickListener {
            listener!!.onEvidenceItemEditListener(PICK_IMAGE_FOR_EVIDENCE_BACK.toString())
        })

        imageViewEvidenceDetail1!!.setOnClickListener(View.OnClickListener {
            listener!!.onEvidenceItemEditListener(PICK_IMAGE_FOR_EVIDENCE_DETAIL1.toString())
        })

        imageViewEvidenceDetail2!!.setOnClickListener(View.OnClickListener {
            listener!!.onEvidenceItemEditListener(PICK_IMAGE_FOR_EVIDENCE_DETAIL2.toString())
        })

        // Cancel button ////////////////
        buCancelBottom = view.findViewById(R.id.bu_evidence_cancel_bottom)
        buCancelTop = view.findViewById(R.id.bu_evidence_cancel_top)

        buCancelTop!!.setOnClickListener(View.OnClickListener {
            listener!!.onEvidenceItemEditListener(CLOSE_EDITING)
        })
        buCancelBottom!!.setOnClickListener(View.OnClickListener {
            listener!!.onEvidenceItemEditListener(CLOSE_EDITING)
        })

        // Pull data from Firestore and populate data into fields ////////////////
        mViewModel.getEvidenceItemByEvidenceId(mViewModel.getEvidenceId())

        mViewModel.evidenceItemLiveData.observe(viewLifecycleOwner, Observer<EvidenceItem?>
        { evidenceItem ->
            mEvidenceItem = EvidenceItem(evidenceItem!!)
            initEvidenceData()

            mViewModel.getCaseFileByCaseId(mEvidenceItem.caseId!!)
            // Find case file data and populate if available ////////////////
            mViewModel.caseFileLiveData.observe(viewLifecycleOwner, Observer<CaseFile?> { caseFile ->
                mCaseFile = CaseFile(caseFile!!)
            })

        })
        return view
    }

    private fun initEvidenceData() {
        textEvidenceLastEditedBy!!.text = mEvidenceItem.evidenceLastEditedBy
        textEvidenceFoundBy!!.setText(mEvidenceItem.evidenceFoundBy)
        textEvidenceDescriptor!!.setText(mEvidenceItem.evidenceDescriptor)
        textEvidenceMake!!.setText(mEvidenceItem.evidenceMake)
        textEvidenceModel!!.setText(mEvidenceItem.evidenceModel)
        textEvidenceSerialNumber!!.setText(mEvidenceItem.evidenceSerialNumber)
        textEvidenceStorageSize!!.setText(mEvidenceItem.evidenceStorageSize.toString())
        textEvidenceProcessingNotes!!.setText(mEvidenceItem.evidenceProcessingNotes)
        textEvidenceAccountInfo!!.setText(mEvidenceItem.evidenceAccountInfo)
        textEvidenceSuspectedOwner!!.setText(mEvidenceItem.evidenceSuspectedOwner)
        textEvidenceFoundLocation!!.setText(mEvidenceItem.evidenceFoundLocation)

        spinnerEvidenceEntryComplete!!.setSelection(spinnerEvidenceEntryCompleteAdapter!!
            .getPosition(mEvidenceItem.evidenceEntryComplete))
        spinnerEvidenceEntryType!!.setSelection(spinnerEvidenceEntryTypeAdapter!!
            .getPosition(mEvidenceItem.evidenceType))
        spinnerEvidenceLocked!!.setSelection(spinnerEvidenceLockedAdapter!!
            .getPosition(mEvidenceItem.evidenceLocked))
        spinnerEvidenceRelevancy!!.setSelection(spinnerEvidenceRelevancyAdapter!!
            .getPosition(mEvidenceItem.evidenceRelevancy))
        spinnerEvidenceStatus!!.setSelection(spinnerEvidenceStatusAdapter!!
            .getPosition(mEvidenceItem.evidenceStatus))

        mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_FRONT]?.let {
            Picasso.get().load(mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_FRONT])
                .fit().centerCrop().placeholder(R.drawable.anim_loading_images)
                .into(imageViewEvidenceFront)
        }
        mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_BACK]?.let {
            Picasso.get().load(mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_BACK])
                .fit().centerCrop().placeholder(R.drawable.anim_loading_images)
                .into(imageViewEvidenceBack)
        }
        mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_DETAIL1]?.let {
            Picasso.get().load(mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_DETAIL1])
                .fit().centerCrop().placeholder(R.drawable.anim_loading_images)
                .into(imageViewEvidenceDetail1)
        }
        mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_DETAIL2]?.let {
            Picasso.get().load(mEvidenceItem.evidencePictureUrls[EVIDENCE_HASHMAP_DETAIL2])
                .fit().centerCrop().placeholder(R.drawable.anim_loading_images)
                .into(imageViewEvidenceDetail2)
        }

        // Set last edited by, (only saves if user saves):
        mViewModel.currentUserLiveData.observe(viewLifecycleOwner, Observer<User?> { user ->
            user!!.userName?.let {mEvidenceItem.evidenceLastEditedBy = user.userName}
        })
    }

    fun saveEvidenceItemData() {
        if (mEvidenceItem.evidenceCreatorUid != null) {
            val dialogClickListener: DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            if (textEvidenceFoundBy!!.text.isNullOrBlank()
                                || textEvidenceDescriptor!!.text.isNullOrBlank()
                                || textEvidenceMake!!.text.isNullOrBlank()
                                || textEvidenceModel!!.text.isNullOrBlank()
                                || textEvidenceSerialNumber!!.text.isNullOrBlank()) {
                                Toast.makeText(context, "'Entered by', 'Descriptor', 'Make', 'Model', and " +
                                        "'Serial Number' fields require input",
                                    Toast.LENGTH_SHORT).show()
                            } else {

                                mEvidenceItem.evidenceFoundBy = textEvidenceFoundBy!!.text.toString()
                                mEvidenceItem.evidenceDescriptor = textEvidenceDescriptor!!.text.toString()
                                mEvidenceItem.evidenceMake = textEvidenceMake!!.text.toString()
                                mEvidenceItem.evidenceModel = textEvidenceModel!!.text.toString()
                                mEvidenceItem.evidenceSerialNumber = textEvidenceSerialNumber!!.text.toString()
                                mEvidenceItem.evidenceStorageSize = textEvidenceStorageSize!!.text.toString().toDouble()
                                mEvidenceItem.evidenceProcessingNotes = textEvidenceProcessingNotes!!.text.toString()
                                mEvidenceItem.evidenceAccountInfo = textEvidenceAccountInfo!!.text.toString()
                                mEvidenceItem.evidenceSuspectedOwner = textEvidenceSuspectedOwner!!.text.toString()
                                mEvidenceItem.evidenceFoundLocation = textEvidenceFoundLocation!!.text.toString()

                                mEvidenceItem.evidenceEntryComplete = spinnerEvidenceEntryComplete!!
                                    .selectedItem.toString()
                                mEvidenceItem.evidenceType = spinnerEvidenceEntryType!!
                                    .selectedItem.toString()
                                mEvidenceItem.evidenceLocked = spinnerEvidenceLocked!!
                                    .selectedItem.toString()
                                mEvidenceItem.evidenceRelevancy = spinnerEvidenceRelevancy!!
                                    .selectedItem.toString()
                                mEvidenceItem.evidenceStatus = spinnerEvidenceStatus!!
                                    .selectedItem.toString()

                                // send new info to database:
                                mViewModel.updateEvidenceItem(mEvidenceItem)
                                listener!!.onEvidenceItemEditListener(CLOSE_EDITING)
                            }
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            if (mViewModel.mAuth.currentUser!!.uid == mEvidenceItem.evidenceCreatorUid) {
                builder.setMessage("Are you sure you wish to save any changes?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
            } else {
                builder.setMessage("You are about to alter an evidence item created by another user.  " +
                        "Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
            }
        }
    }

    fun deleteEvidenceItemByEvidenceId() {
        if (mCaseFile.caseCreatorUid != null && mEvidenceItem.evidenceCreatorUid != null) {
            val dialogClickListener: DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            if(mViewModel.mAuth.currentUser!!.uid == mEvidenceItem.evidenceCreatorUid
                                || mViewModel.mAuth.currentUser!!.uid == mCaseFile.caseCreatorUid) {
                                mViewModel.deleteEvidenceItemByEvidenceId(mEvidenceItem.evidenceId!!)
                                mViewModel.updateCaseFileEvidenceItemCount(mEvidenceItem.caseId!!,
                                    false)
                                Toast.makeText(context, "Evidence Item deleted. ",
                                    Toast.LENGTH_SHORT).show()
                                listener!!.onEvidenceItemEditListener(CLOSE_EDITING)
                            } else {
                                Toast.makeText(context, "Only the evidence item creator or " +
                                        "creating case agent may delete their evidence items",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            builder.setMessage("You are about to delete this evidence item, are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EvidenceItemEditListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement EvidenceItemEditFrag Interfaces")
        }
    }

    fun setImageForEvidenceItem(pickedImage: Uri, picturePosition: Int) {
        var imageViewForPicture: ImageView? = null
        var evidenceImageHashMapKey: String? = ""

        when (picturePosition) {
            PICK_IMAGE_FOR_EVIDENCE_FRONT -> {
                imageViewForPicture = imageViewEvidenceFront
                evidenceImageHashMapKey = EVIDENCE_HASHMAP_FRONT
            }
            PICK_IMAGE_FOR_EVIDENCE_BACK -> {
                imageViewForPicture = imageViewEvidenceBack
                evidenceImageHashMapKey = EVIDENCE_HASHMAP_BACK
            }
            PICK_IMAGE_FOR_EVIDENCE_DETAIL1 -> {
                imageViewForPicture = imageViewEvidenceDetail1
                evidenceImageHashMapKey = EVIDENCE_HASHMAP_DETAIL1
            }
            PICK_IMAGE_FOR_EVIDENCE_DETAIL2 -> {
                imageViewForPicture = imageViewEvidenceDetail2
                evidenceImageHashMapKey = EVIDENCE_HASHMAP_DETAIL2
            }
        }

        imageViewForPicture!!.setImageURI(pickedImage)
        imageViewForPicture!!.isDrawingCacheEnabled = true
        imageViewForPicture!!.buildDrawingCache()
        val mDrawable: Drawable = imageViewForPicture!!.drawable as BitmapDrawable
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
        mViewModel.setImageForEvidenceItem(mEvidenceItem.evidenceId!!,
            imageDataByteArray, evidenceImageHashMapKey!!)

        Toast.makeText(context, "Please wait while image is uploaded...", Toast.LENGTH_SHORT).show()

        // Populate image url if user uploads an image
        mViewModel.evidenceItemImageUrlLiveData.observe(viewLifecycleOwner, Observer<String?>
        {imageUrl ->

            if (imageUrl == "error") {
                Toast.makeText(context, "Error uploading image. ", Toast.LENGTH_SHORT).show()
            } else {
                mEvidenceItem.evidencePictureUrls[evidenceImageHashMapKey] = imageUrl.toString()

                Toast.makeText(context, "Image successfully uploaded. ",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface EvidenceItemEditListener {
        fun onEvidenceItemEditListener(purpose: String)
    }

    companion object {
        private const val CLOSE_EDITING = "CloseEditing"
        private const val PICK_IMAGE_FOR_EVIDENCE_FRONT = 5701
        private const val PICK_IMAGE_FOR_EVIDENCE_BACK = 5711
        private const val PICK_IMAGE_FOR_EVIDENCE_DETAIL1 = 5721
        private const val PICK_IMAGE_FOR_EVIDENCE_DETAIL2 = 5731
        private const val EVIDENCE_HASHMAP_FRONT = "image_front"
        private const val EVIDENCE_HASHMAP_BACK = "image_back"
        private const val EVIDENCE_HASHMAP_DETAIL1 = "image_detail1"
        private const val EVIDENCE_HASHMAP_DETAIL2 = "image_detail2"

        @JvmStatic
        fun newInstance() : EvidenceItemEdit {
            return EvidenceItemEdit()
        }
    }
}
