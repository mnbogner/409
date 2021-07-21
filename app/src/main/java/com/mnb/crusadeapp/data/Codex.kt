package com.mnb.crusadeapp.data

data class Codex(
    var name: String,
    var units: Array<Unit>,
    var abilities: Array<Ability>,
    var factions: Array<Ability>,
    var traits: Array<Ability>,
    var relicWeapons: Array<Weapon>,
    var relicAbilities: Array<Ability>) {
    // empty
}
