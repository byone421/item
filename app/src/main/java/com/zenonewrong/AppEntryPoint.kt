package com.zenonewrong

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zenonewrong.ui.screen.MainScreen
import com.zenonewrong.ui.screen.about.AboutScreen
import com.zenonewrong.ui.screen.classify.ClassifyScreen
import com.zenonewrong.ui.screen.home.HomeScreen
import com.zenonewrong.ui.screen.iteminfo.AddItemScreen
import com.zenonewrong.ui.screen.profile.ExpiryDaysSettingScreen
import com.zenonewrong.ui.screen.profile.ProfileScreen
import com.zenonewrong.ui.screen.search.SearchScreen
import com.zenonewrong.ui.screen.store.DataImportScreen
import com.zenonewrong.ui.screen.store.DataStoreScreen
import com.zenonewrong.viewmodel.AppViewModel


sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector? = null
) {
    object Home : Screen("home", R.string.home, Icons.Filled.Home)
    object Profile : Screen("profile", R.string.profile, Icons.Filled.Person)
    object About : Screen("about", R.string.profile)
    object AddItem : Screen("add", R.string.add)
    object Main : Screen("main", R.string.home)
    object Classify : Screen("classify", R.string.classify)
    object ExpiryDaysSetting : Screen("expiry_days_setting", R.string.expiry_days_setting)
    object Search : Screen("search", R.string.search)
    object DateStore : Screen("data_store", R.string.data_store)
    object DataImport : Screen("data_import", R.string.data_import)
}

@Composable
fun AppEntryPoint() {
    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel(viewModelStoreOwner = LocalActivity.current as ComponentActivity)

    LaunchedEffect(Unit) {
        appViewModel.navigationEvent.collect { route ->
            navController.navigate(route){
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    // 处理返回事件
    LaunchedEffect(Unit) {
        appViewModel.backEvent.collect {
                // 自定义返回逻辑
            navController.popBackStack()
        }
    }
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") { MainScreen(navController) }
        composable("${Screen.AddItem.route}?id={id}&copy={copy}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull()
            val isCopy = backStackEntry.arguments?.getString("copy")?.toBoolean() ?: false
            AddItemScreen(id, isCopy)
        }
        composable(Screen.Classify.route) { ClassifyScreen() }
        composable(Screen.ExpiryDaysSetting.route) { ExpiryDaysSettingScreen() }
        composable("${Screen.Search.route}?title={title}&days={days}&classifyId={classifyId}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "搜索"
            val days = backStackEntry.arguments?.getString("days")
            val classifyId = backStackEntry.arguments?.getString("classifyId")?.toLongOrNull()
            SearchScreen(title, days, classifyId)
        }
        composable(Screen.DateStore.route) { DataStoreScreen() }
        composable(Screen.DataImport.route) { DataImportScreen() }
        composable(Screen.About.route) { AboutScreen() }
    }
//    navController.navigate("main")
//    MainScreen(navController)
}