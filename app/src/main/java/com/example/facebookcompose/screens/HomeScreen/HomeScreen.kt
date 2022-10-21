package com.example.facebookcompose.screens.HomeScreen

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.facebookcompose.ui.theme.ButtonGray
import com.example.facebookcompose.ui.theme.FacebookComposeTheme
import com.facebook.R
import java.util.Date

@Composable
fun HomeScreen(
    navigateToSigIn: (() -> Unit)?
) {
    val viewModel = viewModel<HomeScreenViewModel>()
    val state by viewModel.state.collectAsState()
    when (state) {
        is HomeScreenState.Loading -> LoadingScreen()
        is HomeScreenState.Loaded -> HomeScreenContent(posts = (state as HomeScreenState.Loaded).posts,
            avatarUrl = (state as HomeScreenState.Loaded).avatarUrl,
            onTextChanged = {
                viewModel.onTextChange(it)
            },
            onSendClick = {
                viewModel.onSendClick()
            })
        is HomeScreenState.SignInRequired -> LaunchedEffect(Unit) { navigateToSigIn?.invoke() }
    }

}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenContent(
    posts: List<Post>, avatarUrl: String, onTextChanged: (String) -> Unit, onSendClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()

    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 40.dp)) {
            item {
                TopAppBar()
            }

            stickyHeader {
                TabBar()
            }

            item {
                StatusUpdateBar(
                    avatarUrl = avatarUrl,
                    onTextChange = onTextChanged,
                    onSendClick = onSendClick,
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                StorySection(avatarUrl)
            }
            items(posts) { post ->
                Spacer(modifier = Modifier.height(8.dp))
                PostCard(post)
                Spacer(modifier = Modifier.height(8.dp))

            }

        }

    }
}

@Composable
fun StorySection(avatarUrl: String) {
    Surface {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
        ) {
            item {
                CreateStoryCard(
                    avatarUrl = avatarUrl
                )
            }
        }
    }
}

@Composable
fun CreateStoryCard(avatarUrl: String) {
    Card(Modifier.size(140.dp, 220.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(avatarUrl).crossfade(true)
                    .placeholder(com.example.facebookcompose.R.drawable.ic_placeholder).build(),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RectangleShape)
            )
            var bgHeight by remember {
                mutableStateOf(0.dp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .clip(CircleShape)
                    .height(bgHeight-19.dp)
                    .align(Alignment.BottomCenter),
            )
            val density= LocalDensity.current
            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                 //   .padding(vertical = 8.dp, horizontal = 24.dp)
                    .onGloballyPositioned {
                        bgHeight=with(density){
                            it.size.height.toDp()
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .border(1.dp,MaterialTheme.colors.surface, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(id = com.example.facebookcompose.R.string.create_story)
                    , tint = MaterialTheme.colors.onPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(id = com.example.facebookcompose.R.string.create_story),
                    textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }
}

@Composable
fun PostCard(post: Post) {
    Surface {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(post.authorAvatarUrl)
                        .crossfade(true)
                        .placeholder(com.example.facebookcompose.R.drawable.ic_placeholder).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium)
                    )
                    val today = remember {
                        Date()
                    }
                    Text(
                        text = dateLabel(timestamp = post.timestamp, today = today),
                        color = MaterialTheme.colors.onSurface.copy(
                            alpha = 0.44f
                        )
                    )

                }
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Rounded.MoreHoriz,
                        contentDescription = stringResource(id = com.example.facebookcompose.R.string.menu)
                    )
                }
            }

            Text(
                text = post.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun dateLabel(timestamp: Date, today: Date): String {
    return if (today.time - timestamp.time < 2 * DateUtils.MINUTE_IN_MILLIS) {
        stringResource(id = com.example.facebookcompose.R.string.just_now)
    } else if (timestamp.time - today.time < DateUtils.MINUTE_IN_MILLIS) {
        DateUtils.getRelativeTimeSpanString(
            timestamp.time, today.time, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_SHOW_DATE
        ).toString()
    } else {
        ""
    }

}


data class TabItem(
    val icon: ImageVector, val contentDescription: String
)

@Composable
private fun TopAppBar() {
    Surface() {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(com.example.facebookcompose.R.string.app_name),
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { /*TODO*/ }, modifier = Modifier
                    .clip(CircleShape)
                    .background(ButtonGray)
            ) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.search_menu_title)
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { /*TODO*/ }, modifier = Modifier
                    .clip(CircleShape)
                    .background(ButtonGray)
            ) {
                Icon(
                    Icons.Rounded.ChatBubble,
                    contentDescription = stringResource(R.string.search_menu_title)
                )
            }
        }
    }

}

