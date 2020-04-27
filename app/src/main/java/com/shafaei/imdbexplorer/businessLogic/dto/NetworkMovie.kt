package com.shafaei.imdbexplorer.businessLogic.dto

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

// {
//  "Title": "God the Father",
//  "Year": "2014",
//  "Rated": "R",
//  "Released": "31 Oct 2014",
//  "Runtime": "101 min",
//  "Genre": "Documentary",
//  "Director": "Simon Fellows",
//  "Writer": "Moshe Diamant, Michael Franzese, Galit Hakmon McCord",
//  "Actors": "Tom Benedict Knight, Amanda Fernando Stevens, Robert Ashby, Thomas L. Colford",
//  "Plot": "Michael Franzese, the son of John \"Sonny\" Franzese, an underboss of the Colombo crime family, recounts his spiritual transformation.",
//  "Language": "English",
//  "Country": "USA",
//  "Awards": "1 nomination.",
//  "Poster": "https://m.media-amazon.com/images/M/MV5BMTkzODYzNjYyNl5BMl5BanBnXkFtZTgwNjM0NjA5MjE@._V1_SX300.jpg",
//  "Ratings": [
//    {
//      "Source": "Internet Movie Database",
//      "Value": "6.1/10"
//    }
//  ],
//  "Metascore": "N/A",
//  "imdbRating": "6.1",
//  "imdbVotes": "632",
//  "imdbID": "tt3722118",
//  "Type": "movie",
//  "DVD": "N/A",
//  "BoxOffice": "N/A",
//  "Production": "N/A",
//  "Website": "N/A",
//  "Response": "True"
//}
data class NetworkMovie(
  @SerializedName("Title") @Expose var title: String,
  @SerializedName("Year") @Expose var year: String,
  @SerializedName("Rated") @Expose var rated: String,
  @SerializedName("Released") @Expose var released: String,
  @SerializedName("Runtime") @Expose var runtime: String,
  @SerializedName("Genre") @Expose var genre: String,
  @SerializedName("Director") @Expose var director: String,
  @SerializedName("Writer") @Expose var writer: String,
  @SerializedName("Actors") @Expose var actors: String,
  @SerializedName("Plot") @Expose var plot: String,
  @SerializedName("Language") @Expose var language: String,
  @SerializedName("Country") @Expose var country: String,
  @SerializedName("Awards") @Expose var awards: String,
  @SerializedName("Poster") @Expose var poster: String,
  @SerializedName("Ratings") @Expose var ratings: List<NetworkRating>,
  @SerializedName("Metascore") @Expose var metaScore: String,
  @SerializedName("imdbRating") @Expose var imdbRating: String,
  @SerializedName("imdbVotes") @Expose var imdbVotes: String,
  @SerializedName("imdbID") @Expose var imdbID: String,
  @SerializedName("Type") @Expose var type: String,
  @SerializedName("DVD") @Expose var dvd: String,
  @SerializedName("BoxOffice") @Expose var boxOffice: String,
  @SerializedName("Production") @Expose var production: String,
  @SerializedName("Website") @Expose var website: String,
  @SerializedName("Response") @Expose var response: Boolean,
  @SerializedName("Error") @Expose var error: String? = null
) {

  companion object {
    fun fromJson(json: String): NetworkMovie {
      return Gson().fromJson(json, NetworkMovie::class.java)
    }
  }
}