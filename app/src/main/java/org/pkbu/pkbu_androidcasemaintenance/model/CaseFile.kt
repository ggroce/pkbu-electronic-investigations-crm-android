package org.pkbu.pkbu_androidcasemaintenance.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class CaseFile() {
    var caseId: String? = null
    var caseCreatorUid: String? = null
    var caseDate: Date? = null
    var caseNumber: String? = null
    var caseTitle: String? = null
    var originatingAgency: String? = null
    var caseAgent: String? = null
    var caseAddress: String? = null
    var caseCity: String? = null
    var caseStatus: String? = null
    var caseImageUrl: String? = null
    var caseCreatorImageUrl: String? = null
    var caseEvidenceItemCount: Int? = 0
    var caseNotes: String? = null
    @ServerTimestamp var timestamp: Date? = null

    constructor(caseCreatorUid: String, caseDate: Date, caseAddress: String, caseCity: String) :this(){
        this.caseCreatorUid = caseCreatorUid
        this.caseDate = caseDate
        this.caseAddress = caseAddress
        this.caseCity = caseCity
    }

    constructor(caseFile: CaseFile) :this(){
        this.caseId = caseFile.caseId
        this.caseCreatorUid = caseFile.caseCreatorUid
        this.caseNumber = caseFile.caseNumber
        this.caseTitle = caseFile.caseTitle
        this.originatingAgency = caseFile.originatingAgency
        this.caseAgent = caseFile.caseAgent
        this.caseAddress = caseFile.caseAddress
        this.caseCity = caseFile.caseCity
        this.caseDate = caseFile.caseDate
        this.caseStatus = caseFile.caseStatus
        this.caseImageUrl = caseFile.caseImageUrl
        this.caseCreatorImageUrl = caseFile.caseCreatorImageUrl
        this.caseEvidenceItemCount = caseFile.caseEvidenceItemCount
        this.caseNotes = caseFile.caseNotes
    }
}
