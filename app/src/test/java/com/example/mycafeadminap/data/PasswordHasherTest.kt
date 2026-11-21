package com.example.mycafeadminap.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun verify_correctPassword_returnsTrue() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("Admin123!", salt)
        assertTrue(PasswordHasher.verify("Admin123!", salt, hash))
    }

    @Test
    fun verify_wrongPassword_returnsFalse() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("Admin123!", salt)
        assertFalse(PasswordHasher.verify("admin123!", salt, hash))
    }

    @Test
    fun hash_isNotThePlainPassword() {
        val salt = PasswordHasher.newSalt()
        assertNotEquals("Admin123!", PasswordHasher.hash("Admin123!", salt))
    }

    @Test
    fun sameSaltAndPassword_giveTheSameHash() {
        val salt = PasswordHasher.newSalt()
        assertEquals(
            PasswordHasher.hash("Admin123!", salt),
            PasswordHasher.hash("Admin123!", salt)
        )
    }

    @Test
    fun differentSalts_giveDifferentHashesForTheSamePassword() {
        val salt1 = PasswordHasher.newSalt()
        val salt2 = PasswordHasher.newSalt()
        assertNotEquals(salt1, salt2)
        assertNotEquals(
            PasswordHasher.hash("Admin123!", salt1),
            PasswordHasher.hash("Admin123!", salt2)
        )
    }

    @Test
    fun newSalt_is32HexCharacters() {
        val salt = PasswordHasher.newSalt()
        assertEquals(32, salt.length)   // 16 random bytes written as hex
        assertTrue(salt.matches(Regex("[0-9a-f]+")))
    }
}
