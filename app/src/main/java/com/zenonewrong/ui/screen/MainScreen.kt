package com.zenonewrong.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.zenonewrong.Screen
import com.zenonewrong.ui.screen.classify.ClassifyScreen
import com.zenonewrong.ui.screen.components.Topbar
import com.zenonewrong.ui.screen.home.HomeScreen
import com.zenonewrong.ui.screen.profile.ProfileScreen
import com.zenonewrong.viewmodel.AppViewModel

// 定义 screenMap，存储 route 和对应的 Composable
val screenMap = mapOf<String, @Composable () -> Unit>(
    Screen.Home.route to { HomeScreen() },
    Screen.Classify.route to { ClassifyScreen() },
    Screen.Profile.route to { ProfileScreen() }
)

@Composable
fun MainScreen(navController: NavController) {
    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
//    var currentTab by remember { mutableStateOf(Screen.Home.route) }
    val currentTab = appViewModel.currentTab
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val items = listOf(Screen.Home, Screen.Classify, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
//    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
//    val isMainTabRoute = currentDestination?.route in listOf("home", "search", "profile")

    Scaffold(
//        topBar = {
//            if (currentTab == Screen.Profile.route) {
//                Topbar(
//                    appViewModel = appViewModel,
//                    title = stringResource(Screen.Profile.resourceId),
//                    showBack = false
//                )
//            }
//        },
        floatingActionButton = {
            if (currentTab == Screen.Home.route) {
                FloatingActionButton(
                    modifier = Modifier.size(62.dp),
                    contentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        appViewModel.navigateTo(Screen.Main.route,Screen.AddItem.route)
                              },
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加")
                }
            }
        },
        bottomBar = {

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(68.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.height(56.dp) // 标准导航栏高度
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Icon(
                                            screen.icon!!,
                                            contentDescription = "Home",
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Text(stringResource(screen.resourceId), fontSize = 12.sp)
                                    }
                                }
                            },
                            selected = currentTab == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                // 选中状态颜色
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = Color.Transparent,
                                // 未选中状态颜色
                                unselectedIconColor = MaterialTheme.colorScheme.inversePrimary,
                                unselectedTextColor = MaterialTheme.colorScheme.inversePrimary
                            ),
                            onClick = {
                                currentScreen = screen
                                appViewModel.currentTab = screen.route
                            }
                        )
                    }
                }

        }
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val contentPadding = if (currentTab == Screen.Classify.route || currentTab == Screen.Profile.route ) {
            PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = 0.dp,
                end = innerPadding.calculateEndPadding(layoutDirection),
                bottom = innerPadding.calculateBottomPadding()
            )
        } else {
            innerPadding
        }

        Box(modifier = Modifier.padding(contentPadding)) {
            screenMap[currentTab]?.invoke()
        }
    }
}
