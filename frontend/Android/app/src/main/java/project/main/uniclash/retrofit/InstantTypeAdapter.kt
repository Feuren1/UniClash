package project.main.uniclash.retrofit

import android.util.Log
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.Instant

class InstantTypeAdapter : TypeAdapter<Instant>() {
    private val TAG = "InstantTypeAdapter"
    override fun write(out: JsonWriter?, value: Instant?) {
        throw UnsupportedOperationException("Cannot convert instant to json")
    }

    override fun read(`in`: JsonReader?): Instant {
        val s = `in`?.nextString()
        Log.d(TAG, "read: $s")
        return Instant.parse(s)
    }
}