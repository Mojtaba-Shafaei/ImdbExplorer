package com.shafaei.imdbexplorer.businessLogic.entity

import com.shafaei.imdbexplorer.businessLogic.dto.NetworkMovie
import com.shafaei.imdbexplorer.businessLogic.entity.RatingType.IMDB

/**
 * entity class belongs to its dto [com.shafaei.imdbexplorer.businessLogic.dto.NetworkMovie]
 *
 */
data class Movie(
  val title: String,
  val year: String,
  val rated: String,
  val released: String,
  val runtime: String,
  val genre: String,
  val director: String,
  val writer: String,
  val actors: String,
  val plot: String,
  val language: String,
  val country: String,
  val awards: String,
  val poster: String?,
  val ratings: List<Rating>,
  val metaScore: String,
  val imdbRating: String,
  val imdbVotes: String,
  val imdbID: String,
  val type: String,
//  val dvd: String?,
//  val boxOffice: String,
//  val production: String,
//  val website: String,
  val response: Boolean,
  val error: String? = null
) {

  companion object {

    fun of(networkMovie: NetworkMovie): Movie {
      // prepare maximum 3 Ratings without IMDB type
      val ratings: MutableList<Rating> = ArrayList<Rating>()
      networkMovie.ratings.forEach {
        if (IMDB.toString() != it.source.toLowerCase()) {
          ratings.add(Rating.of(it))
        }
      }
      // add IMDB
      if (ratings.size < 3) {
        ratings.add(0, Rating(source = "IMDB", value = networkMovie.imdbRating, type = IMDB))
      }

      return Movie(
        title = networkMovie.title,
        year = networkMovie.year,
        rated = networkMovie.rated,
        released = networkMovie.released,
        runtime = networkMovie.runtime,
        genre = networkMovie.genre,
        director = networkMovie.director,
        writer = networkMovie.writer,
        actors = networkMovie.actors,
        plot = networkMovie.plot,
        language = networkMovie.language,
        country = networkMovie.country,
        awards = networkMovie.awards,
        poster = if (networkMovie.poster == "N/A") null else networkMovie.poster,
        ratings = ratings,
        metaScore = networkMovie.metaScore,
        imdbRating = networkMovie.imdbRating,
        imdbVotes = networkMovie.imdbVotes,
        imdbID = networkMovie.imdbID,
        type = networkMovie.type,
//        dvd = networkMovie.dvd,
//        boxOffice = networkMovie.boxOffice,
//        production = networkMovie.production,
//        website = networkMovie.website,
        response = networkMovie.response,
        error = networkMovie.error
      )
    }
  }
}
