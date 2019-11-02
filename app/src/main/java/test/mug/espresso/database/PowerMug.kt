package test.mug.espresso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "PowerMugs")
data class PowerMug (
	@PrimaryKey(autoGenerate = true)
	var id: Long = 0L,

	@ColumnInfo
	var name: String,

	@ColumnInfo
	var point: LatLng,

	@ColumnInfo
	var address: String,

	@ColumnInfo
	var numberOfMugs: Int
)
