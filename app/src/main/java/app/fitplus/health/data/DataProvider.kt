package app.fitplus.health.data

import android.content.Context
import app.fitplus.health.R
import app.fitplus.health.data.FirebaseStorage.*

import app.fitplus.health.data.model.Goals
import app.fitplus.health.data.model.Stats
import app.fitplus.health.data.model.User
import app.fitplus.health.system.component.CustomToast
import app.fitplus.health.ui.MainActivity
import app.fitplus.health.ui.MainActivity.REFRESH_DATA
import timber.log.Timber

class DataProvider {
    var user: User? = null
    var stats: Stats? = null
    var goals: Goals? = null

    fun updateUser(add: Boolean, context: Context) {
        val perform = if (add)
            AppDatabase.getInstance(context).userDao().add(user)
        else
            AppDatabase.getInstance(context).userDao().save(user)

        perform.subscribe { Timber.d("User saved") }

        // Sync with online storage
        usersReference().setValue(user)

        CustomToast(context, context as MainActivity, context.getString(R.string.msg_pref_success))
                .show()
    }

    fun updateGoals(add: Boolean, context: Context) {
        val perform = if (add)
            AppDatabase.getInstance(context).goalsDao().add(goals)
        else
            AppDatabase.getInstance(context).goalsDao().save(goals)

        perform.subscribe { Timber.d("Goals saved") }

        // Sync with online storage
        goalsReference().setValue(goals)

        REFRESH_DATA = true
    }

    fun updateStats(add: Boolean, context: Context) {
        val perform = if (add)
            AppDatabase.getInstance(context).statsDao().add(stats)
        else
            AppDatabase.getInstance(context).statsDao().save(stats)

        perform.subscribe { Timber.d("Stats saved") }

        // Sync with online storage
        statsReference().setValue(goals)

        REFRESH_DATA = true
    }
}
