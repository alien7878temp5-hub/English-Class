package com.example.data.local

import com.example.data.model.IeltsPart
import com.example.data.model.IeltsTopic

object PreloadedData {
    val INITIAL_IDIOMS = listOf(
        IdiomEntity(
            id = "chuffed",
            idiom = "Chuffed to bits",
            meaning = "Extremely pleased, proud, or delighted about something.",
            britishExample = "I was chuffed to bits when Arthur said my RP accent was spot on!",
            phoneticRP = "/tʃʌft tuː bɪts/",
            category = "Emotions & Pride",
            masteryLevel = 3,
            isFavorite = true
        ),
        IdiomEntity(
            id = "gutted",
            idiom = "Gutted",
            meaning = "Bitterly disappointed or crushed.",
            britishExample = "He was proper gutted when the train up to Edinburgh was cancelled.",
            phoneticRP = "/ˈɡʌt.ɪd/",
            category = "Emotions & Disappointment",
            masteryLevel = 2,
            isFavorite = false
        ),
        IdiomEntity(
            id = "proper",
            idiom = "Proper",
            meaning = "Truly, genuinely, or completely (used colloquially as an intensifier).",
            britishExample = "That was a proper good cup of Earl Grey tea.",
            phoneticRP = "/ˈprɒp.ə/",
            category = "Colloquial Intensifiers",
            masteryLevel = 3,
            isFavorite = true
        ),
        IdiomEntity(
            id = "dodgy",
            idiom = "Dodgy",
            meaning = "Suspicious, unreliable, or slightly dangerous.",
            britishExample = "That umbrella looks a bit dodgy in this London gale.",
            phoneticRP = "/ˈdɒdʒ.i/",
            category = "Everyday Slang",
            masteryLevel = 2,
            isFavorite = false
        ),
        IdiomEntity(
            id = "cup_of_tea",
            idiom = "Not my cup of tea",
            meaning = "Not something one finds agreeable, enjoyable, or suited to one's taste.",
            britishExample = "Opera is lovely, but heavy metal simply isn't my cup of tea.",
            phoneticRP = "/nɒt maɪ kʌp əv tiː/",
            category = "Preferences",
            masteryLevel = 3,
            isFavorite = true
        ),
        IdiomEntity(
            id = "bits_and_bobs",
            idiom = "Bits and bobs",
            meaning = "Various miscellaneous small things or sundry items.",
            britishExample = "I just need to nip down to the high street for a few bits and bobs.",
            phoneticRP = "/bɪts ənd bɒbz/",
            category = "Everyday Objects",
            masteryLevel = 2,
            isFavorite = true
        ),
        IdiomEntity(
            id = "knackered",
            idiom = "Knackered",
            meaning = "Utterly exhausted or worn out.",
            britishExample = "After walking through Hyde Park all afternoon, I was completely knackered.",
            phoneticRP = "/ˈnæk.əd/",
            category = "State of Being",
            masteryLevel = 2,
            isFavorite = false
        ),
        IdiomEntity(
            id = "bobs_your_uncle",
            idiom = "Bob's your uncle",
            meaning = "And there you have it; it's as simple as that.",
            britishExample = "Add two sugars, a dash of milk, and Bob's your uncle—the perfect cuppa.",
            phoneticRP = "/bɒbz jɔːr ˈʌŋ.kəl/",
            category = "Iconic British Sayings",
            masteryLevel = 3,
            isFavorite = true
        ),
        IdiomEntity(
            id = "spanner_in_works",
            idiom = "Throw a spanner in the works",
            meaning = "To disrupt or derail a carefully made plan.",
            britishExample = "The sudden tube strike threw a spanner in the works for our theatre trip.",
            phoneticRP = "/θrəʊ ə ˈspæn.ə ɪn ðə wɜːks/",
            category = "Problems & Disruptions",
            masteryLevel = 2,
            isFavorite = false
        ),
        IdiomEntity(
            id = "at_sixes_and_sevens",
            idiom = "At sixes and sevens",
            meaning = "In a state of total confusion or disarray.",
            britishExample = "We moved flat last weekend and everything is still at sixes and sevens.",
            phoneticRP = "/ət ˈsɪk.sɪz ənd ˈsɛv.ənz/",
            category = "Confusion & Chaos",
            masteryLevel = 1,
            isFavorite = false
        ),
        IdiomEntity(
            id = "take_the_biscuit",
            idiom = "Take the biscuit",
            meaning = "To be the most extreme, astonishing, or outrageous example of something.",
            britishExample = "Being 45 minutes late without an apology really takes the biscuit!",
            phoneticRP = "/teɪk ðə ˈbɪs.kɪt/",
            category = "Reactions & Annoyance",
            masteryLevel = 2,
            isFavorite = true
        ),
        IdiomEntity(
            id = "cheeky",
            idiom = "A cheeky (drink/treat)",
            meaning = "Doing something slightly indulgent or naughty in a charming, casual manner.",
            britishExample = "Fancy popping into the pub for a cheeky pint after our session?",
            phoneticRP = "/ə ˈtʃiː.ki/",
            category = "Social Life",
            masteryLevel = 3,
            isFavorite = true
        )
    )

