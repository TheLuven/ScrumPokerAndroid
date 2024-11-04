package fr.eseo.ld.android.vv.poker.ui.composable

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.ui.UserRole
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.vv.poker.ui.viewmodels.TeamViewModel
import kotlinx.coroutines.tasks.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTeamPopup(
    onDismiss: () -> Unit,
    onAddTeam: (Team) -> Unit,
    viewModel: AuthenticationViewModel,
    teamViewModel: TeamViewModel,
) {
    var teamName by remember { mutableStateOf("") }
    var selectedScrumMasterEmail by remember { mutableStateOf("") }
    val selectedUsers = remember { mutableStateListOf<String>() } // Store selected users
    var showUserSelectionDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Team") },
        text = {
            Column {
                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team Name") }
                )
                // Display selected users
                selectedUsers.forEach { user ->
                    val isScrumMaster = user == selectedScrumMasterEmail
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = user) // Remove color and font weight changes
                        Spacer(modifier = Modifier.width(8.dp))
                        Checkbox(
                            checked = isScrumMaster,
                            onCheckedChange = { isChecked ->
                                selectedScrumMasterEmail = if (isChecked) user else ""
                            }
                        )
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
            Button(onClick = {
                val newTeam = Team(
                    id = UUID.randomUUID().toString(),
                    name = teamName,
                    members = selectedUsers,
                    scrumMasterEmail = selectedScrumMasterEmail
                )
                teamViewModel.reloadTeams()
                onAddTeam(newTeam)
                onDismiss()
            },
                enabled = teamName.isNotEmpty() && selectedUsers.isNotEmpty() && selectedScrumMasterEmail.isNotEmpty()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelectionDialog(
    onDismiss: () -> Unit,
    onSelectUser: (String) -> Unit,
    viewModel: AuthenticationViewModel,
    teamViewModel: TeamViewModel,
    selectedUsers: List<String>
) {
    val users = remember { mutableStateListOf<String>() } // Store users with "USER" role
    val filteredUsers = users.filter { user ->
        !teamViewModel.teams.value!!.any { team -> user in team.members }
    }
    // Fetch users with "USER" role from Firestore
    LaunchedEffect(Unit) {
        try {
            val db = FirebaseFirestore.getInstance()
            val querySnapshot = db.collection("users")
                .whereEqualTo("role", UserRole.USER.name) // Filter by role
                .get()
                .await()

            users.clear() // Clear existing users
            users.addAll(querySnapshot.documents.mapNotNull { it.getString("email") }) // Add emails
        } catch (e: Exception) {
            // Handle error
            Log.e("UserSelectionDialog", "Error fetching users: ${e.message}")
        }
        //if one of the selected users is in the list of users, remove it
        selectedUsers.forEach { user ->
            if (user in users) {
                users.remove(user)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select User") },
        text = {
            LazyColumn {
                items(filteredUsers) { user ->
                    Text(user, modifier = Modifier.clickable {
                        if (user !in selectedUsers) { // Check for duplicate user
                            onSelectUser(user)
                            onDismiss()
                        } else {
                            // Display a message or prevent the addition
                        }
                    })
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}