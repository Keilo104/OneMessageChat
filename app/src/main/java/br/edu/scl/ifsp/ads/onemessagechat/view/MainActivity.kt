package br.edu.scl.ifsp.ads.onemessagechat.view

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.edu.scl.ifsp.ads.onemessagechat.R
import br.edu.scl.ifsp.ads.onemessagechat.adapter.OneMessageAdapter
import br.edu.scl.ifsp.ads.onemessagechat.controller.OneMessageController
import br.edu.scl.ifsp.ads.onemessagechat.databinding.ActivityMainBinding
import br.edu.scl.ifsp.ads.onemessagechat.model.Constant.EXTRA_ONEMESSAGE
import br.edu.scl.ifsp.ads.onemessagechat.model.Constant.ONEMESSAGE_ARRAY
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage


class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val oneMessageList: MutableList<OneMessage> = mutableListOf()

    private val oneMessageController: OneMessageController by lazy {
        OneMessageController(this)
    }

    private val messageAdapter: OneMessageAdapter by lazy {
        OneMessageAdapter(
            this,
            oneMessageList,
        )
    }

    companion object {
        const val GET_ONEMESSAGE_MSG = 1
        const val GET_ONEMESSAGE_INTERVAL = 2000L
    }

    val updateOneMessageListHandler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if (msg.what == GET_ONEMESSAGE_MSG) {
                oneMessageController.getOneMessages()

                sendMessageDelayed(
                    obtainMessage().apply { what = GET_ONEMESSAGE_MSG },
                    GET_ONEMESSAGE_INTERVAL
                )
            } else {
                msg.data.getParcelableArray(ONEMESSAGE_ARRAY)?.also { oneMessageArray ->
                    oneMessageList.clear()
                    oneMessageArray.forEach {
                        oneMessageList.add(it as OneMessage)
                    }
                    messageAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.title = resources.getString(R.string.main_activity_toolbar_title)

        amb.messageLv.adapter = messageAdapter

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val oneMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(EXTRA_ONEMESSAGE, OneMessage::class.java)
                } else {
                    result.data?.getParcelableExtra<OneMessage>(EXTRA_ONEMESSAGE)
                }

                oneMessage?.let { _oneMessage ->
                    if(oneMessageList.any { it.identifier.equals(_oneMessage.identifier) }) {
                        oneMessageController.editOneMessage(_oneMessage)

                    } else {
                        oneMessageController.insertOneMessage(_oneMessage)
                    }

                    messageAdapter.notifyDataSetChanged()
                }
            }
        }

        amb.messageLv.setOnItemClickListener { _, _, position, _ ->
            val oneMessage = oneMessageList[position]
            launchEditMessageActivity(oneMessage)
        }

        registerForContextMenu(amb.messageLv)

        updateOneMessageListHandler.apply {
            sendMessage(
                obtainMessage().apply { what = GET_ONEMESSAGE_MSG }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.createMessageMi -> {
                launchCreateMessageActivity()
                true
            }

            R.id.subscribeMessageMi -> {
                val alert: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)

                alert.setTitle(R.string.main_activity_alert_title)
                alert.setMessage(R.string.main_activity_alert_message)
                val input = EditText(this)
                alert.setView(input)

                alert.setPositiveButton(
                    R.string.main_activity_alert_positive
                ) { _, _ ->
                    val value: String = input.text.toString()
                    oneMessageController.subscribeToMessage(value)
                }

                alert.setNegativeButton(
                    R.string.main_activity_alert_negative
                ) { _, _ ->
                    Toast.makeText(
                        this,
                        R.string.main_activity_alert_negative_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                alert.show()

                true
            }

            else -> false
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.context_tile_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = (item.menuInfo as AdapterView.AdapterContextMenuInfo).position
        val oneMessage = oneMessageList[position]

        return when (item.itemId) {
            R.id.editMessageMi -> {
                launchEditMessageActivity(oneMessage)
                true
            }

            R.id.unsubscribeMessageMi -> {
                oneMessageController.unsubscribeToMessage(oneMessage.identifier)

                Toast.makeText(
                    this,
                    resources.getString(R.string.main_activity_toast_unsubscribe),
                    Toast.LENGTH_SHORT,
                ).show()

                true
            }

            R.id.deleteMessageMi -> {
                oneMessageController.removeOneMessage(oneMessage)

                Toast.makeText(
                    this,
                    resources.getString(R.string.main_activity_toast_delete),
                    Toast.LENGTH_SHORT,
                ).show()

                true
            }

            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterForContextMenu(amb.messageLv)
    }

    private fun launchCreateMessageActivity() {
        carl.launch(Intent(this, MessageActivity::class.java))
    }

    private fun launchEditMessageActivity(oneMessage: OneMessage) {
        val editOneMessageIntent = Intent(this, MessageActivity::class.java)
        editOneMessageIntent.putExtra(EXTRA_ONEMESSAGE, oneMessage)
        carl.launch(editOneMessageIntent)
    }
}