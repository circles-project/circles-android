package org.futo.circles.feature.circles.pick

import org.futo.circles.model.PickCircleTypeArg

interface PickCircleListener {

    fun onCircleChosen(roomId: String, pickCircleType: PickCircleTypeArg)

}