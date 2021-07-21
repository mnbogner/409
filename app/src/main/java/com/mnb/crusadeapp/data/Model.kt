package com.mnb.crusadeapp.data

import java.util.*

data class Model(
    var name: String,
    var m: String,
    var ws: String,
    var bs: String,
    var s: String,
    var t: String,
    var w: String,
    var a: String,
    var ld: String,
    var sv: String,
    var points: Int,
    var power: Int,
    var count: Int,
    var increment: Int) : Comparable<Model> {

    // override equals to decide whether to add or increment
    override fun equals(other: Any?): Boolean{
        // is other the same instance
        if (this === other) {
            return true
        }

        // is other the same class
        if (other?.javaClass != javaClass) {
            return false
        }

        other as Model

        // does other have the same name
        if (!name.equals(other.name)) {
            return false
        }

        return true
    }

    // override compareTo to sort lists for ui
    override fun compareTo(other: Model): Int {
        // sort based on leadership
        if (ld.equals(other.ld)) {
            // sort based on count
            if (count == other.count) {
                // sort based on name
                return name.compareTo(other.name)
            } else {
                return count - other.count
            }
        } else {
            return ld.toInt() - other.ld.toInt()
        }
    }
}
