package github.informramiz.timetracker.view.createuser

import android.content.Context
import android.widget.ArrayAdapter
import github.informramiz.timetracker.R

class RoleArrayAdapter(context: Context, items: List<RoleUiModel> = listOf()) :
    ArrayAdapter<RoleUiModel>(
        context,
        R.layout.item_exposed_menu,
        items
    ) {

}
