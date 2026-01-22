package com.vadim.manganal.ui.theme.screens

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vadim.manganal.R
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.entity.ProductState
import com.vadim.manganal.domain.entity.SortOption
import com.vadim.manganal.ui.viewModel.FavoritesViewModel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import com.vadim.manganal.ui.theme.DarkBrown
import com.vadim.manganal.ui.theme.LightBeige
import com.vadim.manganal.ui.theme.LightGray
import com.vadim.manganal.ui.theme.MutedTerracotta
import com.vadim.manganal.ui.theme.SageGreen
import com.vadim.manganal.ui.theme.SoftOrange
import com.vadim.manganal.ui.viewModel.CartViewModel
import com.vadim.manganal.ui.viewModel.MangalViewModel
import kotlinx.coroutines.FlowPreview

@Composable
fun HomeScreen(
    mangalViewModel: MangalViewModel,
    cartViewModel: CartViewModel,
    onDetailsClick: (String) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val state by mangalViewModel.state.collectAsState()
    val favorites by favoritesViewModel.favorites.collectAsState()
    var selectedCategory by remember { mutableStateOf("Все") }
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf(SortOption(SortField.PRICE, SortOrder.ASCENDING)) }

    val categories = remember {
        listOf(
            "Все", "Мангальные зоны", "Костровые чаши", "Флюгера",
            "Адресные таблички", "Мангалы в виде животных", "Дымники",
            "Костровые Сферы", "Дровницы", "Чан банный", "Сотовый стол для лазерного станка"
        )
    }

    when(state) {
        is ProductState.Loading -> {
            CircularProgressIndicator()
            Text("Загрузка...", textAlign = TextAlign.Center ,modifier = Modifier.fillMaxSize())
        }
        is ProductState.Error -> {
            Text(textAlign = TextAlign.Center ,modifier = Modifier.fillMaxSize(), text = "Ошибка: ${(state as ProductState.Error).message}")
        }
        is ProductState.Success -> {
            val products = (state as ProductState.Success).products
            val sortedFilteredProducts by remember(
                products,
                selectedCategory,
                sortOption,
                searchQuery
            ) {
                derivedStateOf {
                    val filtered = if (selectedCategory == "Все") products
                    else products.filter { it.category == selectedCategory }

                    val searched = if (searchQuery.isBlank()) filtered
                    else filtered.filter { product ->
                        product.name.contains(searchQuery, ignoreCase = true) ||
                                product.description?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) ?: false ||
                                product.category.contains(searchQuery, ignoreCase = true)
                    }

                    when (sortOption.field) {
                        SortField.PRICE -> if (sortOption.order == SortOrder.ASCENDING)
                            searched.sortedByDescending { it.price }
                        else
                            searched.sortedBy { it.price }

                        SortField.NAME -> if (sortOption.order == SortOrder.ASCENDING)
                            searched.sortedBy { it.name }
                        else
                            searched.sortedByDescending { it.name }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBeige)
            ) {
                SearchBar(
                    onSearch = { query -> searchQuery = query }
                )
                CategoriesDropdown(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryClick = { selectedCategory = it }
                )
                SortBar(currentSort = sortOption, onSortChange = { sortOption = it })
                ContentGrid(
                    products = sortedFilteredProducts,
                    onDetailsClick = onDetailsClick,
                    cartViewModel = cartViewModel,
                    favoritesViewModel = favoritesViewModel,
                    favorites = favorites
                )
            }
        }

    }
}



@OptIn(FlowPreview::class)
@Composable
fun SearchBar(
    onSearch: (String) -> Unit
) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    var text by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        snapshotFlow { text }
            .debounce(300)
            .distinctUntilChanged()
            .collect { query -> onSearch(query) }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = {
                Text(
                    text = "Поиск по товарам",
                    color = DarkBrown,
                    fontFamily = fontFamily,
                    fontSize = 18.sp
                )
            },
            trailingIcon = {
                Row {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = {
                            text = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Очистить",
                                tint = Color.Gray
                            )
                        }
                    }
                    IconButton(onClick = { onSearch(text) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Поиск"
                        )
                    }
                }
            },
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = Color.Black,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LightGray,
                unfocusedContainerColor = LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 50.dp)
                .clip(RoundedCornerShape(20.dp)),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch(text) }
            )
        )
    }
}


@Composable
fun ContentGrid(
    products: List<Product>,
    onDetailsClick: (String) -> Unit,
    cartViewModel: CartViewModel,
    favoritesViewModel: FavoritesViewModel,
    favorites: List<String>
) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(2.dp),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(products) { product ->
            val cardShape = RoundedCornerShape(20.dp)
            val context = LocalContext.current
            val isFavorite = product.id in favorites
            Card(
                colors = CardDefaults.cardColors(containerColor = LightBeige),
                shape = RectangleShape,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current
                    ) { product.id.let(onDetailsClick) }
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(cardShape)
                    ) {
                        AsyncImage(
                            model = product.imageUrl,
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.ic_error_image)
                        )


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = {

                                    favoritesViewModel.toggle(product.id)
                                    val message = if (isFavorite) {
                                        "Удалено из избранного"
                                    } else {
                                        "Добавлено в избранное"
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(35.dp)
                                    .background(Color.White)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Избранное",
                                    modifier = Modifier.size(28.dp),
                                    tint = if (isFavorite) Color.Red else Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = DarkBrown,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    //цена
                    Text(
                        text = "${product.price} ₽",
                        style = TextStyle(
                            fontSize = 25.sp,
                            color = MutedTerracotta,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            cartViewModel.addItem(product)
                            Toast.makeText(context, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            //.padding(horizontal = 8.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MutedTerracotta),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "В корзину",
                            style = TextStyle(
                                fontFamily = fontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesDropdown(
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit
) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    var expanded by remember { mutableStateOf(false) }


    val backgroundColor by animateColorAsState(
        targetValue = if (expanded) SoftOrange else SageGreen,
        animationSpec = tween(durationMillis = 300), label = "backgroundColor"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {

        TextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(text = "Выберите категорию", fontFamily = fontFamily, fontSize = 17.sp, color = DarkBrown)
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Раскрыть/Скрыть",
                    tint = Color(0xFF6A3E1D),
                    modifier = Modifier.animateContentSize()
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF6A3E1D)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                    enabled = true)
                .shadow(2.dp, shape = RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0.8f)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            textStyle = TextStyle(
                fontFamily = fontFamily,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6A3E1D)
            )
        )


        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(LightBeige)
                .shadow(2.dp, shape = RoundedCornerShape(12.dp))
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category,
                            fontFamily = fontFamily,
                            fontSize = 18.sp,
                            fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal,
                            color = if (category == selectedCategory) Color(0xFF6A3E1D) else DarkBrown
                        )
                    },
                    onClick = {
                        onCategoryClick(category)
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (category == selectedCategory) SoftOrange.copy(alpha = 0.2f) else Color.Transparent
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
enum class SortField { PRICE, NAME }
enum class SortOrder { ASCENDING, DESCENDING }




@Composable
fun SortBar(
    currentSort: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBeige)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Сортировать:",
            color = DarkBrown,
            fontFamily = fontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        SortButton(
            label = "По цене",
            field = SortField.PRICE,
            currentSort = currentSort,
            onSortChange = onSortChange
        )

        SortButton(
            label = "По названию",
            field = SortField.NAME,
            currentSort = currentSort,
            onSortChange = onSortChange
        )
    }
}

@Composable
fun SortButton(
    label: String,
    field: SortField,
    currentSort: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    val isSelected = currentSort.field == field
    val isAscending = currentSort.order == SortOrder.ASCENDING

    val backgroundColor = if (isSelected) MutedTerracotta else LightGray.copy(alpha = 0.2f)
    val contentColor = if (isSelected) Color.White else DarkBrown
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(backgroundColor)
            .clickable {
                val newOrder = if (isSelected && isAscending) SortOrder.DESCENDING else SortOrder.ASCENDING
                onSortChange(SortOption(field, newOrder))
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = contentColor,
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = when {
                !isSelected -> Icons.Default.UnfoldMore
                isAscending -> Icons.Default.ArrowUpward
                else -> Icons.Default.ArrowDownward
            },
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
    }
}



/*@Composable
fun CategoriesBar(
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryItem(category: String, isSelected: Boolean, onClick: () -> Unit) {
    val fontFamily = FontFamily(Font(R.font.gilroyblack))
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) SoftOrange else SageGreen,
        animationSpec = tween(durationMillis = 300), label = ""
    )
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = category,
            style = TextStyle(
                fontSize = 20.sp,
                color = if (isSelected) Color(0xFF6A3E1D) else DarkBrown,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal
            )
        )
    }
}*/