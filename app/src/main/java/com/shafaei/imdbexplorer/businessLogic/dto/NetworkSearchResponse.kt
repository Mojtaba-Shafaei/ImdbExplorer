package com.shafaei.imdbexplorer.businessLogic.dto

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @property response if <code>true</code>, the movies param has result.
 * @property movies always has array of movies, if response is true.
 * @property error contains the error description, like "NOT FOUND!" this parameter is filled just if response is false
 * @property totalResults Int, count of all results such as 5000
 */
data class NetworkSearchResponse(
  @SerializedName("Search") @Expose var movies: List<NetworkSearch>,
  @SerializedName("totalResults") @Expose var totalResults: Int,
  @SerializedName("Response") @Expose var response: Boolean,
  @SerializedName("Error") @Expose val error: String? = null
) {

  companion object {
    fun fromJson(json: String): NetworkSearchResponse {
      return Gson().fromJson(json, NetworkSearchResponse::class.java)
    }
  }
}