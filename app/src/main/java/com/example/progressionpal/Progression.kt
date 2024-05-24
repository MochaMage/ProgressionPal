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
    var chordProgression: MutableList<Chord> = mutableListOf()


    private val intervalHelper = IntervalHelper()

    fun addChord(chord: Chord) {
        // Detect the function of the chord being added
        val chordRoot = chord.root
        val scaleNotes = scale.scaleNotes
        var degree = 0

        for (i in scaleNotes.indices){
            val scaleRoot = scaleNotes[i][0]
            if (scaleRoot == chordRoot[0]) {
                degree = i
            }
        }

        val verboseFunction = "${idxToFunctionDegree[degree]}${chord.name.slice(1 until chord.name.length)}"
        chord.harmonicFunction = verboseFunction
        chordProgression.add(chord)
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
            val chord = chordProgression[i]
            val fifth = intervalHelper.getNoteAtInterval(chord.root, Interval.PERFECT_FIFTH)

            // Secondary Dominant
            val secondaryDominant = Chord(name = "${fifth}7")
            passingChords[i].add(Pair("secondaryDominant", secondaryDominant))

            // Secondary Diminished
            val seventh = intervalHelper.getNoteAtInterval(chord.root, Interval.MAJOR_SEVENTH)
            val secondaryDiminished = Chord(name = "${seventh}dim")
            passingChords[i].add(Pair("secondaryDiminished", secondaryDiminished))

            // Negative harmony
            val negativeEquivalent = substitutionHelper.getNegativeHarmonyChord(chord)
            substitutions[i].add(Pair("negativeHarmony", negativeEquivalent))
            passingChords[i].add(Pair("negativeHarmony", negativeEquivalent))

            // V7 to iiDim and tritone substitution
            if (chord.harmonicFunction == "V7") {
                substitutions[i].add(Pair("V7toiidim7", Chord("${scale.scaleNotes[1]}dim7")))

                // Tritone substitution
                val fifthChordRoot = chord.notes[0]
                val tritoneRoot =
                    intervalHelper.getNoteAtInterval(fifthChordRoot, Interval.DIMINISHED_FIFTH)
                substitutions[i].add(Pair("tritoneSubstitution", Chord("${tritoneRoot}7")))
            }

            // Neapolitan 6th
            if (mode == Mode.AEOLIAN &&
                chord.harmonicFunction == "IVm" && chordProgression[i + 1].harmonicFunction.startsWith("V")) {
                val fourthChordNotes = chord.notes
                val fifthNote = fourthChordNotes.removeLast()
                val fifthReplacement =
                    intervalHelper.getNoteAtInterval(fifthNote, Interval.MINOR_SECOND)
                fourthChordNotes.add(fifthReplacement)
                val chordName = Chord(notes=fourthChordNotes)
                substitutions[i].add(Pair("neapolitan6", chordName))
            }

            // French augmented 6th
            if (chord.harmonicFunction == "IVm") {
                val rootNote =
                    intervalHelper.getNoteAtInterval(scale.scaleNotes[0], Interval.MINOR_SIXTH)
                val bass = intervalHelper.getNoteAtInterval(rootNote, Interval.MAJOR_THIRD)
                substitutions[i].add(Pair("french6", Chord("${rootNote}7b5/${bass}")))
                passingChords[i].add(Pair("french6", Chord("${rootNote}7b5/${bass}")))
            }

            // Cadential 6-4
            if (chord.harmonicFunction.startsWith("V")) {
                val firstChord = scale.getChordAtDegree(0)
                val chordNotes = firstChord.notes
                val fifthNote = chordNotes.removeLast()
                chordNotes.add(0, fifthNote)
                val newChordName = Chord(notes=chordNotes)
                passingChords[i].add(Pair("cadential64", newChordName))
            }

        }
        return mutableMapOf("substitutions" to substitutions, "passingChords" to passingChords)
    }
}