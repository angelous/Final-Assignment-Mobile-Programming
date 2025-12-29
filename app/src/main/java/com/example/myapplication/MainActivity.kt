package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val users = fetchUsers()

            binding.recyclerView.adapter = UserAdapter(users) { user ->
                // klik item → ke detail
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
            }

            binding.tvTitle.text = "User List (${users.size})"
        }

        val initialPaddingBottom = binding.headerContainer.paddingBottom

        binding.recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                val scrollY = recyclerView.computeVerticalScrollOffset()
                val maxScroll = 300f

                val progress = (scrollY / maxScroll).coerceIn(0f, 1f)

                // 🔹 Subtitle fade + slide
                binding.tvSubtitle.alpha = 1f - progress
                binding.tvSubtitle.translationY = -20f * progress

                val newPaddingBottom =
                    (initialPaddingBottom * (1f - progress)).toInt()

                binding.headerContainer.setPadding(
                    binding.headerContainer.paddingLeft,
                    binding.headerContainer.paddingTop,
                    binding.headerContainer.paddingRight,
                    newPaddingBottom
                )
            }
        })
    }

    private suspend fun fetchUsers(): List<User> = withContext(Dispatchers.IO) {
        val url = URL("https://jsonplaceholder.typicode.com/users")
        val connection = url.openConnection() as HttpURLConnection
        val users = mutableListOf<User>()

        try {
            val response = connection.inputStream.bufferedReader().readText()
            val jsonArray = JSONArray(response)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val addressObj = obj.getJSONObject("address")
                val companyObj = obj.getJSONObject("company")
                val geoObj = addressObj.optJSONObject("geo")

                users.add(
                    User(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        username = obj.getString("username"),
                        email = obj.getString("email"),
                        phone = obj.getString("phone"),
                        website = obj.getString("website"),
                        address = Address(
                            street = addressObj.getString("street"),
                            suite = addressObj.getString("suite"),
                            city = addressObj.getString("city"),
                            zipcode = addressObj.getString("zipcode"),
                            geo = Geo(
                                geoObj?.optString("lat") ?: "",
                                geoObj?.optString("lng") ?: ""
                            )
                        ),
                        company = Company(
                            companyObj.getString("name"),
                            companyObj.getString("catchPhrase"),
                            companyObj.getString("bs")
                        )
                    )
                )
            }
        } finally {
            connection.disconnect()
        }

        users
    }
}
