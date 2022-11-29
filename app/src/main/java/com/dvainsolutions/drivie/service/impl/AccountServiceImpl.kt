package com.dvainsolutions.drivie.service.impl

import com.dvainsolutions.drivie.service.AccountService
import com.google.firebase.auth.EmailAuthProvider
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

    override fun changeUserData(
        newEmail: String?,
        oldPassword: String?,
        newPassword: String?,
        onResult: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val credential = EmailAuthProvider
            .getCredential(getCurrentUser()?.email.toString(), oldPassword.toString())

        Firebase.auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
            if (it.isSuccessful) {
                if (!newEmail.isNullOrEmpty()) {
                    getCurrentUser()?.updateEmail(newEmail)
                        ?.addOnCompleteListener { changeEmailTask ->
                            if (changeEmailTask.isSuccessful) {
                                if (!newPassword.isNullOrEmpty()) {
                                    getCurrentUser()?.updatePassword(newPassword)
                                        ?.addOnCompleteListener { changePassTask ->
                                            if (changePassTask.isSuccessful) {
                                                onResult.invoke()
                                            } else {
                                                onError.invoke(changePassTask.exception!!)
                                            }
                                        }
                                } else {
                                    onResult.invoke()
                                }
                            } else {
                                onError.invoke(changeEmailTask.exception!!)
                            }
                        }
                } else if (!newPassword.isNullOrEmpty()) {
                    getCurrentUser()?.updatePassword(newPassword)
                        ?.addOnCompleteListener { changePassTask ->
                            if (changePassTask.isSuccessful) {
                                onResult.invoke()
                            } else {
                                onError.invoke(changePassTask.exception!!)
                            }
                        }
                }
            } else {
                onError.invoke(it.exception!!)
            }
        }
    }

    override fun logoutUser() {
        Firebase.auth.signOut()
    }

    override fun setAuthStateListener() {
        /*Firebase.auth.addAuthStateListener {
        }*/
    }
}