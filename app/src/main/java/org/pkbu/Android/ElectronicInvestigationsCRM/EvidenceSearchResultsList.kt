package org.pkbu.Android.ElectronicInvestigationsCRM

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.pkbu.Android.ElectronicInvestigationsCRM.adapter.SearchEvidenceItemAdapter
import org.pkbu.Android.ElectronicInvestigationsCRM.model.EvidenceItem
import org.pkbu.Android.ElectronicInvestigationsCRM.viewmodel.MainViewModel

class EvidenceSearchResultsList : SearchEvidenceItemAdapter.OnItemClickListener, Fragment() {

    private lateinit var mViewModel: MainViewModel
    private var listener: EvidenceSearchResultsListener? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var searchEvidenceItemAdapter: SearchEvidenceItemAdapter

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

        val searchField = mViewModel.getEvidenceSearchField()
        val searchString = mViewModel.getEvidenceSearchString()
        val query: Query = dbRef.whereGreaterThanOrEqualTo(searchField, searchString)
            .orderBy(searchField).endAt("$searchString~")

        // Setup recycler view //////////////////
        val recyclerView: RecyclerView = view.findViewById(R.id.evidence_item_recycler_view)
        val options = FirestoreRecyclerOptions.Builder<EvidenceItem>().setQuery(query,
            EvidenceItem::class.java).build()

        searchEvidenceItemAdapter = SearchEvidenceItemAdapter(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = searchEvidenceItemAdapter

        searchEvidenceItemAdapter.setOnItemClickListener(this)

        return view
    }

    fun refreshQuery() {
        val dbRef = db.collection(EVIDENCE_ITEMS)
        val searchField = mViewModel.getEvidenceSearchField()
        val searchString = mViewModel.getEvidenceSearchString()
        val query: Query = dbRef.whereGreaterThanOrEqualTo(searchField, searchString)
            .orderBy(searchField).endAt("$searchString~")
        val options = FirestoreRecyclerOptions.Builder<EvidenceItem>().setQuery(query,
            EvidenceItem::class.java).build()

        searchEvidenceItemAdapter.updateOptions(options)
    }

    override fun onStart() {
        super.onStart()
        // Recycler view starts listening for data //////////
        searchEvidenceItemAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        // Recycler view stops listening for data //////////
        searchEvidenceItemAdapter.stopListening()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EvidenceSearchResultsListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement EvidenceSearchResults Interfaces")
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
        listener!!.onEvidenceSearchResultsListener(evidenceItem!!.evidenceId!!)
    }

    interface EvidenceSearchResultsListener {
        fun onEvidenceSearchResultsListener(evidenceId: String)
    }

    companion object {
        private const val EVIDENCE_ITEMS = "EvidenceItems"

        @JvmStatic
        fun newInstance(): EvidenceSearchResultsList {
            return EvidenceSearchResultsList()

        }
    }
}
