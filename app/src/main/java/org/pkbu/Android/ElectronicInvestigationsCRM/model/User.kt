package org.pkbu.Android.ElectronicInvestigationsCRM.model

import com.google.firebase.auth.FirebaseUser

class User () {
    var userUid: String? = null
    var userEmail: String? = null
    var userName: String? = null
    var userAgency: String? = null
    var userRole: String? = null
    var userPhone: String? = null
    var userImageUrl: String? = null

    constructor(user: FirebaseUser) :this() {
        //gets some info from Firebase Auth login
        this.userUid = user.uid
        this.userEmail = user.email
        this.userName = user.displayName
        this.userPhone = user.phoneNumber
        this.userAgency = ""
        this.userRole = ""
    }

    constructor(userUid: String, userEmail: String, userName: String, userAgency: String,
    userRole: String, userPhone: String) :this() {
        this.userUid = userUid
        this.userEmail = userEmail
        this.userName = userName
        this.userAgency = userAgency
        this.userRole = userRole
        this.userPhone = userPhone
    }

    constructor(user: User) :this() {
        this.userUid = user.userUid
        this.userEmail = user.userEmail
        this.userName = user.userName
        this.userAgency = user.userAgency
        this.userRole = user.userRole
        this.userPhone = user.userPhone
        this.userImageUrl = user.userImageUrl
    }
}

