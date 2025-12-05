import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import org.json.JSONObject

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "MyAppSession"
        private const val USER_TOKEN = "user_token"
    }

    /** Saves the authentication token to SharedPreferences */
    fun saveAuthToken(token: String?) {
        if (token.isNullOrEmpty()) {
            Log.e("JWT_DEBUG", "Attempted to save null or empty token!")
            return
        }
        prefs.edit().putString(USER_TOKEN, token).apply()
        Log.d("JWT_DEBUG", "Token saved: $token")
    }

    /** Fetches the authentication token from SharedPreferences */
    fun fetchAuthToken(): String? {
        val token = prefs.getString(USER_TOKEN, null)
        Log.d("JWT_DEBUG", "Fetched token: $token")
        return token
    }

    /** Clears the saved token */
    fun clearAuthToken() {
        prefs.edit().remove(USER_TOKEN).apply()
        Log.d("JWT_DEBUG", "Token cleared")
    }

    /** Decodes the JWT payload and returns it as a JSONObject */
    fun decodeJWT(token: String): JSONObject? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            JSONObject(decodedString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    /** Helper function to get the payload of the saved JWT */
    fun getPayload(): JSONObject? {
        val token = fetchAuthToken() ?: return null
        return decodeJWT(token)
    }

    /** Example: get userId from saved token */
    fun getUserId(): Int? {
        val payload = getPayload() ?: return null
        return try {
            payload.getInt("userId")
        } catch (e: Exception) {
            null
        }
    }

    /** Example: get username from saved token */
    fun getUserName(): String? {
        val payload = getPayload() ?: return null

        return try {
            // Try both possible claim keys
            payload.optString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", null)
                ?: payload.optString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", null)
        } catch (e: Exception) {
            null
        }
    }

    fun logout(){
        clearAuthToken()
    }

    fun getUserIdFromToken(): Int? {
        val token = fetchAuthToken() ?: return null

        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, Charsets.UTF_8)
            val json = JSONObject(decodedString)

            // adjust key depending on your token payload
            when {
                json.has("userId") -> json.getInt("userId")
                json.has("sub") -> json.getInt("sub")          // many JWTs store ID in "sub"
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}

