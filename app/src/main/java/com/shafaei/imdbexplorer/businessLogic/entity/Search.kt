package com.shafaei.imdbexplorer.businessLogic.entity

import com.shafaei.imdbexplorer.businessLogic.dto.NetworkSearch

//  {
//    "Title": "God Father",
//    "Year": "2020",
//    "imdbID": "tt11531324",
//    "Type": "movie",
//    "Poster": "https://m.media-amazon.com/images/M/MV5BOGYzODZhMmYtODI0Yy00ZTliLTgwMzYtN2Y3YjAxOTgxMDY0XkEyXkFqcGdeQXVyMTEzNTkzNzI2._V1_SX300.jpg"
//  },
//  {
//    "Title": "Father and God Father",
//    "Year": "1912",
//    "imdbID": "tt1318958",
//    "Type": "movie",
//    "Poster": "N/A"
//  },

/**
 * entity class belongs to its dto [com.shafaei.imdbexplorer.businessLogic.dto.NetworkSearch]
 *
 */
data class Search(
  val imdbId: String,
  val title: String,
  val year: String,
  val type: String,
  val poster: String?
) {

  companion object {
    fun of(networkSearch: NetworkSearch): Search {
      return Search(
        imdbId = networkSearch.imdbId,
        title = networkSearch.title,
        year = networkSearch.year,
        type = networkSearch.type,
        poster = if (networkSearch.poster == "N/A") null else networkSearch.poster
      )
    }
  }
}