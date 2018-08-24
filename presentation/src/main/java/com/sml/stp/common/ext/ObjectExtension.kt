package com.sml.stp.common.ext

import java.lang.ref.WeakReference

fun <T> T.weak() = WeakReference(this)