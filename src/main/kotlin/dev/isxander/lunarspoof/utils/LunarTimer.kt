package dev.isxander.lunarspoof.utils

class LunarTimer(
    var time: Int,
    val bl: Boolean
) {
    var current = System.currentTimeMillis()

    fun check(): Boolean {
        return if (System.currentTimeMillis() - current <= time * 1000L) {
            false
        } else {
            if (bl) {
                time = (time * 2).coerceAtMost(120)
            }
            current = System.currentTimeMillis()
            true
        }
    }

}