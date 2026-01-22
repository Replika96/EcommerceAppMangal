package com.vadim.manganal.Navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vadim.manganal.R
import com.vadim.manganal.ui.ViewModel.FavoritesViewModel
import com.vadim.manganal.ui.ViewModel.RegistrationViewModel
import com.vadim.manganal.ui.screens.CheckoutScreen
import com.vadim.manganal.ui.screens.ContactsScreen
import com.vadim.manganal.ui.screens.EditAddressScreen
import com.vadim.manganal.ui.screens.EditPersonalInfoScreen
import com.vadim.manganal.ui.screens.LoginScreen
import com.vadim.manganal.ui.theme.ViewModel.CartViewModel
import com.vadim.manganal.ui.ViewModel.MangalViewModel
import com.vadim.manganal.ui.theme.ViewModel.ProductDetailsViewModel
import com.vadim.manganal.ui.theme.screens.AdminScreen
import com.vadim.manganal.ui.theme.screens.CartScreen
import com.vadim.manganal.ui.theme.screens.FavoritesScreen
import com.vadim.manganal.ui.theme.screens.HomeScreen
import com.vadim.manganal.ui.theme.screens.ProductDetailsScreen
import com.vadim.manganal.ui.screens.ProfileScreen
import com.vadim.manganal.ui.screens.PurchaseHistoryScreen
import com.vadim.manganal.ui.screens.RegistrationScreen

