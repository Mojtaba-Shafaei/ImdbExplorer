package com.shafaei.imdbexplorer.businessLogic.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//  {
//    "Source": "Internet Movie Database",
//    "Value": "8.6/10"
//  },
//  {
//    "Source": "Rotten Tomatoes",
//    "Value": "91%"
//  },
//  {
//    "Source": "Metacritic",
//    "Value": "79/100"
//  }
data class NetworkRating(
  @SerializedName("Source") @Expose val source: String,
  @SerializedName("Value") @Expose val value: String
)