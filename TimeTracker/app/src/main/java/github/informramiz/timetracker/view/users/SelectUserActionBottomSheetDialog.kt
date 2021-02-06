package github.informramiz.timetracker.view.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import github.informramiz.timetracker.databinding.DialogAdminSelectUserActionBinding

class SelectUserActionBottomSheetDialog(
    private val userId: String,
    private val onActionSelectedListener: OnActionSelectedListener
) : BottomSheetDialogFragment() {
    private lateinit var viewBinding: DialogAdminSelectUserActionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DialogAdminSelectUserActionBinding.inflate(inflater, container, false)
            .also { viewBinding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.editUserTextView.setOnClickListener {
            onActionSelectedListener.onSelected(UserAction.EditUser(userId))
        }
        viewBinding.openUserWorkLogsTextView.setOnClickListener {
            onActionSelectedListener.onSelected(UserAction.OpenUserLogs(userId))
        }
    }

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    sealed class UserAction(open val userId: String) {
        data class EditUser(override val userId: String) : UserAction(userId)
        data class OpenUserLogs(override val userId: String) : UserAction(userId)
    }

    fun interface OnActionSelectedListener {
        fun onSelected(userAction: UserAction)
    }
}
