package app.fitplus.health.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@Entity(tableName = "Goals")
@IgnoreExtraProperties
class Goals : Serializable {

    @PrimaryKey(autoGenerate = false)
    lateinit var userId: String

    var calorie: Int = 0
    var steps: Int = 0
    var distance: Int = 0


    constructor()

    @Ignore
    constructor(calorie: Int, steps: Int, distance: Int) {
        this.calorie = calorie
        this.steps = steps
        this.distance = distance
    }
}
