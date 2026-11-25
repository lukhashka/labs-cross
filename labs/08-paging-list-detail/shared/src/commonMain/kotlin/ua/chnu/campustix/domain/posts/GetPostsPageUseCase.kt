package ua.chnu.campustix.domain.posts

import ua.chnu.campustix.core.Result

class GetPostsPageUseCase(
    private val postRepository: PostRepository,
) {
    suspend operator fun invoke(limit: Int, skip: Int): Result<PostsPage> {
        return postRepository.getPostsPage(limit = limit, skip = skip)
    }
}
