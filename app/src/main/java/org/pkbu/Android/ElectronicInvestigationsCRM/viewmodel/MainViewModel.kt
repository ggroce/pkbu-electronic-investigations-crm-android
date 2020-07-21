package org.pkbu.Android.ElectronicInvestigationsCRM.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import org.pkbu.Android.ElectronicInvestigationsCRM.model.CaseFile
import org.pkbu.Android.ElectronicInvestigationsCRM.model.EvidenceItem
import org.pkbu.Android.ElectronicInvestigationsCRM.model.User
import org.pkbu.Android.ElectronicInvestigationsCRM.repository.FirestoreRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var currentUserLiveData: LiveData<User>
    lateinit var caseFileLiveData: LiveData<CaseFile>
    lateinit var caseIdLiveData: LiveData<String>
    lateinit var caseFileImageUrlLiveData: LiveData<String>
    lateinit var evidenceItemImageUrlLiveData: LiveData<String>
    lateinit var userImageUrlLiveData: LiveData<String>
    lateinit var evidenceItemLiveData: LiveData<EvidenceItem>
    lateinit var evidenceIdLiveData: LiveData<String>
    private var caseIdForEdit: String? = null
    private var caseIdForEvidence: String? = null
    private var evidenceId: String? = null
    private var evidenceSearchString: String? = null
    private var evidenceSearchField: String? = null

    // Init Firebase
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mRepository: FirestoreRepository? =
        FirestoreRepository.getInstance(application.applicationContext)
    val mDb = mRepository!!.db
    val mStorageRef = mRepository!!.storageRef

    // User info functions: ////////////////////////////////
    fun addBasicUserInfo(user: User) {
        mRepository!!.addBasicUserInfo(user)
    }

    fun getCurrentUser(userUid: String) {
        currentUserLiveData = mRepository!!.getUserByUid(userUid)
    }

    fun updateAllUserInfo(user: User) {
        mRepository!!.updateAllUserInfo(user)
    }

    fun logUser(user: User) {
        mRepository!!.logUser(user)
    }

    // Case file functions: ////////////////////////////////
    fun addCaseFile(caseFile: CaseFile) {
        caseIdLiveData = mRepository!!.addCaseFile(caseFile)
    }

    fun deleteCaseFileByCaseId(caseId: String) {
        mRepository!!.deleteCaseFileByCaseId(caseId)
    }

    fun updateCaseFile(caseFile: CaseFile) {
        mRepository!!.updateCaseFile(caseFile)
    }

    fun getCaseFileByCaseId(caseId: String) {
        caseFileLiveData = mRepository!!.getCaseFileByCaseId(caseId)
    }

    fun setCaseIdForEdit(caseId: String) {
        this.caseIdForEdit = caseId
    }

    fun getCaseIdForEdit(): String {
        return this.caseIdForEdit!!
    }

    fun updateCaseFileEvidenceItemCount(caseId: String, increment: Boolean) {
        mRepository!!.updateCaseFileEvidenceItemCount(caseId, increment)
    }

    fun setImageForCaseFile(caseId: String, imageDataByteArray: ByteArray) {
        caseFileImageUrlLiveData = mRepository!!.setImageForCaseFile(caseId, imageDataByteArray)
    }

    fun setImageForEvidenceItem(evidenceId: String, imageDataByteArray: ByteArray,
                                evidenceImageHashMapKey: String) {
        evidenceItemImageUrlLiveData = mRepository!!.setImageForEvidenceItem(evidenceId,
            imageDataByteArray, evidenceImageHashMapKey)
    }

    fun setImageForUser(userUid: String, imageDataByteArray: ByteArray) {
        userImageUrlLiveData = mRepository!!.setImageForUser(userUid, imageDataByteArray)
    }

    // Evidence Item functions: ////////////////////////////////
    fun addEvidenceItem(evidenceItem: EvidenceItem) {
        val initLowerCaseEvidenceItem = EvidenceItem(evidenceItem)
        evidenceIdLiveData = mRepository!!.addEvidenceItem(initLowerCaseEvidenceItem)
    }

    fun deleteEvidenceItemByEvidenceId(evidenceId: String) {
        mRepository!!.deleteEvidenceItemByEvidenceId(evidenceId)
    }

    fun updateEvidenceItem(evidenceItem: EvidenceItem) {
        val initLowerCaseEvidenceItem = EvidenceItem(evidenceItem)
        mRepository!!.updateEvidenceItem(initLowerCaseEvidenceItem)
    }

    fun getEvidenceItemByEvidenceId(evidenceId: String) {
        evidenceItemLiveData = mRepository!!.getEvidenceItemByEvidenceId(evidenceId)
    }

    fun setCaseIdForEvidence(caseId: String) {
        this.caseIdForEvidence = caseId
    }

    fun getCaseIdForEvidence(): String {
        return this.caseIdForEvidence!!
    }

    fun setEvidenceId(evidenceId: String) {
        this.evidenceId = evidenceId
    }

    fun getEvidenceId(): String {
        return this.evidenceId!!
    }

    fun setSearchStrings(evidenceSearchString: String, evidenceSearchField: String) {
        this.evidenceSearchString = evidenceSearchString
        this.evidenceSearchField = evidenceSearchField
    }

    fun getEvidenceSearchString() : String {
        return this.evidenceSearchString!!
    }

    fun getEvidenceSearchField() : String {
        return this.evidenceSearchField!!
    }
}
