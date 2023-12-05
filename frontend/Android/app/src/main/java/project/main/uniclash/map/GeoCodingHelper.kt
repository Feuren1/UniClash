package project.main.uniclash.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.Locale


class GeoCodingHelper(private val context: Context) {

    //is only executed by opening the marker
    fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = ""

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            println("${addresses?.toString()} size der addresses")
            if (!addresses.isNullOrEmpty()) {
                addressText = addresses.get(0).getAddressLine(0);
                //val address: Address = addresses[0]
                //for (i in 0 until address.maxAddressLineIndex) {
               //     addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
               // }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return addressText
    }
}