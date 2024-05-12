package com.example.progressionpal

import kotlin.math.abs

class IntervalHelper {

    val intervalNames = mapOf(
        Interval.UNISON to R.string.interval_unison,
        Interval.MINOR_SECOND to R.string.interval_minor_second,
        Interval.MAJOR_SECOND to R.string.interval_major_second,
        Interval.MINOR_THIRD to R.string.interval_minor_third,
        Interval.MAJOR_THIRD to R.string.interval_major_third,
        Interval.PERFECT_FOURTH to R.string.interval_perfect_fourth,
        Interval.DIMINISHED_FIFTH to R.string.interval_diminished_fifth,
        Interval.PERFECT_FIFTH to R.string.interval_perfect_fifth,
        Interval.MINOR_SIXTH to R.string.interval_minor_sixth,
        Interval.MAJOR_SIXTH to R.string.interval_major_sixth,
        Interval.MINOR_SEVENTH to R.string.interval_minor_seventh,
        Interval.MAJOR_SEVENTH to R.string.interval_major_seventh,
        Interval.OCTAVE to R.string.interval_octave)

    private val noteIntervals: Map<Char, Int> = mapOf(
        'C' to Note.C.semitones,
        'D' to Note.D.semitones,
        'E' to Note.E.semitones,
        'F' to Note.F.semitones,
        'G' to Note.G.semitones,
        'A' to Note.A.semitones,
        'B' to Note.B.semitones
    )

    private val notes: List<Char> = listOf('C', 'D', 'E', 'F', 'G', 'A', 'B')

    private fun getNoteValue(note: String): Int {
        if(note.length > 3){
            throw Exception("Invalid Note Name")
        }
        var modifier = 0

        if(note.length > 1) {
            if (note.endsWith("bb")) {
                modifier -= 2
            } else if (note.endsWith("b")) {
                modifier -= 1
            } else if (note.endsWith("##")){
                modifier += 2
            } else if (note.endsWith("#")){
                modifier += 1
            }
        }
        val baseNoteName = note[0]
        val baseNoteValue = noteIntervals[baseNoteName]!!

        return baseNoteValue + modifier
    }

    fun getIntervalDistance(note1: String, note2: String): Interval {
        val note1Val = getNoteValue(note1)
        val note2Val = getNoteValue(note2)
        var semitoneDistance = note2Val - note1Val
        if (semitoneDistance < 0){
            semitoneDistance += Interval.OCTAVE.semitones
        }
        val baseNote1 = note1[0]
        val baseNote2 = note2[0]

        val note1Idx = notes.indexOf(baseNote1)
        val note2Idx = notes.indexOf(baseNote2)
        var distance = note2Idx - note1Idx
        if(distance < 0) {
            distance += notes.size
        }
        val intervalValues = Interval.entries.toTypedArray()
        var matchingInterval: Interval = Interval.UNISON
        for(interval in intervalValues){
            if(interval.semitones == semitoneDistance && interval.note_distance == distance){
                matchingInterval = interval
                break
            }
        }
        return matchingInterval
    }

    fun getNoteAtInterval(noteName: String, interval: Interval): String {
        val noteValue = getNoteValue(noteName)
        val rootIdx = notes.indexOf(noteName[0])
        val newNoteIdx = (rootIdx + interval.note_distance) % notes.size
        val newNoteValue = (noteValue + interval.semitones) % Interval.OCTAVE.semitones

        val newNote = notes[newNoteIdx]
        var accidental = ""

        val intervalDiff = noteIntervals[newNote]!! - newNoteValue

        accidental = when (intervalDiff) {
            1 -> "b"
            2 -> "bb"
            -11 -> "b" // Special case for Cb
            11 -> "#" // Special case for B#
            -1 -> "#"
            -2 -> "##"
            else -> ""
        }

        return newNote + accidental
    }
}