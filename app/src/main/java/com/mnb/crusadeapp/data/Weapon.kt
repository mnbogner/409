package com.mnb.crusadeapp.data

import android.util.Log

data class Weapon(
    var name: String,
    var type: String,
    var range: String,
    var s: String,
    var ap: String,
    var d: String,
    var description: String,
    var points: Int,
    var power: Int,
    var count: Int,
    var alt: Array<Weapon>) : Comparable<Weapon> {

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

        other as Weapon

        // does other have the same name
        if (!name.equals(other.name)) {
            return false
        }

        return true
    }

    // override compareTo to sort lists for ui
    override fun compareTo(other: Weapon): Int {
        // sort based on type
        val thisTypeParts: List<String> = type.split(" ")
        val otherTypeParts: List<String> = other.type.split(" ")

        Log.d("COMPARE", "${type} VS ${other.type}")

        if (type.equals(other.type) || thisTypeParts[0].equals(otherTypeParts[0])) {
            // sort based on count
            if (count == other.count) {
                // sort based on name
                return name.compareTo(other.name)
            } else {
                return other.count - count
            }
        } else if (type.startsWith("RapidFire")) {
            return -1;
        } else if (other.type.startsWith("RapidFire")) {
            return 1;
        } else if (type.startsWith("Assault")) {
            return -1;
        } else if (other.type.startsWith("Assault")) {
            return 1;
        } else if (type.startsWith("Heavy")) {
            return -1;
        } else if (other.type.startsWith("Heavy")) {
            return 1;
        } else if (type.startsWith("Pistol")) {
            return -1;
        } else if (other.type.startsWith("Pistol")) {
            return 1;
        } else if (type.startsWith("Grenade")) {
            return -1;
        } else if (other.type.startsWith("Grenade")) {
            return 1;
        } else if (type.startsWith("Melee")) {
            return -1;
        } else if (other.type.startsWith("Melee")) {
            return 1;
        } else {
            // ???
            if (count == other.count) {
                // ???
                return name.compareTo(other.name)
            } else {
                return count - other.count
            }
        }
    }
}
