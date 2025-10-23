package com.zenonewrong.ui.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.zenonewrong.R
import com.zenonewrong.Screen
import com.zenonewrong.ui.screen.classify.ClassifyScreenTopBar
import com.zenonewrong.ui.screen.home.HomeScreen
import com.zenonewrong.ui.screen.iteminfo.AddItemScreenTopBar
import com.zenonewrong.ui.screen.profile.ProfileScreen
import com.zenonewrong.viewmodel.AppViewModel

// 定义 screenMap，存储 route 和对应的 Composable
val screenMap = mapOf<String, @Composable () -> Unit>(
    Screen.Home.route to { HomeScreen() },
    Screen.Profile.route to { ProfileScreen() }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(screen: Screen){
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding().height(50.dp).fillMaxHeight(),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(screen.resourceId),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor =  Color.White ,
            titleContentColor =  Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val appViewModel: AppViewModel =
        viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)
//    var currentTab by remember { mutableStateOf(Screen.Home.route) }
    val currentTab = appViewModel.currentTab
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    val items = listOf(Screen.Home, Screen.Profile)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
//    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
//    val isMainTabRoute = currentDestination?.route in listOf("home", "search", "profile")

    Scaffold(
        topBar = {
            when(navController.currentDestination?.route) {
//                Screen.AddItem.route -> AddItemScreenTopBar(navController)
//                Screen.Classify.route -> ClassifyScreenTopBar(navController)
                else -> AppTopBar(currentScreen)
            }
        },
        floatingActionButton = {
//            if (isMainTabRoute) {
                FloatingActionButton(
                    contentColor = MaterialTheme.colorScheme.surface,
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        appViewModel.navigateTo(Screen.Main.route,Screen.AddItem.route)
                              },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加")
                }
//            }
        },
        bottomBar = {

                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(56.dp)
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
                                            modifier = Modifier.size(28.dp)
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
        Box(modifier = Modifier.padding(innerPadding)) {
            screenMap[currentTab]?.invoke()
        }
    }
}
