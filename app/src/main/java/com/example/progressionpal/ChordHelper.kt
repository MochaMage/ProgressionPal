package com.example.progressionpal

class ChordHelper {
    // Triads
    private val majorChord = setOf(Interval.UNISON,
                                    Interval.MAJOR_THIRD,
                                    Interval.PERFECT_FIFTH)
    private val minorChord = setOf(Interval.UNISON,
                                    Interval.MINOR_THIRD,
                                    Interval.PERFECT_FIFTH)
    private val augmentedChord = setOf(Interval.UNISON,
                                    Interval.MAJOR_THIRD,
                                    Interval.AUGMENTED_FIFTH)
    private val diminishedChord = setOf(Interval.UNISON,
                                         Interval.MINOR_THIRD,
                                         Interval.DIMINISHED_FIFTH)
    private val suspendedFourChord = setOf(Interval.UNISON,
                                           Interval.PERFECT_FOURTH,
                                           Interval.PERFECT_FIFTH)
    private val suspendedTwoChord = setOf(Interval.UNISON,
                                          Interval.MAJOR_SECOND,
                                          Interval.PERFECT_FIFTH)

    // Tetrads
    private val halfDiminishedChord = setOf(Interval.UNISON,
                                           Interval.MINOR_THIRD,
                                           Interval.DIMINISHED_FIFTH,
                                           Interval.MINOR_SEVENTH)
    private val dominantSeventhChord = setOf(Interval.UNISON,
                                             Interval.MAJOR_THIRD,
                                             Interval.PERFECT_FIFTH,
                                             Interval.MINOR_SEVENTH)
    private val majorSeventhChord = setOf(Interval.UNISON,
                                          Interval.MAJOR_THIRD,
                                          Interval.PERFECT_FIFTH,
                                          Interval.MAJOR_SEVENTH)
    private val minorSeventhChord = setOf(Interval.UNISON,
                                          Interval.MINOR_THIRD,
                                          Interval.PERFECT_FIFTH,
                                          Interval.MINOR_SEVENTH)
    private val diminishedSeventhChord = setOf(Interval.UNISON,
                                                Interval.MINOR_THIRD,
                                                Interval.DIMINISHED_FIFTH,
                                                Interval.DIMINISHED_SEVENTH)
    private val majorSixthChord = setOf(Interval.UNISON,
                                        Interval.MAJOR_THIRD,
                                        Interval.PERFECT_FIFTH,
                                        Interval.MAJOR_SIXTH)

    fun getNotesForChord(chordName: String): MutableList<String> {
        val chordRegex = "^([a-gA-G])([b#])?(m|\\+|dim7|m7b5|dim)?(M7|7|9|11|13)?(sus[2,4])?$"
        val regex = Regex(chordRegex)
        val results = regex.find(chordName)
        val rootNoteGroup = results?.groups?.get(1)
        val accidentalGroup = results?.groups?.get(2)
        val qualityGroup = results?.groups?.get(3)
        val extensionGroup = results?.groups?.get(4)
        val suspendedGroup = results?.groups?.get(5)

        var rootNote = rootNoteGroup!!.value
        if(accidentalGroup != null){
           rootNote += accidentalGroup.value
        }

        var chordNotes: MutableList<String> = mutableListOf()

        if(qualityGroup == null) {
           chordNotes = getNotesForChord(rootNote, majorChord)
        } else if (qualityGroup.value == "m") {
            chordNotes = getNotesForChord(rootNote, minorChord)
        } else if (qualityGroup.value == "+") {
            chordNotes = getNotesForChord(rootNote, augmentedChord)
        } else if (qualityGroup.value == "dim"){
            chordNotes = getNotesForChord(rootNote, diminishedChord)
        } else if (qualityGroup.value == "m7b5") {
            chordNotes = getNotesForChord(rootNote, halfDiminishedChord)
        } else if (qualityGroup.value == "dim7"){
            chordNotes = getNotesForChord(rootNote, diminishedSeventhChord)
        } else if (suspendedGroup != null){
            chordNotes = if(suspendedGroup.value == "sus4") {
                getNotesForChord(rootNote, suspendedFourChord)
            } else {
                getNotesForChord(rootNote, suspendedTwoChord)
            }
        }

        if(extensionGroup != null) {
            val extensions = getExtensionNotes(rootNote, extensionGroup.value)
            chordNotes.addAll(extensions)
        }

        return chordNotes
    }

