package org.futo.circles.core.list

interface IdEntity<out IdClass> {
    val id: IdClass
}