    val IELTS_TOPICS = listOf(
        IeltsTopic(
            id = "ielts_p1_hometown",
            part = IeltsPart.PART_1,
            topicTitle = "Hometown & Local Neighbourhood",
            promptDescription = "Examiner Warm-up: Let's discuss where you grew up and what life is like there.",
            bulletPoints = listOf(
                "Do you live in a house or a flat?",
                "What is your favourite thing about your local area?",
                "Has your hometown changed much since your childhood?",
                "Would you recommend someone visit your town?"
            ),
            suggestedBritishPhrases = listOf(
                "It's within walking distance of...",
                "A bustling high street",
                "Quaint and residential",
                "To be completely frank..."
            ),
            targetBandLevel = "Band 7.5 - 8.5"
        ),
        IeltsTopic(
            id = "ielts_p1_tea_coffee",
            part = IeltsPart.PART_1,
            topicTitle = "Daily Habits, Tea & Coffee Culture",
            promptDescription = "Examiner Warm-up: British culture revolves heavily around a warm brew.",
            bulletPoints = listOf(
                "How do you usually start your morning?",
                "Do you prefer tea or coffee, and why?",
                "When do people in your country usually drink hot beverages?",
                "Do you enjoy visiting cafés alone or with companions?"
            ),
            suggestedBritishPhrases = listOf(
                "I'm an ardent tea enthusiast",
                "Can't function without my morning brew",
                "A quintessential British tradition"
            ),
            targetBandLevel = "Band 7.0 - 8.0"
        ),
        IeltsTopic(
            id = "ielts_p2_historic_place",
            part = IeltsPart.PART_2,
            topicTitle = "Describe an Impressive Historic Location",
            promptDescription = "Candidate Cue Card: You have 1 minute to prepare and 2 minutes to speak uninterrupted.",
            bulletPoints = listOf(
                "Where this historic place is located",
                "When and why you visited it (or wish to visit)",
                "What historical events or architecture made it special",
                "And explain why it left such a profound impression on you"
            ),
            suggestedBritishPhrases = listOf(
                "Steeped in rich historical significance",
                "Imposing architecture that took my breath away",
                "Standing the test of time",
                "A remarkably well-preserved heritage site"
            ),
            targetBandLevel = "Band 8.0 - 9.0"
        ),
        IeltsTopic(
            id = "ielts_p2_piece_of_advice",
            part = IeltsPart.PART_2,
            topicTitle = "Describe a Helpful Piece of Advice",
            promptDescription = "Candidate Cue Card: Speak for up to 2 minutes on meaningful counsel you received.",
            bulletPoints = listOf(
                "Who offered you this advice",
                "What the circumstance or dilemma was",
                "What they specifically advised you to do",
                "And explain how this counsel influenced your subsequent choices"
            ),
            suggestedBritishPhrases = listOf(
                "Words of wisdom that resonated with me",
                "I found myself at a critical crossroads",
                "Invaluable guidance",
                "It stood me in good stead"
            ),
            targetBandLevel = "Band 8.0 - 9.0"
        ),
        IeltsTopic(
            id = "ielts_p3_preservation",
            part = IeltsPart.PART_3,
            topicTitle = "Heritage Preservation vs Modern Development",
            promptDescription = "Examiner Analytical Discussion: Deeper philosophical debate on societal balance.",
            bulletPoints = listOf(
                "Should governments invest heavily in restoring centuries-old buildings?",
                "How does modern architectural uniformity affect national identity?",
                "Can historical tourism cause irreparable damage to delicate landmarks?",
                "To what extent should traditions evolve with modern generations?"
            ),
            suggestedBritishPhrases = listOf(
                "Strike a delicate balance between...",
                "Cultural heritage serves as an anchor",
                "Having said that, pragmatic necessity dictates...",
                "An inevitable consequence of globalisation"
            ),
            targetBandLevel = "Band 8.5+"
        )
    )
}
