package ua.chnu.campustix.domain.posts

// A post in the campus discussion feed
data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val tags: List<String>,
    val likes: Int,
    val dislikes: Int,
    val views: Int,
    val authorId: Int,
)

// One page of the feed: the posts and the numbers needed to know if there is more
data class PostsPage(
    val posts: List<Post>,
    val skip: Int,
    val total: Int,
)
