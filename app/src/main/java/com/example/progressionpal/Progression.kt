package com.example.progressionpal

class Progression(private val key: String, private val mode: Mode) {
    private var idxToFunctionDegree = mapOf(0 to "I",
                                            1 to "II",
                                            2 to "III",
                                            3 to "IV",
                                            4 to "V",
                                            5 to "VI",
                                            6 to "VII")

    private var scale: Scale = Scale(key, mode)
    var chordProgression: MutableList<String> = mutableListOf()
    var degreeProgression: MutableList<String> = mutableListOf()


    private val intervalHelper = IntervalHelper()

    fun addChord(chord: String): String {
        // Detect the function of the chord being added
        val chordRoot = chord[0]
        val scaleNotes = scale.scaleNotes
        var degree = 0

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

    fun findSubstitutionsAndPassingChords(): MutableMap<String, MutableList<MutableList<Pair<String, Chord>>>> {
        val substitutions: MutableList<MutableList<Pair<String, Chord>>> = mutableListOf()
        val passingChords: MutableList<MutableList<Pair<String, Chord>>> = mutableListOf()
        val substitutionHelper = SubstitutionHelper(key)
        for (i in chordProgression.indices) {
            if (substitutions.size - 1 < i) {
                substitutions.add(mutableListOf())
            }
            if (passingChords.size - 1 < i) {
                passingChords.add(mutableListOf())
            }
            // Negative harmony
            val negativeEquivalent = substitutionHelper.getNegativeHarmonyChord(chordProgression[i])
            substitutions[i].add(Pair("negativeHarmony", negativeEquivalent))
            passingChords[i].add(Pair("negativeHarmony", negativeEquivalent))

            // V7 to iiDim and tritone substitution
            if (degreeProgression[i] == "V7") {
                substitutions[i].add(Pair("V7toiidim7", Chord("${scale.scaleNotes[1]}dim7")))

                // Tritone substitution
                val fifthChordRoot = Chord(chordProgression[i]).notes[0]
                val tritoneRoot =
                    intervalHelper.getNoteAtInterval(fifthChordRoot, Interval.DIMINISHED_FIFTH)
                substitutions[i].add(Pair("tritoneSubstitution", Chord("${tritoneRoot}7")))
            }

            // Neapolitan 6th
            if (mode == Mode.AEOLIAN &&
                degreeProgression[i] == "IVm" && degreeProgression[i + 1].startsWith("V")) {
                val fourthChordNotes = Chord(chordProgression[i]).notes
                val fifth = fourthChordNotes.removeLast()
                val fifthReplacement =
                    intervalHelper.getNoteAtInterval(fifth, Interval.MINOR_SECOND)
                fourthChordNotes.add(fifthReplacement)
                val chordName = Chord(notes=fourthChordNotes)
                substitutions[i].add(Pair("neapolitan6", chordName))
            }

            // French augmented 6th
            if (degreeProgression[i] == "IVm") {
                val rootNote =
                    intervalHelper.getNoteAtInterval(scale.scaleNotes[0], Interval.MINOR_SIXTH)
                substitutions[i].add(Pair("french6", Chord("${rootNote}7b5")))
                passingChords[i].add(Pair("french6", Chord("${rootNote}7b5")))
            }

            // Cadential 6-4
            if (degreeProgression[i].startsWith("V")) {
                val chord = scale.getChordAtDegree(0)
                val chordNotes = chord.notes
                val fifth = chordNotes.removeLast()
                chordNotes.add(0, fifth)
                val newChordName = Chord(notes=chordNotes)
                passingChords[i].add(Pair("cadential64", newChordName))
            }
        }
        return mutableMapOf("substitutions" to substitutions, "passingChords" to passingChords)
    }
}