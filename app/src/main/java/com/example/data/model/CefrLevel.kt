package com.example.data.model

enum class CefrLevel(
    val code: String,
    val label: String,
    val ieltsBand: String,
    val speechRate: Float,
    val description: String,
    val pedagogicalFocus: String
) {
    A1(
        code = "A1",
        label = "Beginner (A1)",
        ieltsBand = "Band 3.0 - 4.0",
        speechRate = 0.80f,
        description = "Clear, gentle tempo with foundational everyday British vocabulary.",
        pedagogicalFocus = "Simple present tense, basic greetings, polite British etiquette ('please', 'cheers')."
    ),
    A2(
        code = "A2",
        label = "Elementary (A2)",
        ieltsBand = "Band 4.0 - 5.0",
        speechRate = 0.86f,
        description = "Supported conversational pace. Connects simple ideas and daily routines.",
        pedagogicalFocus = "Past simple, basic phrasal verbs, describing weekend activities and travel."
    ),
    B1(
        code = "B1",
        label = "Intermediate (B1)",
        ieltsBand = "Band 5.5 - 6.0",
        speechRate = 0.92f,
        description = "Standard friendly British tempo. Explores personal experiences and plans.",
        pedagogicalFocus = "First conditional, everyday British slang ('gutted', 'chuffed'), IELTS Part 1 topics."
    ),
    B2(
        code = "B2",
        label = "Upper Intermediate (B2)",
        ieltsBand = "Band 6.5 - 7.0",
        speechRate = 0.98f,
        description = "Natural conversational flow. Expresses opinions with British nuance and idioms.",
        pedagogicalFocus = "Complex sentence structures, recasting prepositions, IELTS Part 2 monologue scaffolding."
    ),
    C1(
        code = "C1",
        label = "Advanced (C1)",
        ieltsBand = "Band 7.5 - 8.0",
        speechRate = 1.04f,
        description = "Fast, authentic British cadence with subtle wit, understatement, and advanced idioms.",
        pedagogicalFocus = "Discourse markers ('Mind you', 'Having said that'), analytical synthesis, IELTS Part 3 debates."
    ),
    C2(
        code = "C2",
        label = "Mastery (C2 / IELTS 8.5+)",
        ieltsBand = "Band 8.5 - 9.0",
        speechRate = 1.08f,
        description = "Complete native-like RP speed, rhetorical elegance, British cultural references, and mastery.",
        pedagogicalFocus = "Received Pronunciation nuances (trap-bath split, dark L, non-rhotic RP), effortless idiomatic precision."
    );

    companion object {
        fun fromCode(code: String): CefrLevel = entries.find { it.code.equals(code, ignoreCase = true) } ?: B1
    }
}
