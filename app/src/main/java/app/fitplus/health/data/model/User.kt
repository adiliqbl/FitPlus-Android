package app.fitplus.health.data.model

import java.io.Serializable

class User : Serializable {
    var id: String? = null
    var name: String? = null
    var email: String? = null
    var phone: String? = null
    var username: String? = null
    var weight = 0
}
