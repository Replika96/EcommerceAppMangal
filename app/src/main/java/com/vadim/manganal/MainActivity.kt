package com.vadim.manganal

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vadim.manganal.Navigation.NavigationGraph
import com.vadim.manganal.Navigation.NavigationItem
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.SoftOrange
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            val showBottomBar = currentRoute in listOf(
                NavigationItem.Home.route,
                NavigationItem.Favorites.route,
                NavigationItem.Cart.route,
                NavigationItem.Profile.route
            )

            val view = LocalView.current
            val window = (view.context as Activity).window

            // установка цвета панели и стиля текста в статус-баре
            SideEffect {
                window.statusBarColor = LightBeige.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = true
                }
            }

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavigationBar(navController)
                    }
                }
            ) { innerPadding ->
                NavigationGraph(navController = navController, innerPadding = innerPadding)
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Favorites,
        NavigationItem.Cart,
        NavigationItem.Profile
    )

    NavigationBar(
        containerColor = LightBeige,
        contentColor = DarkBrown,

    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SoftOrange,    // иконка при выборе
                    selectedTextColor = SoftOrange,    // текст при выборе
                    unselectedIconColor = DarkBrown,   // иконка по умолчанию
                    unselectedTextColor = DarkBrown,   // текст по умолчанию
                    indicatorColor = LightBeige),


                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = {
                    val fontFamily = FontFamily(Font(R.font.gilroyblack))
                    Text(item.label, style = TextStyle(
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkBrown
                )
                ) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { //очищает стек навигации до стартового экрана
                                saveState = true //сохраняет состояние экрана
                            }
                            launchSingleTop = true //если экран уже находится на вершине стека, он не будет создаваться заново
                            restoreState = true //восстанавливает состояние экрана, если оно было сохранено
                        }
                    }
                }
            )
        }
    }
}






