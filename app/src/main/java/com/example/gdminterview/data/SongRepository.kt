package com.example.gdminterview.data

data class Song(
    val id: String,
    val name: String,
    val artist: String,
    val albumName: String,
    val durationMs: Long,
    val albumArtKey: String? = null,
)

object SongRepository {
    val songs = listOf(
        Song(
            "1",
            "Black Friday (pretty like the sun)",
            "Lost Frequencies, Tom Odell, Poppy Baskcomb",
            "Purple Noon",
            311_000L,
            "purple_noon"
        ),
        Song(
            "2",
            "Send in the Clowns",
            "Glynis Johns, Len Cariou",
            "A Little Night Music (Original Broadway Cast Recording)",
            263_000L,
            "little_night_music"
        ),
        Song(
            "3",
            "Being Alive",
            "Dean Jones, Company Ensemble",
            "Company (Original Broadway Cast Recording)",
            288_000L,
            "company"
        ),
        Song(
            "4",
            "By the Sea",
            "Len Cariou, Angela Lansbury",
            "Sweeney Todd: The Demon Barber of Fleet Street (Original Broadway Cast Recording)",
            333_000L
        ),
        Song(
            "5",
            "Losing My Mind",
            "Dorothy Collins",
            "Follies: Original Broadway Cast",
            239_000L,
            "follies"
        ),
    )
}
