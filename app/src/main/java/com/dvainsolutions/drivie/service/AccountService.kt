package com.dvainsolutions.drivie.service

import com.google.firebase.auth.FirebaseUser

interface AccountService {
    fun createAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun isUserLoggedIn(): Boolean
    fun getCurrentUser(): FirebaseUser?
    fun setAuthStateListener()
}