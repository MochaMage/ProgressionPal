package com.example.progressionpal

class Progression(private val key: String, private val mode: Mode) {
    private var idxToFunctionDegree = mapOf(0 to "I",
                                            1 to "II",
                                            2 to "III",
                                            3 to "IV",
                                            4 to "V",
                                            5 to "VI",
                                            6 to "VII")

    var scale: Scale = Scale(key, mode)
    var chordProgression: MutableList<String> = mutableListOf()
    var degreeProgression: MutableList<String> = mutableListOf()
    var substitutions: MutableList<MutableList<Pair<String, String>>> = mutableListOf()

    private val chordHelper = ChordHelper()
    private val intervalHelper = IntervalHelper()

    fun addChord(chord: String): String {
        // Detect the function of the chord being added
        val chordRoot = chord[0]
        val scaleNotes = scale.getScaleNotes()
        var degree: Int = 0

        for (i in scaleNotes.indices){
            val scaleRoot = scaleNotes[i][0]
            if (scaleRoot == chordRoot) {
                degree = i
            }
        }

        val verboseFunction = "${idxToFunctionDegree[degree]}${chord.slice(1 until chord.length)}"
        chordProgression.add(chord)
        degreeProgression.add(verboseFunction)

        return verboseFunction
    }

    fun findSubstitutions(): MutableList<MutableList<kotlin.Pair<String, String>>> {
        val substitutionHelper = SubstitutionHelper(key)
        for (i in chordProgression.indices) {
            // Negative harmony
            val negativeEquivalent = substitutionHelper.getNegativeHarmonyChord(chordProgression[i])
            if (substitutions.size - 1 < i) {
                substitutions.add(mutableListOf())
            }
            substitutions[i].add(Pair("negativeHarmony", negativeEquivalent))

            // V7 to iiDim
            if (degreeProgression[i] == "V7") {
                substitutions[i].add(Pair("V7toiidim7","${scale.getScaleNotes()[1]}dim7"))
            }

            // Neapolitan 6th
            if (mode == Mode.AEOLIAN && degreeProgression[i] == "IVm" && degreeProgression[i + 1].startsWith("V")){
                val fourthChordNotes =  chordHelper.getNotesForChord(chordProgression[i])
                val fifth = fourthChordNotes.removeLast()
                val fifthReplacement = intervalHelper.getNoteAtInterval(fifth, Interval.MINOR_SECOND)
                fourthChordNotes.add(fifthReplacement)
                val chordName = chordHelper.identifyChord(fourthChordNotes, slashNotation = true)
                substitutions[i].add(Pair("neapolitan6", chordName))
            }
        }

        return substitutions
    }
}