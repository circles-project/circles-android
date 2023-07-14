package org.futo.circles.core

import android.content.Context

class SessionIsNotCreatedException(context: Context) :
    Exception(context.getString(R.string.session_is_not_created))
