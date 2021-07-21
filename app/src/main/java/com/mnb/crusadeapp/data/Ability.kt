package com.mnb.crusadeapp.data

data class Ability(
    var name: String,
    var description: String,
    var points: Int,
    var power: Int,
    var count: Int) : Comparable<Ability> {

    // override equals to decide whether to add or increment
    override fun equals(other: Any?): Boolean {
        // is other the same instance
        if (this === other) {
            return true
        }

        // is other the same class
        if (other?.javaClass != javaClass) {
            return false
        }

        other as Ability

        // does other have the same name
        if (!name.equals(other.name)) {
            return false
        }

        return true
    }

    // override compareTo to sort lists for ui
    override fun compareTo(other: Ability): Int {
        // sort based on name
        return name.compareTo(other.name)
    }
}
