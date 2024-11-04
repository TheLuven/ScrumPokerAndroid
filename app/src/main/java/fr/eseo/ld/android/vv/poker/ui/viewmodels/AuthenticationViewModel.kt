package fr.eseo.ld.android.vv.poker.ui.viewmodels // Replace with your package

import android.app.Application
import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.ld.android.vv.poker.repositories.AuthenticationRepository
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    application: Application,
    private val authenticationRepository: AuthenticationRepository,
) : AndroidViewModel(application) {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: MutableLiveData<FirebaseUser?>
        get() = _user

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    init {
        _user.value = authenticationRepository.getCurrentUser()
        if (_user.value == null) {
            loginAnonymously()
        }
    }

    fun loginAnonymously() {
        authenticationRepository.loginAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = authenticationRepository.getCurrentUser()
            } else {
                _user.value = null
            }
        }
    }

    fun signUp(email: String, password: String) {
        //Verify if the email adresse already exists
        authenticationRepository.getUserByEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _loginResult.value = LoginResult.Error("This email address is already in use.")
                Log.v("AuthenticationViewModel", "email already in use")
                _user.value = null
                return@addOnCompleteListener //TODO FInish the same email error and handler
            }
        }
        if (email.isEmpty()) {
            _loginResult.value = LoginResult.Error("Please enter an email address.")
            Log.v("AuthenticationViewModel", "email empty")
            _user.value = null
            return
        }
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            _loginResult.value = LoginResult.Error("Please enter a valid email address.")
            Log.v("AuthenticationViewModel", "email invalid")
            _user.value = null
            return
        }
        if (password.isEmpty()) {
            _loginResult.value = LoginResult.Error("Please enter a password.")
            Log.v("AuthenticationViewModel", "password empty")
            _user.value = null
            return
        }
        authenticationRepository.signUpWithEmail(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = authenticationRepository.getCurrentUser()
                    _loginResult.value = LoginResult.Success
                    Log.v("AuthenticationViewModel", "login success")
                } else {
                    _user.value = null
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> "Invalid user credentials."
                        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
                        else -> "Login failed. Please try again later."
                    }
                    Log.v("AuthenticationViewModel", errorMessage)
                    _loginResult.value = LoginResult.Error(errorMessage)
                }
            }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty()) {
            _loginResult.value = LoginResult.Error("Please enter an email address.")
            Log.v("AuthenticationViewModel", "email empty")
            _user.value = null
            return
        }

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            _loginResult.value = LoginResult.Error("Please enter a valid email address.")
            Log.v("AuthenticationViewModel", "email invalid")
            _user.value = null
            return
        }
        if (password.isEmpty()) {
            _loginResult.value = LoginResult.Error("Please enter a password.")
            Log.v("AuthenticationViewModel", "password empty")
            _user.value = null
            return
        }
        authenticationRepository.loginWithEmail(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = authenticationRepository.getCurrentUser()
                    _user.value = currentUser
                    _loginResult.value = LoginResult.Success
                    Log.v("AuthenticationViewModel", "login success")
                } else {
                    _user.value = null
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> "Invalid user credentials."
                        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
                        else -> "Login failed. Please try again later."
                    }
                    Log.v("AuthenticationViewModel", errorMessage)
                    _loginResult.value = LoginResult.Error(errorMessage)
                }
            }
    }

    fun logout() {
        val currentUser = authenticationRepository.getCurrentUser()
        if (currentUser != null && currentUser.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
            // User is logged in with Google, call logoutGoogle()
            Log.v("AuthenticationViewModel", "logout google")
            authenticationRepository.logoutGoogle()
            _loginResult.value = LoginResult.Error("You have been logged out.")
        } else {
            // User is not logged in with Google, call logout()
            Log.v("AuthenticationViewModel", "logout")
            authenticationRepository.logout()
            _loginResult.value = LoginResult.Error("You have been logged out.")
        }
        loginAnonymously()
    }

    fun signInWithGoogle(idToken: String) {
        authenticationRepository.loginWithGoogle(idToken)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = authenticationRepository.getCurrentUser()
                    _loginResult.value = LoginResult.Success
                    Log.v("AuthenticationViewModel", "login success")
                } else {
                    _user.value = null
                    val exception = task.exception
                    val errorMessage = when (exception) {
                        is FirebaseAuthInvalidUserException -> "Invalid user credentials."
                        is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
                        else -> "Login failed. Please try again later."
                    }
                    Log.v("AuthenticationViewModel", errorMessage)
                    _loginResult.value = LoginResult.Error(errorMessage)
                }
            }
    }

}
sealed class LoginResult {
    object Success : LoginResult()
    data class Error(val message: String) : LoginResult()
}