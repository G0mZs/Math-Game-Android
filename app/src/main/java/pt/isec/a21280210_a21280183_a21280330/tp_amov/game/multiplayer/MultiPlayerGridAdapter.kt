package pt.isec.a21280210_a21280183_a21280330.tp_amov.game.multiplayer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import pt.isec.a21280210_a21280183_a21280330.tp_amov.R

class MultiPlayerGridAdapter(
    private val viewModel: MultiPlayerGameViewModel,
    private val context: Context
) : BaseAdapter() {

    private var layoutInflater: LayoutInflater? = null
    private lateinit var cell: TextView
    private lateinit var layout : LinearLayout

    override fun getCount(): Int {
        return viewModel.getBoard().size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        // on blow line we are checking if layout inflater
        // is null, if it is null we are initializing it.
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            // on below line we are passing the layout file
            // which we have to inflate for each item of grid view.
            convertView = layoutInflater!!.inflate(R.layout.item_grid, null)
        }

        cell = convertView!!.findViewById(R.id.grid_cell)
        layout = convertView.findViewById(R.id.layout_grid)
        cell.text = viewModel.getBoard()[position]

        if(viewModel.getBoard()[position] == "x" || viewModel.getBoard()[position] == "+" || viewModel.getBoard()[position] == "-" || viewModel.getBoard()[position] == "÷"){
            cell.background.setTint(Color.rgb(160,226,250))
            layout.setBackgroundColor(Color.rgb(160,226,250))
        }
        else if(viewModel.getBoard()[position] == ""){
            cell.background.setTint(Color.WHITE)
            layout.setBackgroundColor(Color.WHITE)
        }
        else {
            cell.background.setTint(Color.rgb(25, 208, 189))
            layout.setBackgroundColor(Color.rgb(25, 208, 189))
        }

        return convertView
    }
}