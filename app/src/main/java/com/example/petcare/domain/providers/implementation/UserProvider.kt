package com.example.petcare.domain.providers.implementation

import android.content.SharedPreferences // Import potrzebny
import com.example.petcare.config.Settings
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.exceptions.AuthFailure
import java.util.UUID
import javax.inject.Inject

private const val USER_ID_KEY = Settings.USER_ID_KEY

class UserProvider @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : IUserProvider {

    override fun getUserId(): String? {
        // Odczyt ID użytkownika z trwałej pamięci.
        // Zwraca null, jeśli klucz nie istnieje (użytkownik nie jest zalogowany).
        val user_id = sharedPreferences.getString(USER_ID_KEY, null)
        if (user_id == null) {
            return null
        }
        return user_id
    }

    override fun setUserId(id: String) {
        sharedPreferences.edit()
            .putString(USER_ID_KEY, id)
            .apply()
    }

    override fun clearUserData() {
        sharedPreferences.edit()
            .remove(USER_ID_KEY)
            .apply()

    }

}