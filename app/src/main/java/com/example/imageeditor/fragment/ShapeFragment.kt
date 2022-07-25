package com.example.imageeditor.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ShapeFragment : BottomSheetDialogFragment() {
    private var listener: ShapeListener? = null

    interface ShapeListener {
        fun onClick(emojiUnicode: String)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make the background transparent
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val contentView = View.inflate(context, R.layout.fragment_bottom_shape_dialog, null)
        dialog.setContentView(contentView)
        val recyclerView: RecyclerView = contentView.findViewById(R.id.recycler_shape)
        recyclerView.layoutManager = GridLayoutManager(activity, 5)
        recyclerView.adapter = ShapeAdapter(requireContext())
        return dialog
    }

    fun setListener(listener: ShapeListener) {
        this.listener = listener
    }

    inner class ShapeAdapter(private val context: Context) : RecyclerView.Adapter<ShapeAdapter.ViewHolder>() {

        val shapeList: ArrayList<String> by lazy {
            getEmojis(context)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_emoji, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = shapeList[position]
        }

        override fun getItemCount(): Int {
            return shapeList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.text_shape)
            init {
                itemView.setOnClickListener {
                    listener?.onClick(shapeList[layoutPosition])
                    dismiss()
                }
            }
        }
    }

    companion object {

        /**
         * Provide the list of emoji in form of unicode string
         *
         * @param context context
         * @return list of emoji unicode
         */
        fun getEmojis(context: Context): ArrayList<String> {
            val emojiList = context.resources.getStringArray(R.array.photo_editor_emoji)
            return emojiList.map {
                convertEmoji(it)
            } as ArrayList<String>
        }

        private fun convertEmoji(emoji: String): String {
            return try {
                val convertEmojiToInt = emoji.substring(2).toInt(16)
                String(Character.toChars(convertEmojiToInt))
            } catch (e: java.lang.NumberFormatException) {
                ""
            }
        }
    }
}