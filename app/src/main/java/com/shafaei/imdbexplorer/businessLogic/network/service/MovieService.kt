package com.shafaei.imdbexplorer.businessLogic.network.service

import com.shafaei.imdbexplorer.businessLogic.dto.NetworkMovie
import com.shafaei.imdbexplorer.businessLogic.dto.NetworkSearchResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
  /**
   * @see <a href="http://www.omdbapi.com/?apikey=3e7c6655&s=god&plot=full&v=1&r=json">OMDB API Doc</a>
   *
   */
  @GET("/")
  fun search(
    @Query("apikey") apiKey: String = "3e7c6655",
    @Query("s") search: String,
    @Query("type") type: String?,
    @Query("y") year: String?,
    @Query("page") page: String

  ): Single<Result<NetworkSearchResponse>>

  /**
   * such <a href="http://www.omdbapi.com/?apikey=3e7c6655&s=god&plot=full&v=1&r=json">OMDB API Doc</a>
   */
  @GET("/")
  fun getMovie(
    @Query("apikey") apiKey: String = "3e7c6655",
    @Query("i") id: String,
    @Query("plot") plot: String = "full"
  ): Single<Result<NetworkMovie>>
}