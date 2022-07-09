package com.example.imageeditor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class EditorAdapter : RecyclerView.Adapter<EditorAdapter.ViewHolder>() {
    private val toolList: MutableList<ToolModel> = ArrayList()

    init {
        toolList.add(ToolModel(R.string.editor_shape, R.drawable.ic_editor_shape))
        toolList.add(ToolModel(R.string.editor_image, R.drawable.ic_editor_image))
    }

    // Store the resource such as R.string, R.drawable
    internal class ToolModel(
        val name: Int,
        val icon: Int
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_editing_tools, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = toolList[position]
        holder.textView.text = holder.itemView.context.getText(item.name)
        holder.imageView.setImageResource(item.icon)
    }

    override fun getItemCount(): Int {
        return toolList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_editor_icon)
        val textView: TextView = itemView.findViewById(R.id.text_editor)
    }

}