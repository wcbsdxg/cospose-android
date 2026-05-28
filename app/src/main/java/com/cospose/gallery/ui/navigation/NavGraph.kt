package com.cospose.gallery.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cospose.gallery.ui.auth.LoginScreen
import com.cospose.gallery.ui.auth.RegisterScreen
import com.cospose.gallery.ui.board.BoardDetailScreen
import com.cospose.gallery.ui.board.BoardListScreen
import com.cospose.gallery.ui.compare.CompareScreen
import com.cospose.gallery.ui.detail.DetailScreen
import com.cospose.gallery.ui.duplicates.DuplicatesScreen
import com.cospose.gallery.ui.home.HomeScreen
import com.cospose.gallery.ui.preview.PreviewScreen
import com.cospose.gallery.ui.search.SearchScreen
import com.cospose.gallery.ui.settings.SettingsScreen
import com.cospose.gallery.ui.smartfolder.SmartFolderDetailScreen
import com.cospose.gallery.ui.smartfolder.SmartFolderListScreen
import com.cospose.gallery.ui.upload.UploadScreen

sealed class Screen(val route: String, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    data object Home : Screen("home", "首页", Icons.Filled.Home, Icons.Outlined.Home)
    data object Search : Screen("search", "搜索", Icons.Filled.Search, Icons.Outlined.Search)
    data object Profile : Screen("profile", "我的", Icons.Filled.Person, Icons.Outlined.Person)
    data object Settings : Screen("settings", "设置", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val bottomNavItems = listOf(Screen.Home, Screen.Search, Screen.Profile, Screen.Settings)

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route?.let { route ->
        bottomNavItems.any { it.route == route }
    } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onImageClick = { imageId -> navController.navigate("image/$imageId") },
                    onUploadClick = { navController.navigate("upload") },
                    onPreviewClick = { startIndex, imageIds ->
                        navController.navigate("preview/$startIndex?imageIds=$imageIds")
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onImageClick = { imageId -> navController.navigate("image/$imageId") }
                )
            }

            composable(Screen.Profile.route) {
                BoardListScreen(
                    onBoardClick = { boardId -> navController.navigate("board/$boardId") }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen()
            }

            composable(
                route = "image/{imageId}",
                arguments = listOf(navArgument("imageId") { type = NavType.StringType })
            ) { backStackEntry ->
                val imageId = backStackEntry.arguments?.getString("imageId") ?: return@composable
                DetailScreen(
                    imageId = imageId,
                    onBack = { navController.popBackStack() },
                    onImageClick = { id -> navController.navigate("image/$id") },
                    onPreviewClick = { startIndex, imageIds ->
                        navController.navigate("preview/$startIndex?imageIds=$imageIds")
                    },
                    onCompare = { id1, id2 ->
                        navController.navigate("compare/$id1/$id2")
                    }
                )
            }

            composable("upload") {
                UploadScreen(
                    onBack = { navController.popBackStack() },
                    onUploaded = { navController.popBackStack() }
                )
            }

            composable(
                route = "preview/{startIndex}?imageIds={imageIds}",
                arguments = listOf(
                    navArgument("startIndex") { type = NavType.IntType },
                    navArgument("imageIds") { type = NavType.StringType }
                )
            ) {
                PreviewScreen(
                    onBack = { navController.popBackStack() },
                    onImageClick = { imageId -> navController.navigate("image/$imageId") }
                )
            }

            composable(
                route = "board/{boardId}",
                arguments = listOf(navArgument("boardId") { type = NavType.StringType })
            ) { backStackEntry ->
                val boardId = backStackEntry.arguments?.getString("boardId") ?: return@composable
                BoardDetailScreen(
                    boardId = boardId,
                    onBack = { navController.popBackStack() },
                    onImageClick = { imageId -> navController.navigate("image/$imageId") }
                )
            }

            // Smart Folders
            composable("smart_folders") {
                SmartFolderListScreen(
                    onFolderClick = { folderId -> navController.navigate("smart_folder/$folderId") },
                    onCreateFolder = { /* TODO: show create sheet */ }
                )
            }

            composable(
                route = "smart_folder/{folderId}",
                arguments = listOf(navArgument("folderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getString("folderId") ?: return@composable
                SmartFolderDetailScreen(
                    folderId = folderId,
                    onBack = { navController.popBackStack() },
                    onImageClick = { imageId -> navController.navigate("image/$imageId") }
                )
            }

            // Duplicates
            composable("duplicates") {
                DuplicatesScreen(
                    onBack = { navController.popBackStack() },
                    onImageClick = { imageId -> navController.navigate("image/$imageId") },
                    onCompare = { id1, id2 -> navController.navigate("compare/$id1/$id2") }
                )
            }

            // Compare
            composable(
                route = "compare/{imageId1}/{imageId2}",
                arguments = listOf(
                    navArgument("imageId1") { type = NavType.StringType },
                    navArgument("imageId2") { type = NavType.StringType }
                )
            ) {
                CompareScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // Auth routes
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },
                    onSkip = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
