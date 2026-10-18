package com.example.block_3.di

import com.example.block_3.data.posts.AppPostRepository
import com.example.block_3.data.posts.service.AppPostApiService
import com.example.block_3.data.posts.service.PostApiService
import com.example.block_3.domain.posts.PostRepository
import com.example.block_3.domain.posts.create.CreatePostUseCase
import com.example.block_3.domain.posts.edit.EditPostUseCase
import com.example.block_3.domain.posts.obtain.ObtainPostUseCase
import com.example.block_3.domain.posts.remove.RemovePostUseCase
import com.example.block_3.presentation.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    singleOf(::AppPostApiService) { bind<PostApiService>() }
}

val appModule = module {
    includes(networkModule)
    // data
    singleOf(::AppPostRepository) { bind<PostRepository>() }
    // domain
    factoryOf(::ObtainPostUseCase)
    factoryOf(::CreatePostUseCase)
    factoryOf(::EditPostUseCase)
    factoryOf(::RemovePostUseCase)
    // presentation
    viewModelOf(::MainViewModel)
}
