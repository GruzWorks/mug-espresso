package test.mug.espresso.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import test.mug.espresso.domain.PowerMug

@Entity(tableName = "PowerMugs")
data class DbPowerMug(
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

fun DbPowerMug.asDomainModel(): PowerMug {
	return PowerMug(
		id,
		name,
		LatLng(lat, lng),
		address,
		numberOfMugs
	)
}

fun List<DbPowerMug>.asDomainModel(): List<PowerMug> {
	return map {
		it.asDomainModel()
	}
}

fun PowerMug.asDbModel(): DbPowerMug {
	return DbPowerMug(
		id,
		name,
		point.latitude,
		point.longitude,
		address,
		numberOfMugs
	)
}
