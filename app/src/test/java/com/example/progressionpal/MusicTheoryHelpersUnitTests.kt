package com.example.progressionpal

import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * Unit tests for music theory libraries
 */
class UnitTests {
    @Test
    fun testGetNoteAtInterval() {
        val helper = IntervalHelper()
        var result = helper.getNoteAtInterval("C", Interval.PERFECT_FIFTH)
        assertEquals(result, "G")
        result = helper.getNoteAtInterval("C", Interval.PERFECT_FOURTH)
        assertEquals(result, "F")
        result = helper.getNoteAtInterval("C", Interval.MINOR_SIXTH)
        assertEquals(result, "Ab")
        result = helper.getNoteAtInterval("C", Interval.MINOR_THIRD)
        assertEquals(result, "Eb")
        result = helper.getNoteAtInterval("C", Interval.MINOR_SEVENTH)
        assertEquals(result, "Bb")
        result = helper.getNoteAtInterval("C#", Interval.PERFECT_FIFTH)
        assertEquals(result, "G#")
        result = helper.getNoteAtInterval("Cb", Interval.PERFECT_FIFTH)
        assertEquals(result, "Gb")
    }

    @Test
    fun testGetNegativeHarmonyMapping() {
        var subHelper = SubstitutionHelper("C")
        var expectedMapping = mutableListOf(Pair("C", "G"), Pair("F", "D"), Pair("Bb", "A"),
            Pair("Eb", "E"), Pair("Ab", "B"), Pair("Db", "Gb"))
        var result = subHelper.getNegativeHarmonyMapping()
        assertEquals(expectedMapping, result)

        subHelper = SubstitutionHelper("Cb")
        expectedMapping = mutableListOf(Pair("Cb", "Gb"), Pair("Fb", "Db"), Pair("Bbb", "Ab"),
            Pair("Ebb", "Eb"), Pair("Abb", "Bb"), Pair("Dbb", "Gbb"))
        result = subHelper.getNegativeHarmonyMapping()
        assertEquals(expectedMapping, result)
    }

    @Test
    fun testGetNotesForChord(){
        // Base
        val chordHelper = ChordHelper()
        var result = chordHelper.getNotesForChord("A")
        assertEquals(result, mutableListOf("A", "C#", "E"))
        result = chordHelper.getNotesForChord("A+")
        assertEquals(result, mutableListOf("A", "C#", "E#"))
        result = chordHelper.getNotesForChord("Asus2")
        assertEquals(result, mutableListOf("A", "B", "E"))
        result = chordHelper.getNotesForChord("Asus4")
        assertEquals(result, mutableListOf("A", "D", "E"))
        result = chordHelper.getNotesForChord("Am")
        assertEquals(result, mutableListOf("A", "C", "E"))

        // Accidentals
        result = chordHelper.getNotesForChord("Ab")
        assertEquals(result, mutableListOf("Ab", "C", "Eb"))
        result = chordHelper.getNotesForChord("A#")
        assertEquals(result, mutableListOf("A#", "C##", "E#"))

        // Minors and extensions
        result = chordHelper.getNotesForChord("Am7")
        assertEquals(result, mutableListOf("A", "C", "E", "G"))
        result = chordHelper.getNotesForChord("Adim")
        assertEquals(result, mutableListOf("A", "C", "Eb"))
        result = chordHelper.getNotesForChord("Am7b5")
        assertEquals(result, mutableListOf("A", "C", "Eb", "G"))
        result = chordHelper.getNotesForChord("Adim7")
        assertEquals(result, mutableListOf("A", "C", "Eb", "Gb"))
        result = chordHelper.getNotesForChord("AM7")
        assertEquals(result, mutableListOf("A", "C#", "E", "G#"))
        result = chordHelper.getNotesForChord("A7")
        assertEquals(result, mutableListOf("A", "C#", "E", "G"))

    }

    @Test
    fun testGetNegativeHarmonyChord(){
        val subHelper: SubstitutionHelper = SubstitutionHelper("C")
        var result = subHelper.getNegativeHarmonyChord("C")
        assertEquals(result, "Cm")
        result = subHelper.getNegativeHarmonyChord("Dm")
        assertEquals(result, "Bb")
        result = subHelper.getNegativeHarmonyChord("G")
        assertEquals(result, "Fm")
        result = subHelper.getNegativeHarmonyChord("Bdim7")
        assertEquals(result, "Bdim7")
        result = subHelper.getNegativeHarmonyChord("Em")
        assertEquals(result, "Ab")
    }

    @Test
    fun testIdentifyChord(){
        val chordHelper = ChordHelper()
        var result = chordHelper.identifyChord(mutableListOf("C", "A", "E"))
        assertEquals(result, "Am")
        result = chordHelper.identifyChord(mutableListOf("C", "E", "A"))
        assertEquals(result, "Am")
        result = chordHelper.identifyChord(mutableListOf("A", "E", "C"))
        assertEquals(result, "Am")
        result = chordHelper.identifyChord(mutableListOf("G", "E", "C"))
        assertEquals(result, "C")
        result = chordHelper.identifyChord(mutableListOf("G", "C", "E"))
        assertEquals(result, "C")
        result = chordHelper.identifyChord(mutableListOf("G", "C", "E", "A"))
        assertEquals(result, "C6")
        result = chordHelper.identifyChord(mutableListOf("A", "C", "Eb"))
        assertEquals(result, "Adim")
        result = chordHelper.identifyChord(mutableListOf("C", "A", "Eb"))
        assertEquals(result, "Adim")
        result = chordHelper.identifyChord(mutableListOf("A", "Eb", "C", "G"))
        assertEquals(result, "Am7b5")
        result = chordHelper.identifyChord(mutableListOf("Gb", "A", "Eb", "C"))
        assertEquals(result, "Adim7")
        result = chordHelper.identifyChord(mutableListOf("A", "B", "E"))
        assertEquals(result, "Asus2")
        result = chordHelper.identifyChord(mutableListOf("A", "D", "E"))
        assertEquals(result, "Asus4")
    }

    @Test
    fun testScaleHelper(){
        var scale = Scale("C", Mode.AEOLIAN)
        var result = scale.getScaleNotes()
        assertEquals(listOf("C", "D", "Eb", "F", "G", "Ab", "Bb"), result)
        scale = Scale("G", Mode.LYDIAN)
        result = scale.getScaleNotes()
        assertEquals(listOf("G", "A", "B", "C#", "D", "E", "F#"), result)
        scale = Scale("G#", Mode.LYDIAN)
        result = scale.getScaleNotes()
        assertEquals(listOf("G#", "A#", "B#", "C##", "D#", "E#", "F##"), result)
    }

    @Test
    fun testScaleGetChordAtDegree(){
        var scale = Scale("C", Mode.AEOLIAN)
        var result = scale.getChordAtDegree(2)
        assertEquals("Eb", result)
        result = scale.getChordAtDegree(0)
        assertEquals("Cm", result)
        result = scale.getChordAtDegree(1)
        assertEquals("Ddim", result)
        result = scale.getChordAtDegree(1, seventh = true)
        assertEquals("Dm7b5", result)
    }
}