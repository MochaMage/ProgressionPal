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
    private val diminishedSeventhChord = setOf(Interval.UNISON,
                                                Interval.MINOR_THIRD,
                                                Interval.DIMINISHED_FIFTH,
                                                Interval.DIMINISHED_SEVENTH)
    private val majorSixthChord = setOf(Interval.UNISON,
                                        Interval.MAJOR_THIRD,
                                        Interval.PERFECT_FIFTH,
                                        Interval.MAJOR_SIXTH)

    fun getNotesForChord(chordName: String): MutableList<String> {
        val chordRegex = "^([a-gA-G])([b#])?(m|\\+|dim7|sus[2,4]|m7b5|dim)?(M7|7|9|11|13)?$"
        val regex = Regex(chordRegex)
        val results = regex.find(chordName)
        val rootNoteGroup = results?.groups?.get(1)
        val accidentalGroup = results?.groups?.get(2)
        val qualityGroup = results?.groups?.get(3)
        val extensionGroup = results?.groups?.get(4)

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
        } else if (qualityGroup.value.startsWith("sus")){
            if(qualityGroup.value == "sus4") {
                chordNotes = getNotesForChord(rootNote, suspendedFourChord)
            } else {
                chordNotes = getNotesForChord(rootNote, suspendedTwoChord)
            }
        }

        if(extensionGroup != null) {
            var extensions = getExtensionNotes(rootNote, extensionGroup.value)
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
        if(intervals.containsAll(majorSixthChord)){
            return "${notes[0]}6"
        }

        // If sixths are found and were not handled by the special case above,
        // we invert them until we get the root in the first position
        if (intervals.contains(Interval.MINOR_SIXTH)) {
            val sixthIdx = intervals.indexOf(Interval.MINOR_SIXTH)
            val removedNote = notes.removeAt(sixthIdx)
            notes.add(0, removedNote)
            return identifyChord(notes)
        } else if (intervals.contains(Interval.MAJOR_SIXTH)){
            val sixthIdx = intervals.indexOf(Interval.MAJOR_SIXTH)
            val removedNote = notes.removeAt(sixthIdx)
            notes.add(0, removedNote)
            return identifyChord(notes)
        }
        val rootNote = notes[0]
        if (intervals.containsAll(diminishedSeventhChord)) {
            return "${rootNote}dim7"
        } else if (intervals.containsAll(halfDiminishedChord)) {
            return "${rootNote}m7b5"
        } else if (intervals.containsAll(suspendedTwoChord)){
            return "${rootNote}sus2"
        } else if (intervals.containsAll(suspendedFourChord)){
            return "${rootNote}sus4"
        } else if (intervals.containsAll(dominantSeventhChord)) {
            return "${rootNote}7"
        } else if(intervals.containsAll(majorSeventhChord)) {
            return "${rootNote}M7"
        } else if (intervals.containsAll(majorChord)) {
            return rootNote
        } else if (intervals.containsAll(minorChord)) {
            return "${rootNote}m"
        } else if (intervals.containsAll(augmentedChord)) {
            return "${rootNote}+"
        } else if (intervals.containsAll(diminishedChord)) {
            return "${rootNote}dim"
        }

        return "no chord found"
    }
}