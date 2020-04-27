package com.shafaei.imdbexplorer.businessLogic.entity.param

import com.shafaei.imdbexplorer.businessLogic.entity.PlotType
import com.shafaei.imdbexplorer.businessLogic.entity.PlotType.FULL

// i	Optional*		<empty>	A valid IMDb ID (e.g. tt1285016) in this app this id assumed as required
// plot	No	short, full	short	Return short or full plot.
/**
 * @property id    A valid IMDb ID (e.g. tt1285016). map as "i"
 * @property plot  Return short or full plot. (short, full), map as "plot"
 *
 */
data class MovieParam(val id: String, val plot: PlotType = FULL)