@Composable
fun TabBar() {
    Surface() {
        var tabIndex by remember {
            mutableStateOf(0)
        }
        TabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val tabs = listOf(
                TabItem(
                    Icons.Rounded.Home, stringResource(com.example.facebookcompose.R.string.home)
                ),
                TabItem(Icons.Rounded.Tv, stringResource(com.example.facebookcompose.R.string.tv)),
                TabItem(
                    Icons.Rounded.Store, stringResource(com.example.facebookcompose.R.string.store)
                ),

                TabItem(
                    Icons.Rounded.Newspaper,
                    stringResource(com.example.facebookcompose.R.string.news)
                ),
                TabItem(
                    Icons.Rounded.Notifications,
                    stringResource(com.example.facebookcompose.R.string.notifications)
                ),
                TabItem(
                    Icons.Rounded.Menu, stringResource(com.example.facebookcompose.R.string.menu)
                ),

                )

            tabs.forEachIndexed { i, item ->
                Tab(
                    selected = tabIndex == i,
                    onClick = { tabIndex = i },
                    modifier = Modifier.heightIn(48.dp)
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.contentDescription,
                        tint = if (tabIndex == i) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.onSurface.copy(0.44f)
                        }
                    )
                }
            }


        }
    }

}

@Composable
fun StatusUpdateBar(
    avatarUrl: String, onTextChange: (String) -> Unit, onSendClick: () -> Unit
) {
    Surface {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)) {
            Row(
                Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(
                        LocalContext.current
                    ).data(avatarUrl).crossfade(true)
                        .placeholder(com.example.facebookcompose.R.drawable.ic_placeholder).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                //           Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = stringResource(id = com.example.facebookcompose.R.string.whats_on_your_mind),
//                    modifier = Modifier.padding(8.dp),
//                    style = MaterialTheme.typography.body1,
//                    color = MaterialTheme.colors.onSurface.copy(
//                        alpha = 0.66f
//                    )
//                )
                var text by remember {
                    mutableStateOf("")
                }
                TextField(modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    value = text,
                    onValueChange = {
                        text = it
                        onTextChange(it)
                    },
                    placeholder = {
                        Text(text = stringResource(id = com.example.facebookcompose.R.string.whats_on_your_mind))
                    },
                    keyboardActions = KeyboardActions(onSend = {
                        onSendClick()
                        text = ""
                    }),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    )
                )
            }
            Divider(modifier = Modifier.fillMaxWidth(), thickness = Dp.Hairline)

            Row() {
                StatusAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.VideoCall,
                    text = stringResource(id = com.example.facebookcompose.R.string.live)
                )
                VerticalDivider(Modifier.height(48.dp), thickness = Dp.Hairline)
                StatusAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.PhotoAlbum,
                    text = stringResource(id = com.example.facebookcompose.R.string.photo)
                )
                VerticalDivider(Modifier.height(48.dp), thickness = Dp.Hairline)
                StatusAction(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Rounded.ChatBubble,
                    text = stringResource(id = com.example.facebookcompose.R.string.discuss)
                )
            }


        }

    }
}

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
    thickness: Dp = 1.dp,
    topIndent: Dp = 0.dp
) {
    val indentMod = if (topIndent.value != 0f) {
        Modifier.padding(top = topIndent)
    } else {
        Modifier
    }
    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    } else {
        thickness
    }
    //TODO see why this not work without specifying height()
    Box(
        modifier
            .then(indentMod)
            .fillMaxHeight()
            .width(targetThickness)
            .background(color = color)
    )
}

@Composable
private fun StatusAction(modifier: Modifier = Modifier, icon: ImageVector, text: String) {
    TextButton(
        modifier = modifier, onClick = { }, colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon, contentDescription = text
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FacebookComposeTheme {
        HomeScreenContent(posts = listOf(), avatarUrl = "", onTextChanged = {

        }, onSendClick = {

        })
    }
}