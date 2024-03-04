package org.futo.circles.core.model

import org.futo.circles.core.R

enum class CircleType(val nameResId: Int, val messageResId: Int) {
    Public(R.string.public_type, R.string.public_circle_explanation),
    Private(R.string.private_type, R.string.private_circle_explanation)
}