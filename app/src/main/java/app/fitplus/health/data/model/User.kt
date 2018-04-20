package app.fitplus.health.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "User")
class User : Serializable {

    @PrimaryKey(autoGenerate = false)
    lateinit var id: String

    var weight = 0
    var sessionLength = 0
}
