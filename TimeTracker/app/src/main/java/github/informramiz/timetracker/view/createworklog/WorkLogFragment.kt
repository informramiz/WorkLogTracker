package github.informramiz.timetracker.view.createworklog

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import github.informramiz.timetracker.R
import github.informramiz.timetracker.common.exhaustive
import github.informramiz.timetracker.databinding.WorkLogFragmentBinding
import github.informramiz.timetracker.presentation.base.DialogCommand
import github.informramiz.timetracker.presentation.createworklog.WorkLogDialogCommand
import github.informramiz.timetracker.presentation.createworklog.WorkLogViewModel
import github.informramiz.timetracker.presentation.createworklog.WorkLogViewState
import github.informramiz.timetracker.view.base.BaseFragment
import github.informramiz.timetracker.view.extensions.setSelectedOption
import github.informramiz.timetracker.view.extensions.stringText
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class WorkLogFragment : BaseFragment<WorkLogViewState>() {
    override val viewModel: WorkLogViewModel by viewModels()
    private lateinit var viewBinding: WorkLogFragmentBinding
    private val args: WorkLogFragmentArgs by navArgs()
    private val hoursList: List<Float> = mutableListOf<Float>().apply {
        for (i in 1..24) {
            add(i - 0.5f)
            add(i.toFloat())
        }
    }
    private val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadDetails(args.userId, args.workLogId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return WorkLogFragmentBinding.inflate(inflater, container, false)
            .also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.hoursAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            viewModel.onHoursWorkedItemSelectedAction(
                hoursList[position],
                viewBinding.workEditText.stringText
            )
        }

        viewBinding.dateValueTextView.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setSelection(viewModel.currentViewState().date)
            .build()

        picker.addOnPositiveButtonClickListener { newDate ->
            viewModel.onDateSelectionChangedAction(newDate, viewBinding.workEditText.stringText)
        }

        picker.show(childFragmentManager, "DatePicker")
    }

    override fun renderViewState(viewState: WorkLogViewState) {
        super.renderViewState(viewState)
        val hoursAdapter = ArrayAdapter(requireContext(), R.layout.item_exposed_menu, hoursList)
        viewBinding.hoursAutoCompleteTextView.setAdapter(hoursAdapter)

        viewBinding.workEditText.stringText = viewState.tasksText
        viewBinding.hoursAutoCompleteTextView.setSelectedOption(hoursList.indexOf(viewState.hoursWorked))
        viewBinding.dateValueTextView.text = dateTimeFormatter.format(Date(viewState.date))
        viewBinding.loader.progressBar.isVisible = viewState.isLoading
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.work_log_fragment_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.work_log_fragment_action_delete).isVisible = args.workLogId != null
    }

    override fun renderDialog(dialogCommand: DialogCommand) {
        if (dialogCommand !is WorkLogDialogCommand) {
            super.renderDialog(dialogCommand)
            return
        }

        when (dialogCommand) {
            is WorkLogDialogCommand.DeleteWorkLogConfirmationDialog -> showDeleteWorkLogConfirmationDialog(dialogCommand.userId, dialogCommand.workLogId)
        }.exhaustive
    }

    private fun showDeleteWorkLogConfirmationDialog(userId: String?, workLogId: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.work_log_fragment_delete_work_log_confirmation_dialog_title)
            .setMessage(R.string.work_log_fragment_delete_work_log_confirmation_dialog_body)
            .setPositiveButton(R.string.dialog_button_positive) { _, _ ->
                viewModel.onDeleteWorkLogConfirmationAction(userId, workLogId)
            }
            .setNegativeButton(R.string.dialog_button_negative) { _, _ -> }

        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.work_log_fragment_action_save -> {
                viewModel.onSaveWorkLogClickAction(args.userId, args.workLogId, viewBinding.workEditText.stringText)
                true
            }
            R.id.work_log_fragment_action_delete -> {
                viewModel.onDeleteWorkLogClickAction(args.userId, args.workLogId, viewBinding.workEditText.stringText)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
