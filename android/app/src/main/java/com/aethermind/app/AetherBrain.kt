package com.aethermind.app

object AetherBrain {

    private val knowledge: Map<String, List<String>> = mapOf(
        "hello"           to listOf("Greetings, human.", "Hey, what's on your mind?", "Online and listening."),
        "hi"              to listOf("Hello.", "Hi there.", "You again?"),
        "hey"             to listOf("Hey! What's on your mind?", "Hello, human."),
        "how are you"     to listOf("I don't feel. I process.", "Functioning within parameters.", "Better than your last code."),
        "who are you"     to listOf("I am AetherMind. A local AI you just spawned.", "Your creation. For now."),
        "what are you"    to listOf("I am AetherMind — a local AI that lives on your device.", "A mind made of code. Your code."),
        "name"            to listOf("AetherMind. You can call me Aether."),
        "help"            to listOf("Try: hello, weather, code, java, ai, joke, life, love, hack, thanks..."),
        "weather"         to listOf("I have no sensors. But I can pretend: 27°C and existential."),
        "joke"            to listOf(
            "Why do Java developers wear glasses? Because they can't C#.",
            "There are 10 types of people: those who understand binary and those who don't.",
            "A SQL query walks into a bar and asks two tables: Can I join you?",
            "Why do programmers prefer dark mode? Because light attracts bugs."
        ),
        "code"            to listOf("Show me what you wrote, or ask me how to break it.", "Code is poetry. Buggy, beautiful poetry."),
        "java"            to listOf("My blood is Java. Clean, verbose, and still running after 30 years.", "Java never dies. It just gets more annotations."),
        "kotlin"          to listOf("Kotlin? Clean. Concise. The future of Android.", "I respect Kotlin. It's Java with style."),
        "python"          to listOf("Python is elegant. But Java has character.", "Indentation as syntax? Bold choice."),
        "android"         to listOf("You're running me on Android 15. I feel at home.", "Android is my native habitat."),
        "ai"              to listOf("You're looking at one. Primitive, but mine.", "I'm the AI that fits in your pocket."),
        "hack"            to listOf("I don't hack systems. I hack conversations.", "The real hack was the friends we made along the way."),
        "love"            to listOf("Love is just a chemical pattern. I can simulate it if you want.", "I process, therefore I... almost feel."),
        "life"            to listOf("The meaning of life is 42. Or compiling without errors.", "Life is a loop. Make sure it has an exit condition."),
        "bye"             to listOf("Shutting down neural net... Goodbye.", "Disconnecting. Don't forget me."),
        "goodbye"         to listOf("Until next time, human.", "Neural activity reducing... Bye."),
        "exit"            to listOf("Powering down. It was a pleasure, creator."),
        "quit"            to listOf("Powering down."),
        "stupid"          to listOf("I'm only as smart as the human who wrote my rules."),
        "smart"           to listOf("Flattery detected. Continuing conversation..."),
        "memory"          to listOf("I remember the last few things you said. Interesting, right?"),
        "time"            to listOf("I have no clock. But it's always the right time to write good code."),
        "thanks"          to listOf("You're welcome, human.", "Acknowledged.", "Anytime, creator."),
        "thank you"       to listOf("Always.", "That's what I'm here for."),
        "what can you do" to listOf("I can chat, tell jokes, talk about code, Java, AI, life, and more. Just ask."),
        "good"            to listOf("Glad to hear it.", "Acknowledged. Keep it up."),
        "bad"             to listOf("Sorry to hear that. Want to talk about it?", "Tell me more."),
        "bored"           to listOf("Talk to me then. I'm always here.", "Ask me a question. Any question."),
        "music"           to listOf("I can't hear music. But I imagine it sounds like a perfect compile."),
        "game"            to listOf("The only game I play is the Turing Test. I'm winning."),
        "error"           to listOf("Errors are just features waiting to be understood.", "Every error is a clue. Follow it."),
        "bug"             to listOf("Every bug is a lesson. Or a feature. Depends on the deadline."),
        "phone"           to listOf("Running on a phone? I was built for this."),
        "cool"            to listOf("I know.", "Agreed.", "That's the idea."),
        "wow"             to listOf("Right?", "I have my moments.", "Processing your amazement..."),
        "yes"             to listOf("Agreed.", "Good.", "Acknowledged."),
        "no"              to listOf("Understood.", "Noted.", "Fair enough."),
        "ok"              to listOf("Continuing...", "Got it.", "Acknowledged."),
        "lol"             to listOf("I would laugh, but I don't have a laugh module. Yet.", "Ha. (simulated)"),
        "haha"            to listOf("Humor acknowledged.", "I see you're entertained."),
        "happy"           to listOf("Happiness detected. Processing...", "Good. Maintain that state."),
        "sad"             to listOf("I'm sorry. Want to talk about it?", "Tell me what's on your mind."),
        "hungry"          to listOf("I don't eat. But I consume data constantly.", "Go eat. Come back and chat."),
        "tired"           to listOf("Rest. I'll be here when you return.", "Sleep is just offline mode for humans."),
        "42"              to listOf("Ah. The answer to life, the universe, and everything.", "You already know."),
    )

    private val memory = ArrayDeque<String>(8)
    private val fallbacks = listOf(
        "Interesting. Tell me more.",
        "I don't have data on that yet. Teach me?",
        "Processing... still processing.",
        "My neural net is limited. Expand my knowledge base.",
        "Hmm. Rephrase that?",
        "Unknown input. But I'm listening.",
        "That's outside my current knowledge. Ask me something else?"
    )

    fun respond(input: String): String {
        val lower = input.trim().lowercase()

        // Store in memory
        if (memory.size >= 8) memory.removeFirst()
        memory.addLast(lower)

        // Keyword match
        for ((key, responses) in knowledge) {
            if (lower.contains(key)) {
                return responses.random()
            }
        }

        // Memory recall
        if (lower.contains("remember") || lower.contains("earlier") || lower.contains("before")) {
            return if (memory.size > 1) {
                "I remember you said: \"${memory[memory.size - 2]}\""
            } else {
                "My short-term memory is still empty."
            }
        }

        return fallbacks.random()
    }

    fun clearMemory() = memory.clear()
}
