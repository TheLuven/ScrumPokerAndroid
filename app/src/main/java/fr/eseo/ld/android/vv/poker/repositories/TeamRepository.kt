package fr.eseo.ld.android.vv.poker.repositories

import androidx.activity.result.launch
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.model.UserStory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TeamRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userStoryRepository: UserStoryRepository
) {

    suspend fun updateTeam(team: Team) {
        db.collection("teams")
            .document(team.id)
            .set(team) // Or use update() if you only want to update specific fields
            .await()
    }

    suspend fun getTeams(): List<Team> {
        return db.collection("teams")
            .get()
            .await()
            .documents
            .mapNotNull { document ->
                document.toObject(Team::class.java)
            }
    }

    suspend fun addTeam(team: Team) {
        db.collection("teams")
            .document(team.id)
            .set(team)
            .await()
    }

    suspend fun deleteTeam(teamId: String) {
        db.collection("teams")
            .document(teamId)
            .delete()
            .await()
    }

    suspend fun addUserToTeam(teamId: String, userEmail: String) {
        val teamRef = db.collection("teams").document(teamId)
        teamRef.get().addOnSuccessListener { document ->
            val team = document.toObject(Team::class.java)
            if (team != null) {
                val updatedMembers = team.members.toMutableList()
                if (!updatedMembers.contains(userEmail)) {
                    updatedMembers.add(userEmail)
                    teamRef.update("members", updatedMembers)
                }
            }
        }
    }

    suspend fun removeUserFromTeam(teamId: String, userEmail: String) {
        val teamRef = db.collection("teams").document(teamId)
        teamRef.get().addOnSuccessListener { document ->
            val team = document.toObject(Team::class.java)
            if (team != null) {
                val updatedMembers = team.members.toMutableList()
                updatedMembers.remove(userEmail)
                teamRef.update("members", updatedMembers)
            }
        }
    }

    suspend fun addUserStoryToTeam(teamId: String, userStoryId: String) {
        val teamRef = db.collection("teams").document(teamId)
        teamRef.update("userStories", FieldValue.arrayUnion(userStoryId)).await()
    }

    suspend fun removeUserStoryFromTeam(teamId: String, userStoryId: String) {
        val teamRef = db.collection("teams").document(teamId)
        teamRef.update("userStories", FieldValue.arrayRemove(userStoryId)).await()
    }

}