package com.futo.circles.base

interface IdEntity<out IdClass> {
    val id: IdClass
}