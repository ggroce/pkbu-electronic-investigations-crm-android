package org.pkbu.pkbu_androidcasemaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.squareup.picasso.Picasso
import org.pkbu.pkbu_androidcasemaintenance.R
import org.pkbu.pkbu_androidcasemaintenance.model.CaseFile
import org.pkbu.pkbu_androidcasemaintenance.utils.DateUtils

class CaseFileAdapter(options: FirestoreRecyclerOptions<CaseFile>) :
    FirestoreRecyclerAdapter<CaseFile, CaseFileAdapter.CaseFileHolder>(options) {

    private var listener: OnItemClickListener? = null

    inner class CaseFileHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var textViewCaseOriginatingAgency: TextView
        var textViewCaseFileAgent: TextView
        var textViewCaseFileCaseNumber: TextView
        var textViewCaseFileDate: TextView
        var textViewCaseFileEvidenceItemCount: TextView
        var textViewCaseFileStatus: TextView
        var textViewCaseFileAddress: TextView
        var textViewCaseFileCity: TextView

        var imageViewCaseBannerImage: ImageView
        var imageViewCaseCreatorImage: ImageView

        var buEditCase: Button? = null
        var buViewEvidence: Button? = null

        init {
            textViewCaseOriginatingAgency = itemView.findViewById(R.id.case_originating_agency)
            textViewCaseFileAgent = itemView.findViewById(R.id.case_file_agent)
            textViewCaseFileCaseNumber = itemView.findViewById(R.id.case_file_case_number)
            textViewCaseFileDate = itemView.findViewById(R.id.case_file_date)
            textViewCaseFileEvidenceItemCount = itemView.findViewById(R.id.case_file_evidence_item_count)
            textViewCaseFileStatus = itemView.findViewById(R.id.case_file_status)
            textViewCaseFileAddress = itemView.findViewById(R.id.case_file_address)
            textViewCaseFileCity = itemView.findViewById(R.id.case_file_city)
            imageViewCaseBannerImage = itemView.findViewById(R.id.image_case_banner)
            imageViewCaseCreatorImage = itemView.findViewById(R.id.image_creator_image)
            buEditCase = itemView.findViewById(R.id.bu_edit_case)
            buViewEvidence = itemView.findViewById(R.id.bu_view_evidence)

            buEditCase!!.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener!!.onItemClick(snapshots.getSnapshot(position), position, true)
                }
            })

            buViewEvidence!!.setOnClickListener(View.OnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener!!.onItemClick(snapshots.getSnapshot(position), position, false)
                }
            })
        }
    }

    interface OnItemClickListener {
        fun onItemClick(documentSnapshot: DocumentSnapshot?, position: Int, editCase: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseFileHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.fragment_case_file,
        parent, false)
        return CaseFileHolder(view)
    }

    override fun onBindViewHolder(holder: CaseFileHolder, position: Int, model: CaseFile) {

        holder.textViewCaseOriginatingAgency.text = model.originatingAgency
        holder.textViewCaseFileAgent.text = model.caseAgent
        holder.textViewCaseFileCaseNumber.text = model.caseNumber
        holder.textViewCaseFileDate.text = DateUtils.dateFormat.format(model.caseDate!!)
        holder.textViewCaseFileEvidenceItemCount.text = model.caseEvidenceItemCount.toString()
        holder.textViewCaseFileStatus.text = model.caseStatus
        holder.textViewCaseFileAddress.text = model.caseAddress
        holder.textViewCaseFileCity.text = model.caseCity
        // Sets image

        if(model.caseCreatorImageUrl != null) {
            Picasso.get().load(model.caseCreatorImageUrl).fit().centerCrop()
                .placeholder(R.drawable.anim_loading_images).into(holder.imageViewCaseCreatorImage)
        }
        if(model.caseImageUrl != null) {
            Picasso.get().load(model.caseImageUrl).fit().centerCrop()
                .placeholder(R.drawable.anim_loading_images).into(holder.imageViewCaseBannerImage)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}