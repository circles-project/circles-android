package org.futo.circles.core.model

enum class TimelineTypeArg { ALL_CIRCLES, CIRCLE, GROUP, DM, GALLERY, THREAD }

fun TimelineTypeArg.isThread() = this == TimelineTypeArg.THREAD

fun TimelineTypeArg.isCircle() = this == TimelineTypeArg.CIRCLE

fun TimelineTypeArg.isAllPosts() = this == TimelineTypeArg.ALL_CIRCLES