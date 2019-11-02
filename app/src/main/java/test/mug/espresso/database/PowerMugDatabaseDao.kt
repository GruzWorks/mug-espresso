package test.mug.espresso.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PowerMugDatabaseDao {
	@Insert
	fun insert(place: PowerMug)

	@Update
	fun update(place: PowerMug)

	@Query("SELECT * from PowerMugs WHERE id = :key")
	fun get(key: Long): PowerMug?

	@Query("DELETE FROM PowerMugs")
	fun clear()

	@Query("SELECT * FROM PowerMugs ORDER BY id ASC")
	fun getAll(): LiveData<List<PowerMug>>
}
