package castofo.com.co.nower.models

import com.google.gson.annotations.SerializedName

import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by Santiago Vanegas on 12/9/17.
 */
open class Promo(
        @PrimaryKey
        @Required
        var id: String? = null,
        var name: String? = null,
        var description: String? = null,
        var terms: String? = null,
        var stock: Int = 0,
        var price: Float = 0.toFloat(),
        @SerializedName("start_date")
        var startDate: String? = null,
        @SerializedName("end_date")
        var endDate: String? = null,
        @LinkingObjects("promos")
        val branches: RealmResults<Branch>? = null
) : RealmObject()
