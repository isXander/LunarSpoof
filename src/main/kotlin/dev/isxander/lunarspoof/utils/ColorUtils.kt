package dev.isxander.lunarspoof.utils

fun getRedF(rgb: Int): Float =
    getRedI(rgb) / 255f

fun getRedI(rgb: Int): Float =
    (rgb shr 16 and 0xFF).toFloat()

fun getGreenF(rgb: Int): Float =
    getGreenI(rgb) / 255f

fun getGreenI(rgb: Int): Float =
    (rgb shr 8 and 0xFF).toFloat()

fun getBlueF(rgb: Int): Float =
    getBlueI(rgb) / 255f

fun getBlueI(rgb: Int): Float =
    (rgb shr 0 and 0xFF).toFloat()

fun getAlphaF(rgb: Int): Float =
    getAlphaI(rgb) / 255f

fun getAlphaI(rgb: Int): Float =
    (rgb shr 24 and 0xFF).toFloat()
