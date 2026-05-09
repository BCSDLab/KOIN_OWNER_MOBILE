package `in`.koreatech.business.domain.util

import kotlinx.coroutines.CancellationException

/**
 * 코루틴 취소를 보존하는 runCatching.
 *
 * runCatching은 CancellationException까지 흡수해 Result.failure로 감싸므로,
 * 호출 측이 정상 실패와 취소를 구분하지 못한다. 이 헬퍼는 CE를 즉시 재전파하여
 * 구조적 동시성을 깨지 않도록 한다.
 */
@Suppress("TooGenericExceptionCaught")
inline fun <T> runCatchingCancellable(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}
