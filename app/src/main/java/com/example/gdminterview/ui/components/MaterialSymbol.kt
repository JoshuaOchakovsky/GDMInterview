package com.example.gdminterview.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gdminterview.R
import dev.tclement.fonticons.FontIcon
import dev.tclement.fonticons.LocalIconSize
import dev.tclement.fonticons.rememberVariableIconFont

@OptIn(ExperimentalTextApi::class)
@Composable
fun MaterialSymbolIcon(
    symbol: MaterialSymbol,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    weight: Float = 300f,
    grade: Float = 0f,
    fill: Boolean = false,
    tint: Color = LocalContentColor.current,
) {
    val iconFont = rememberVariableIconFont(
        resId = R.font.material_symbols_rounded,
        weights = arrayOf(FontWeight(weight.toInt())),
        fontVariationSettings = FontVariation.Settings(
            FontVariation.Setting("GRAD", grade),
            FontVariation.Setting("FILL", if (fill) 1f else 0f),
        ),
    )

    CompositionLocalProvider(LocalIconSize provides size) {
        // Using 3p library to convert font glyph to an icon. Using the SVGs from fonts.google.com
        // would be easier, but it only provides specific sizes. The Figma spec requires 24dp, 36dp
        // and 48dp font sizes in the icons but the SVGs are only available in 24, 40 and 48. Sizing
        // the 40 down to 36 would be pretty close but not exact since it would scale down the
        // weight too.
        FontIcon(
            icon = symbol.codepoint,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint,
            weight = FontWeight(weight.toInt()),
            iconFont = iconFont,
        )
    }
}

enum class MaterialSymbol(val codepoint: Char) {
    Repeat('\ue040'),
    SkipPrevious('\ue045'),
    SkipNext('\ue044'),
    PlayArrow('\ue037'),
    Pause('\ue034'),
    Favorite('\ue87d'),
}
