package org.futo.circles.core.base.list

interface IdEntity<out IdClass> {
    val id: IdClass
}