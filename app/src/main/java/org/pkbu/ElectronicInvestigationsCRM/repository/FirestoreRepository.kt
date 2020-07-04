package org.pkbu.ElectronicInvestigationsCRM.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.pkbu.ElectronicInvestigationsCRM.model.CaseFile
import org.pkbu.ElectronicInvestigationsCRM.model.EvidenceItem
import org.pkbu.ElectronicInvestigationsCRM.model.User



class FirestoreRepository private constructor(context: Context) {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var storageRef: StorageReference = FirebaseStorage.getInstance().reference


    // Case Files ////////////////////////////////////////////////////////

    fun addCaseFile(caseFile: CaseFile): MutableLiveData<String> {
        val dbRef = db.collection(CASEFILE_ROOT).document()
        val caseIdMutableLiveData: MutableLiveData<String> = MutableLiveData<String>()

        caseFile.caseId = dbRef.id
        dbRef.set(caseFile)
            .addOnSuccessListener {
                caseIdMutableLiveData.value = caseFile.caseId
                Log.d(TAG, "Case file added successfully") }
            .addOnFailureListener { e -> Log.w(TAG, "Error creating new case file", e)}

        return caseIdMutableLiveData
    }

    fun deleteCaseFileByCaseId(caseId: String) {
        val dbRef = db.collection(CASEFILE_ROOT)

        dbRef.document(caseId).delete()
            .addOnSuccessListener { document -> Log.w(TAG, "Case successfully deleted")}
            .addOnFailureListener {e -> Log.w(TAG, "Error when deleting case file!", e)}
    }

