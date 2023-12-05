package br.edu.scl.ifsp.ads.onemessagechat.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import br.edu.scl.ifsp.ads.onemessagechat.R
import br.edu.scl.ifsp.ads.onemessagechat.databinding.TileMessageBinding
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage

class OneMessageAdapter(
    context: Context,
    private val oneMessageList: MutableList<OneMessage>
) : ArrayAdapter<OneMessage>(context, R.layout.tile_message, oneMessageList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val oneMessage = oneMessageList[position]
        var tmb: TileMessageBinding? = null

        var messageTileView = convertView
        if (messageTileView == null) {
            tmb = TileMessageBinding.inflate(
                context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                parent,
                false
            )

            messageTileView = tmb.root

            val tileMessageHolder = TileMessageHolder(
                tmb.messageIdentifierTv,
                tmb.messageContentTv,
            )

            messageTileView.tag = tileMessageHolder
        }

        val holder = messageTileView.tag as TileMessageHolder

        holder.messageIdentifierTv.text = oneMessage.identifier
        holder.messageContentTv.text = oneMessage.content

        return messageTileView
    }

    private class TileMessageHolder(
        val messageIdentifierTv: TextView,
        val messageContentTv: TextView,
    )
}