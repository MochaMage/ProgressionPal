package com.example.progressionpal

data class Chord(var name: String = "", var notes: MutableList<String> = mutableListOf()){

    private val chordHelper = ChordHelper()
    private val intervals: MutableList<Interval> = mutableListOf()
    var harmonicFunction = 0
    val root = ""
    val third = ""
    val fifth = ""
    val seventh = ""

    init {
        if (name.isEmpty()){
            name = chordHelper.identifyChord(notes)
        } else if (notes.isEmpty()){
            notes = chordHelper.getNotesForChord(name)
        } else {
            throw Exception("Invalid chord configuration")
        }

        val intervalHelper = IntervalHelper()
        for (note in notes) {
            val interval = intervalHelper.getIntervalDistance(notes[0], note)
            intervals.add(interval)
        }
    }

    fun setFunction(degree: Int) {}

}
