package com.example.vidstreem.Ui.Fragments

import com.example.vidstreem.Util.MovieAdapter
import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.R
import com.example.vidstreem.Ui.LoginMainActivity
import com.example.vidstreem.Ui.MovieDetailActivity
import com.example.vidstreem.Util.PrefManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter  // Reusing your existing adapter!
    private lateinit var loginText: TextView
    private lateinit var coinsCount: TextView
    private lateinit var assetCount: TextView
    private lateinit var viplogo: LinearLayout
    private lateinit var reedemlogo: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        viplogo = view.findViewById(R.id.viplogo)
        reedemlogo = view.findViewById(R.id.reedemlogo)
        // Initialize views
        loginText = view.findViewById(R.id.login_text)
        historyRecyclerView = view.findViewById(R.id.history_recycler_view)

        // Setup History RecyclerView (Horizontal) - REUSING YOUR EXISTING ADAPTER!


        lifecycleScope.launch {
            try {
                val session = SessionManager(requireContext())
                val userId = session.getUserIdFromToken()

                if (userId == null) {
                    Log.e("Profile", "User ID not found in token")
                    return@launch
                }

                val response = RetrofitInstance.userapi.getprofiledetails(userId)
                if(response != null){
                    Log.d("Profile", "profile data fetched successfully")
                }else{
                    Log.e("Profile", "Failed to fetch profile: ${response}")
                }
            } catch (e: Exception) {
                Log.e("Profile", "Error fetching profile: ${e.message}", e)
            }
        }
        setupHistoryRecyclerView()

        // Check if user is logged in
        checkLoginStatus()

        // Setup click listeners
        setupClickListeners(view)

        // Load watch history
        loadWatchHistory()
        getuserdata()
    }

    private fun setupHistoryRecyclerView() {
        // REUSE your existing com.example.vidstreem.Util.MovieAdapter!
        movieAdapter = MovieAdapter { movie ->
            // Navigate to video player
            val intent = Intent(requireContext(), MovieDetailActivity::class.java)
            intent.putExtra("Id", movie.id)
            startActivity(intent)
        }

        // The ONLY difference: Change from GridLayoutManager to HORIZONTAL LinearLayoutManager
        historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,  // Horizontal orientation
                false
            )
            adapter = movieAdapter
        }
    }

    private fun checkLoginStatus() {
        val token = sessionManager.fetchAuthToken()
        if (token != null) {
            val userdata = sessionManager.getUserName()

            if (userdata != null) {
//                viplogo.visibility = View.GONE
//                reedemlogo.visibility = View.GONE
                loginText.text = userdata
            }
            // You can decode JWT and show user name here
        } else {
            loginText.text = "Login"
        }
    }

    private fun setupClickListeners(view: View) {
        // Profile header click - navigate to login
        view.findViewById<View>(R.id.profile_icon)?.setOnClickListener {
            if (sessionManager.fetchAuthToken() == null) {
                val intent = Intent(requireContext(), LoginMainActivity::class.java)
                startActivity(intent)
            }
        }

        // Join VIP button
        view.findViewById<MaterialButton>(R.id.btn_join_vip)?.setOnClickListener {
            Toast.makeText(requireContext(), "VIP feature coming soon!", Toast.LENGTH_SHORT).show()
        }


        // Redeem VIP
        view.findViewById<MaterialButton>(R.id.btn_enter_redeem)?.setOnClickListener {
            Toast.makeText(requireContext(), "Redeem feature coming soon!", Toast.LENGTH_SHORT)
                .show()
        }

        // View all history
        view.findViewById<View>(R.id.btn_view_all_history)?.setOnClickListener {
            Toast.makeText(requireContext(), "All history", Toast.LENGTH_SHORT).show()
        }

        // Menu items
        view.findViewById<LinearLayout>(R.id.menu_download)?.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(requireContext(), LoginMainActivity::class.java)
            startActivity(intent)
        }
//
//        view.findViewById<LinearLayout>(R.id.menu_watchlist)?.setOnClickListener {
//            Toast.makeText(requireContext(), "Watchlist", Toast.LENGTH_SHORT).show()
//        }
//
//        view.findViewById<LinearLayout>(R.id.menu_help)?.setOnClickListener {
//            Toast.makeText(requireContext(), "Help & Support", Toast.LENGTH_SHORT).show()
//        }
//
//        view.findViewById<LinearLayout>(R.id.menu_settings)?.setOnClickListener {
//            Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun loadWatchHistory() {
        val token = sessionManager.fetchAuthToken()

        // Get user ID from token
        lifecycleScope.launch {
            try {
                // For now, load random videos as history
                // Later replace with actual watch history API call
                val response = RetrofitInstance.api.getAllVideos()
                if (response.isSuccessful) {
                    val videos = response.body() ?: emptyList()
                    // Use the existing updateMovies method from your adapter
                    movieAdapter.updateMovies(videos.take(10)) // Show only 10 in history
                } else {
                    Log.e("ProfileFragment", "Failed to load videos: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error loading history: ${e.message}", e)
            }
        }
    }

    private fun getuserdata() {
        lifecycleScope.launch {
            try {
                val session = SessionManager(requireContext())
                val userId = session.getUserIdFromToken()

                if (userId == null) {
                    Log.e("Profile", "User ID not found in token")
                    return@launch
                }

                val response = RetrofitInstance.userapi.getprofiledetails(userId)
                if(response != null){
                    Log.d("Profile", "profile data fetched successfully")
                }else{
                    Log.e("Profile", "Failed to fetch profile: ${response}")
                }
            } catch (e: Exception) {
                Log.e("Profile", "Error fetching profile: ${e.message}", e)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // Reload history when fragment comes back to view
        loadWatchHistory()
    }
}
