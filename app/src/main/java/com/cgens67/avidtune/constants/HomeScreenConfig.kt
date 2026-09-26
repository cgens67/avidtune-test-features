package com.cgens67.avidtune.constants

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.cgens67.avidtune.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class HomeSectionConfig(
    val id: String,
    val isVisible: Boolean = true,
    val isPinned: Boolean = false,
)

enum class HomeSectionType(
    val id: String,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    val description: String,
) {
    QUICK_PICKS(
        id = "quick_picks",
        titleRes = R.string.quick_picks,
        iconRes = R.drawable.trending_up,
        description = "Songs tailored to your listening habits"
    ),
    KEEP_LISTENING(
        id = "keep_listening",
        titleRes = R.string.keep_listening,
        iconRes = R.drawable.history,
        description = "Albums, artists, and tracks you frequently replay"
    ),
    ACCOUNT_PLAYLISTS(
        id = "account_playlists",
        titleRes = R.string.your_ytb_playlists,
        iconRes = R.drawable.queue_music,
        description = "Playlists synced from your YouTube account"
    ),
    SIMILAR_RECOMMENDATIONS(
        id = "similar_recommendations",
        titleRes = R.string.similar_content,
        iconRes = R.drawable.similar,
        description = "Music similar to artists and songs you love"
    ),
    YTM_SECTIONS(
        id = "ytm_sections",
        titleRes = R.string.explore,
        iconRes = R.drawable.explore_outlined,
        description = "Curated mixes and personalized feeds from YouTube Music"
    ),
    NEW_RELEASES(
        id = "new_releases",
        titleRes = R.string.new_release_albums,
        iconRes = R.drawable.album,
        description = "Freshly released albums and singles"
    ),
    FORGOTTEN_FAVORITES(
        id = "forgotten_favorites",
        titleRes = R.string.forgotten_favorites,
        iconRes = R.drawable.replay,
        description = "Past favorites you haven't played in a while"
    );

    companion object {
        fun fromId(id: String): HomeSectionType? = entries.firstOrNull { it.id == id }
    }
}

object HomeScreenConfigHelper {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val defaultSections: List<HomeSectionConfig> = listOf(
        HomeSectionConfig(HomeSectionType.QUICK_PICKS.id, isVisible = true, isPinned = false),
        HomeSectionConfig(HomeSectionType.KEEP_LISTENING.id, isVisible = true, isPinned = false),
        HomeSectionConfig(HomeSectionType.ACCOUNT_PLAYLISTS.id, isVisible = true, isPinned = false),
        HomeSectionConfig(HomeSectionType.SIMILAR_RECOMMENDATIONS.id, isVisible = true, isPinned = false),
        HomeSectionConfig(HomeSectionType.YTM_SECTIONS.id, isVisible = true, isPinned = false),
        HomeSectionConfig(HomeSectionType.NEW_RELEASES.id, isVisible = true, isPinned = false),
        HomeSectionConfig(HomeSectionType.FORGOTTEN_FAVORITES.id, isVisible = true, isPinned = false),
    )

    val defaultConfigJson: String = json.encodeToString(defaultSections)

    fun parseConfig(jsonString: String?): List<HomeSectionConfig> {
        val list = try {
            if (!jsonString.isNullOrBlank()) {
                json.decodeFromString<List<HomeSectionConfig>>(jsonString)
            } else {
                defaultSections
            }
        } catch (_: Exception) {
            defaultSections
        }

        val existingIds = list.map { it.id }.toSet()
        val missing = defaultSections.filter { it.id !in existingIds }
        val fullList = (list + missing).filter { config ->
            HomeSectionType.fromId(config.id) != null
        }

        // Pinned sections always float to the top
        val pinned = fullList.filter { it.isPinned }
        val unpinned = fullList.filter { !it.isPinned }
        return pinned + unpinned
    }

    fun parseConfigRaw(jsonString: String?): List<HomeSectionConfig> {
        val list = try {
            if (!jsonString.isNullOrBlank()) {
                json.decodeFromString<List<HomeSectionConfig>>(jsonString)
            } else {
                defaultSections
            }
        } catch (_: Exception) {
            defaultSections
        }

        val existingIds = list.map { it.id }.toSet()
        val missing = defaultSections.filter { it.id !in existingIds }
        return (list + missing).filter { config ->
            HomeSectionType.fromId(config.id) != null
        }
    }

    fun encodeConfig(list: List<HomeSectionConfig>): String {
        return json.encodeToString(list)
    }
}
