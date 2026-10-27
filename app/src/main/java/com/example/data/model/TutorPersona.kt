package com.example.data.model

enum class TutorGender {
    MALE,
    FEMALE
}

data class TutorPersona(
    val gender: TutorGender,
    val name: String,
    val title: String,
    val bio: String,
    val greeting: String,
    val pitch: Float,
    val accentRPDescription: String,
    val signaturePhrases: List<String>
) {
    companion object {
        val ARTHUR = TutorPersona(
            gender = TutorGender.MALE,
            name = "Arthur",
            title = "Senior RP British Tutor & IELTS Examiner",
            bio = "Articulate, patient, and warm gentleman tutor from Oxford. Specializes in Received Pronunciation, crisp elocution, and IELTS Band 8.5+ cadence.",
            greeting = "Hello there! Arthur here. Grand to hear from you. How's your day treating you?",
            pitch = 0.90f,
            accentRPDescription = "Modern Southern British / Received Pronunciation (Crisp, resonant, calm tempo)",
            signaturePhrases = listOf(
                "Grand to hear from you.",
                "Splendid effort indeed.",
                "A Brit would usually say...",
                "Spot on, my friend."
            )
        )

        val ELEANOR = TutorPersona(
            gender = TutorGender.FEMALE,
            name = "Eleanor",
            title = "Elite Cambridge British Voice & Fluency Coach",
            bio = "Warm, refined, and exceptionally encouraging British tutor from Cambridge. Guides you through natural British idioms, intonation, and effortless conversational flow.",
            greeting = "Hello there! Eleanor here. Splendid to speak with you. How is your day treating you?",
            pitch = 1.15f,
            accentRPDescription = "Modern Received Pronunciation (Melodic, articulate, engaging intonation)",
            signaturePhrases = listOf(
                "Splendid to speak with you.",
                "Brilliant progress today!",
                "In Britain, we often express that as...",
                "You're making fabulous strides."
            )
        )

        fun getPersona(gender: TutorGender): TutorPersona = when (gender) {
            TutorGender.MALE -> ARTHUR
            TutorGender.FEMALE -> ELEANOR
        }
    }
}
