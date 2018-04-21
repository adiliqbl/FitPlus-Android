package app.fitplus.health.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@Entity(tableName = "User")
@IgnoreExtraProperties
class User : Serializable {

    @PrimaryKey(autoGenerate = false)
    lateinit var id: String

    var weight: Int = 0
    var sessionLength: Int = 0
}
