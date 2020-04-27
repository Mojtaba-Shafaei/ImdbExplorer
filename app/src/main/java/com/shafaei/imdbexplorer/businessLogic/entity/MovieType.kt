package com.shafaei.imdbexplorer.businessLogic.entity

enum class MovieType(private val type: String) {
  MOVIE("movie"),
  SERIES("series"),
  EPISODE("episode");

  override fun toString(): String {
    return type
  }
}