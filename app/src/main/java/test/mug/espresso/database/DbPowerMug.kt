package test.mug.espresso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import test.mug.espresso.domain.PowerMug

@Entity(tableName = "PowerMugs")
data class DbPowerMug (
	@PrimaryKey(autoGenerate = true)
	var id: Long = 0L,

	@ColumnInfo
	var name: String,

	@ColumnInfo
	var lat: Double,
	@ColumnInfo
	var lng: Double,

	@ColumnInfo
	var address: String,

	@ColumnInfo
	var numberOfMugs: Int
)

fun List<DbPowerMug>.asDomainModel(): List<PowerMug> {
	return map {
		PowerMug(
			id = it.id,
			name = it.name,
			point = LatLng(it.lat, it.lng),
			address = it.address,
			numberOfMugs = it.numberOfMugs)
	}
}
