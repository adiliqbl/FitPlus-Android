package app.fitplus.health.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.*

@Entity(tableName = "Stats")
@IgnoreExtraProperties
class Stats : Serializable {

    @PrimaryKey(autoGenerate = false)
    lateinit var userId: String

    var calorieBurned: Float = 0f
    var distance: Float = 0f
    var steps: Float = 0f
    var time: Date? = null
}
