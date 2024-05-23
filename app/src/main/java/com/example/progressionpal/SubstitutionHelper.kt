package com.example.progressionpal

class SubstitutionHelper(private val key: String) {
    private var negativeHarmonyMapping: MutableList<Pair<String, String>> = mutableListOf<Pair<String, String>>()
    private var intervalHelper = IntervalHelper()

    init {
        // Find the negative harmony mappings for the given scale
        generateNegativeHarmonyMapping()
    }

    private fun generateNegativeHarmonyMapping(){
        if (negativeHarmonyMapping.isEmpty()) {
            negativeHarmonyMapping.add(
                Pair(key, intervalHelper.getNoteAtInterval(key, Interval.PERFECT_FIFTH))
            )
        }
        for(i in 1..4) {

             val x = intervalHelper.getNoteAtInterval(negativeHarmonyMapping[i - 1].first, Interval.PERFECT_FOURTH)
             val y = intervalHelper.getNoteAtInterval(negativeHarmonyMapping[i - 1].second, Interval.PERFECT_FIFTH)
             negativeHarmonyMapping.add(Pair(x,y))
        }

        val x = intervalHelper.getNoteAtInterval(negativeHarmonyMapping.last().first, Interval.PERFECT_FOURTH)
        val y = intervalHelper.getNoteAtInterval(x, Interval.PERFECT_FOURTH)
        negativeHarmonyMapping.add(Pair(x,y))
    }

    fun getNegativeHarmonyMapping(): MutableList<Pair<String, String>> {
        return negativeHarmonyMapping
    }

    fun getNegativeHarmonyEquivalentNote(note: String): String {
        for (pair in negativeHarmonyMapping){
            if (note == pair.first) {
                return pair.second
            } else if (note == pair.second) {
                return pair.first
            }
        }
        return ""
    }

    fun getNegativeHarmonyChord(chordName: String): Chord {
        val chordNotes: MutableList<String> = Chord(chordName).notes
        val negativeHarmonyChordNotes: MutableList<String> = mutableListOf()

        for (note in chordNotes) {
            negativeHarmonyChordNotes.add(getNegativeHarmonyEquivalentNote(note))
        }
        return Chord(notes = negativeHarmonyChordNotes)
    }
}