package org.pkbu.ElectronicInvestigationsCRM

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProviders
import org.pkbu.ElectronicInvestigationsCRM.viewmodel.MainViewModel
import java.util.*


class EvidenceSearchDialog : AppCompatDialogFragment() {

    private var listener: EvidenceSearchDialogListener? = null

    private var textEvidenceSearchTerm: EditText? = null
    private var spinnerSearchField: Spinner? = null
    private var spinnerSearchFieldAdapter: ArrayAdapter<CharSequence>? = null

    private var buCancel: Button? = null
    private var buSearch: Button? = null
    private lateinit var mViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_evidence_search, container)

        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)

        // Bind widgets ////////////////
        textEvidenceSearchTerm = view.findViewById(R.id.edit_evidence_search_term)

        buCancel = view.findViewById(R.id.bu_cancel_search)
        buSearch = view.findViewById(R.id.bu_search)

        spinnerSearchField = view.findViewById(R.id.spinner_evidence_type)
        spinnerSearchFieldAdapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.evidence_search_field_descriptions, android.R.layout.simple_spinner_item)
        spinnerSearchFieldAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSearchField!!.adapter = spinnerSearchFieldAdapter

        // Create and handle button click handlers ////////////////
        buCancel!!.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        buSearch!!.setOnClickListener(View.OnClickListener {
            onButtonSearch()
        })

        return view
    }

    private fun onButtonSearch() {
        if (textEvidenceSearchTerm!!.text.isNullOrBlank()) {
            Toast.makeText(context, "Enter a search term, and select the appropriate field",
                Toast.LENGTH_SHORT).show()
        } else {
            var evidenceSearchString = textEvidenceSearchTerm!!.text.toString().toLowerCase(Locale.US)
            val evidenceSearchField = resources
                .getStringArray(R.array.evidence_search_fields_by_model_name)[spinnerSearchField!!
                .selectedItemPosition]

            Log.d("evidencesearchdialog", "field: $evidenceSearchField, string: $evidenceSearchString")

            mViewModel.setSearchStrings(evidenceSearchString, evidenceSearchField)
            listener!!.onEvidenceSearchDialogListener()
            textEvidenceSearchTerm!!.setText("")
            dismiss()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EvidenceSearchDialogListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement EvidenceSearchDialogListener Interfaces")
        }
    }

    interface EvidenceSearchDialogListener {
        fun onEvidenceSearchDialogListener()
    }

    companion object {
        private const val SELECT_PICTURE = "SelectPicture"
        private const val TAG = "UserInfoFrag"
        @JvmStatic

        fun newInstance() : EvidenceSearchDialog {
            return EvidenceSearchDialog()
        }
    }
}
