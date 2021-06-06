package com.strangecoder.socialmedia.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        binding.bottomNavBar.apply {
            setupWithNavController(navHostFragment.findNavController())
            setOnNavigationItemReselectedListener {}
        }

        navHostFragment.navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val id = destination.id
            binding.bottomNavBar.isVisible = (id == R.id.homeFragment
                    || id == R.id.searchFragment
                    || id == R.id.messagesFragment
                    || id == R.id.profileFragment)
        }
    }
}