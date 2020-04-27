package com.shafaei.imdbexplorer.ui.kotlinExt

fun String.toDoubleOrNull(): Double? {
  return try {
    if (this == "N/A") null
    else
      java.lang.Double.parseDouble(this)
  } catch (e: Exception) {
    null
  }
}