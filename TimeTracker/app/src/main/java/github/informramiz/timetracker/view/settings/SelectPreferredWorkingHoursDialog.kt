package github.informramiz.timetracker.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import github.informramiz.timetracker.R
import github.informramiz.timetracker.databinding.DialogSelectPreferredWorkingHoursBinding
import github.informramiz.timetracker.view.extensions.setSelectedOption

class SelectPreferredWorkingHoursDialog(
    private val onWorkingHoursSelectedListener: OnWorkingHoursSelectedListener
) : DialogFragment() {
    private lateinit var viewBinding: DialogSelectPreferredWorkingHoursBinding
    private val hoursList: List<Float> = mutableListOf<Float>().apply {
        for (i in 1..24) {
            add(i - 0.5f)
            add(i.toFloat())
        }
    }
    private var selectedHoursCount = 1f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DialogSelectPreferredWorkingHoursBinding.inflate(inflater, container, false)
            .also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hoursAdapter = ArrayAdapter(requireContext(), R.layout.item_exposed_menu, hoursList)
        viewBinding.hoursAutoCompleteTextView.setAdapter(hoursAdapter)
        viewBinding.hoursAutoCompleteTextView.setSelectedOption(hoursList.indexOf(selectedHoursCount))
        registerListener()
    }

    private fun registerListener() {
        viewBinding.hoursAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            selectedHoursCount = hoursList[position]
        }

        viewBinding.okButton.setOnClickListener {
            onWorkingHoursSelectedListener.onSelected(selectedHoursCount)
            dismiss()
        }

        viewBinding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    fun interface OnWorkingHoursSelectedListener {
        fun onSelected(hours: Float)
    }
}
