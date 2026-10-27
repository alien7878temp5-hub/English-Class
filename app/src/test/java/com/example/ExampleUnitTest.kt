package com.example

import com.example.data.local.PreloadedData
import com.example.data.model.CefrLevel
import com.example.data.model.TutorGender
import com.example.data.model.TutorPersona
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testTutorPersonas() {
        val arthur = TutorPersona.getPersona(TutorGender.MALE)
        assertEquals("Arthur", arthur.name)
        assertTrue(arthur.greeting.contains("Arthur here"))
        assertTrue(arthur.pitch < 1.0f)

        val eleanor = TutorPersona.getPersona(TutorGender.FEMALE)
        assertEquals("Eleanor", eleanor.name)
        assertTrue(eleanor.greeting.contains("Eleanor here"))
        assertTrue(eleanor.pitch > 1.0f)
    }

    @Test
    fun testCefrLevels() {
        val b1 = CefrLevel.fromCode("B1")
        assertEquals(CefrLevel.B1, b1)
        assertEquals("Band 5.5 - 6.0", b1.ieltsBand)

        val c2 = CefrLevel.fromCode("C2")
        assertEquals("Band 8.5 - 9.0", c2.ieltsBand)
        assertTrue(c2.speechRate > b1.speechRate)
    }

    @Test
    fun testPreloadedData() {
        assertTrue(PreloadedData.INITIAL_IDIOMS.isNotEmpty())
        assertTrue(PreloadedData.IELTS_TOPICS.isNotEmpty())

        val chuffed = PreloadedData.INITIAL_IDIOMS.find { it.id == "chuffed" }
        assertNotNull(chuffed)
        assertTrue(chuffed!!.phoneticRP.contains("tʃʌft"))
    }
}
