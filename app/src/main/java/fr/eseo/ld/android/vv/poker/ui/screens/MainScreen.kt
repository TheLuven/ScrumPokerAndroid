package fr.eseo.ld.android.vv.poker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import fr.eseo.ld.android.vv.poker.ui.UserRole
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    navController: NavHostController,
    viewModel: AuthenticationViewModel,
) {
    val currentUser by viewModel.user.observeAsState()
    var userRole by remember { mutableStateOf<UserRole?>(null) } // State for user role
    val db = FirebaseFirestore.getInstance()
    LaunchedEffect(key1 = currentUser) {
        if (currentUser != null) {
            db.collection("users").document(currentUser!!.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    userRole = if (role != null) {
                        UserRole.valueOf(role) // Convert role string to enum
                    } else {
                        // Assign default role and update Firestore
                        val user = hashMapOf(
                            "email" to currentUser!!.email,
                            "role" to UserRole.USER.name
                        )
                        db.collection("users").document(currentUser!!.uid).set(user)
                        UserRole.USER
                    }
                }
                .addOnFailureListener { exception ->
                    // Assign default role and update Firestore
                    val user = hashMapOf(
                        "email" to currentUser!!.email,
                        "role" to UserRole.USER.name
                    )
                    db.collection("users").document(currentUser!!.uid).set(user)
                    userRole = UserRole.USER
                }
        } else {
            userRole = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentUser != null) {
            Text("User Role: ${userRole?.name}", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = { /*navController.navigate(PokerScreens.GameScreen.route)*/ }) {
                Text("Game")
            }
        } else {
            Text("You are not logged in.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}