package com.futo.circles.core

interface IdEntity<out IdClass> {
    val id: IdClass
}