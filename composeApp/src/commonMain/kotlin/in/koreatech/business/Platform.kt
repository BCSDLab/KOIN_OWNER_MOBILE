package `in`.koreatech.business

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform