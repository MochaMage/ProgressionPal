package com.example.progressionpal

enum class Interval(val semitones: Int, val note_distance: Int) {
    UNISON(0, 0),
    FLAT(-1, 0),
    SHARP(1, 0),
    MINOR_SECOND(1, 1),
    MAJOR_SECOND(2, 1),
    MINOR_THIRD(3,2),
    MAJOR_THIRD(4, 2),
    PERFECT_FOURTH(5, 3),
    AUGMENTED_FOURTH(6, 3),
    DIMINISHED_FIFTH(6, 4),
    PERFECT_FIFTH(7, 4),
    AUGMENTED_FIFTH(8, 4),
    MINOR_SIXTH(8, 5),
    MAJOR_SIXTH(9, 5),
    DIMINISHED_SEVENTH(9, 6),
    MINOR_SEVENTH(10, 6),
    MAJOR_SEVENTH(11, 6),
    OCTAVE(12, 7)
}