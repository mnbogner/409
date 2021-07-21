package com.mnb.crusadeapp.data

data class Unit(
    var name: String,
    var type: String,
    var models: Array<Model>,
    var track: Array<Damage>,
    var weapons: Array<Weapon>,
    var abilities: Array<Ability>) : Comparable<Unit> {

    // override compareTo to sort lists for ui
    override fun compareTo(other: Unit): Int {
        // sort based on type
        if (type.equals(other.type)) {
            // sort based on name
            return name.compareTo(other.name)
        } else if (type.equals("HQ")) {
            return -1;
        } else if (other.type.equals("HQ")) {
            return 1;
        } else if (type.equals("Troops")) {
            return -1;
        } else if (other.type.equals("Troops")) {
            return 1;
        } else if (type.equals("Elites")) {
            return -1;
        } else if (other.type.equals("Elites")) {
            return 1;
        } else if (type.equals("Fast Attack")) {
            return -1;
        } else if (other.type.equals("Fast Attack")) {
            return 1;
        } else if (type.equals("Flyers")) {
            return -1;
        } else if (other.type.equals("Flyers")) {
            return 1;
        } else if (type.equals("Heavy Support")) {
            return -1;
        } else if (other.type.equals("Heavy Support")) {
            return 1;
        } else if (type.equals("Dedicated Transport")) {
            return -1;
        } else if (other.type.equals("Dedicated Transport")) {
            return 1;
        } else {
            // ???
            return name.compareTo(other.name)
        }
    }
}
