package fr.eseo.ld.android.vv.poker.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import fr.eseo.ld.android.vv.poker.ui.navigation.PokerScreens
import fr.eseo.ld.android.vv.poker.ui.viewmodels.AuthenticationViewModel
import fr.eseo.ld.android.vv.poker.ui.viewmodels.LoginResult

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    navController: NavHostController,
    authenticationViewModel: AuthenticationViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginResult by authenticationViewModel.loginResult.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginResult) {
        when (loginResult) {
            is LoginResult.Success -> {
                snackbarHostState.showSnackbar(
                    message = "Login successful!",
                    duration = SnackbarDuration.Short
                )
                Log.v("ConnectionScreen", "navigate to Main menu")
                navController.navigate(PokerScreens.MAIN.id)
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display error message if loginResult is Error
            if (loginResult is LoginResult.Error) {
                Text(
                    text = (loginResult as LoginResult.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    authenticationViewModel.login(email, password)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authenticationViewModel.signUp(email, password)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up")
            }
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}