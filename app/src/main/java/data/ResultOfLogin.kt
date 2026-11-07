package com.example.mycafeadminap.data

sealed class LoginResult {
    data class Success(val user: User) : LoginResult() /* Login worked*/
    object InvalidCredentials : LoginResult() /* Wrong email or password */
    object NotAdmin : LoginResult() /*Password correct but email is not admins so doesnt work */
}