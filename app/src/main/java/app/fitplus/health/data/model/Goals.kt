package app.fitplus.health.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Goals")
class Goals : Serializable {

    @PrimaryKey(autoGenerate = false)
    lateinit var userId: String

    var calorie: Int = 0
    var steps: Int = 0
    var distance: Int = 0
}
