package project.main.uniclash.datatypes

import androidx.compose.ui.graphics.Color

enum class CustomColor(val selection: Color) {
    DarkPurple(Color(37, 0, 89, 255)),
    Purple(Color(205,185,249,255));

    fun getColor(): Color {
        return selection
    }
}
