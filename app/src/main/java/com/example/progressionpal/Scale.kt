package com.example.progressionpal

class Scale(private var root: String, private var mode: Mode = Mode.IONIAN) {
    private val majorScaleSteps: List<Step> = listOf(Step.WHOLE,
                                                     Step.WHOLE,
                                                     Step.HALF,
                                                     Step.WHOLE,
                                                     Step.WHOLE,
                                                     Step.WHOLE,
                                                     Step.HALF)

    var scaleNotes: MutableList<String> = mutableListOf()
    private val intervalHelper = IntervalHelper()
    init {
        if (root.contains('m')) {
            mode = Mode.AEOLIAN
        }
        generateScaleNotes()
    }

    private fun generateScaleNotes() {
        scaleNotes.add(root)

        var i = mode.scaleIdx
        while(scaleNotes.size < majorScaleSteps.size){
            val newNote: String = if(majorScaleSteps[i++ % majorScaleSteps.size] == Step.WHOLE){
                intervalHelper.getNoteAtInterval(scaleNotes.last(), Interval.MAJOR_SECOND)
            } else {
                intervalHelper.getNoteAtInterval(scaleNotes.last(), Interval.MINOR_SECOND)
            }
            scaleNotes.add(newNote)
        }
    }

    fun getChordAtDegree(degree: Int, seventh: Boolean = false): Chord {
        val chordNotes: MutableList<String> = mutableListOf()

        val steps = when(seventh) {
            true -> 4
            else -> 3
        }

        var idx = degree
        while(chordNotes.size < steps) {
            val note = scaleNotes[idx % scaleNotes.size]
            chordNotes.add(note)
            idx += 2
        }
        return Chord(notes=chordNotes)
    }
}