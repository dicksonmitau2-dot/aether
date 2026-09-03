package com.aethermind.app

import android.content.Context
import org.json.JSONObject
import java.util.regex.Pattern

object AetherBrain {

    const val MEMORY_SIZE = 8
    const val GOODBYE = "Neural activity ceasing... Goodbye, creator."

    private data class Topic(val key: String, val pattern: Pattern, val replies: List<String>)

    private val topics = mutableListOf<Topic>()
    private val fallbacks = mutableListOf<String>()
    private val memory = ArrayDeque<String>(MEMORY_SIZE)
    private val recall = Pattern.compile("\\b(remember|earlier|before)\\b")

    fun isExit(raw: String): Boolean {
        val s = raw.trim().lowercase()
        return s == "exit" || s == "quit"
    }

    fun load(context: Context) {
        topics.clear()
        fallbacks.clear()
        val json = context.assets.open("knowledge.json").bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val fb = root.getJSONArray("fallbacks")
        for (i in 0 until fb.length()) fallbacks.add(fb.getString(i))
        val arr = root.getJSONArray("topics")
        for (i in 0 until arr.length()) {
            val t = arr.getJSONObject(i)
            val key = t.getString("key").lowercase()
            val replies = mutableListOf<String>()
            val rs = t.getJSONArray("replies")
            for (j in 0 until rs.length()) replies.add(rs.getString(j))
            if (key.isNotEmpty() && replies.isNotEmpty()) {
                topics.add(
                    Topic(
                        key,
                        Pattern.compile("\\b" + Pattern.quote(key) + "\\b"),
                        replies
                    )
                )
            }
        }
        topics.sortByDescending { it.key.length }
        if (topics.isEmpty() || fallbacks.isEmpty()) {
            throw IllegalStateException("knowledge.json is missing topics or fallbacks")
        }
    }

    fun respond(input: String): String {
        val lower = input.trim().lowercase()
        if (memory.size >= MEMORY_SIZE) memory.removeFirst()
        memory.addLast(lower)

        for (t in topics) {
            if (t.pattern.matcher(lower).find()) {
                return t.replies.random()
            }
        }
        if (recall.matcher(lower).find()) {
            return if (memory.size > 1) {
                "I remember you said: \"${memory.elementAt(memory.size - 2)}\""
            } else {
                "My short-term memory is still empty."
            }
        }
        return fallbacks.random()
    }

    fun memorySnapshot(): List<String> = memory.toList()

    fun restoreMemory(items: List<String>) {
        memory.clear()
        items.takeLast(MEMORY_SIZE).forEach { memory.addLast(it) }
    }

    fun clearMemory() = memory.clear()
}
