package test.mug.espresso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PowerMug (
	@PrimaryKey(autoGenerate = true)
	var id: Long = 0L,

	@ColumnInfo
	var name: String,

	@ColumnInfo
	var point: MapPoint,

	@ColumnInfo
	var address: String,

	@ColumnInfo
	var numberOfMugs: Int
)

data class MapPoint (
	var lat: Double,
	var lan: Double
)
