package fr.eseo.ld.android.vv.poker.ui.composable.userStory

import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import fr.eseo.ld.android.vv.poker.model.UserStory

@Composable
fun ModifyUserStoryDialog(
    userStory: UserStory?,
    onDismiss: () -> Unit,
    onModifyUserStory: (UserStory) -> Unit
) {
    var description by remember { mutableStateOf(userStory?.description ?: "") }
    var id by remember { mutableStateOf(userStory?.id ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modify User Story") },
        text = {
            Column {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                userStory?.let {
                    val updatedUserStory = it.copy(description = description, id = id)
                    onModifyUserStory(updatedUserStory)
                }
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}