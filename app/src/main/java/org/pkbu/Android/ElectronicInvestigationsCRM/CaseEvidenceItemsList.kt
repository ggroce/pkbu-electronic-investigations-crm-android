package org.pkbu.Android.ElectronicInvestigationsCRM

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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.pkbu.Android.ElectronicInvestigationsCRM.adapter.CaseEvidenceItemAdapter
import org.pkbu.Android.ElectronicInvestigationsCRM.model.EvidenceItem
import org.pkbu.Android.ElectronicInvestigationsCRM.viewmodel.MainViewModel

class CaseEvidenceItemsList : CaseEvidenceItemAdapter.OnItemClickListener, Fragment() {

    private lateinit var mViewModel: MainViewModel
    private var listener: CaseEvidenceItemsListener? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var caseEvidenceItemAdapter: CaseEvidenceItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel =
            ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        db = mViewModel.mDb
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_evidence_item_list, container,
            false)

        // Setup firestore query for data listed in recycler view: ////////////////
        val dbRef = db.collection(EVIDENCE_ITEMS)
        val query: Query = dbRef.whereEqualTo("caseId",
            mViewModel.getCaseIdForEvidence())

        // Setup recycler view //////////////////
        val recyclerView: RecyclerView = view.findViewById(R.id.evidence_item_recycler_view)
        val options = FirestoreRecyclerOptions.Builder<EvidenceItem>().setQuery(query,
            EvidenceItem::class.java).build()

        caseEvidenceItemAdapter = CaseEvidenceItemAdapter(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = caseEvidenceItemAdapter

        caseEvidenceItemAdapter.setOnItemClickListener(this)

        return view
    }

    fun refreshQuery() {
        val dbRef = db.collection(EVIDENCE_ITEMS)
        val query: Query = dbRef.whereEqualTo("caseId",
            mViewModel.getCaseIdForEvidence())

        val options = FirestoreRecyclerOptions.Builder<EvidenceItem>().setQuery(query,
            EvidenceItem::class.java).build()

        caseEvidenceItemAdapter.updateOptions(options)
    }

    override fun onStart() {
        super.onStart()
        // Recycler view starts listening for data //////////
        caseEvidenceItemAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        // Recycler view stops listening for data //////////
        caseEvidenceItemAdapter.stopListening()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CaseEvidenceItemsListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement CaseEvidenceItemsList Interfaces")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onItemClick(documentSnapshot: DocumentSnapshot?, position: Int) {
        val evidenceItem = documentSnapshot!!.toObject(EvidenceItem::class.java)
        val id = documentSnapshot.id
        val path = documentSnapshot.reference.path
        listener!!.onCaseEvidenceItemsListener(evidenceItem!!.evidenceId!!)
    }

    fun onClickAdd(fm: FragmentManager) {
        val dialog: EvidenceItemCreateDialog = EvidenceItemCreateDialog.newInstance()
        dialog.show(fm, "EvidenceItemInputFrag")
    }

    interface CaseEvidenceItemsListener {
        fun onCaseEvidenceItemsListener(evidenceId: String)
    }

    companion object {
        private const val EVIDENCE_ITEMS = "EvidenceItems"

        @JvmStatic
        fun newInstance(): CaseEvidenceItemsList {
            return CaseEvidenceItemsList()

        }
    }
}
