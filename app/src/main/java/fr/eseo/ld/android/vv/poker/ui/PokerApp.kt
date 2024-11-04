package fr.eseo.ld.android.vv.poker.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import fr.eseo.ld.android.vv.poker.ui.composable.TopAppBar
import fr.eseo.ld.android.vv.poker.ui.navigation.PokerScreens
import fr.eseo.ld.android.vv.poker.ui.screens.ConnectionScreen
import fr.eseo.ld.android.vv.poker.ui.screens.MainScreen
import fr.eseo.ld.android.vv.poker.ui.screens.TeamScreen
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.vv.poker.ui.viewmodels.TeamViewModel

@Composable
fun PokerApp(
    navController: NavHostController = rememberNavController(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    teamViewModel: TeamViewModel = hiltViewModel()
) {
    Log.v("PokerApp", "PokerApp")
    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            Log.v("PokerApp", "start")
            val user by authenticationViewModel.user.observeAsState()
            LaunchedEffect(user) {
                if(user == null) {
                    Log.v("PokerApp", "loginAnonymously")
                    authenticationViewModel.loginAnonymously()
                } else {
                    Log.v("PokerApp", "navigate to connection")
                    navController.navigate(PokerScreens.CONNECTION.id) {
                        popUpTo("start"){inclusive = true}
                    }
                }
            }
        }
        composable(PokerScreens.CONNECTION.id) {
            ConnectionScreen(navController,authenticationViewModel)
        }
        composable(PokerScreens.MAIN.id) {
            TopAppBar(
                viewModel = authenticationViewModel,
                onLogout = { authenticationViewModel.logout() },
                navController = navController
            )
            MainScreen(
                onLogout = { authenticationViewModel.logout() },
                navController = navController,
                viewModel = authenticationViewModel
            )
        }
        composable(PokerScreens.TEAMS.id) {
            TeamScreen(
                navController = navController,
                teamViewModel = teamViewModel,
                authenticationViewModel = authenticationViewModel
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PokerAppPreview() {
        PokerApp()
}
