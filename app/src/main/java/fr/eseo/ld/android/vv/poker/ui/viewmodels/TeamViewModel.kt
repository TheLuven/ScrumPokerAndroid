package fr.eseo.ld.android.vv.poker.ui.viewmodels

import androidx.activity.result.launch
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.eseo.ld.android.vv.poker.model.Team
import fr.eseo.ld.android.vv.poker.repositories.TeamRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val _teams = MutableLiveData<List<Team>>()
    val teams: MutableLiveData<List<Team>> = _teams

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

    fun reloadTeams() {
        viewModelScope.launch {
            _teams.value = teamRepository.getTeams()
        }
    }
}