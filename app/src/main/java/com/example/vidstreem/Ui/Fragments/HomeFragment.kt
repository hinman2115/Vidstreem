package com.example.vidstreem.Ui.Fragments

import android.content.Intent
import android.os.*
import android.util.*
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.example.vidstreem.Adapters.BannerAdapter
import com.example.vidstreem.Data.Api.RetrofitInstance
import com.example.vidstreem.Data.Model.*
import com.example.vidstreem.R
import com.example.vidstreem.Ui.MovieDetailActivity
import com.example.vidstreem.Util.*
import com.google.android.material.tabs.TabLayout
import retrofit2.*
import java.time.Instant
import java.time.format.DateTimeParseException

class HomeFragment : Fragment() {

    // Views
    private lateinit var heroBannerViewPager: ViewPager2
    private lateinit var sectionsRecycler: RecyclerView
    private lateinit var wetvHotRecycler: RecyclerView
    private lateinit var newReleaseRecycler: RecyclerView
    private lateinit var continueWatchingRecycler: RecyclerView
   // private lateinit var wetvHotTabs: TabLayout

    // Adapters
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var sectionAdapter: SectionAdapter
    private lateinit var wetvHotAdapter: MovieAdapter
    private lateinit var newReleaseAdapter: MovieAdapter
    private lateinit var continueWatchingAdapter: MovieAdapter

    companion object {
        private const val TAG = "HomeFragment"
    }
    private val uploadedOnFormat = java.text.SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        java.util.Locale.US
    ).apply {
        timeZone = java.util.TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: inflating fragment_home")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: start")

        initializeViews(view)
        Log.d(TAG, "initializeViews: done")

        setupHeroBanner()
        Log.d(TAG, "setupHeroBanner: done")

        // Initialize FIXED row adapters before any network call
        try {
            Log.d(TAG, "init fixed row adapters: start")
            wetvHotAdapter = MovieAdapter { movie -> navigateToMovieDetail(movie) }
            wetvHotRecycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            wetvHotRecycler.adapter = wetvHotAdapter

            newReleaseAdapter = MovieAdapter { movie -> navigateToMovieDetail(movie) }
            newReleaseRecycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            newReleaseRecycler.adapter = newReleaseAdapter

            continueWatchingAdapter = MovieAdapter { movie -> navigateToMovieDetail(movie) }
            continueWatchingRecycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            continueWatchingRecycler.adapter = continueWatchingAdapter
            Log.d(TAG, "init fixed row adapters: done")
        } catch (e: Exception) {
            Log.e(TAG, "init fixed row adapters: crash", e)
        }
