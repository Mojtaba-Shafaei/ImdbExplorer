package com.shafaei.imdbexplorer.businessLogic.entity

/**
 * enum class to keep plot types short, and full
 */
enum class PlotType(private val type: String) {

  SHORT("short"),
  FULL("full");

  override fun toString(): String {
    return type
  }
}