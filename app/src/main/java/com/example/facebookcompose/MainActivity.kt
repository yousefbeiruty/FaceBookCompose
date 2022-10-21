package com.example.facebookcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.facebookcompose.screens.HomeScreen.HomeScreen
import com.example.facebookcompose.screens.SiginScreen.SignInScreen
import com.example.facebookcompose.ui.theme.FacebookComposeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FacebookComposeTheme {
                TransParentSystemBars()
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen {
                            navController.navigate("signin") {
                                popUpTo("home") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    composable("signin") {
                        SignInScreen(navigateToHome={
                            navController.navigate("home"){
                                popUpTo("signin"){
                                    inclusive=true
                                }
                            }
                        })
                    }
                }


            }
        }
    }
}

@Composable
fun TransParentSystemBars() {
// Remember a SystemUiController
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight

    DisposableEffect(systemUiController, useDarkIcons) {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, darkIcons = useDarkIcons
        )

        // setStatusBarColor() and setNavigationBarColor() also exist

        onDispose {}
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

