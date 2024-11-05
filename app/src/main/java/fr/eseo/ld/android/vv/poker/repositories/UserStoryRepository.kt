package fr.eseo.ld.android.vv.poker.repositories

import com.google.firebase.firestore.FirebaseFirestore
import fr.eseo.ld.android.vv.poker.model.UserStory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.io.path.exists

class UserStoryRepository @Inject constructor (private val db: FirebaseFirestore) {

    suspend fun addUserStory(userStory: UserStory): String {
        val newStoryRef = db.collection("userStories").document()
        newStoryRef.set(userStory).await()
        return newStoryRef.id
    }
    suspend fun getAllUserStories(): List<UserStory> {
        return suspendCoroutine { continuation ->
            db.collection("userStories").get()
                .addOnSuccessListener { querySnapshot ->
                    val userStories = querySnapshot.documents.mapNotNull { it.toObject(UserStory::class.java) }
                    continuation.resume(userStories)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
    suspend fun updateUserStory(userStory: UserStory) {
        val userStoryRef = db.collection("userStories").document(userStory.id)
        userStoryRef.update(
            mapOf(
                "description" to userStory.description,
                "userPoints" to userStory.userPoints
                // ... other fields to update
            )
        ).await()
    }

    suspend fun removeUserStory(userStoryId: String) {
        db.collection("userStories").document(userStoryId).delete().await()
    }

    suspend fun getUserStory(userStoryId: String): UserStory? {
        val userStoryDoc = db.collection("userStories").document(userStoryId).get().await()
        return if (userStoryDoc.exists()) {
            userStoryDoc.toObject(UserStory::class.java)
        } else {
            null
        }
    }
}