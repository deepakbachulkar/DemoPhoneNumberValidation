package com.demo.lloydstest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.demo.lloydstest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding

    companion object {
        lateinit var context: WeakReference<Context>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = WeakReference(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.fragmentMain) as NavHostFragment)
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        graph.id = R.id.fragmentMain
        navHostFragment.navController.graph = graph
    }

    override fun onStart() {
        super.onStart()
        context = WeakReference(this)
    }

    override fun onRestart() {
        super.onRestart()
        context = WeakReference(this)
    }

    override fun onResume() {
        super.onResume()
        context = WeakReference(this)
    }
}