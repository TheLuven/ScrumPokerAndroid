package fr.eseo.ld.android.vv.poker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.ui.PokerAppPreview
import fr.eseo.ld.android.vv.poker.ui.composable.TopAppBarBackButton
import fr.eseo.ld.android.vv.poker.ui.navigation.PokerScreens
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.vv.poker.ui.viewmodels.TeamViewModel

@Composable
fun ScrumTeamsScreen(navController: NavHostController, teamViewModel: TeamViewModel,authenticationViewModel: AuthenticationViewModel) {
    val teams by teamViewModel.teams.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBarBackButton(
                onLogout = { authenticationViewModel.logout() },
                navController = navController,
                onBack = { navController.popBackStack() } // Handle back navigation
            )
        },
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(teams) { team ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate(PokerScreens.STORIES.id+"/${team.id}") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = team.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScrumTeamItem(team: Team, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Text(
            text = team.name,
            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}