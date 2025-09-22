//package com.example.vidstreem.viewmodels
//
//import SessionManager
//import android.content.Intent
//import androidx.core.content.ContextCompat.startActivity
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.vidstreem.Data.Api.RetrofitInstance
//import com.example.vidstreem.Data.Model.LoginRequest
//import com.example.vidstreem.Data.Model.LoginResponse
//import com.example.vidstreem.Data.Model.RegisterRequest
//import com.example.vidstreem.Ui.HomeActivity
//import com.example.vidstreem.Util.Resource
//import kotlinx.coroutines.launch
//
//class AuthViewModel(private val sessionManager: SessionManager) : ViewModel() {
//
//    // LiveData for observing registration status from the UI
//    private val _registrationStatus = MutableLiveData<Resource<Unit>>()
//    val registrationStatus: LiveData<Resource<Unit>> = _registrationStatus
//
//    // LiveData for observing login status from the UI
//    private val _loginStatus = MutableLiveData<Resource<LoginResponse>>()
//    val loginStatus: LiveData<Resource<LoginResponse>> = _loginStatus
//
//    /**
//     * Registers a new user with the provided details.
//     * Posts Loading, Success, or Error states to the registrationStatus LiveData.
//     */
//    fun registration(name: String, email: String, password: String, phone: String) {
//        // Post loading state to the UI to show a progress indicator
//        _registrationStatus.postValue(Resource.Loading())
//        viewModelScope.launch {
//            try {
//                // Assuming "user" is a default role for new registrations
//                val request = RegisterRequest(name, email, password, phone, "user")
//                val response = RetrofitInstance.api.register(request)
//
//                if (response.isSuccessful) {
//                    _registrationStatus.postValue(Resource.Success(Unit))
//                    val i = Intent(this, HomeActivity::class.java)
//                    startActivity(i)
//                } else {
//                    // Try to parse a more specific error message from the server response
//                    val errorMsg = response.errorBody()?.string() ?: response.message()
//                    _registrationStatus.postValue(Resource.Error("Registration failed: $errorMsg"))
//                }
//            } catch (e: Exception) {
//                // Handle exceptions like network errors
//                _registrationStatus.postValue(
//                    Resource.Error(e.message ?: "An unknown error occurred")
//                )
//            }
//        }
//    }
//
//    /**
//     * Logs in a user with the provided credentials.
//     * On success, the JWT is saved to SharedPreferences via the SessionManager.
//     */
//    fun login(email: String, password: String) {
//        _loginStatus.postValue(Resource.Loading())
//        viewModelScope.launch {
//            try {
//                val request = LoginRequest(email, password)
//                val response = RetrofitInstance.api.login(request)
//
//                if (response.isSuccessful && response.body() != null) {
//                    val loginResponse = response.body()!!
//                    // Save the token using the injected SessionManager
//                    sessionManager.saveAuthToken(loginResponse.token)
//                    _loginStatus.postValue(Resource.Success(loginResponse))
//                } else {
//                    val errorMsg = response.errorBody()?.string() ?: response.message()
//                    _loginStatus.postValue(Resource.Error("Login failed: $errorMsg"))
//                }
//            } catch (e: Exception) {
//                _loginStatus.postValue(
//                    Resource.Error(e.message ?: "An unknown error occurred")
//                )
//            }
//        }
//    }
//}
