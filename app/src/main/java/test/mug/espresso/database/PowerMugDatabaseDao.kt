package test.mug.espresso.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PowerMugDatabaseDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(vararg videos: DbPowerMug)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(place: DbPowerMug)

	@Update(onConflict = OnConflictStrategy.REPLACE)
	fun update(place: DbPowerMug)

	@Delete
	fun delete(place: DbPowerMug)

	@Query("SELECT * FROM PowerMugs WHERE id = :key")
	fun get(key: Long): DbPowerMug?

	@Query("DELETE FROM PowerMugs")
	fun clear()

	@Query("SELECT * FROM PowerMugs ORDER BY id ASC")
	fun getAll(): LiveData<List<DbPowerMug>>

	@Query("SELECT * FROM PowerMugs WHERE name LIKE :query OR address LIKE :query")
	fun searchFor(query: String): LiveData<List<DbPowerMug>>
}
