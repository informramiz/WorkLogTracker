package github.informramiz.timetracker.view.worklogs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import github.informramiz.timetracker.R
import github.informramiz.timetracker.databinding.ItemWorkLogBinding
import github.informramiz.timetracker.domain.worklogs.model.WorkLogDomainModel
import github.informramiz.timetracker.presentation.settings.WorkLogUiModel
import github.informramiz.timetracker.view.extensions.getColorCompat
import java.text.SimpleDateFormat
import java.util.*

val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<WorkLogUiModel>() {
    override fun areItemsTheSame(
        oldItem: WorkLogUiModel,
        newItem: WorkLogUiModel
    ): Boolean {
        return oldItem.workLogId == newItem.workLogId
    }

    override fun areContentsTheSame(
        oldItem: WorkLogUiModel,
        newItem: WorkLogUiModel
    ): Boolean {
        return oldItem == newItem
    }
}

class WorkLogsRecyclerAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<WorkLogUiModel, WorkLogsRecyclerAdapter.WorkLogItemViewHolder>(
        DIFF_ITEM_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkLogItemViewHolder {
        return WorkLogItemViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: WorkLogItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun interface OnItemClickListener {
        fun onItemClick(uiModel: WorkLogUiModel)
    }

    class WorkLogItemViewHolder(
        private val viewBinding: ItemWorkLogBinding,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        private val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun bind(model: WorkLogUiModel) {
            viewBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(model)
            }

            viewBinding.taskTextView.text = model.tasksText
            viewBinding.dateTextView.text = dateTimeFormatter.format(Date(model.date))
            viewBinding.hoursTextView.text = model.hoursWorked.toString()
            if (model.isBackgroundColorEnabled) {
                updateItemViewBackgroundColor(model.isUnderPreferredHoursCount)
            }
        }

        private fun updateItemViewBackgroundColor(isUnderPreferredHoursCount: Boolean) {
            val backgroundColor = if (isUnderPreferredHoursCount) {
                itemView.context.getColorCompat(R.color.very_light_red)
            } else {
                itemView.context.getColorCompat(R.color.very_light_green)
            }

            viewBinding.root.setCardBackgroundColor(backgroundColor)
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClickListener: OnItemClickListener
            ): WorkLogItemViewHolder {
                return WorkLogItemViewHolder(
                    ItemWorkLogBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    onItemClickListener
                )
            }
        }
    }
}
