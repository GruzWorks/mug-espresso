package test.mug.espresso.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PowerMugDatabaseDao {
	@Insert
	fun insert(place: DbPowerMug)

	@Update
	fun update(place: DbPowerMug)

	@Query("SELECT * FROM PowerMugs WHERE id = :key")
	fun get(key: Long): DbPowerMug?

	@Query("DELETE FROM PowerMugs")
	fun clear()

	@Query("SELECT * FROM PowerMugs ORDER BY id ASC")
	fun getAll(): LiveData<List<DbPowerMug>>
}
