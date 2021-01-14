package ru.netology.motivationapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.motivationapp.dto.PostEntity
import java.text.SimpleDateFormat
import java.util.*

@Dao
interface PostDao {

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query("""UPDATE PostEntity SET 
        author = :author, 
        content = :content, 
        pictureName = :pictureName 
        WHERE id = :id"""
    )
    fun updateContentById(id: Long, author: String, content: String, pictureName: String)

    fun save(post: PostEntity) =
        if (post.id == 0L) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
            val currentDate = dateFormat.format(Date())
            insert(
                post.copy(
                    author = post.author,
                    content = post.content,
                    datePublished = currentDate,
                    pictureName = post.pictureName,
                    dateCompare = Date().time
                )
            )
        } else {
            updateContentById(post.id, post.author, post.content, post.pictureName)
        }

    @Query("""UPDATE PostEntity SET likes = likes + 1 WHERE id = :id""")
    fun likeById(id: Long)

    @Query("""UPDATE PostEntity SET dislike = dislike + 1 WHERE id = :id""")
    fun dislikeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)

}