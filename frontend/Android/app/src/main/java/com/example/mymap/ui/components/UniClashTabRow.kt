package com.example.mymap.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mymap.UniClashDestination
import java.util.Locale


private val TabHeight = 56.dp

@Composable
fun UniClashTabRow( allScreens: List<UniClashDestination>,
                    onTabSelected: (UniClashDestination) -> Unit,
                    currentScreen: UniClashDestination) {

    Surface(
        Modifier
            .height(TabHeight)
            .fillMaxWidth()) {

        Row(Modifier.selectableGroup()) {
            allScreens.forEach { screen ->
                UniClashTab(text = screen.route,
                    icon = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen)
            }
        }
    }
}

@Composable
private fun UniClashTab(text: String,
                        icon: ImageVector,
                        onSelected: () -> Unit,
                        selected: Boolean) {
    Row(modifier = Modifier
        .padding(16.dp)
        .animateContentSize()
        .height(TabHeight)
        .selectable(
            selected = selected,
            onClick = onSelected,
            role = Role.Tab,
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(
                bounded = false,
                radius = Dp.Unspecified,
                color = Color.Unspecified
            )
        )
        .clearAndSetSemantics { contentDescription = text }) {

        Icon(imageVector = icon, contentDescription = text)
        if (selected) {
            Spacer(Modifier.width(12.dp))
            Text(text.uppercase(Locale.getDefault()))
        }
    }
}