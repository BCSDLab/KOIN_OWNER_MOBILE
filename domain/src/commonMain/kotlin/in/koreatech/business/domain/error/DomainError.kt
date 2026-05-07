package `in`.koreatech.business.domain.error

sealed class DomainError(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {
    class Network(message: String, cause: Throwable? = null) : DomainError(message, cause)

    class Validation(message: String, cause: Throwable? = null) : DomainError(message, cause)

    class Auth(message: String, cause: Throwable? = null) : DomainError(message, cause)

    class Unknown(message: String, cause: Throwable? = null) : DomainError(message, cause)
}
