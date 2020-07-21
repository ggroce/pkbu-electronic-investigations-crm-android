package org.pkbu.Android.ElectronicInvestigationsCRM.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class EvidenceItem() {
    var caseId: String? = null
    var evidenceId: String? = null
    var evidenceCreatorUid: String? = null
    var evidenceFoundBy: String? = null
    var evidenceFoundBy_lower: String? = null
    var evidenceLastEditedBy: String? = null
    var evidenceLastEditedBy_lower: String? = null
    var evidenceEntryDate: Date? = null
    var evidenceDescriptor: String? = null
    var evidenceDescriptor_lower: String? = null
    var evidenceType: String? = null
    var evidenceType_lower: String? = null
    var evidenceMake: String? = null
    var evidenceMake_lower: String? = null
    var evidenceModel: String? = null
    var evidenceModel_lower: String? = null
    var evidenceSerialNumber: String? = null
    var evidenceSerialNumber_lower: String? = null
    var evidenceProcessingNotes: String? = null
    var evidenceProcessingNotes_lower: String? = null
    var evidenceAccountInfo: String? = null
    var evidenceAccountInfo_lower: String? = null
    var evidenceSuspectedOwner: String? = null
    var evidenceSuspectedOwner_lower: String? = null
    var evidenceFoundLocation: String? = null
    var evidenceFoundLocation_lower: String? = null
    var evidenceEntryComplete: String? = null
    var evidenceEntryComplete_lower: String? = null
    var evidenceRelevancy: String? = null
    var evidenceRelevancy_lower: String? = null
    var evidenceLocked: String? = null
    var evidenceLocked_lower: String? = null
    var evidenceStorageSize: Double? = 0.0
    var evidenceStatus: String? = null
    var evidenceStatus_lower: String? = null
    var evidencePictureUrls = hashMapOf<String, String>()
    @ServerTimestamp var timestamp: Date? = null

    constructor(evidenceCreatorUid: String, enteredBy: String, evidenceName: String, evidenceMake: String,
                evidenceModel: String, evidenceSerialNumber: String) :this() {
        this.evidenceCreatorUid = evidenceCreatorUid
        this.evidenceLastEditedBy = enteredBy
        this.evidenceLastEditedBy_lower = enteredBy.toLowerCase(Locale.US)
        this.evidenceDescriptor = evidenceName
        this.evidenceDescriptor_lower = evidenceName.toLowerCase(Locale.US)
        this.evidenceMake = evidenceMake
        this.evidenceMake_lower = evidenceMake.toLowerCase(Locale.US)
        this.evidenceModel = evidenceModel
        this.evidenceModel_lower = evidenceModel.toLowerCase(Locale.US)
    }

    constructor(evidenceItem: EvidenceItem) :this() {
        this.caseId = evidenceItem.caseId
        this.evidenceId = evidenceItem.evidenceId
        this.evidenceCreatorUid = evidenceItem.evidenceCreatorUid
        this.evidenceFoundBy = evidenceItem.evidenceFoundBy
        this.evidenceFoundBy_lower = evidenceItem.evidenceFoundBy?.toLowerCase(Locale.US)
        this.evidenceLastEditedBy = evidenceItem.evidenceLastEditedBy
        this.evidenceLastEditedBy_lower = evidenceItem.evidenceLastEditedBy?.toLowerCase(Locale.US)
        this.evidenceEntryDate = evidenceItem.evidenceEntryDate
        this.evidenceDescriptor = evidenceItem.evidenceDescriptor
        this.evidenceDescriptor_lower = evidenceItem.evidenceDescriptor?.toLowerCase(Locale.US)
        this.evidenceType = evidenceItem.evidenceType
        this.evidenceType_lower = evidenceItem.evidenceType?.toLowerCase(Locale.US)
        this.evidenceMake = evidenceItem.evidenceMake
        this.evidenceMake_lower = evidenceItem.evidenceMake?.toLowerCase(Locale.US)
        this.evidenceModel = evidenceItem.evidenceModel
        this.evidenceModel_lower = evidenceItem.evidenceModel?.toLowerCase(Locale.US)
        this.evidenceSerialNumber = evidenceItem.evidenceSerialNumber
        this.evidenceSerialNumber_lower = evidenceItem.evidenceSerialNumber?.toLowerCase(Locale.US)
        this.evidenceProcessingNotes = evidenceItem.evidenceProcessingNotes
        this.evidenceProcessingNotes_lower = evidenceItem.evidenceProcessingNotes?.toLowerCase(Locale.US)
        this.evidenceAccountInfo = evidenceItem.evidenceAccountInfo
        this.evidenceAccountInfo_lower = evidenceItem.evidenceAccountInfo?.toLowerCase(Locale.US)
        this.evidenceSuspectedOwner = evidenceItem.evidenceSuspectedOwner
        this.evidenceSuspectedOwner_lower = evidenceItem.evidenceSuspectedOwner?.toLowerCase(Locale.US)
        this.evidenceFoundLocation = evidenceItem.evidenceFoundLocation
        this.evidenceFoundLocation_lower = evidenceItem.evidenceFoundLocation?.toLowerCase(Locale.US)
        this.evidenceEntryComplete = evidenceItem.evidenceEntryComplete
        this.evidenceEntryComplete_lower = evidenceItem.evidenceEntryComplete?.toLowerCase(Locale.US)
        this.evidenceRelevancy = evidenceItem.evidenceRelevancy
        this.evidenceRelevancy_lower = evidenceItem.evidenceRelevancy?.toLowerCase(Locale.US)
        this.evidenceLocked = evidenceItem.evidenceLocked
        this.evidenceLocked_lower = evidenceItem.evidenceLocked?.toLowerCase(Locale.US)
        this.evidenceStorageSize = evidenceItem.evidenceStorageSize
        this.evidenceStatus = evidenceItem.evidenceStatus
        this.evidenceStatus_lower = evidenceItem.evidenceStatus?.toLowerCase(Locale.US)
        this.evidencePictureUrls = evidenceItem.evidencePictureUrls
    }
}