////////////////////////////////////////////////////////
        // Initialize dynamic sections (optional). If you are NOT using SectionAdapter, you can skip this block.
        try {
            Log.d(TAG, "init sectionAdapter (optional): start")
            sectionAdapter = SectionAdapter { movie -> navigateToMovieDetail(movie) }
            sectionsRecycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            sectionsRecycler.adapter = sectionAdapter
            Log.d(TAG, "init sectionAdapter (optional): done")
        } catch (e: Exception) {
            Log.e(TAG, "init sectionAdapter (optional): crash", e)
        }

        setupClickListeners(view)
        Log.d(TAG, "setupClickListeners: done")

        Log.d(TAG, "loadMoviesForSection: calling with 'all'")
        loadMoviesForSection("all")
    }

    private fun initializeViews(view: View) {
        heroBannerViewPager = view.findViewById(R.id.hero_banner_viewpager)
        sectionsRecycler = view.findViewById(R.id.sectionsRecycler)
        wetvHotRecycler = view.findViewById(R.id.wetv_hot_recycler)
        newReleaseRecycler = view.findViewById(R.id.new_release_recycler)
        continueWatchingRecycler = view.findViewById(R.id.continue_watching_recycler)
       // wetvHotTabs = view.findViewById(R.id.wetv_hot_tabs)
        Log.d(TAG, "initializeViews: IDs bound")
    }

    private fun setupHeroBanner() {
        bannerAdapter = BannerAdapter(
            onBannerClick = { movie -> navigateToMovieDetail(movie) },
            onColorExtracted = { dominantColor, vibrantColor, darkVibrantColor ->
                changeSystemBarsColor(vibrantColor ?: dominantColor, darkVibrantColor)
            }
        )
        heroBannerViewPager.adapter = bannerAdapter
        heroBannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(TAG, "Hero banner page selected: $position")
            }
        })
    }

    private fun changeSystemBarsColor(primaryColor: Int, darkColor: Int?) {
        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                val statusBarColor = darkColor ?: ColorExtractor.getDarkerColor(primaryColor, 0.7f)
                window.statusBarColor = statusBarColor
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.navigationBarColor = primaryColor
                    val isLightColor = isColorLight(primaryColor)
                    var flags = window.decorView.systemUiVisibility
                    flags = if (isLightColor) {
                        flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    } else {
                        flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                    }
                    window.decorView.systemUiVisibility = flags
                }
            }
        }
    }

    private fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * android.graphics.Color.red(color) +
                0.587 * android.graphics.Color.green(color) +
                0.114 * android.graphics.Color.blue(color)) / 255
        return darkness < 0.5
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<TextView>(R.id.btn_big_hit_more)?.setOnClickListener {
            Log.d(TAG, "btn_big_hit_more clicked")
            Toast.makeText(context, "View all Big Hit", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.btn_new_release_more)?.setOnClickListener {
            Log.d(TAG, "btn_new_release_more clicked")
            Toast.makeText(context, "View all New Releases", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<TextView>(R.id.btn_continue_more)?.setOnClickListener {
            Log.d(TAG, "btn_continue_more clicked")
            Toast.makeText(context, "View all Continue Watching", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMoviesForSection(section: String) {
        Log.d(TAG, "API: enqueue getMovies()")
        RetrofitInstance.api.getMovies().enqueue(object : Callback<List<Movie>> {
            override fun onResponse(
                call: Call<List<Movie>?>,
                response: Response<List<Movie>?>
            ) {
                Log.d(TAG, "API: onResponse")
                if (!isAdded) {
                    Log.w(TAG, "Fragment not added; abort UI update")
                    return
                }
                if (!response.isSuccessful) {
                    Log.w(TAG, "HTTP fail code=${response.code()} msg=${response.message()}")
                    Toast.makeText(context, "Failed to fetch movies", Toast.LENGTH_SHORT).show()
                    return
                }

                val movies = response.body().orEmpty()
                Log.d(TAG, "movies.size=${movies.size}")

                if (movies.isEmpty()) {
                    Log.w(TAG, "movies empty; clearing adapters")
                    bannerAdapter.submitList(emptyList())
                    if (::wetvHotAdapter.isInitialized) wetvHotAdapter.updateMovies(emptyList()) else Log.w(
                        TAG,
                        "wetvHotAdapter not initialized"
                    )
                    if (::newReleaseAdapter.isInitialized) newReleaseAdapter.updateMovies(emptyList()) else Log.w(
                        TAG,
                        "newReleaseAdapter not initialized"
                    )
                    if (::continueWatchingAdapter.isInitialized) continueWatchingAdapter.updateMovies(
                        emptyList()
                    ) else Log.w(TAG, "continueWatchingAdapter not initialized")
                    if (::sectionAdapter.isInitialized) sectionAdapter.submitSections(emptyList()) else Log.w(
                        TAG,
                        "sectionAdapter not initialized"
                    )
                    return
                }

                // Hero banner
                val shuffled = movies.shuffled()
                Log.d(TAG, "banner submitList top5")
                bannerAdapter.submitList(shuffled.take(5))

                // Group by category
                val byCategory: Map<String, List<Movie>> = movies.groupBy {
                    it.category?.name?.trim().orEmpty()
                        .ifEmpty { it.categoryName?.trim().orEmpty() }
                        .ifEmpty { "Uncategorized" }
                }
                Log.d(TAG, "category keys=${byCategory.keys}")





                fun parseMillisOrNull(s: String?): Long? = try {
                    if (s.isNullOrBlank()) null else uploadedOnFormat.parse(s)?.time
                } catch (_: Exception) { null }


                val newReleaseDateSorted: List<Movie> = movies

                    .mapNotNull {

                            m ->
                        parseMillisOrNull(m.uploadedOn)?.let { ts -> m to ts }
                    }
                    .sortedByDescending { it.second }
                    .map { it.first }
                    .take(10)

                // Fixed-row bindings
                val wetvHotList = byCategory["Fantasy"].orEmpty() // adjust mapping as needed
                val continueWList =
                    byCategory["Uncategorized"].orEmpty().ifEmpty { movies.takeLast(10) }

                try {
                    if (::wetvHotAdapter.isInitialized) {
                        Log.d(TAG, "update wetvHotAdapter size=${wetvHotList.size}")
                        wetvHotAdapter.updateMovies(wetvHotList)
                    } else Log.w(TAG, "wetvHotAdapter not initialized")

                    if (::newReleaseAdapter.isInitialized) {
                        Log.d(
                            TAG,
                            "update newReleaseAdapter (date-sorted) size=${newReleaseDateSorted.size}"
                        )
                        newReleaseAdapter.updateMovies(newReleaseDateSorted)
                    } else Log.w(TAG, "newReleaseAdapter not initialized")

                    if (::continueWatchingAdapter.isInitialized) {
                        Log.d(TAG, "update continueWatchingAdapter size=${continueWList.size}")
                        continueWatchingAdapter.updateMovies(continueWList)
                    } else Log.w(TAG, "continueWatchingAdapter not initialized")
                } catch (e: Exception) {
                    Log.e(TAG, "Updating fixed adapters crashed", e)
                }

                // Optional: dynamic sections (merge date-based New on top, then category sections)
                try {
                    if (::sectionAdapter.isInitialized) {
                        val preferredOrder = listOf("Top 10", "New", "Trending")
                        val categorySections = byCategory.entries
                            .sortedWith(
                                compareBy<Map.Entry<String, List<Movie>>> { e ->
                                    val idx = preferredOrder.indexOf(e.key)
                                    if (idx == -1) Int.MAX_VALUE else idx
                                }.thenBy { it.key.lowercase() }
                            )
                            .map { e -> Section(title = e.key, items = e.value) }

                        // Inject a synthetic "New" section from uploadDate at the top,
                        // and avoid duplicate if backend also exposes "New".
                        val finalSections = buildList {
                            if (newReleaseDateSorted.isNotEmpty()) add(
                                Section(
                                    "New",
                                    newReleaseDateSorted
                                )
                            )
                            addAll(categorySections.filterNot {
                                it.title.equals(
                                    "New",
                                    ignoreCase = true
                                )
                            })
                        }

                        Log.d(TAG, "submitSections count=${finalSections.size}")
                        sectionAdapter.submitSections(finalSections)
                    } else {
                        Log.w(TAG, "sectionAdapter not initialized")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "submitSections crashed", e)
                }
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                Log.e(TAG, "API: onFailure", t)
                if (!isAdded) return
                Toast.makeText(context, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToMovieDetail(movie: Movie) {
        Log.d(TAG, "navigateToMovieDetail id=${movie.id}")
        val intent = Intent(context, MovieDetailActivity::class.java).putExtra("Id", movie.id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: reset bars")
        super.onDestroyView()
        activity?.window?.let { window ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor =
                    ContextCompat.getColor(requireContext(), R.color.dark_background)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.navigationBarColor =
                        ContextCompat.getColor(requireContext(), android.R.color.black)
                }
            }
        }
    }
}
