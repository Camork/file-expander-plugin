package com.github.camork.util;

/**
 * Place-holder class to access 'EntryInfo'
 *
 * @author Charles Wu
 */
data class EntryInfo(
    val shortName: CharSequence,
    val isDirectory: Boolean,
    val length: Long,
    val timestamp: Long,
    val parent: EntryInfo?
)