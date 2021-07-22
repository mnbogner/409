 package com.mnb.crusadeapp.rules

import com.mnb.crusadeapp.data.*
import com.mnb.crusadeapp.data.Unit

 class ArmyCalculator {
    companion object {

        val TAG: String = "ArmyCalculator"

        fun getArmyPoints(army: Army): Int {
            var total: Int = 0;

            // sum of army ability points
            for (a: Ability in army.abilities) {
                total += (a.points * a.count)
            }

            // sum of unit points
            for (u: Unit in army.units) {
                total += getUnitPoints(u)
            }

            return total
        }

        fun getUnitPoints(army: Army, name: String): Int {
            for (u: Unit in army.units) {
                if (u.name.equals(name)) {
                    return getUnitPoints(u)
                }
            }
            return 0
        }

        fun getUnitPoints(unit: Unit): Int {
            var total: Int = 0;

            // sum of unit model points
            for (m: Model in unit.models) {
                total += (m.points * m.count)
            }

            // sum of unit weapon points
            for (w: Weapon in unit.weapons) {
                total += (w.points * w.count)
            }

            // sum of unit ability points
            for (a: Ability in unit.abilities) {
                total += (a.points * a.count)
            }

            return total
        }

        fun getDefaultPoints(unit: Unit): Int {
            var total: Int = 0;

            // get model points
            for (m: Model in unit.models) {
                // these values expected to be the same for each model in a unit
                total = (m.points * m.increment)
            }

            return total
        }

        fun getArmyPower(army: Army): Int {
            var total: Int = 0;

            // sum of army ability power
            for (a: Ability in army.abilities) {
                total += (a.points * a.count)
            }

            // sum of unit power
            for (u: Unit in army.units) {
                total += getUnitPower(u)
            }

            return total
        }

        fun getUnitPower(army: Army, name: String): Int {
            for (u: Unit in army.units) {
                if (u.name.equals(name)) {
                    return getUnitPower(u)
                }
            }
            return 0
        }

        fun getUnitPower(unit: Unit): Int {
            var total: Int = 0;

            // sum of unit ability power
            for (a: Ability in unit.abilities) {
                total += (a.points * a.count)
            }

            // sum of model power
            var totalCount: Int = 0
            var modelPower: Int = 0
            var modelIncrement: Int = 0
            for (m: Model in unit.models) {
                totalCount += m.count
                // these values expected to be the same for each model in a unit
                modelPower = m.power
                modelIncrement = m.increment
            }

            // TEMP
            if (modelIncrement < 1) {
                modelIncrement = 1
            }

            var multiplier: Int = totalCount / modelIncrement
            if (totalCount % modelIncrement > 0) {
                multiplier++;
            }
            total += (modelPower * multiplier)

            // sum of model weapon power
            for (w: Weapon in unit.weapons) {
                total += (w.power * w.count)
            }

            // sum of model ability power
            for (a: Ability in unit.abilities) {
                total += (a.power * a.count)
            }

            return total
        }

        fun getDefaultPower(unit: Unit): Int {
            var total: Int = 0;

            // get model power
            for (m: Model in unit.models) {
                total = m.power
            }

            return total
        }
    }
}