    private fun getNotesForChord(rootNote: String, chordType:Set<Interval>): MutableList<String> {
        val chordNotes: MutableList<String> = mutableListOf()
        val intervalHelper = IntervalHelper()
        for(interval in chordType){
            chordNotes.add(intervalHelper.getNoteAtInterval(rootNote, interval))
        }
        return chordNotes
    }

    private fun getExtensionNotes(rootNote: String, extension: String): MutableList<String> {
        val extensionNotes: MutableList<String> = mutableListOf()
        val intervalHelper = IntervalHelper()

        if (extension == "M7"){
            extensionNotes.add(intervalHelper.getNoteAtInterval(rootNote, Interval.MAJOR_SEVENTH))
        } else if (extension == "7") {
            extensionNotes.add(intervalHelper.getNoteAtInterval(rootNote, Interval.MINOR_SEVENTH))
        }

        return extensionNotes
    }

    private fun reorderNotes(notes: MutableList<String>,
                             intervals: MutableList<Interval>,
                             interval: Interval): MutableList<String>{
        val idx = intervals.indexOf(interval)
        val removedNote = notes.removeAt(idx)
        notes.add(0, removedNote)
        return notes
    }

    fun identifyChord(notes: MutableList<String>): String {
        val firstNote = notes[0]
        val otherNotes = notes.slice(1..<notes.size)
        val intervals: MutableList<Interval> = mutableListOf(Interval.UNISON)
        val intervalHelper = IntervalHelper()
        for (note in otherNotes) {
            val interval = intervalHelper.getIntervalDistance(firstNote, note)
            intervals.add(interval)
        }

        //Special case: Sixth chords
        if (intervals.containsAll(majorSixthChord)) {
            return "${notes[0]}6"
        }

        // If sixths are found and were not handled by the special case above,
        // we invert them until we get the root in the first position
        // Same goes for a chord containing both a third and a fourth. Invert it until we get the
        // expected sus chord.
        if (intervals.contains(Interval.MINOR_SIXTH)) {
            return identifyChord(reorderNotes(notes, intervals, Interval.MINOR_SIXTH))
        } else if (intervals.contains(Interval.MAJOR_SIXTH)) {
            return identifyChord(reorderNotes(notes, intervals, Interval.MAJOR_SIXTH))
        } else if (intervals.contains(Interval.PERFECT_FOURTH) && (intervals.contains(Interval.MINOR_THIRD) || intervals.contains(Interval.MAJOR_THIRD))){
            return identifyChord(reorderNotes(notes, intervals, Interval.PERFECT_FOURTH))
        }
        val rootNote = notes[0]
        var seventhType = ""
        var suspensionType = ""
        var qualityType = ""

        if (intervals.contains(Interval.MINOR_SEVENTH)) {
            seventhType = "7"
        } else if (intervals.contains(Interval.MAJOR_SEVENTH)) {
            seventhType = "M7"
        }

        if (intervals.contains(Interval.AUGMENTED_FIFTH)) {
            qualityType = "+"
        } else if (intervals.contains(Interval.DIMINISHED_FIFTH)) {
            if (intervals.contains(Interval.MINOR_SEVENTH)) {

                seventhType = "m7b5"
            } else if (intervals.contains(Interval.DIMINISHED_SEVENTH)) {
                seventhType = "dim7"
            } else {
                qualityType = "dim"
            }
        } else {
            if (intervals.contains(Interval.MINOR_THIRD)) {
                qualityType = "m"
            } else if (intervals.contains(Interval.MAJOR_SECOND)) {
                suspensionType = "sus2"
            } else if (intervals.contains(Interval.PERFECT_FOURTH)) {
                suspensionType = "sus4"
            }
        }

        return "${rootNote}${qualityType}${seventhType}${suspensionType}"
    }
}