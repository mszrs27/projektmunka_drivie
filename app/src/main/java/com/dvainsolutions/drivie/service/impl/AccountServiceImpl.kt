package com.dvainsolutions.drivie.service.impl

import com.dvainsolutions.drivie.service.AccountService
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {
    override fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getCurrentUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    override fun setAuthStateListener() {
        Firebase.auth.addAuthStateListener {
            //it.
        }
    }
}