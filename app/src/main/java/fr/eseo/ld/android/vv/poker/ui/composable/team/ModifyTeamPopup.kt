package fr.eseo.ld.android.vv.poker.ui.composable.team

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.vv.poker.ui.viewmodels.TeamViewModel
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyTeamPopup(
    team: Team?,
    onDismiss: () -> Unit,
    onDeleteTeam: (Team) -> Unit,
    onUpdateTeam: (Team) -> Unit,
    viewModel: AuthenticationViewModel,
    teamViewModel: TeamViewModel
) {
    var teamName by remember { mutableStateOf(team?.name ?: "") }
    val selectedUsers = remember { mutableStateListOf<String>() }
    var showUserSelectionDialog by remember { mutableStateOf(false) }
    var selectedScrumMasterEmail by remember { mutableStateOf(team?.scrumMasterEmail ?: "") }

    // Initialize selectedUsers with the team's members
    LaunchedEffect(team) {
        if (team != null) {
            selectedUsers.addAll(team.members)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modify Team") },
        text = {
            Column {
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team Name") }
                )

                // Display selected users with checkbox and delete icon
                selectedUsers.forEach { user ->
                    val isScrumMaster = user == selectedScrumMasterEmail
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isScrumMaster,
                            onCheckedChange = { isChecked ->
                                selectedScrumMasterEmail = if (isChecked) user else ""
                            }
                        )
                        Text("Add $user")
                        Spacer(modifier = Modifier.weight(1f)) // Push delete icon to the end
                        IconButton(onClick = {
                            selectedUsers.remove(user)
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Remove User")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Button to add users
                Button(onClick = { showUserSelectionDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add User")
                    Text("Add User")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (team != null) {
                        val updatedTeam = team.copy(name = teamName, members = selectedUsers, scrumMasterEmail = selectedScrumMasterEmail)
                        onUpdateTeam(updatedTeam)
                    }
                    teamViewModel.reloadTeams()
                    onDismiss()
                },
                enabled = teamName.isNotEmpty() && selectedUsers.isNotEmpty() // Disable if invalid
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Row {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onDeleteTeam(team!!)
                    onDismiss()
                    teamViewModel.reloadTeams()             },
                    enabled = team != null) {
                    Text("Delete Team")
                }
            }
        }
    )

    if (showUserSelectionDialog) {
        UserSelectionDialog(
            onDismiss = { showUserSelectionDialog = false },
            onSelectUser = { user ->
                // Add user to selectedUsers list
                selectedUsers.add(user)
            },
            viewModel = viewModel,
            teamViewModel = teamViewModel,
            selectedUsers = selectedUsers
        )
    }
}