package com.michaldrabik.storage.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.michaldrabik.storage.database.model.User

@Dao
interface UserDao {

  @Query("SELECT * FROM user WHERE id == 1")
  suspend fun get(): User?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(user: User)
}