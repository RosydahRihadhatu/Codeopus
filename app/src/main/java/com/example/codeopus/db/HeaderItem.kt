package com.example.codeopus.db

data class HeaderItem(
    val title: String,
    val childItems: List<ChildItem>,
    var isExpanded: Boolean = false
)
