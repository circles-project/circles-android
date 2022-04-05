package com.futo.circles.core.list

interface IdEntity<out IdClass> {
    val id: IdClass
}