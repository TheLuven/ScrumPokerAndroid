package fr.eseo.ld.android.vv.poker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.ui.composable.team.AddTeamPopup
import fr.eseo.ld.android.vv.poker.ui.composable.team.ModifyTeamPopup
import fr.eseo.ld.android.vv.poker.ui.composable.TopAppBarBackButton
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.vv.poker.ui.viewmodels.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    navController: NavHostController,
    teamViewModel: TeamViewModel,
    authenticationViewModel: AuthenticationViewModel

) {
    val teams by teamViewModel.teams.observeAsState()
    var showAddTeamPopup by remember { mutableStateOf(false) }
    var showModifyTeamPopup by remember { mutableStateOf(false) }
    var selectedTeam: Team? by remember { mutableStateOf(null) }

    Scaffold(
        topBar = {
            TopAppBarBackButton(
                onLogout = { authenticationViewModel.logout() },
                navController = navController,
                onBack = { navController.popBackStack() } // Handle back navigation
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAddTeamPopup = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Team")
            }
        }
    ) { innerPadding ->
        TeamList(
            teams = teams ?: emptyList(),
            onClick = { teamId ->
                // Navigate to team details screen
            },
            onLongClick = { team ->
                // Handle team deletion
            },
            modifier = Modifier.padding(innerPadding),
            authenticationViewModel = authenticationViewModel,
            teamViewModel = teamViewModel,
        )
    }
    if (showModifyTeamPopup) {
        ModifyTeamPopup(
            team = selectedTeam,
            onDismiss = { showModifyTeamPopup = false },
            onDeleteTeam = { team -> teamViewModel.deleteTeam(team.id) },
            onUpdateTeam = { team -> teamViewModel.updateTeam(team) }, // Add updateTeam function
            viewModel = authenticationViewModel,
            teamViewModel = teamViewModel
        )
    }
    if (showAddTeamPopup) {
        AddTeamPopup(
            onDismiss = { showAddTeamPopup = false },
            onAddTeam = { team -> teamViewModel.addTeam(team) },
            viewModel = authenticationViewModel, // Pass AuthenticationViewModel
            teamViewModel = teamViewModel, // Pass TeamViewModel,
        )
    }
}

@Composable
fun TeamList(
    teams: List<Team>,
    onClick: (String) -> Unit,
    onLongClick: (Team) -> Unit,
    modifier: Modifier = Modifier,
    authenticationViewModel: AuthenticationViewModel,
    teamViewModel: TeamViewModel
) {
    var showModifyTeamPopup by remember { mutableStateOf(false) }
    var selectedTeam: Team? by remember { mutableStateOf(null) }
    LazyVerticalStaggeredGrid(
        modifier = modifier.padding(8.dp),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 8.dp
    ) {
        items(teams) { team ->
            TeamItem(
                team,
                onClick = onClick,
                onLongClick = onLongClick,
                modifier = Modifier.fillMaxWidth(),
                onModify = { team ->
                    selectedTeam = team
                    showModifyTeamPopup = true
                },
            )
        }
    }
    if (showModifyTeamPopup) {
        ModifyTeamPopup(
            team = selectedTeam,
            onDismiss = { showModifyTeamPopup = false },
            onDeleteTeam = { team -> teamViewModel.deleteTeam(team.id) },
            onUpdateTeam = { team -> teamViewModel.updateTeam(team) },
            viewModel = authenticationViewModel,
            teamViewModel = teamViewModel,
        )
    }
}

@Composable
fun TeamItem(team: Team, onClick: (String) -> Unit, onLongClick: (Team) -> Unit, modifier: Modifier = Modifier, onModify: (Team) -> Unit) {
    Card(
        onClick = { onModify(team) }, // Call onModify when clicked
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = team.name,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Display members with Scrum Master in bold
            team.members.forEach { member ->
                val isScrumMaster = member == team.scrumMasterEmail
                Text(
                    text = member,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    fontWeight = if (isScrumMaster) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}