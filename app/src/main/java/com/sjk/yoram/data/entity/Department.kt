package com.sjk.yoram.data.entity

data class Department(
    var code: Int,
    var name: String,
    var parent: Int ) {
    constructor(pos: Position): this(pos.code, pos.name, pos.cat)
}