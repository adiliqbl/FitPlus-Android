package app.fitplus.health.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Health")
class Health : Serializable {

    @PrimaryKey(autoGenerate = false)
    lateinit var userId: String

    var weight: Int = 0
}