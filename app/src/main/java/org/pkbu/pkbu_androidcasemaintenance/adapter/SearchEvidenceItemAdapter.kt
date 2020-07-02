package org.pkbu.pkbu_androidcasemaintenance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import org.pkbu.pkbu_androidcasemaintenance.R
import org.pkbu.pkbu_androidcasemaintenance.model.EvidenceItem
import org.pkbu.pkbu_androidcasemaintenance.utils.DateUtils

class SearchEvidenceItemAdapter(options: FirestoreRecyclerOptions<EvidenceItem>) :
    FirestoreRecyclerAdapter<EvidenceItem,
            SearchEvidenceItemAdapter.EvidenceItemHolder>(options) {

    private var listener: OnItemClickListener? = null
    private var context: Context? = null

    inner class EvidenceItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textViewEvidenceMake: TextView
        var textViewEvidenceModel: TextView
        var textViewEvidenceType: TextView
        var textViewEvidenceEntryDate: TextView
        var textViewEvidenceDescriptors: TextView
        var textViewEvidenceProcessingDetails: TextView
        var textViewEvidenceLastEditedBy: TextView
        var textViewEvidenceCapacity: TextView
        var textViewEvidenceRelevancy: TextView
        var textViewEvidenceLocked: TextView
        var textViewEvidenceEntryComplete: TextView
        var textViewEvidenceFoundAt: TextView
        var imageViewEvidenceFront: ImageView
        var imageViewEvidenceBack: ImageView
        var buEditItem: Button

        init {
            textViewEvidenceMake = itemView.findViewById(R.id.evidence_make)
            textViewEvidenceModel = itemView.findViewById(R.id.evidence_model)
            textViewEvidenceType = itemView.findViewById(R.id.evidence_type)
            textViewEvidenceEntryDate = itemView.findViewById(R.id.evidence_entry_date)
            textViewEvidenceDescriptors = itemView.findViewById(R.id.evidence_descriptors)
            textViewEvidenceProcessingDetails =
                itemView.findViewById(R.id.evidence_processing_details)
            textViewEvidenceLastEditedBy = itemView.findViewById(R.id.evidence_last_edited_by)
            textViewEvidenceCapacity = itemView.findViewById(R.id.evidence_capacity)
            textViewEvidenceRelevancy = itemView.findViewById(R.id.evidence_relevancy)
            textViewEvidenceLocked = itemView.findViewById(R.id.evidence_locked)
            textViewEvidenceEntryComplete = itemView.findViewById(R.id.evidence_entry_complete)
            textViewEvidenceFoundAt = itemView.findViewById(R.id.evidence_located_at)
            imageViewEvidenceFront = itemView.findViewById(R.id.image_evidence_front)
            imageViewEvidenceBack= itemView.findViewById(R.id.image_evidence_back)
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
            R.layout.fragment_case_evidence_item,
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
        holder.textViewEvidenceLastEditedBy.text = (model.evidenceLastEditedBy)
        holder.textViewEvidenceCapacity.text = (model.evidenceStorageSize.toString())
        if (model.evidenceRelevancy == "Target Evidence") {
            holder.textViewEvidenceRelevancy.setTextColor(ContextCompat.getColor(context!!,
                R.color.colorRed))
        } else if (model.evidenceRelevancy == "Interest") {
            holder.textViewEvidenceRelevancy.setTextColor(ContextCompat.getColor(context!!,
                R.color.colorSecondary))
        } else {
            holder.textViewEvidenceRelevancy.setTextColor(ContextCompat.getColor(context!!,
                R.color.colorWhite))
        }
        holder.textViewEvidenceRelevancy.text = (model.evidenceRelevancy)
        if (model.evidenceLocked == "Yes") {
            holder.textViewEvidenceLocked.setTextColor(ContextCompat.getColor(context!!,
                R.color.colorRed))
        } else {
            holder.textViewEvidenceLocked.setTextColor(ContextCompat.getColor(context!!,
                R.color.colorWhite))
        }
        holder.textViewEvidenceLocked.text = (model.evidenceLocked)
        holder.textViewEvidenceEntryComplete.text = (model.evidenceEntryComplete)
        holder.textViewEvidenceFoundAt.text = (model.evidenceFoundLocation)

        if (model.evidencePictureUrls["image_front"] != null) {
            Picasso.get().load(model.evidencePictureUrls["image_front"])
                .fit().centerCrop().placeholder(R.drawable.anim_loading_images)
                .into(holder.imageViewEvidenceFront)
        }
        if (model.evidencePictureUrls["image_back"] != null) {
            Picasso.get().load(model.evidencePictureUrls["image_back"])
                .fit().centerCrop().placeholder(R.drawable.anim_loading_images)
                .into(holder.imageViewEvidenceBack)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}