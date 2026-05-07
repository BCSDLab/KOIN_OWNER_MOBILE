package `in`.koreatech.business.feature.settings.utils

import `in`.koreatech.business.feature.settings.PolicySection
import `in`.koreatech.business.feature.settings.resources.Res
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class Term(val header: String, val articles: List<TermArticle>, val footer: String)

@Serializable
private data class TermArticle(val article: String, val content: List<String>)

enum class TermCategory(val file: String) {
    KOIN("files/Terms_koin_sign_up.json"),
    PERSONAL_INFORMATION("files/Terms_personal_information.json")
}

fun termToPolicySection(termCategory: TermCategory): Pair<String, List<PolicySection>> = runBlocking {
    val bytes = Res.readBytes(termCategory.file)

    val term = Json.decodeFromString<Term>(bytes.decodeToString())
    return@runBlocking Pair(term.header, term.articles.map { it.toPolicySection() })
}

private fun TermArticle.toPolicySection(): PolicySection = PolicySection(
    heading = article,
    body = content.joinToString("\n")
)
