package com.ld.lockeddoor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ld.lockeddoor.R
import com.ld.lockeddoor.models.SpinnerModel
class SpinnerAdapterIcon(ctx: Context,
                       iconDataList: List<SpinnerModel>) :
    ArrayAdapter<SpinnerModel>(ctx, 0, iconDataList) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val iconData = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_item,
            parent,
            false
        )


        val imageIcon:ImageView=view.findViewById(R.id.iconImage)
        val imageText:TextView=view.findViewById(R.id.iconText)

        iconData?.image?.let { imageIcon.setImageResource(it)
          imageText.text=iconData.description
        }


        return view
    }
}