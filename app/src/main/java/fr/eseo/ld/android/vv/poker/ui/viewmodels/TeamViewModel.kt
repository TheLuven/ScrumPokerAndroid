package fr.eseo.ld.android.vv.poker.ui.viewmodels

import androidx.activity.result.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.model.UserStory
import fr.eseo.ld.android.vv.poker.repositories.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
) : ViewModel() {

    private val _teams = MutableLiveData<List<Team>>()
    val teams: MutableLiveData<List<Team>> = _teams

    private val _userStories = MutableStateFlow<List<UserStory>>(emptyList())
    val userStories: StateFlow<List<UserStory>> = _userStories.asStateFlow()

    init {
        getTeams()
    }

    fun updateTeam(team: Team) {
        viewModelScope.launch {
            teamRepository.updateTeam(team) // Add updateTeam to repository
        }
    }
    fun getTeams() {
        viewModelScope.launch {
            _teams.value = teamRepository.getTeams()
        }
    }

    fun addTeam(team: Team) {
        viewModelScope.launch {
            teamRepository.addTeam(team)
        }
    }

    fun deleteTeam(teamId: String) {
        viewModelScope.launch {
            teamRepository.deleteTeam(teamId)
        }
    }

    fun addUserToTeam(teamId: String, userEmail: String) {
        viewModelScope.launch {
            teamRepository.addUserToTeam(teamId, userEmail)
        }
    }
    fun removeUserFromTeam(teamId: String, userEmail: String) {
        viewModelScope.launch {
            teamRepository.removeUserFromTeam(teamId, userEmail)
        }
    }

    fun addUserStory(userStory: UserStory, teamId: String) {
        viewModelScope.launch {
            teamRepository.addUserStoryToTeam(teamId = teamId, userStoryId = userStory.id)
        }
    }
    fun removeUserStory(userStory: UserStory, teamId: String) {
        viewModelScope.launch {
            teamRepository.removeUserStoryFromTeam(teamId = teamId, userStoryId = userStory.id)
        }
    }

    fun reloadTeams() {
        viewModelScope.launch {
            _teams.value = teamRepository.getTeams()
        }
    }

    fun getTeam(teamId: String): Team? {
        return _teams.value?.find { it.id == teamId }
    }
}