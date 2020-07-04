package org.pkbu.ElectronicInvestigationsCRM

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
import org.pkbu.ElectronicInvestigationsCRM.adapter.UserEvidenceItemAdapter
import org.pkbu.ElectronicInvestigationsCRM.model.EvidenceItem
import org.pkbu.ElectronicInvestigationsCRM.viewmodel.MainViewModel

class UserEvidenceItemsList : UserEvidenceItemAdapter.OnItemClickListener, Fragment() {

    private lateinit var mViewModel: MainViewModel
    private var listener: UserEvidenceItemsListener? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var userEvidenceItemAdapter: UserEvidenceItemAdapter

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
        // Initially sets up a bunk query in the case that a user isn't logged in yet:
        val query: Query = dbRef.whereEqualTo("evidenceCreator", "0001")

        // Setup recycler view //////////////////
        val recyclerView: RecyclerView = view.findViewById(R.id.evidence_item_recycler_view)
        val options = FirestoreRecyclerOptions.Builder<EvidenceItem>().setQuery(query,
            EvidenceItem::class.java).build()

        userEvidenceItemAdapter = UserEvidenceItemAdapter(options)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = userEvidenceItemAdapter

        userEvidenceItemAdapter.setOnItemClickListener(this)

        return view
    }

    fun refreshQuery() {
        val dbRef = db.collection(EVIDENCE_ITEMS)
        val query: Query = dbRef.whereEqualTo("evidenceCreatorUid",
            mViewModel.mAuth.currentUser!!.uid)

        val options = FirestoreRecyclerOptions.Builder<EvidenceItem>().setQuery(query,
            EvidenceItem::class.java).build()

        userEvidenceItemAdapter.updateOptions(options)
    }

    override fun onStart() {
        super.onStart()
        // Recycler view starts listening for data //////////
        userEvidenceItemAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        // Recycler view stops listening for data //////////
        userEvidenceItemAdapter.stopListening()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserEvidenceItemsListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement UserEvidenceItemsList Interfaces")
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
        listener!!.onUserEvidenceItemsListener(evidenceItem!!.evidenceId!!)
    }

    interface UserEvidenceItemsListener {
        fun onUserEvidenceItemsListener(evidenceId: String)
    }

    companion object {
        private const val EVIDENCE_ITEMS = "EvidenceItems"

        @JvmStatic
        fun newInstance(): UserEvidenceItemsList {
            return UserEvidenceItemsList()

        }
    }
}
