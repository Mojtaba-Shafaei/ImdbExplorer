package com.shafaei.imdbexplorer.businessLogic.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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
data class NetworkSearch(
  @SerializedName("imdbID") @Expose var imdbId: String,
  @SerializedName("Title") @Expose var title: String,
  @SerializedName("Year") @Expose var year: String,
  @SerializedName("Type") @Expose var type: String,
  @SerializedName("Poster") @Expose var poster: String?
)