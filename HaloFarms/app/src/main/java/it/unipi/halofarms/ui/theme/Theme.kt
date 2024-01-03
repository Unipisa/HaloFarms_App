package it.unipi.halofarms.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val ColorPalette = HaloFarmsColors(
    brand = MediumGreen,
    uiBackground = Neutral0,
    uiBorder = Neutral4,
    uiFloated = FunctionalGrey,
    textSecondary = Neutral7,
    textHelp = Neutral6,
    textInteractive = Neutral0,
    iconSecondary = Neutral7,
    iconInteractive = Neutral0,
    iconInteractiveInactive = Neutral1,
    error = FunctionalRed,
    homescreen = listOf(Neutral0, MediumGreen, Neutral8),
)

@Composable
fun HaloFarmsTheme(
    content: @Composable () -> Unit
) {
    ProvideHaloFarmsColors(ColorPalette, content)
}

object HaloFarmsTheme {
    val colors: HaloFarmsColors
        @Composable
        get() = LocalHaloFarmsColors.current
}

/**
 * HaloFarms custom Color Palette
 */
@Stable
class HaloFarmsColors(
    homescreen: List<Color>,
    brand: Color,
    uiBackground: Color,
    uiBorder: Color,
    uiFloated: Color,
    textPrimary: Color = brand,
    textSecondary: Color,
    textHelp: Color,
    textInteractive: Color,
    iconPrimary: Color = brand,
    iconSecondary: Color,
    iconInteractive: Color,
    iconInteractiveInactive: Color,
    error: Color
) {
    private var homescreen by mutableStateOf(homescreen)
    private var brand by mutableStateOf(brand)
    private var uiBackground by mutableStateOf(uiBackground)
    private var uiBorder by mutableStateOf(uiBorder)
    private var uiFloated by mutableStateOf(uiFloated)
    private var textPrimary by mutableStateOf(textPrimary)
    private var textSecondary by mutableStateOf(textSecondary)
    private var textHelp by mutableStateOf(textHelp)
    private var textInteractive by mutableStateOf(textInteractive)
    var iconPrimary by mutableStateOf(iconPrimary)
    private var iconSecondary by mutableStateOf(iconSecondary)
    private var iconInteractive by mutableStateOf(iconInteractive)
    private var iconInteractiveInactive by mutableStateOf(iconInteractiveInactive)
    private var error by mutableStateOf(error)

    fun update(other: HaloFarmsColors) {
        brand = other.brand
        uiBackground = other.uiBackground
        uiBorder = other.uiBorder
        uiFloated = other.uiFloated
        textPrimary = other.textPrimary
        textSecondary = other.textSecondary
        textHelp = other.textHelp
        textInteractive = other.textInteractive
        iconPrimary = other.iconPrimary
        iconSecondary = other.iconSecondary
        iconInteractive = other.iconInteractive
        iconInteractiveInactive = other.iconInteractiveInactive
        error = other.error
    }

    fun copy(): HaloFarmsColors = HaloFarmsColors(
        homescreen = homescreen,
        brand = brand,
        uiBackground = uiBackground,
        uiBorder = uiBorder,
        uiFloated = uiFloated,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textHelp = textHelp,
        textInteractive = textInteractive,
        iconPrimary = iconPrimary,
        iconSecondary = iconSecondary,
        iconInteractive = iconInteractive,
        iconInteractiveInactive = iconInteractiveInactive,
        error = error,
    )
}

@Composable
fun ProvideHaloFarmsColors(
    colors: HaloFarmsColors,
    content: @Composable () -> Unit
) {
    val colorPalette = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colors]
        // provided, and overwrite the values set in it.
        colors.copy()
    }
    colorPalette.update(colors)
    CompositionLocalProvider(LocalHaloFarmsColors provides colorPalette, content = content)
}

private val LocalHaloFarmsColors = staticCompositionLocalOf<HaloFarmsColors> {
    error("No HaloFarmsColorPalette provided")
}