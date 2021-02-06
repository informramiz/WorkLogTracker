package github.informramiz.timetracker.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.R
import github.informramiz.timetracker.databinding.ActivityMainBinding
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.main.MainViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.usersFragment,
                R.id.workLogsFragment
            ), viewBinding.drawerLayout
        )
    }
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewBinding.mainNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel.loadDetails()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(this) { viewState ->
            updateNavigationViewOptions(viewState.userRole)
            updateNavGraph(viewState.userRole)
        }
    }

    private fun updateNavGraph(userRole: UserRole) {
        val validStartDestination = if (userRole == UserRole.REGULAR) R.id.workLogsFragment else R.id.usersFragment
        if (navController.graph.startDestination != validStartDestination) {
            navController.graph = navController.graph.apply {
                startDestination = validStartDestination
            }
        }
    }

    private fun updateNavigationViewOptions(role: UserRole) {
        viewBinding.mainNavigationView.menu.setGroupVisible(
            R.id.user_group,
            role == UserRole.REGULAR
        )
        viewBinding.mainNavigationView.menu.setGroupVisible(
            R.id.user_manager_group,
            role == UserRole.MANAGER
        )
        viewBinding.mainNavigationView.menu.setGroupVisible(
            R.id.admin_group,
            role == UserRole.ADMIN
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
