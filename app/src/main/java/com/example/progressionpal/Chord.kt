package com.example.progressionpal

data class Chord(var name: String = "",
                 var notes: MutableList<String> = mutableListOf(),
                 val intervals: MutableList<Interval> = mutableListOf(),
                 var quality: ChordQuality = ChordQuality.MAJOR,
                 var bass: String = "",
                 var extension: String = "",
                 var harmonicFunction: String = ""
                 ){

    init {
        if (name.isEmpty()){
            name = identifyChord(notes.toMutableList(), true)
        } else if (notes.isEmpty()){
            notes = getNotesForChord(name)
        } else {
            throw Exception("Invalid chord configuration")
        }

        val intervalHelper = IntervalHelper()
        for (note in notes) {
            val interval = intervalHelper.getIntervalDistance(notes[0], note)
            intervals.add(interval)
        }
    }

    fun setHarmonicFunction(degree: Int) {

    }

    fun isSuspendedChord(): Boolean {
        return intervals.intersect(setOf(Interval.MAJOR_SECOND, Interval.PERFECT_FOURTH)).isNotEmpty()
    }

    fun isSeventhChord(): Boolean {
        return intervals.intersect(setOf(Interval.DIMINISHED_SEVENTH, Interval.MAJOR_SEVENTH, Interval.MINOR_SEVENTH)).isNotEmpty()
    }
    private fun getNotesForChord(chordName: String): MutableList<String> {
        val chordRegex = "^([a-gA-G])([b#])?(m|dim7|m7b5|7b5|dim|\\+)?(6|M7|7)?(sus[2,4]?)?/?([a-gA-G][b#]?)?$"
        val regex = Regex(chordRegex)
        val results = regex.find(chordName)
        val rootNoteGroup = results?.groups?.get(1)
        val accidentalGroup = results?.groups?.get(2)
        val qualityGroup = results?.groups?.get(3)
        val extensionGroup = results?.groups?.get(4)
        val suspendedGroup = results?.groups?.get(5)
        val slashGroup = results?.groups?.get(6)

        var rootNote = rootNoteGroup!!.value
        this.bass = rootNote
        if(accidentalGroup != null){
            rootNote += accidentalGroup.value
        }

        val chordNotes: MutableList<String> = mutableListOf(rootNote)
        val intervalHelper = IntervalHelper()

        if (qualityGroup != null){
            val quality = qualityGroup.value

            //third
            if (quality in setOf("m", "dim7", "dim", "m7b5")){
                val minorThird = intervalHelper.getNoteAtInterval(rootNote, Interval.MINOR_THIRD)
                chordNotes.add(minorThird)
                this.quality = ChordQuality.MINOR
            }  else {
                val majorThird = intervalHelper.getNoteAtInterval(rootNote, Interval.MAJOR_THIRD)
                chordNotes.add(majorThird)
            }

            //fifth
            when (quality) {
                in setOf("dim", "dim7", "m7b5", "7b5") -> {
                    val diminishedFifth = intervalHelper.getNoteAtInterval(rootNote, Interval.DIMINISHED_FIFTH)
                    chordNotes.add(diminishedFifth)
                    this.quality = ChordQuality.DIMINISHED
                }
                "+" -> {
                    val augmentedFifth = intervalHelper.getNoteAtInterval(rootNote, Interval.AUGMENTED_FIFTH)
                    chordNotes.add(augmentedFifth)
                    this.quality = ChordQuality.AUGMENTED
                }
                else -> {
                    val perfectFifth = intervalHelper.getNoteAtInterval(rootNote, Interval.PERFECT_FIFTH)
                    chordNotes.add(perfectFifth)
                }
            }

            // special case for m7b5
            if (quality in setOf("m7b5", "7b5")) {
                val minorSeventh = intervalHelper.getNoteAtInterval(rootNote, Interval.MINOR_SEVENTH)
                chordNotes.add(minorSeventh)
            } else if (quality == "dim7"){
                val dimSeventh = intervalHelper.getNoteAtInterval(rootNote, Interval.DIMINISHED_SEVENTH)
                chordNotes.add(dimSeventh)
            }
        } else if (suspendedGroup != null) {
            if (suspendedGroup.value == "sus2") {
                val majorSecond = intervalHelper.getNoteAtInterval(rootNote, Interval.MAJOR_SECOND)
                chordNotes.add(majorSecond)
            } else {
                val perfectFourth= intervalHelper.getNoteAtInterval(rootNote, Interval.PERFECT_FOURTH)
                chordNotes.add(perfectFourth)
            }
            val perfectFifth = intervalHelper.getNoteAtInterval(rootNote, Interval.PERFECT_FIFTH)
            chordNotes.add(perfectFifth)
            this.quality = ChordQuality.SUSPENDED
        } else {
            val majorThird = intervalHelper.getNoteAtInterval(rootNote, Interval.MAJOR_THIRD)
            chordNotes.add(majorThird)
            val perfectFifth = intervalHelper.getNoteAtInterval(rootNote, Interval.PERFECT_FIFTH)
            chordNotes.add(perfectFifth)
        }

        if(extensionGroup != null) {
            val extensions = getExtensionNotes(rootNote, extensionGroup.value)
            chordNotes.addAll(extensions)
        }

        if (slashGroup != null) {
            val bass = slashGroup.value
            val bassIdx = chordNotes.indexOf(bass)

            if (bassIdx == -1){
                throw Exception("GTFO jazz dork")
            }

            val removedNote = chordNotes.removeAt(bassIdx)
            chordNotes.add(0, removedNote)
            this.bass = removedNote
        }

        return chordNotes
    }

    private fun getExtensionNotes(rootNote: String, extension: String): MutableList<String> {
        val extensionNotes: MutableList<String> = mutableListOf()
        val intervalHelper = IntervalHelper()

        when (extension) {
            "M7" -> {
                extensionNotes.add(intervalHelper.getNoteAtInterval(rootNote, Interval.MAJOR_SEVENTH))
            }
            "7" -> {
                extensionNotes.add(intervalHelper.getNoteAtInterval(rootNote, Interval.MINOR_SEVENTH))
            }
            "6" -> {
                extensionNotes.add(intervalHelper.getNoteAtInterval(rootNote, Interval.MAJOR_SIXTH))
            }
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

    private fun identifyChord(notes: MutableList<String>, slashNotation: Boolean = false): String {
        val firstNote = notes[0]
        val otherNotes = notes.slice(1..<notes.size)
        val intervals: MutableList<Interval> = mutableListOf(Interval.UNISON)
        val intervalHelper = IntervalHelper()
        for (note in otherNotes) {
            val interval = intervalHelper.getIntervalDistance(firstNote, note)
            intervals.add(interval)
        }

        var slashNote = ""

        if (slashNotation) {
            slashNote = "/${firstNote}"
            this.bass = firstNote
        }

        // If sixths are found and the chord doesn't also contain a perfect fifth
        // we invert them until we get the root in the first position
        // Same goes for a chord containing both a third and a fourth. Invert it until we get the
        // expected sus chord.
        val sixthNote = intervals.intersect(setOf(Interval.MINOR_SIXTH, Interval.MAJOR_SIXTH))
        if (sixthNote.isNotEmpty() && !intervals.contains(Interval.PERFECT_FIFTH)) {
            return "${identifyChord(reorderNotes(notes, intervals, sixthNote.first()))}${slashNote}"
        }

        if (intervals.contains(Interval.PERFECT_FOURTH) && intervals.intersect(setOf(Interval.MINOR_THIRD, Interval.MAJOR_THIRD)).isNotEmpty()) {
            return "${identifyChord(reorderNotes(notes, intervals, Interval.PERFECT_FOURTH))}${slashNote}"
        }

        val rootNote = notes[0]
        var extension = ""
        var suspensionType = ""
        var qualityType = ""

        if (intervals.contains(Interval.MINOR_SEVENTH)) {
            extension = "7"
        } else if (intervals.contains(Interval.MAJOR_SEVENTH)) {
            extension = "M7"
        } else if (intervals.contains(Interval.MAJOR_SIXTH)) {
            extension = "6"
        }

        if (intervals.contains(Interval.AUGMENTED_FIFTH)) {
            qualityType = "+"
        } else if (intervals.contains(Interval.DIMINISHED_FIFTH)) {
            if (intervals.contains(Interval.MINOR_SEVENTH)) {
                extension = "m7b5"
            } else if (intervals.contains(Interval.DIMINISHED_SEVENTH)) {
                extension = "dim7"
            } else {
                qualityType = "dim"
            }
        } else {
            if (intervals.contains(Interval.MINOR_THIRD)) {
                qualityType = "m"
                this.quality = ChordQuality.MINOR
            } else if (intervals.contains(Interval.MAJOR_SECOND)) {
                suspensionType = "sus2"
                this.quality = ChordQuality.SUSPENDED
            } else if (intervals.contains(Interval.PERFECT_FOURTH)) {
                suspensionType = "sus4"
                this.quality = ChordQuality.SUSPENDED
            }
        }

        return "${rootNote}${qualityType}${extension}${suspensionType}"
    }
}
