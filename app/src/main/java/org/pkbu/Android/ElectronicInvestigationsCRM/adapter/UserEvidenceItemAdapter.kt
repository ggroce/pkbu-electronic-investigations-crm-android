package org.pkbu.Android.ElectronicInvestigationsCRM.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import org.pkbu.Android.ElectronicInvestigationsCRM.R
import org.pkbu.Android.ElectronicInvestigationsCRM.model.EvidenceItem
import org.pkbu.Android.ElectronicInvestigationsCRM.utils.DateUtils

class UserEvidenceItemAdapter(options: FirestoreRecyclerOptions<EvidenceItem>) :
    FirestoreRecyclerAdapter<EvidenceItem, UserEvidenceItemAdapter.EvidenceItemHolder>(options) {

    private var listener: OnItemClickListener? = null
    private var context: Context? = null

    inner class EvidenceItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textViewEvidenceMake: TextView
        var textViewEvidenceModel: TextView
        var textViewEvidenceEntryDate: TextView
        var textViewEvidenceDescriptors: TextView
        var textViewEvidenceProcessingDetails: TextView
        var textViewEvidenceCapacity: TextView
        var textViewEvidenceRelevancy: TextView
        var textViewEvidenceStatus: TextView
        var textViewEvidenceEntryComplete: TextView
        var buEditItem: Button

        init {
            textViewEvidenceMake = itemView.findViewById(R.id.evidence_make)
            textViewEvidenceModel = itemView.findViewById(R.id.evidence_model)
            textViewEvidenceEntryDate = itemView.findViewById(R.id.evidence_entry_date)
            textViewEvidenceDescriptors = itemView.findViewById(R.id.evidence_descriptors)
            textViewEvidenceProcessingDetails = itemView.findViewById(R.id.evidence_processing_details)
            textViewEvidenceCapacity = itemView.findViewById(R.id.evidence_capacity)
            textViewEvidenceRelevancy = itemView.findViewById(R.id.evidence_relevancy)
            textViewEvidenceStatus = itemView.findViewById(R.id.evidence_status)
            textViewEvidenceEntryComplete = itemView.findViewById(R.id.evidence_entry_complete)
            buEditItem = itemView.findViewById(R.id.bu_edit_item)

            buEditItem!!.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener!!.onItemClick(snapshots.getSnapshot(position), position)
                }
            })
        }
    }

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvidenceItemHolder {

        var view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_my_evidence_item,
        parent, false)
        context = parent.context
        return EvidenceItemHolder(view)
    }

    override fun onBindViewHolder(holder: EvidenceItemHolder, position: Int, model: EvidenceItem) {
        holder.textViewEvidenceMake.text = model.evidenceMake
        holder.textViewEvidenceModel.text = model.evidenceModel
        holder.textViewEvidenceEntryDate.text = DateUtils.dateFormat
            .format(model.evidenceEntryDate!!).toString()
        holder.textViewEvidenceDescriptors.text = (model.evidenceDescriptor)
        holder.textViewEvidenceProcessingDetails.text = (model.evidenceProcessingNotes)
        holder.textViewEvidenceCapacity.text = (model.evidenceStorageSize.toString())
        holder.textViewEvidenceRelevancy.text = (model.evidenceRelevancy)
        holder.textViewEvidenceStatus.text = (model.evidenceStatus)

        if (model.evidenceEntryComplete == "No") {
            holder.textViewEvidenceEntryComplete.setTextColor(
                ContextCompat.getColor(context!!,
                R.color.colorSecondary))
        } else {
            holder.textViewEvidenceEntryComplete.setTextColor(
                ContextCompat.getColor(context!!,
                R.color.colorWhite))
        }
        holder.textViewEvidenceEntryComplete.text = (model.evidenceEntryComplete)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}