package com.mnb.crusadeapp.data

data class Army(
    var name: String,
    var codex: String,
    var units: Array<Unit>,
    var abilities: Array<Ability>) {
    // empty
}
