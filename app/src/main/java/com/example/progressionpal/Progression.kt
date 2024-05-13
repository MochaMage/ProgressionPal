package com.example.progressionpal

class Progression(key: String, mode: Mode) {
    private var idxToFunctionDegree = mapOf(0 to "I",
                                            1 to "ii",
                                            2 to "ii",
                                            3 to "IV",
                                            4 to "V",
                                            5 to "vi",
                                            6 to "vii")

    private var key = key
    private var mode = mode

    private var scale: Scale
    private var chordProgression: MutableList<Pair<String, String>> = mutableListOf()

    init {
        scale = Scale(key, mode)
    }

    fun getChordProgression(): MutableList<Pair<String, String>> {
        return chordProgression
    }
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
        chordProgression.add(Pair(verboseFunction, chord))

        return verboseFunction
    }

}