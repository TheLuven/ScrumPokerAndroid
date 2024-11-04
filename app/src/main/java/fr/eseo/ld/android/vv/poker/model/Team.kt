package fr.eseo.ld.android.vv.poker.model

import java.util.Date

data class Team(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val creationDate: Date = Date(),
    val modificationDate: Date = Date(),
    val scrumMasterEmail: String = "",
)