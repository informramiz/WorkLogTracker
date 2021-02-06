package github.informramiz.timetracker.view.worklogs

import android.os.Bundle
import android.os.Environment
import android.view.*
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.BuildConfig
import github.informramiz.timetracker.R
import github.informramiz.timetracker.common.exhaustive
import github.informramiz.timetracker.databinding.WorkLogsFragmentBinding
import github.informramiz.timetracker.extensions.showToast
import github.informramiz.timetracker.presentation.base.DialogCommand
import github.informramiz.timetracker.presentation.base.NavigationDestination
import github.informramiz.timetracker.presentation.worklogs.WorkLogsDialogCommand
import github.informramiz.timetracker.presentation.worklogs.WorkLogsNavigationDestination
import github.informramiz.timetracker.presentation.worklogs.WorkLogsViewModel
import github.informramiz.timetracker.presentation.worklogs.WorkLogsViewState
import github.informramiz.timetracker.view.base.BaseFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WorkLogsFragment : BaseFragment<WorkLogsViewState>() {
    override val viewModel: WorkLogsViewModel by viewModels()
    private lateinit var viewBinding: WorkLogsFragmentBinding
    private val args: WorkLogsFragmentArgs by navArgs()
    private val workLogsRecyclerAdapter = WorkLogsRecyclerAdapter { model ->
        viewModel.onWorkLogItemClickAction(args.userId, model.workLogId)
    }
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return WorkLogsFragmentBinding.inflate(inflater, container, false)
            .also { viewBinding = it }.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart(args.userId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.logsList.adapter = workLogsRecyclerAdapter
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.createWorkLogFab.setOnClickListener {
            viewModel.onCreateWorkLogFabClickAction(args.userId)
        }

        viewBinding.swipeToRefreshView.setOnRefreshListener {
            viewModel.onSwipeToRefreshAction(args.userId)
        }

        viewBinding.clearFilterImageView.setOnClickListener {
            viewModel.onClearDateFilterClickAction()
        }

        viewBinding.exportFilteredListFab.setOnClickListener {
            viewModel.onExportFilteredListAction()
        }
    }

    override fun renderViewState(viewState: WorkLogsViewState) {
        super.renderViewState(viewState)
        viewBinding.loader.progressBar.isVisible = viewState.isLoading
        viewBinding.swipeToRefreshView.isRefreshing = viewState.isRefreshing
        viewBinding.preferredWorkingHoursTextView.isVisible = viewState.preferredWorkingHours != null
        viewBinding.preferredWorkingHoursTextView.text = getString(R.string.work_logs_fragment_preferred_working_hours_value_text, viewState.preferredWorkingHours ?: 0f)

        listOf(
            viewBinding.filterTextView,
            viewBinding.clearFilterImageView,
            viewBinding.exportFilteredListFab
        ).forEach { it.isVisible = viewState.filter != null }

        if (viewState.filter == null) {
            workLogsRecyclerAdapter.submitList(viewState.workLogs)
        } else {
            viewBinding.filterTextView.text = getString(
                R.string.work_logs_fragment_filter_text_formatted,
                dateFormatter.format(Date(viewState.filter.startDate)),
                dateFormatter.format(Date(viewState.filter.endDate))
            )
            workLogsRecyclerAdapter.submitList(viewState.filter.filteredItems)
        }
        requireActivity().invalidateOptionsMenu()
    }

    override fun navigateToDestination(destination: NavigationDestination) {
        if (destination !is WorkLogsNavigationDestination) {
            super.navigateToDestination(destination)
            return
        }

        when (destination) {
            is WorkLogsNavigationDestination.WorkLogScreen -> navController.navigate(
                WorkLogsFragmentDirections.actionWorkLogsFragmentToWorkLogFragment(
                    destination.userId,
                    destination.workLogId
                )
            )
        }.exhaustive
    }

    override fun renderDialog(dialogCommand: DialogCommand) {
        if (dialogCommand !is WorkLogsDialogCommand) {
            super.renderDialog(dialogCommand)
            return
        }

        when (dialogCommand) {
            is WorkLogsDialogCommand.DateRangePicker -> showDateRangePicker()
            is WorkLogsDialogCommand.ShareFile -> shareFile(dialogCommand.filePath)
        }.exhaustive
    }

    private fun shareFile(path: String) {
        try {
            val uri =
                FileProvider.getUriForFile(requireContext(), BuildConfig.FILE_AUTHORITY, File(path))
            ShareCompat.IntentBuilder.from(requireActivity())
                .addStream(uri)
                .setChooserTitle(R.string.work_logs_fragment_share_html_sheet_choose_title)
                .setType("text/html")
                .startChooser()
        } catch (e: Exception) {
            showToast(e.message ?: getString(R.string.work_logs_fragment_share_html_failure_msg))
        }
    }

    private fun showDateRangePicker() {
        val datePickerDialog = MaterialDatePicker.Builder.dateRangePicker().build()
        datePickerDialog.addOnPositiveButtonClickListener { dateRange ->
            viewModel.onFilterDateRangeSelectedAction(
                dateRange.first ?: System.currentTimeMillis(),
                dateRange.second ?: System.currentTimeMillis()
            )
        }
        datePickerDialog.show(childFragmentManager, "DateRangePicker")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.work_logs_fragment_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.work_logs_action_filter).isVisible = viewModel.currentViewState().workLogs.isNotEmpty()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.work_logs_action_filter -> {
                viewModel.onFilterActionClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
