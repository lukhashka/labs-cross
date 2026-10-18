package com.example.block_3

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform