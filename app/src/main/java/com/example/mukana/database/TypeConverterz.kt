package com.example.mukana.database

import androidx.room.TypeConverter
import com.example.mukana.model.Rarity

// The Room database needs some help in persisting the Rarity-type objects.
// The object name contains a 'z' because an 's' there seemed to cause some kind of
// confusion with the identically named annotation.
object TypeConverterz {

    @TypeConverter @JvmStatic
    fun fromRarity(value: Rarity): String {

        // to deal with the underscore.
        return if (value == Rarity.EXTREMELY_RARE) "EXTREMELY_RARE" else value.text.toUpperCase()
    }

    @TypeConverter @JvmStatic
    fun toRarity(value: String): Rarity {

        return Rarity.valueOf(value)
    }

} // TypeConverterz