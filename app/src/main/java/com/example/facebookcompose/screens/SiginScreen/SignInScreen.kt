package com.example.facebookcompose.screens.SiginScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Facebook
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun SignInScreen(navigateToHome:()->Unit) {
val context=LocalContext.current
    Box(
        Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxSize()
    ) {

        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.Facebook,
                contentDescription = null,
                modifier = Modifier.size(90.dp),
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Sign In With Facebook", style = MaterialTheme.typography.body1)
            SiginButton(
                onSiginFailed = {
                    Toast.makeText(context,"It does not  works",Toast.LENGTH_LONG).show()

                },
                onSiginedIn = {
                    navigateToHome
                }
            )

        }
    }
}

@Composable
fun SiginButton(
    onSiginFailed: (java.lang.Exception) -> Unit, onSiginedIn: () -> Unit
) {
    val scope = rememberCoroutineScope()
    AndroidView({
        LoginButton(it).apply {
            setPermissions("email", "public_profile")
            val mCallbackManager = CallbackManager.Factory.create()
            registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    //TODO Nothing
                }

                override fun onError(error: FacebookException) {
                    onSiginFailed(error)
                }

                override fun onSuccess(result: LoginResult) {
                    scope.launch {
                        val token = result.accessToken.token
                        val credential = FacebookAuthProvider.getCredential(token)
                        val authResult = Firebase.auth.signInWithCredential(credential).await()
                        if (authResult.user != null) {
                            onSiginedIn()
                        } else {
                            onSiginFailed(java.lang.RuntimeException("Could not Sign in with Firebase"))
                        }
                    }
                }

            })
        }
    })
}
