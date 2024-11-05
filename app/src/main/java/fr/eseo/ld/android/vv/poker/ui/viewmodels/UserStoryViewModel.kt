package fr.eseo.ld.android.vv.poker.ui.viewmodels

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.ld.android.vv.poker.model.UserStory
import fr.eseo.ld.android.vv.poker.repositories.UserStoryRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStoryViewModel @Inject constructor (private val userStoryRepository: UserStoryRepository) :
    ViewModel() {

    fun addUserStory(userStory: UserStory) {
        viewModelScope.launch {
            userStoryRepository.addUserStory(userStory)
        }
    }
    fun updateUserStory(userStory: UserStory) {
        viewModelScope.launch {
            userStoryRepository.updateUserStory(userStory)
        }
    }
    fun removeUserStory(userStoryId: String) {
        viewModelScope.launch {
            userStoryRepository.removeUserStory(userStoryId)
        }
    }
    fun getUserStory(userStoryId: String) {
        viewModelScope.launch {
            userStoryRepository.getUserStory(userStoryId)
        }
    }

    suspend fun getAllUserStories(): List<UserStory> {
        return userStoryRepository.getAllUserStories()
    }
}