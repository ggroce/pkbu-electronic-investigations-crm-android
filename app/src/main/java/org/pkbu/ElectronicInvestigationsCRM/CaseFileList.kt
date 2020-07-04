package org.pkbu.ElectronicInvestigationsCRM

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.pkbu.ElectronicInvestigationsCRM.adapter.CaseFileAdapter
import org.pkbu.ElectronicInvestigationsCRM.model.CaseFile
import org.pkbu.ElectronicInvestigationsCRM.viewmodel.MainViewModel

class CaseFileList : CaseFileAdapter.OnItemClickListener, Fragment() {

    private lateinit var mViewModel: MainViewModel
    private var listener: CaseFileListListener? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var caseFileAdapter: CaseFileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        db = mViewModel.mDb
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_case_file_list, container,
            false)

        // Setup firestore query for data listed in recycler view: ////////////////
        val caseFileRef: CollectionReference = db.collection("CaseFiles")
        val caseFileQuery: Query = caseFileRef.orderBy("caseDate",
            Query.Direction.DESCENDING)

        // Setup recycler view //////////////////
        val recyclerView: RecyclerView = view.findViewById(R.id.case_file_recycler_view)
        val options = FirestoreRecyclerOptions.Builder<CaseFile>().setQuery(caseFileQuery,
            CaseFile::class.java).build()

        caseFileAdapter = CaseFileAdapter(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = caseFileAdapter

        caseFileAdapter.setOnItemClickListener(this)

        return view
    }

    override fun onStart() {
        super.onStart()
        // Recycler view starts listening for data //////////
        caseFileAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        // Recycler view stops listening for data //////////
        caseFileAdapter.stopListening()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CaseFileListListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement CaseFile Interfaces")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onItemClick(documentSnapshot: DocumentSnapshot?, position: Int, editCase: Boolean) {
        val caseFile = documentSnapshot!!.toObject(CaseFile::class.java)
        val id = documentSnapshot.id
        val path = documentSnapshot.reference.path
        listener!!.onCaseFileListListener(caseFile!!.caseId!!, editCase)
    }

    fun onClickAdd(fm: FragmentManager) {
        val dialog: CaseFileCreateDialog = CaseFileCreateDialog.newInstance()
        dialog.show(fm, "CaseFileInputFrag")
    }

    interface CaseFileListListener {
        fun onCaseFileListListener(caseId: String, editCase: Boolean)
    }

    companion object {

        @JvmStatic
        fun newInstance(): CaseFileList {
            return CaseFileList()

        }
    }
}
