package ua.chnu.campustix.data.posts.model

import kotlinx.serialization.Serializable
import ua.chnu.campustix.domain.posts.Post
import ua.chnu.campustix.domain.posts.PostsPage

// GET /auth/posts?limit=..&skip=.. -> {"posts":[...],"total":251,"skip":0,"limit":15}
@Serializable
data class PostsResponse(
    val posts: List<PostResponse>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)

@Serializable
data class PostResponse(
    val id: Int,
    val title: String,
    val body: String,
    val tags: List<String> = emptyList(),
    val reactions: ReactionsResponse = ReactionsResponse(),
    val views: Int = 0,
    val userId: Int,
)

@Serializable
data class ReactionsResponse(
    val likes: Int = 0,
    val dislikes: Int = 0,
)

fun PostsResponse.toDomain(): PostsPage = PostsPage(
    posts = posts.map { it.toDomain() },
    skip = skip,
    total = total,
)

fun PostResponse.toDomain(): Post = Post(
    id = id,
    title = title,
    body = body,
    tags = tags,
    likes = reactions.likes,
    dislikes = reactions.dislikes,
    views = views,
    authorId = userId,
)