sealed class NavigationItem(val route: String, val label: String ="", val icon: Int = R.drawable.ic_lock) {
    object Home : NavigationItem("home", "Главная", R.drawable.ic_home)
    object Favorites : NavigationItem("favorites", "Избранное", R.drawable.ic_favorite)
    object Cart : NavigationItem("cart", "Корзина", R.drawable.ic_shopping_cart)
    object Profile : NavigationItem("profile", "Профиль", R.drawable.ic_user_fill)
    object Admin : NavigationItem("admin", "Админка")
    object Registration: NavigationItem("registration")
    object Checkout: NavigationItem("checkout")
    object Login : NavigationItem("login")
    object PersonalIndoEdit : NavigationItem("edit_personal_info")
    object EditAddress : NavigationItem("edit_address")
    object Contacts : NavigationItem("contacts")
    object PurchaseHistoryScreen: NavigationItem("history")

}
sealed class Screen(val route: String) {
    object ProductDetails : Screen("product_details/{id}") {
        fun createRoute(id: String) = "product_details/$id"
    }
}
@Composable
fun NavigationGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.Home.route,
        modifier = Modifier.padding(innerPadding)
    ) {

        //HOME ---------------------------------------------------------------------
        composable(NavigationItem.Home.route) {
            val mangalViewModel: MangalViewModel = hiltViewModel()
            val cartViewModel: CartViewModel = hiltViewModel()
            val favoritesViewModel: FavoritesViewModel = hiltViewModel()
            HomeScreen(
                mangalViewModel = mangalViewModel,
                cartViewModel = cartViewModel,
                onDetailsClick = { id: String ->
                    navController.navigate(Screen.ProductDetails.createRoute(id))
                },
                favoritesViewModel = favoritesViewModel
            )
        }

        // PRODUCT ---------------------------------------------------------------------
        composable(
            route = Screen.ProductDetails.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("id") /*backStackEntry содержит информацию о текущем маршруте
                                                                                arguments хранят параметры из URL
                                                                                getString("id") извлекает значение параметра id */
            val productDetailsViewModel: ProductDetailsViewModel = hiltViewModel()
            val cartViewModel: CartViewModel = hiltViewModel()
            val favoritesViewModel: FavoritesViewModel = hiltViewModel()
            productId?.let {
                ProductDetailsScreen(
                    productId = it,
                    productViewModel = productDetailsViewModel,
                    cartViewModel = cartViewModel,
                    favoritesViewModel = favoritesViewModel,
                    onBack = { navController.popBackStack() },
                    onCheckoutClick = {navController.navigate(NavigationItem.Checkout.route)}
                )
            }
        }

        // FAVORITE ---------------------------------------------------------------------
        composable(NavigationItem.Favorites.route) {
            val mangalViewModel: MangalViewModel = hiltViewModel()
            val favoritesViewModel: FavoritesViewModel = hiltViewModel()
            FavoritesScreen(mangalViewModel,
                favoritesViewModel = favoritesViewModel,
                onDetailsClick = { id: String ->
                    navController.navigate(Screen.ProductDetails.createRoute(id))
                })

        }

        // CART ---------------------------------------------------------------------
        composable(NavigationItem.Cart.route) {
            CartScreen(onBack = { navController.popBackStack()},
            onCheckoutClick = {
                navController.navigate(NavigationItem.Checkout.route)
            },
            cartViewModel = hiltViewModel()) }
        // CHECKOUT ---------------------------------------------------------------------
        composable(NavigationItem.Checkout.route) { backStackEntry ->
            val regVm: RegistrationViewModel = hiltViewModel(backStackEntry)
            val cartVm: CartViewModel        = hiltViewModel(backStackEntry)

            CheckoutScreen(
                onBack          = { navController.popBackStack() },
                cartViewModel   = cartVm,
                regViewModel    = regVm,
                onOrderConfirm  = { /* … */ },
                onEditAddress = { navController.navigate(NavigationItem.EditAddress.route) }
            )
        }

        // PROFILE ---------------------------------------------------------------------
        composable(NavigationItem.Profile.route) {
            val registrationViewModel: RegistrationViewModel = hiltViewModel()
            ProfileScreen(
                viewModel = registrationViewModel,
                onAdminClick = { navController.navigate(NavigationItem.Admin.route) },
                onRegistrationClick = {
                    navController.navigate(NavigationItem.Registration.route) {
                        launchSingleTop = true          // не создаёт дубликатов
                    }
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onEditPersonalInfo = { navController.navigate(NavigationItem.PersonalIndoEdit.route) },
                onEditAddress = { navController.navigate(NavigationItem.EditAddress.route) },
                onContact = { navController.navigate(NavigationItem.Contacts.route) },
                onHistory = { navController.navigate(NavigationItem.PurchaseHistoryScreen.route) }
            )

        }
        // PERSONAL ---------------------------------------------------------------------
        composable(NavigationItem.PersonalIndoEdit.route) {
            val viewModel: RegistrationViewModel = hiltViewModel()
            EditPersonalInfoScreen(viewModel = viewModel,
                onBack = { navController.popBackStack()} )
        }
        // EDIT ---------------------------------------------------------------------
        composable(NavigationItem.EditAddress.route) {
            val registrationViewModel: RegistrationViewModel = hiltViewModel()
            EditAddressScreen(
                viewModel = registrationViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // HISTORY ---------------------------------------------------------------------
        composable(NavigationItem.PurchaseHistoryScreen.route) {
            PurchaseHistoryScreen(onBack = { navController.popBackStack()} )
        }

        // ADMIN ---------------------------------------------------------------------
        composable(NavigationItem.Admin.route) {
            AdminScreen(
                mangalViewModel = hiltViewModel(),
                imageViewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        // REGISTRATION --------------------------------------------------------------
        composable(NavigationItem.Registration.route) { backStackEntry ->
            val vm: RegistrationViewModel = hiltViewModel(backStackEntry)

            RegistrationScreen(
                authViewModel = vm,
                onNavigateToHome = {
                    navController.navigate(NavigationItem.Profile.route) {
                        popUpTo(NavigationItem.Login.route) {
                            inclusive = true
                        }
                        popUpTo(NavigationItem.Registration.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onLoginClick = {
                    navController.navigate(NavigationItem.Login.route) {
                        launchSingleTop = true                               // без дубликатов
                    }
                }
            )
        }

        // LOGIN ---------------------------------------------------------------------
        composable(NavigationItem.Login.route) { backStackEntry ->
            val vm: RegistrationViewModel = hiltViewModel(backStackEntry)
            LoginScreen(
                authViewModel = vm,
                onNavigateToHome = {
                    navController.navigate(NavigationItem.Profile.route) {
                        popUpTo(NavigationItem.Login.route) {
                            inclusive = true
                        }
                        popUpTo(NavigationItem.Registration.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegistration = {
                    navController.navigate(NavigationItem.Registration.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        // Contact ---------------------------------------------------------------------
        composable(NavigationItem.Contacts.route
        ) { backStackEntry ->
            ContactsScreen(onNavigateToHome = { navController.popBackStack() })
        }
    }
}



