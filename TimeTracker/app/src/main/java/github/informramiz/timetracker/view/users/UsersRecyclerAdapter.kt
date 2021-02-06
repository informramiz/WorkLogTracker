package github.informramiz.timetracker.view.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import github.informramiz.timetracker.databinding.ItemUserBinding
import github.informramiz.timetracker.domain.authentication.model.UserInfoDomainModel
import github.informramiz.timetracker.view.extensions.toStringResource

val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<UserInfoDomainModel>() {
    override fun areItemsTheSame(
        oldItem: UserInfoDomainModel,
        newItem: UserInfoDomainModel
    ): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(
        oldItem: UserInfoDomainModel,
        newItem: UserInfoDomainModel
    ): Boolean {
        return oldItem == newItem
    }
}

class UsersRecyclerAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<UserInfoDomainModel, UsersRecyclerAdapter.UserItemViewHolder>(
        DIFF_ITEM_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        return UserItemViewHolder.create(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun interface OnItemClickListener {
        fun onItemClick(userInfoDomainModel: UserInfoDomainModel)
    }

    class UserItemViewHolder(
        private val viewBinding: ItemUserBinding,
        private val onItemClickListener: OnItemClickListener
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(userInfoDomainModel: UserInfoDomainModel) {
            viewBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(
                    userInfoDomainModel
                )
            }

            viewBinding.nameTextView.text = userInfoDomainModel.name
            viewBinding.emailTextView.text = userInfoDomainModel.email
            viewBinding.roleTextView.text = itemView.context.getText(userInfoDomainModel.role.toStringResource())
        }

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClickListener: OnItemClickListener
            ): UserItemViewHolder {
                return UserItemViewHolder(
                    ItemUserBinding.inflate(
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
