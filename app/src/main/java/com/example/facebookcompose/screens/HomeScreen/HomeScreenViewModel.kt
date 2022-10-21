package com.example.facebookcompose.screens.HomeScreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    object SignInRequired : HomeScreenState()
    data class Loaded(
        val posts: kotlin.collections.List<Post>, val avatarUrl: String
    ) : HomeScreenState()
}

class HomeScreenViewModel : ViewModel() {
    private val mutableState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)

    val state = mutableState.asStateFlow()

    val textState = MutableStateFlow("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val currentUser = Firebase.auth.currentUser
            //TODO check for user signed in
            if (currentUser != null) {
                observePosts(currentUser)
            } else {
                mutableState.emit(HomeScreenState.SignInRequired)
            }
        }
    }

    fun onTextChange(text: String) {
        viewModelScope.launch {
            textState.emit(text)
        }
    }

    fun onSendClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val post=textState.value
            val currentUser= requireNotNull(Firebase.auth.currentUser){
                "Tried to crate post without a signed in  user"
            }
            Firebase.firestore.collection("posts").add(
                hashMapOf(
                    "text" to post,
                    "date_posted" to Date(),
                    "author_name" to currentUser.displayName.orEmpty(),
                    "author_avatar_url" to getAvatar(currentUser)
                )
            )
        }
    }

    private suspend fun observePosts(currentUser: FirebaseUser?) {
        observePost().map {
            HomeScreenState.Loaded(
                avatarUrl = getAvatar(currentUser!!), posts = it
            )
        }.collect {
            mutableState.emit(it)
        }
    }

    private fun observePost(): Flow<List<Post>> {
        return callbackFlow<List<Post>> {
            val listener =
                Firebase.firestore.collection("posts").addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                    } else if (value != null) {
                        val posts = value.map {
                            Post(
                                text = it.getString("text").orEmpty(),
                                timestamp = it.getDate("date_posted") ?: Date(),
                                authorName = it.getString("author_name").orEmpty(),
                                authorAvatarUrl = it.getString("author_avatar_url").orEmpty()
                            )

                        }.sortedByDescending { it.timestamp }
                        trySend(posts)
                    }
                }
            awaitClose {
                listener.remove()
            }
        }
    }

    private fun getAvatar(currentUser: FirebaseUser): String {
        val accessToken = AccessToken.getCurrentAccessToken()?.token
        return "${requireNotNull(currentUser.photoUrl)}?.access_token=$accessToken&type=large"
    }


}
