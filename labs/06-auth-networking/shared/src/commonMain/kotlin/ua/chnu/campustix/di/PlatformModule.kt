package ua.chnu.campustix.di

import org.koin.core.module.Module

// KSafe and the Ktor engine differ on every platform.
expect val platformModule: Module
