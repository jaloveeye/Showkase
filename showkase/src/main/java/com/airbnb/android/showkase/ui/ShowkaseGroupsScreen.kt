package com.airbnb.android.showkase.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.airbnb.android.showkase.models.ShowkaseBrowserColor
import com.airbnb.android.showkase.models.ShowkaseBrowserComponent
import com.airbnb.android.showkase.models.ShowkaseBrowserScreenMetadata
import com.airbnb.android.showkase.models.ShowkaseBrowserTypography
import com.airbnb.android.showkase.models.ShowkaseCurrentScreen
import com.airbnb.android.showkase.models.update
import java.util.Locale

@Composable
internal fun ShowkaseGroupsScreen(
    groupedTypographyMap: Map<String, List<*>>,
    showkaseBrowserScreenMetadata: MutableState<ShowkaseBrowserScreenMetadata>,
    navController: NavHostController,
    onClick: () -> Unit
) {
    val filteredMap = getFilteredSearchList(
        groupedTypographyMap.toSortedMap(),
        showkaseBrowserScreenMetadata
    )

    LazyColumn {
        items(
            items = filteredMap.entries.toList(),
            itemContent = { (group, list) ->
                val size = getNumOfUIElements(list)
                SimpleTextCard(
                    text = "$group ($size)",
                    onClick = {
                        showkaseBrowserScreenMetadata.update {
                            copy(
                                currentGroup = group,
                                isSearchActive = false,
                                searchQuery = null
                            )
                        }
                        onClick()
                    }
                )
            }
        )
    }
    BackButtonHandler {
        goBackToCategoriesScreen(showkaseBrowserScreenMetadata, navController)
    }
}

internal fun getNumOfUIElements(list: List<*>): Int {
    val isComponentList = list.filterIsInstance(ShowkaseBrowserComponent::class.java)
    return when {
        isComponentList.isNotEmpty() -> isComponentList.distinctBy { it.componentName }.size
        else -> list.size
    }
}

internal fun <T> getFilteredSearchList(
    map: Map<String, List<T>>,
    showkaseBrowserScreenMetadata: MutableState<ShowkaseBrowserScreenMetadata>
) =
    when (showkaseBrowserScreenMetadata.value.isSearchActive) {
        false -> map
        !showkaseBrowserScreenMetadata.value.searchQuery.isNullOrBlank() -> {
            map.filter {
                matchSearchQuery(
                    showkaseBrowserScreenMetadata.value.searchQuery!!,
                    it.key
                )
            }
        }
        else -> map
    }

@Composable
internal fun ShowkaseComponentGroupsScreen(
    groupedComponentMap: Map<String, List<ShowkaseBrowserComponent>>,
    showkaseBrowserScreenMetadata: MutableState<ShowkaseBrowserScreenMetadata>,
    navController: NavHostController
) {
    ShowkaseGroupsScreen(
        groupedComponentMap,
        showkaseBrowserScreenMetadata,
        navController
    ) {
        navController.navigate(ShowkaseCurrentScreen.COMPONENTS_IN_A_GROUP)
    }
}

@Composable
internal fun ShowkaseColorGroupsScreen(
    groupedColorsMap: Map<String, List<ShowkaseBrowserColor>>,
    showkaseBrowserScreenMetadata: MutableState<ShowkaseBrowserScreenMetadata>,
    navController: NavHostController
) {
    ShowkaseGroupsScreen(
        groupedColorsMap,
        showkaseBrowserScreenMetadata,
        navController
    ) {
        navController.navigate(ShowkaseCurrentScreen.COLORS_IN_A_GROUP)
    }
}

@Composable
internal fun ShowkaseTypographyGroupsScreen(
    groupedTypographyMap: Map<String, List<ShowkaseBrowserTypography>>,
    showkaseBrowserScreenMetadata: MutableState<ShowkaseBrowserScreenMetadata>,
    navController: NavHostController
) {
    ShowkaseGroupsScreen(
        groupedTypographyMap,
        showkaseBrowserScreenMetadata,
        navController
    ) {
        navController.navigate(ShowkaseCurrentScreen.TYPOGRAPHY_IN_A_GROUP)
    }
}
