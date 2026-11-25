package ua.chnu.campustix.domain.posts

import ua.chnu.campustix.core.Result

// No paging types here: the domain only knows how to get one page
interface PostRepository {
    suspend fun getPostsPage(limit: Int, skip: Int): Result<PostsPage>
}
