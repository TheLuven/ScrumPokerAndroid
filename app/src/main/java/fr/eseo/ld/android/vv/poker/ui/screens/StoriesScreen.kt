package fr.eseo.ld.android.vv.poker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.model.UserStory
import fr.eseo.ld.android.vv.poker.ui.composable.TopAppBarBackButton
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.vv.poker.ui.viewmodels.TeamViewModel
import fr.eseo.ld.android.vv.poker.ui.composable.userStory.AddUserStoryDialog
import fr.eseo.ld.android.vv.poker.ui.composable.userStory.ModifyUserStoryDialog
import fr.eseo.ld.android.vv.poker.ui.viewmodels.UserStoryViewModel

@Composable
fun StoriesScreen(
    teamId: String?,
    navController: NavHostController,
    teamViewModel: TeamViewModel,
    authenticationViewModel: AuthenticationViewModel,
    userStoryViewModel: UserStoryViewModel
) {
    var showAddUserStoryDialog by remember { mutableStateOf(false) }
    var showModifyUserStoryDialog by remember { mutableStateOf(false) }
    var selectedUserStory: UserStory? by remember { mutableStateOf(null) }


    // Fetch the team object
    var team by remember { mutableStateOf<Team?>(null) }
    var allUserStories by remember { mutableStateOf<List<UserStory>>(emptyList()) }

    LaunchedEffect(teamId) {
        if (teamId != null) {
            team = teamViewModel.getTeam(teamId)
            allUserStories = userStoryViewModel.getAllUserStories()
        }
    }

    Scaffold(
        topBar = {
            TopAppBarBackButton(
                onLogout = { authenticationViewModel.logout() },
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddUserStoryDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add User Story")
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(team?.userStories ?: emptyList()) { userStoryId ->
                val userStory = allUserStories.find { it.id == userStoryId }
                if (userStory != null) {
                    UserStoryItem(userStory) {
                        selectedUserStory = userStory
                        showModifyUserStoryDialog = true
                    }
                }
            }
        }
    }

    if (showAddUserStoryDialog) {
        AddUserStoryDialog(
            onDismiss = { showAddUserStoryDialog = false;

            },
            onAddUserStory = { userStory ->
                if (teamId != null) {
                    userStoryViewModel.addUserStory(userStory = userStory)
                    teamViewModel.addUserStory(teamId = teamId, userStory = userStory)
                }
            }
        )
    }

    if (showModifyUserStoryDialog) {
        ModifyUserStoryDialog(
            userStory = selectedUserStory,
            onDismiss = { showAddUserStoryDialog = false
            },
            onModifyUserStory = { userStory -> userStoryViewModel.addUserStory(userStory) } // Assuming you have updateUserStory in your TeamViewModel
        )
    }
}

@Composable
fun UserStoryItem(userStory: UserStory, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(8.dp)) {
        Text(text = userStory.description)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = userStory.userPoints?.toString() ?: "") // Display user points if available
    }
}