package it.unipi.halofarms.ui.theme

import androidx.compose.runtime.*
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
    var homescreen by mutableStateOf(homescreen)
        private set
    var brand by mutableStateOf(brand)
        private set
    var uiBackground by mutableStateOf(uiBackground)
        private set
    var uiBorder by mutableStateOf(uiBorder)
        private set
    var uiFloated by mutableStateOf(uiFloated)
        private set
    var textPrimary by mutableStateOf(textPrimary)
        private set
    var textSecondary by mutableStateOf(textSecondary)
        private set
    var textHelp by mutableStateOf(textHelp)
        private set
    var textInteractive by mutableStateOf(textInteractive)
        private set
    var iconPrimary by mutableStateOf(iconPrimary)
        private set
    var iconSecondary by mutableStateOf(iconSecondary)
        private set
    var iconInteractive by mutableStateOf(iconInteractive)
        private set
    var iconInteractiveInactive by mutableStateOf(iconInteractiveInactive)
        private set
    var error by mutableStateOf(error)
        private set

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