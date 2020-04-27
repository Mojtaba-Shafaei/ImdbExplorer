package com.shafaei.imdbexplorer.businessLogic.entity.param

import com.shafaei.imdbexplorer.businessLogic.entity.PlotType
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieParamTest {

  @Test
  fun toMap_IDPlot_ReturnsTrue() {
    val lvID = "tt3722118"
    val lvPlot = PlotType.FULL

    val map = MovieParam(id = lvID, plot = lvPlot).toMap()
    assertEquals(3, map.size)
    assertEquals(lvID, map[MovieParam.ID_KEY])
    assertEquals(lvPlot.toString(), map[MovieParam.PLOT_KEY])
  }
}