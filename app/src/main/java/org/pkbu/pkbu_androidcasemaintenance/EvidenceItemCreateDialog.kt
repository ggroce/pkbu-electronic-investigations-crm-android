package org.pkbu.pkbu_androidcasemaintenance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.pkbu.pkbu_androidcasemaintenance.model.CaseFile
import org.pkbu.pkbu_androidcasemaintenance.model.EvidenceItem
import org.pkbu.pkbu_androidcasemaintenance.model.User
import org.pkbu.pkbu_androidcasemaintenance.viewmodel.MainViewModel
import java.util.*

/*
Purpose of fragment dialog is to quickly create a new evidence item
with the minimum information needed.  Provides a smooth UI transition from
the case files/evidence items tabbed view, and into an editing mode.
If addition button is mistakenly selected, prevents the jarring effect
of going all the way into an editing mode, only to have to reverse back
into the original tabbed viewer.
 */

class EvidenceItemCreateDialog : AppCompatDialogFragment() {
    private lateinit var mViewModel: MainViewModel
    private var listener: EvidenceItemCreateDialogListener? = null
    private var mEvidenceItem: EvidenceItem = EvidenceItem()
    private lateinit var mUser: User
    private lateinit var mCaseFile: CaseFile

    private var textEvidenceMake: EditText? = null
    private var textEvidenceModel: EditText? = null
    private var textEvidenceSerialNumber: EditText? = null
    private var textEvidenceDescriptors: EditText? = null

    private var spinnerEvidenceRelevancy: Spinner? = null
    private var spinnerEvidenceRelevancyAdapter: ArrayAdapter<CharSequence>? = null

    private var buCancel: Button? = null
    private var buContinue: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_evidence_item_create, container)
        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)

        // Bind widgets ////////////////
        textEvidenceMake = view.findViewById(R.id.edit_init_evidence_make)
        textEvidenceModel = view.findViewById(R.id.edit_init_evidence_model)
        textEvidenceSerialNumber = view.findViewById(R.id.edit_init_evidence_serial)
        textEvidenceDescriptors = view.findViewById(R.id.edit_init_evidence_descriptors)
        spinnerEvidenceRelevancy = view.findViewById(R.id.spinner_init_evidence_relevancy)

        spinnerEvidenceRelevancyAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.evidence_relevancy, android.R.layout.simple_spinner_item)
        spinnerEvidenceRelevancyAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEvidenceRelevancy!!.adapter = spinnerEvidenceRelevancyAdapter

        buCancel = view.findViewById(R.id.bu_evidence_init_cancel)
        buContinue = view.findViewById(R.id.bu_evidence_init_continue)

        initEvidenceItem()

        // Create and handle button click handlers ////////////////
        buCancel!!.setOnClickListener(View.OnClickListener {
            dismiss()
        })
        buContinue!!.setOnClickListener(View.OnClickListener {
            onButtonContinue()
        })
        return view
    }

    private fun initEvidenceItem() {
        mEvidenceItem.evidenceEntryDate = Date()
        mEvidenceItem.caseId = mViewModel.getCaseIdForEvidence()

        // Get user info and enter into evidence item
        mViewModel.currentUserLiveData.observe(this, Observer<User?> {user ->
            mUser = User(user!!)
            mUser.userUid?.let {mEvidenceItem.evidenceCreatorUid = mUser.userUid}
            mUser.userName?.let {mEvidenceItem.evidenceFoundBy = mUser.userName}
            mUser.userName?.let {mEvidenceItem.evidenceLastEditedBy = mUser.userName}
        })
    }

    // Retrieve data from fields and send to MainActivity
    private fun onButtonContinue() {
        if(textEvidenceMake!!.text.toString().isNullOrBlank() || textEvidenceModel!!.text.isNullOrBlank() ||
            textEvidenceSerialNumber!!.text.isNullOrBlank() || textEvidenceDescriptors!!.text.isNullOrBlank() ||
            spinnerEvidenceRelevancy!!.selectedItem == null) {
            Toast.makeText(context, "All fields require input, (enter N/A if none)",
                Toast.LENGTH_SHORT).show()
        } else {
            mEvidenceItem.evidenceMake = textEvidenceMake!!.text.toString()
            mEvidenceItem.evidenceModel = textEvidenceModel!!.text.toString()
            mEvidenceItem.evidenceSerialNumber = textEvidenceSerialNumber!!.text.toString()
            mEvidenceItem.evidenceDescriptor = textEvidenceDescriptors!!.text.toString()
            mEvidenceItem.evidenceRelevancy = spinnerEvidenceRelevancy!!
                .selectedItem.toString()

            mViewModel.addEvidenceItem(mEvidenceItem)
            mViewModel.evidenceIdLiveData.observe(this, Observer<String?> {evidenceId ->
                Toast.makeText(context, "Evidence Item created", Toast.LENGTH_SHORT).show()

                mViewModel.updateCaseFileEvidenceItemCount(mEvidenceItem.caseId!!, true)
                listener!!.onEvidenceItemCreateDialogListener(evidenceId)
                dismiss()
            })
        }
    }

    // Create listener for transfer of data from dialog fragment to calling activity ////////////
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EvidenceItemCreateDialogListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement EvidenceItemCreate Interfaces")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // for listener/transfer of data from dialog fragment to calling activity
    interface EvidenceItemCreateDialogListener {
        fun onEvidenceItemCreateDialogListener(evidenceId: String?)
    }

    companion object {

        @JvmStatic
        fun newInstance() : EvidenceItemCreateDialog {
            return EvidenceItemCreateDialog()
        }
    }
}

