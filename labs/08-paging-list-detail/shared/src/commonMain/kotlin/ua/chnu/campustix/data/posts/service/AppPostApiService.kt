package ua.chnu.campustix.data.posts.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ua.chnu.campustix.core.Result
import ua.chnu.campustix.data.auth.service.AUTH_API
import ua.chnu.campustix.data.auth.service.BASE_URL
import ua.chnu.campustix.data.common.safeRequest
import ua.chnu.campustix.data.posts.model.PostsResponse

// The authorized client: /auth/posts answers 401 without a token,
// so loading the feed also uses the automatic token refresh from lab 06
class AppPostApiService(
    private val authorizedClient: HttpClient,
) : PostApiService {

    override suspend fun getPosts(limit: Int, skip: Int): Result<PostsResponse> {
        return authorizedClient.safeRequest {
            get("$BASE_URL/$AUTH_API/$POSTS_API") {
                parameter(LIMIT_PARAM, limit)
                parameter(SKIP_PARAM, skip)
            }
        }
    }
}
