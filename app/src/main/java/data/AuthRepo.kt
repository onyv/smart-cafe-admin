package com.example.mycafeadminap.data

import androidx.room.Dao

class AuthRepository(private val userDao: UserDb) {

    suspend fun login(email: String, password: String): LoginResult {
        val user = userDao.findByEmail(email.trim().lowercase())
            ?: return LoginResult.InvalidCredentials
        if (!PasswordHasher.verify(password, user.salt, user.passwordHash)) {
            return LoginResult.InvalidCredentials
        }
        if (user.role != "admin") {
            return LoginResult.NotAdmin
        }
        return LoginResult.Success(user)
    }
}