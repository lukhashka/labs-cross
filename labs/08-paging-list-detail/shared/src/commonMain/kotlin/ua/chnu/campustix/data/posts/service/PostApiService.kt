package ua.chnu.campustix.data.posts.service

import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.posts.model.PostsResponse

const val POSTS_API = "posts"
const val LIMIT_PARAM = "limit"
const val SKIP_PARAM = "skip"

interface PostApiService {
    // One page: `limit` posts starting after the first `skip` posts
    suspend fun getPosts(limit: Int, skip: Int): Result<PostsResponse>
}
