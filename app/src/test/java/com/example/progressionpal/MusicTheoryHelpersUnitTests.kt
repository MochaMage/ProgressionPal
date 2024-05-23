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
        var result = Chord("A")
        assertEquals(mutableListOf("A", "C#", "E"), result.notes)
        result = Chord("A+")
        assertEquals(mutableListOf("A", "C#", "E#"), result.notes)
        result = Chord("Asus2")
        assertEquals(mutableListOf("A", "B", "E"), result.notes)
        result = Chord("Asus4")
        assertEquals(mutableListOf("A", "D", "E"), result.notes)
        result = Chord("Am")
        assertEquals(mutableListOf("A", "C", "E"), result.notes)

        // Accidentals
        result = Chord("Ab")
        assertEquals(result.notes, mutableListOf("Ab", "C", "Eb"))
        result = Chord("A#")
        assertEquals(result.notes, mutableListOf("A#", "C##", "E#"))

        // Minors and extensions
        result = Chord("Am7")
        assertEquals(result.notes, mutableListOf("A", "C", "E", "G"))
        result = Chord("Adim")
        assertEquals(result.notes, mutableListOf("A", "C", "Eb"))
        result = Chord("Am7b5")
        assertEquals(result.notes, mutableListOf("A", "C", "Eb", "G"))
        result = Chord("A7b5")
        assertEquals(result.notes, mutableListOf("A", "C#", "Eb", "G"))
        result = Chord("Adim7")
        assertEquals(result.notes, mutableListOf("A", "C", "Eb", "Gb"))
        result = Chord("AM7")
        assertEquals(result.notes, mutableListOf("A", "C#", "E", "G#"))
        result = Chord("A7")
        assertEquals(result.notes, mutableListOf("A", "C#", "E", "G"))

        // Suspensions
        result = Chord("A7sus4")
        assertEquals(result.notes, mutableListOf("A", "D", "E", "G"))
        result = Chord("A7sus")
        assertEquals(result.notes, mutableListOf("A", "D", "E", "G"))

        // Inversions
        result = Chord("A/C#")
        assertEquals(result.notes, mutableListOf("C#", "A", "E"))
    }

    @Test
    fun testGetNegativeHarmonyChord(){
        val subHelper = SubstitutionHelper("C")
        var result = subHelper.getNegativeHarmonyChord("C")
        assertEquals(result.name, "Cm/G")
        result = subHelper.getNegativeHarmonyChord("Dm")
        assertEquals(result.name, "Bb/F")
        result = subHelper.getNegativeHarmonyChord("G")
        assertEquals(result.name, "Fm/C")
        result = subHelper.getNegativeHarmonyChord("Bdim7")
        assertEquals(result.name, "Bdim7/Ab")
        result = subHelper.getNegativeHarmonyChord("Em")
        assertEquals(result.name, "Ab/Eb")
    }

    @Test
    fun testIdentifyChord(){
        var result = Chord(notes=mutableListOf("C", "A", "E"))
        assertEquals(result.name, "Am/C")
        result = Chord(notes=mutableListOf("C", "A", "E"))
        assertEquals(result.name, "Am/C")
        result = Chord(notes=mutableListOf("C", "E", "A"))
        assertEquals(result.name, "Am/C")
        result = Chord(notes=mutableListOf("A", "E", "C"))
        assertEquals(result.name, "Am")
        result = Chord(notes=mutableListOf("G", "E", "C"))
        assertEquals(result.name, "C/G")
        result = Chord(notes=mutableListOf("G", "C", "E"))
        assertEquals(result.name, "C/G")
        result = Chord(notes=mutableListOf("G", "C", "E", "A"))
        assertEquals(result.name, "C6/G")
        result = Chord(notes=mutableListOf("A", "C", "Eb"))
        assertEquals("Adim", result.name)
        result = Chord(notes=mutableListOf("C", "A", "Eb"))
        assertEquals(result.name, "Adim/C")
        result = Chord(notes=mutableListOf("A", "Eb", "C", "G"))
        assertEquals("Am7b5", result.name)
        result = Chord(notes=mutableListOf("Gb", "A", "Eb", "C"))
        assertEquals(result.name, "Adim7/Gb")
        result = Chord(notes=mutableListOf("A", "B", "E"))
        assertEquals(result.name, "Asus2")
        result = Chord(notes=mutableListOf("A", "D", "E"))
        assertEquals(result.name, "Asus4")
        result = Chord(notes=mutableListOf("A", "B", "E", "G"))
        assertEquals("A7sus2", result.name)
        result = Chord(notes=mutableListOf("G", "E", "B", "A"))
        assertEquals("A7sus2/G", result.name)
        result = Chord(notes=mutableListOf("C#", "A"))
        assertEquals("A/C#", result.name)
        result = Chord(notes=mutableListOf("C#", "A", "G"))
        assertEquals("A7/C#", result.name)
    }

    @Test
    fun testScaleHelper(){
        var scale = Scale("C", Mode.AEOLIAN)
        var result = scale.scaleNotes
        assertEquals(listOf("C", "D", "Eb", "F", "G", "Ab", "Bb"), result)
        scale = Scale("G", Mode.LYDIAN)
        result = scale.scaleNotes
        assertEquals(listOf("G", "A", "B", "C#", "D", "E", "F#"), result)
        scale = Scale("G#", Mode.LYDIAN)
        result = scale.scaleNotes
        assertEquals(listOf("G#", "A#", "B#", "C##", "D#", "E#", "F##"), result)
    }

    @Test
    fun testScaleGetChordAtDegree(){
        var scale = Scale("C", Mode.AEOLIAN)
        var result = scale.getChordAtDegree(2)
        assertEquals("Eb", result.name)
        result = scale.getChordAtDegree(0)
        assertEquals("Cm", result.name)
        result = scale.getChordAtDegree(1)
        assertEquals("Ddim", result.name)
        result = scale.getChordAtDegree(1, seventh = true)
        assertEquals("Dm7b5", result.name)
        scale = Scale("F#", Mode.LYDIAN)
        result = scale.getChordAtDegree(6, true)
        assertEquals("E#m7", result.name)
    }

    @Test
    fun testProgression(){
        val progression = Progression("A", Mode.AEOLIAN)
        progression.addChord("Am")
        progression.addChord("Dm")
        progression.addChord("E7")
        progression.addChord("Am")
        println(progression.chordProgression)
        println(progression.degreeProgression)
        println(progression.findSubstitutionsAndPassingChords())
    }

    @Test
    fun testChord(){
        var chord = Chord(name = "Am")
        assertEquals(ChordQuality.MINOR, chord.quality)
        assertEquals("A", chord.bass)
        assertEquals(mutableListOf("A", "C", "E"), chord.notes)

        chord = Chord(name = "Am/C")
        assertEquals(ChordQuality.MINOR, chord.quality)
        assertEquals("C", chord.bass)
        assertEquals("C", chord.notes.first())

        chord = Chord(notes = mutableListOf("C", "A", "E"))
        assertEquals("Am/C", chord.name)
        assertEquals(ChordQuality.MINOR, chord.quality)
        assertEquals("C", chord.bass)
        assertEquals("C", chord.notes.first())

        chord = Chord(notes = mutableListOf("E", "A", "C"))
        assertEquals("Am/E", chord.name)
        assertEquals(ChordQuality.MINOR, chord.quality)
        assertEquals("E", chord.bass)
        assertEquals("E", chord.notes.first())

        chord = Chord(notes = mutableListOf("E#", "A#", "C#"))
        assertEquals("A#m/E#", chord.name)
        assertEquals(ChordQuality.MINOR, chord.quality)
        assertEquals("E#", chord.bass)
        assertEquals("E#", chord.notes.first())

        chord = Chord(notes = mutableListOf("B", "A", "E"))
        assertEquals("B7sus4", chord.name)
        assertEquals(ChordQuality.SUSPENDED, chord.quality)
        assertEquals("B", chord.bass)
        assertEquals("B", chord.notes.first())

        chord = Chord(notes = mutableListOf("A", "C", "E", "F#"))
        assertEquals("Am6", chord.name)
        assertEquals(ChordQuality.MINOR, chord.quality)
        assertEquals("A", chord.bass)
        assertEquals("A", chord.notes.first())

        chord = Chord(notes = mutableListOf("C", "A", "E", "F#"))
        assertEquals("Am6/C", chord.name)
        assertEquals(ChordQuality.MINOR, chord.quality)
        assertEquals("C", chord.bass)
        assertEquals("C", chord.notes.first())

        chord = Chord(notes = mutableListOf("A", "D", "E", "F#"))
        assertEquals("A6sus4", chord.name)
        assertEquals(ChordQuality.SUSPENDED, chord.quality)
        assertEquals("A", chord.bass)
        assertEquals("A", chord.notes.first())
    }
}