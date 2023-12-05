package br.edu.scl.ifsp.ads.onemessagechat.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.scl.ifsp.ads.onemessagechat.R
import br.edu.scl.ifsp.ads.onemessagechat.databinding.ActivityMainBinding
import br.edu.scl.ifsp.ads.onemessagechat.model.Constant.EXTRA_ONEMESSAGE
import br.edu.scl.ifsp.ads.onemessagechat.model.OneMessage

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val oneMessageList: MutableList<OneMessage> = mutableListOf()

    private val messageAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            oneMessageList.map { _message ->
                _message.content
            }
        )
    }

    private lateinit var carl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.title = resources.getString(R.string.main_activity_toolbar_title)

        amb.messageLv.adapter = messageAdapter

        amb.addMessageFab.setOnClickListener() {
            launchMessageActivity()
        }

        carl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val participant = result.data?.getParcelableExtra<OneMessage>(EXTRA_ONEMESSAGE)
                participant?.let { _message ->
                    oneMessageList.add(_message)
                    messageAdapter.add(_message.content)
                    messageAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addMessageMi -> {
                launchMessageActivity()
                true
            }
            else -> false
        }
    }

    private fun launchMessageActivity() {
        carl.launch(Intent(this, MessageActivity::class.java))
    }
}