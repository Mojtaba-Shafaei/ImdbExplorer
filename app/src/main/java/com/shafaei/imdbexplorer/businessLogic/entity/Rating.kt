package com.shafaei.imdbexplorer.businessLogic.entity

import com.shafaei.imdbexplorer.businessLogic.dto.NetworkRating

/**
 * entity class belongs to its dto, [com.shafaei.imdbexplorer.businessLogic.dto.NetworkRating]
 */
data class Rating(val source: String, val value: String, val type: RatingType) {

  companion object {
    fun of(networkRating: NetworkRating): Rating {
      return Rating(source = networkRating.source, value = networkRating.value, type = RatingType.of(networkRating.source))
    }
  }
}

// keep these enums as sort as now,
enum class RatingType(private val type: String) {

  METASCORE("metascore"),
  ROTTEN("rotten tomatoes"),
  METACRITIC("metacritic"),
  IMDB("internet movie database"),
  NA("N/A");

  override fun toString(): String {
    return type
  }

  companion object {
    fun of(t: String): RatingType {
      return when (t.toLowerCase()) {
        IMDB.toString() -> IMDB
        ROTTEN.toString() -> ROTTEN
        METASCORE.toString() -> METASCORE
        METACRITIC.toString() -> METACRITIC
        else -> NA
      }
    }
  }

}