import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "MyAppSession"
        private const val USER_TOKEN = "user_token"
    }

    /**
     * Saves the authentication token to SharedPreferences.
     *
     * @param token The JWT received from the server upon successful login.
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    /**
     * Fetches the authentication token from SharedPreferences.
     *
     * @return The saved authentication token, or null if it doesn't exist.
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    /**
     * Clears the saved authentication token from SharedPreferences.
     * Useful for logging out the user.
     */
    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.apply()
    }
}
