#!/usr/bin/env kotlin

/*
 * Copyright (c) 2025. BCSD Lab.
 */

import java.io.File
import kotlin.random.Random

/**
 * If developer is a mentor, set isMentor to true.
 * If developer should not be picked as a reviewer, set shouldPick to false.
 */
enum class Developer(val githubName: String) {
    KONGWOOJIN("kongwoojin"),
    KYM_P("KYM-P"),
}

/**
 * Reviewer count
 * Need to edit pick_reviewer.yml too
 */
val reviewerCount = 1

/**
 * Reviewer pairs for each developer.
 * first element is developer and second element is reviewer.
 *
 * Pair rule
 * don't add mentor here
 */
val reviewerPair = listOf(
    Developer.KONGWOOJIN to Developer.KYM_P,
    Developer.KYM_P to Developer.KONGWOOJIN,
)

/**
 * Export the reviewer name to GitHub Actions output.
 * @param reviewers The name of the reviewers.
 */
fun exportReviewer(reviewers: List<String>) {
    val githubOutput = System.getenv("GITHUB_OUTPUT")
    reviewers.forEachIndexed { index, reviewer ->
        File(githubOutput).appendText("reviewer${index + 1}=$reviewer\n")
    }
}

/**
 * Pick a paired reviewer for the developer.
 * The developer and reviewer should not be in the same team.
 */
fun pickPairedReviewer(developer: Developer) {
    val reviewer = reviewerPair.first { it.first == developer }.second
    exportReviewer(listOf(reviewer.githubName))
}

/**
 * Pick a random reviewer.
 */
fun pickRandomReviewer(developer: Developer?) {
    val reviewers = Developer.entries
        .filter { it != developer }
        .shuffled()
        .take(reviewerCount)
    exportReviewer(reviewers.map { it.githubName })
}

fun main(args: Array<String>) {
    val githubActor = System.getenv("GITHUB_ACTOR")
    val developer = Developer.entries.firstOrNull { it.githubName == githubActor }

    pickRandomReviewer(developer)
}

main(args)
