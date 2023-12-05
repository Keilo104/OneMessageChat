package br.edu.scl.ifsp.ads.onemessagechat.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.scl.ifsp.ads.onemessagechat.R
import br.edu.scl.ifsp.ads.onemessagechat.adapter.OneMessageAdapter
import br.edu.scl.ifsp.ads.onemessagechat.databinding.ActivityMainBinding
import br.edu.scl.ifsp.ads.onemessagechat.model.Constant.EXTRA_ONEMESSAGE
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val oneMessageList: MutableList<OneMessage> = mutableListOf()

    private val messageAdapter: OneMessageAdapter by lazy {
        OneMessageAdapter(
            this,
            oneMessageList,
        )
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
                val oneMessage = result.data?.getParcelableExtra<OneMessage>(EXTRA_ONEMESSAGE)
                oneMessage?.let { _oneMessage ->
                    if(oneMessageList.any { it.identifier.equals(_oneMessage.identifier) }) {
                        val position = oneMessageList.indexOfFirst {
                            it.identifier.equals(_oneMessage.identifier)
                        }

                        oneMessageList[position] = _oneMessage

                    } else {
                        oneMessageList.add(_oneMessage)
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
        return when (item.itemId) {
            R.id.editMessageMi -> {
                val oneMessage = oneMessageList[position]
                launchEditMessageActivity(oneMessage)
                true
            }

            R.id.unsubscribeMessageMi -> {
                oneMessageList.removeAt(position)
                messageAdapter.notifyDataSetChanged()

                Toast.makeText(
                    this,
                    resources.getString(R.string.main_activity_toast_unsubscribe),
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