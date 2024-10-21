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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    navController: NavHostController,
    viewModel: AuthenticationViewModel
) {
    val currentUser by viewModel.user.observeAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentUser != null) {
            Text("Hello, ${currentUser!!.email}!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {onLogout
                navController.navigate("start") {
                    popUpTo("start") { inclusive = true }
                }
            }) {
                Text("Logout")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { //navController.navigate(PokerScreens.GameScreen.route)
            }) {
                Text("Game")
            }
        } else {
            Text("You are not logged in.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}