package fr.eseo.ld.android.vv.poker.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    viewModel: AuthenticationViewModel,
    onLogout: () -> Unit,
    navController: NavHostController
) {
    val currentUser by viewModel.user.observeAsState()
    CenterAlignedTopAppBar(
        title = { Text("Scrum Poker") },
        actions = {
            if (currentUser != null) {
                Text(
                    text = currentUser!!.email ?: "",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Button(onClick = {
                    onLogout()
                    navController.navigate("start") {
                        popUpTo("start") { inclusive = true }
                    }
                }) {
                    Text("Logout")
                }
            }
        }
    )
}