    fun getCaseFileByCaseId(caseId: String) : MutableLiveData<CaseFile> {
        val dbRef = db.collection(CASEFILE_ROOT)
        val caseFileMutableLiveData: MutableLiveData<CaseFile> = MutableLiveData<CaseFile>()

        dbRef.whereEqualTo("caseId", caseId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    caseFileMutableLiveData.value =
                        CaseFile(document.toObject(CaseFile::class.java))
                }
            }.addOnFailureListener {e -> Log.w(TAG, "Error when looking for case file!", e)}
        return caseFileMutableLiveData
    }

    fun updateCaseFile(caseFile: CaseFile) {
        val dbRef = db.collection(CASEFILE_ROOT)

        dbRef.document(caseFile.caseId!!).set(caseFile, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "Case file data successfully written") }
            .addOnFailureListener { e -> Log.w(TAG,
                "Error writing case file data to document", e) }
    }

    fun updateCaseFileEvidenceItemCount(caseId: String, increment: Boolean) {
        val dbRef = db.collection(CASEFILE_ROOT)

        if(increment) {
            dbRef.document(caseId).update("caseEvidenceItemCount", FieldValue.increment(1))
                .addOnSuccessListener { Log.d(TAG, "Case file data successfully written") }
                .addOnFailureListener { e -> Log.w(TAG,
                    "Error writing case file data to document", e) }
        } else {
            dbRef.document(caseId).update("caseEvidenceItemCount", FieldValue.increment(-1))
                .addOnSuccessListener { Log.d(TAG, "Case file data successfully written") }
                .addOnFailureListener { e -> Log.w(TAG,
                    "Error writing case file data to document", e) }
        }

    }

    fun setImageForCaseFile(caseId: String, imageDataByteArray: ByteArray)
            : MutableLiveData<String>{
        val mStorageRef = storageRef.child(CASEFILE_IMAGES + caseId)
        val caseFileImageUrlLiveData: MutableLiveData<String> = MutableLiveData<String>()
        var imageUrl: String = ""

        val uploadTask: UploadTask = mStorageRef.putBytes(imageDataByteArray)

        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            val uri = taskSnapshot.storage.downloadUrl
            uri.addOnSuccessListener { uri ->
                caseFileImageUrlLiveData.value = uri.toString()
                Log.e("TAG:", "Image placed into firebase, url: " +
                        "${caseFileImageUrlLiveData.value}, reference: ${mStorageRef.name}")
            }
        }).addOnFailureListener {
            Log.d(TAG, "Firebase storage image upload failure, ${it.message}")
            imageUrl = "error"
        }

        return caseFileImageUrlLiveData
    }

    // Evidence items ////////////////////////////////////////////////////////

    fun addEvidenceItem(evidenceItem: EvidenceItem): MutableLiveData<String> {
        val dbRef = db.collection(EVIDENCEITEM_ROOT).document()
        val evidenceIdMutableLiveData: MutableLiveData<String> = MutableLiveData<String>()

        evidenceItem.evidenceId = dbRef.id
        dbRef.set(evidenceItem)
            .addOnSuccessListener {
                evidenceIdMutableLiveData.value = evidenceItem.evidenceId
                Log.d(TAG, "Evidence item added successfully") }
            .addOnFailureListener { e -> Log.w(TAG, "Error creating new evidence item", e)}

        return evidenceIdMutableLiveData
    }

    fun deleteEvidenceItemByEvidenceId(evidenceId: String) {
        val dbRef = db.collection(EVIDENCEITEM_ROOT)

        dbRef.document(evidenceId).delete()
            .addOnSuccessListener { document -> Log.w(TAG, "Evidence Item " +
                    "successfully deleted")}
            .addOnFailureListener {e -> Log.w(TAG, "Error when deleting Evidence Item!", e)}
    }

    fun getEvidenceItemByEvidenceId(evidenceId: String) : MutableLiveData<EvidenceItem> {
        val dbRef = db.collection(EVIDENCEITEM_ROOT)
        val evidenceItemMutableLiveData: MutableLiveData<EvidenceItem> =
            MutableLiveData<EvidenceItem>()

        dbRef.whereEqualTo("evidenceId", evidenceId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    evidenceItemMutableLiveData.value =
                        EvidenceItem(document.toObject(EvidenceItem::class.java))
                }
            }.addOnFailureListener {e -> Log.w(TAG, "Error when looking for evidence " +
                    "item!", e)}

        return evidenceItemMutableLiveData
    }

    fun updateEvidenceItem(evidenceItem: EvidenceItem) {
        val dbRef = db.collection(EVIDENCEITEM_ROOT)

        dbRef.document(evidenceItem.evidenceId!!).set(evidenceItem, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "Evidence item data successfully written") }
            .addOnFailureListener { e -> Log.w(TAG,
                "Error writing evidence item data to document", e) }
    }

    fun setImageForEvidenceItem(evidenceId: String, imageDataByteArray: ByteArray,
                                evidenceImageHashMapKey: String)
            : MutableLiveData<String>{
        val mStorageRef = storageRef.child(EVIDENCEITEM_IMAGES + evidenceId + "/" +
                evidenceId + "_" + evidenceImageHashMapKey)
        val evidenceItemImageUrlLiveData: MutableLiveData<String> = MutableLiveData<String>()
        var imageUrl: String = ""

        val uploadTask: UploadTask = mStorageRef.putBytes(imageDataByteArray)

        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            val uri = taskSnapshot.storage.downloadUrl
            uri.addOnSuccessListener { uri ->
                evidenceItemImageUrlLiveData.value = uri.toString()
                Log.e("TAG:", "Evidence $evidenceImageHashMapKey image placed into " +
                        "firebase, url: ${evidenceItemImageUrlLiveData.value}, " +
                        "reference: ${mStorageRef.name}")
            }
        }).addOnFailureListener {
            Log.d(TAG, "Firebase storage evidence image upload failure, ${it.message}")
            imageUrl = "error"
        }

        return evidenceItemImageUrlLiveData
    }

    // Users ////////////////////////////////////////////////////////

    fun getUserByUid(userUid: String) : MutableLiveData<User> {
        val dbRef = db.collection(USER_ROOT)
        val userMutableLiveData: MutableLiveData<User> = MutableLiveData<User>()

        dbRef.whereEqualTo("userUid", userUid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    userMutableLiveData.value = User(document.toObject(User::class.java))
                }
            }.addOnFailureListener {e -> Log.w(TAG, "Error when looking for user object!", e)}

        return userMutableLiveData
    }

    fun updateAllUserInfo(user: User) {
        val dbRef = db.collection(USER_ROOT)

        dbRef.document(user.userEmail.toString()).set(user, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "User information successfully written") }
            .addOnFailureListener { e -> Log.w(TAG,
                "Error writing User information to document", e) }
    }

    fun addBasicUserInfo(user: User) {
        val dbRef = db.collection(USER_ROOT)
        var basicUserInfo = hashMapOf(
            "userEmail" to user.userEmail,
            "userUid" to user.userUid,
            "userName" to user.userName
        )
        dbRef.document(user.userEmail.toString()).set(basicUserInfo, SetOptions.merge())
    }

    fun logUser(user: User) {
        val dbRef = db.collection(USER_LOGINS)

        val userLogin = hashMapOf(
            "uid" to user.userUid,
            "email" to user.userEmail,
            "name" to user.userName,
            "timestamp" to FieldValue.serverTimestamp()
        )

        dbRef.document(user.userEmail.toString()).set(userLogin)
            .addOnSuccessListener { Log.d(TAG, "User information successfully written") }
            .addOnFailureListener { e -> Log.w(TAG,
                "Error writing User information to document", e) }
    }

    fun setImageForUser(userUid: String, imageDataByteArray: ByteArray)
            : MutableLiveData<String>{
        val mStorageRef = storageRef.child(USER_PROFILE_IMAGES + userUid)
        val userImageUrlLiveData: MutableLiveData<String> = MutableLiveData<String>()
        var imageUrl: String = ""

        val uploadTask: UploadTask = mStorageRef.putBytes(imageDataByteArray)

        uploadTask.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            val uri = taskSnapshot.storage.downloadUrl
            uri.addOnSuccessListener { uri ->
                userImageUrlLiveData.value = uri.toString()
                Log.e("TAG:", "Image placed into firebase, url: " +
                        "${userImageUrlLiveData.value}, reference: ${mStorageRef.name}")
            }
        }).addOnFailureListener {
            Log.d(TAG, "Firebase storage image upload failure, ${it.message}")
            imageUrl = "error"
        }

        return userImageUrlLiveData
    }

    fun searchEvidenceByField(evidenceSearchString: String, evidenceSearchField: String) {
        val dbRef = db.collection(EVIDENCEITEM_ROOT)

        dbRef.whereGreaterThanOrEqualTo(evidenceSearchField, evidenceSearchString)
    }

    companion object {
        private const val TAG = "FirestoreRepository"
        private const val CASEFILE_ROOT = "CaseFiles"
        private const val USER_ROOT = "Users"
        private const val USER_LOGINS = "UserLogins"
        private const val EVIDENCEITEM_ROOT = "EvidenceItems"
        private const val CASEFILE_IMAGES = "/CaseFileImages/"
        private const val EVIDENCEITEM_IMAGES = "/EvidenceItemImages/"
        private const val USER_PROFILE_IMAGES = "/UserProfileImages/"
        private const val EVIDENCE_HASHMAP_FRONT = "image_front"
        private const val EVIDENCE_HASHMAP_BACK = "image_back"
        private const val EVIDENCE_HASHMAP_DETAIL1 = "image_detail1"
        private const val EVIDENCE_HASHMAP_DETAIL2 = "image_detail2"
        private const val SEARCH_BY_ENTRY_DATE = "evidenceEntryDate"
        private const val SEARCH_BY_TYPE = "evidenceType"
        private const val SEARCH_BY_MAKE = "evidenceMake"
        private const val SEARCH_BY_MODEL = "evidenceModel"
        private const val SEARCH_BY_LAST_EDITED_BY = "evidenceLastEditedBy"
        private const val SEARCH_BY_FOUND_BY = "evidenceFoundBy"
        private const val SEARCH_BY_SERIAL_NUMBER = "evidenceSerialNumber"
        private const val SEARCH_BY_DESCRIPTOR = "evidenceDescriptor"
        private const val SEARCH_BY_PROCESSING_NOTES = "evidenceProcessingNotes"
        private const val SEARCH_BY_ACCOUNT_INFO = "evidenceAccountInfo"
        private const val SEARCH_BY_SUSPECT = "evidenceSuspectedOwner"
        private const val SEARCH_BY_RELEVANCY = "evidenceRelevancy"
        private const val SEARCH_BY_LOCKED = "evidenceLocked"
        private const val SEARCH_BY_STATUS = "evidenceStatus"
        private const val SEARCH_BY_ENTRY_COMPLETE = "evidenceEntryComplete"



        private var ourInstance: FirestoreRepository? = null
        @JvmStatic
        fun getInstance(context: Context): FirestoreRepository? {
            if (ourInstance == null) {
                ourInstance = FirestoreRepository(context)
            }
            return ourInstance
        }
    }
}