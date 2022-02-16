package com.futo.circles.provider

import org.matrix.android.sdk.api.Matrix

object MatrixProvider {
    lateinit var matrix: Matrix
        private set

    fun saveMatrixInstance(matrixInstance: Matrix) {
        matrix = matrixInstance
    }
}