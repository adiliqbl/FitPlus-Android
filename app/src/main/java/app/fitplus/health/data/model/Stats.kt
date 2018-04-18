package app.fitplus.health.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "Stats")
class Stats : Serializable {

    @PrimaryKey(autoGenerate = false)
    lateinit var userId: String

    var calorieBurned = 0
    var distance = 0
    var steps = 0
    var time: Date? = null
}
