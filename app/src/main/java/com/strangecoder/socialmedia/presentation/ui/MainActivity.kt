package com.strangecoder.socialmedia.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        bottomNav = binding.bottomNavBar

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
            R.id.chatsFragment,
            R.id.profileFragment
        )

        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navGraphIds)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            val id = destination.id

            if (id == R.id.homeFragment
                || id == R.id.searchFragment
                || id == R.id.chatsFragment
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