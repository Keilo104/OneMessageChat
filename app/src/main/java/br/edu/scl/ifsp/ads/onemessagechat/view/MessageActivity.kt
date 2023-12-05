package br.edu.scl.ifsp.ads.onemessagechat.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.scl.ifsp.ads.onemessagechat.R
import br.edu.scl.ifsp.ads.onemessagechat.databinding.ActivityMessageBinding
import br.edu.scl.ifsp.ads.onemessagechat.model.Constant.EXTRA_ONEMESSAGE
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage

class MessageActivity : AppCompatActivity() {
    private val amb : ActivityMessageBinding by lazy {
        ActivityMessageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.title = resources.getString(R.string.message_activity_toolbar_title_create)
        supportActionBar?.subtitle = resources.getString(R.string.message_activity_toolbar_subtitle)

        val receivedOneMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("DATA", OneMessage::class.java)
        } else {
            intent.getParcelableExtra<OneMessage>("DATA")
        }

        receivedOneMessage?.let { _receivedOneMessage ->
            with(amb) {
                supportActionBar?.title = resources.getString(R.string.message_activity_toolbar_title_edit)
                messageIdentifierEt.setText(_receivedOneMessage.identifier)
                messageContentEt.setText(_receivedOneMessage.content)

                messageIdentifierEt.isEnabled = false
            }
        }

        with(amb) {
            saveBt.setOnClickListener {
                val oneMessage = OneMessage(
                    identifier = messageIdentifierEt.text.toString(),
                    content = messageContentEt.text.toString(),
                )

                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_ONEMESSAGE, oneMessage)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}