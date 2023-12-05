package br.edu.scl.ifsp.ads.onemessagechat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OneMessage(
    var identifier: String = "",
    var content: String = "",
) : Parcelable
