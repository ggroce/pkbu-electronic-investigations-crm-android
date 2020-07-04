package org.pkbu.ElectronicInvestigationsCRM

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.pkbu.ElectronicInvestigationsCRM.model.CaseFile
import org.pkbu.ElectronicInvestigationsCRM.model.User
import org.pkbu.ElectronicInvestigationsCRM.utils.DateUtils
import org.pkbu.ElectronicInvestigationsCRM.viewmodel.MainViewModel
import java.util.*

/*
Purpose of fragment dialog is to quickly create a new case file
with the minimum information needed.  Provides a smooth UI transition from
the case files/evidence items tabbed view, and into an editing mode.
If addition button is mistakenly selected, prevents the jarring effect
of going all the way into an editing mode, only to have to reverse back
into the original tabbed viewer.
 */

class CaseFileCreateDialog : AppCompatDialogFragment() {
    private lateinit var mViewModel: MainViewModel
    private var listener: CaseFileCreateDialogListener? = null
    private lateinit var mUser: User

    private var textCaseDate: EditText? = null
    private var mDatePicker: DatePickerDialog.OnDateSetListener? = null
    private var mCalendar: Calendar = Calendar.getInstance()
    private var textCaseAddress: EditText? = null
    private var textCaseCity: EditText? = null
    private var buCancel: Button? = null
    private var buContinue: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_case_file_create, container)
        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)

        // Bind widgets ////////////////
        textCaseDate = view.findViewById(R.id.edit_init_case_date)
        textCaseDate!!.showSoftInputOnFocus = false //prevents keyboard from appearing
        textCaseAddress = view.findViewById(R.id.edit_init_case_address)
        textCaseCity = view.findViewById(R.id.edit_init_case_city)
        buCancel = view.findViewById(R.id.bu_case_init_cancel)
        buContinue = view.findViewById(R.id.bu_case_init_continue)

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

            textCaseDate!!.setText(DateUtils.dateFormat.format(mCalendar.getTime()))
        }

        // Get user info
        mViewModel.currentUserLiveData.observe(this, Observer<User?> {user ->
            mUser = User(user!!)
        })

        // Create and handle button click handlers ////////////////
        buCancel!!.setOnClickListener(View.OnClickListener {
            dismiss()
        })
        buContinue!!.setOnClickListener(View.OnClickListener {
            onButtonContinue()
        })

        return view
    }

    // Retrieve data from fields and send to MainActivity
    private fun onButtonContinue() {
        if(textCaseDate!!.text.isNullOrBlank() || textCaseAddress!!.text.isNullOrBlank() ||
            textCaseCity!!.text.isNullOrBlank()) {
            Toast.makeText(context, "All fields require input", Toast.LENGTH_SHORT).show()
        } else {
            val date: Date? = DateUtils.dateFormat.parse(textCaseDate!!.text.toString())
            val caseFile = CaseFile(mUser.userUid.toString(), date!!,
                textCaseAddress!!.text.toString(), textCaseCity!!.text.toString())

            mUser.userAgency?.let {caseFile.originatingAgency = mUser.userAgency}
            mUser.userName?.let {caseFile.caseAgent = mUser.userName}
            mUser.userImageUrl?.let {caseFile.caseImageUrl = mUser .userImageUrl}
            mUser.userImageUrl?.let {caseFile.caseCreatorImageUrl = mUser .userImageUrl}

            mViewModel.addCaseFile(caseFile)
            mViewModel.caseIdLiveData.observe(this, Observer<String?> {caseId ->
                Toast.makeText(context, "Case file created", Toast.LENGTH_SHORT).show()

                listener!!.onCaseFileCreateDialogListener(caseId)
                dismiss()
            })
        }
    }

    // Create listener for transfer of data from dialog fragment to calling activity ////////////
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CaseFileCreateDialogListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement CaseFileFragInput Interfaces")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // for listener/transfer of data from dialog fragment to calling activity
    interface CaseFileCreateDialogListener {
        fun onCaseFileCreateDialogListener(caseId: String?)
    }

    companion object {

        @JvmStatic
        fun newInstance() : CaseFileCreateDialog {
            return CaseFileCreateDialog()
        }
    }
}

