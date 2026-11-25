package ua.chnu.campustix.data.posts

import ua.chnu.campustix.core.Result
import ua.chnu.campustix.core.map
import ua.chnu.campustix.data.posts.model.toDomain
import ua.chnu.campustix.data.posts.service.PostApiService
import ua.chnu.campustix.domain.posts.PostRepository
import ua.chnu.campustix.domain.posts.PostsPage

class AppPostRepository(
    private val postApiService: PostApiService,
) : PostRepository {

    override suspend fun getPostsPage(limit: Int, skip: Int): Result<PostsPage> {
        return postApiService.getPosts(limit = limit, skip = skip).map { it.toDomain() }
    }
}
