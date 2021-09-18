package dev.isxander.lunarspoof.utils

import com.google.common.collect.HashBiMap

fun <K, V> createHashBiMap(vararg values: Pair<K, V>): HashBiMap<K, V> =
    HashBiMap.create(mapOf(*values))
