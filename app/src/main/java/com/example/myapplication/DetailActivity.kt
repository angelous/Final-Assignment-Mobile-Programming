package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        val user = intent.getParcelableExtra<User>("user") ?: return

        findViewById<TextView>(R.id.tvAvatar).text = user.username.trim().first().uppercase()
        findViewById<TextView>(R.id.tvName).text = user.name
        findViewById<TextView>(R.id.tvUsername).text = user.username
        findViewById<TextView>(R.id.tvEmail).text = user.email
        findViewById<TextView>(R.id.tvPhone).text = user.phone
        findViewById<TextView>(R.id.tvWebsite).text = user.website
        findViewById<TextView>(R.id.tvCompany).text = user.company.name
        findViewById<TextView>(R.id.tvAddress).text = "${user.address.street}, ${user.address.city}"
    }
}