package com.viktorger.zulip_client.app.core.common

import kotlinx.coroutines.CancellationException

inline fun <R> runCatchingNonCancellation(block: () -> R): Result<R> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}