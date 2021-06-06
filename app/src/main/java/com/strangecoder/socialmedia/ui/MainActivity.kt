package com.strangecoder.socialmedia.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
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

        setUpNavigation()
    }

    private fun setUpNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        binding.bottomNavBar.apply {
            setupWithNavController(navHostFragment.findNavController())
            setOnNavigationItemReselectedListener {}
        }

        val navGraphIds = setOf(
            R.id.homeFragment,
            R.id.searchFragment,
            R.id.messagesFragment,
            R.id.profileFragment
        )

        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navGraphIds)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        navHostFragment.navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val id = destination.id

            if (id == R.id.homeFragment
                || id == R.id.searchFragment
                || id == R.id.messagesFragment
                || id == R.id.profileFragment
            ) {
                binding.bottomNavBar.isVisible = true
                binding.toolbar.isVisible = true
            } else if (id == R.id.loginFragment
                || id == R.id.registerFragment
            ) {
                binding.bottomNavBar.isVisible = false
                binding.toolbar.isVisible = false
            } else {
                binding.bottomNavBar.isVisible = false
                binding.toolbar.isVisible = true
            }
        }
    }
}