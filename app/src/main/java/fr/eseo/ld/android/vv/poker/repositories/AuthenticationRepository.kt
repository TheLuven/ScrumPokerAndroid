package fr.eseo.ld.android.vv.poker.repositories

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class AuthenticationRepository @Inject constructor (
    private val firebaseAuth : FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) {

    fun loginAnonymously() =
        firebaseAuth.signInAnonymously()

    fun signUpWithEmail(email : String, password : String) =
        firebaseAuth.createUserWithEmailAndPassword(email, password)

    fun loginWithEmail(email : String, password : String) =
        firebaseAuth.signInWithEmailAndPassword(email, password)

    fun logout() {
        firebaseAuth.signOut()
        firebaseAuth.currentUser?.delete()
    }

    fun logoutGoogle() {
        firebaseAuth.signOut()
        // Sign out from Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.v("AuthenticationRepository", "Google Sign-Out successful")
                // Google Sign-Out successful
            } else {
                Log.v("AuthenticationRepository", "Google Sign-Out failed")
                // Google Sign-Out failed, handle error
            }
        }
    }

    fun getCurrentUser() =
        firebaseAuth.currentUser

    fun loginWithGoogle(idToken: String) =
        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
}