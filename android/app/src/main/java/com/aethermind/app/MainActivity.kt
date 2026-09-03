package com.aethermind.app

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aethermind.app.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter
    private var restoring = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AetherBrain.load(this)
        setupRecyclerView()
        setupInput()
        if (!restoreHistory()) {
            showBootMessages()
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupInput() {
        binding.inputField.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                handleSend()
                true
            } else false
        }

        binding.sendButton.setOnClickListener { handleSend() }
    }

    private fun showBootMessages() {
        lifecycleScope.launch {
            delay(300)
            addAetherMessage("⬡ AETHER MIND v0.9 — Local AI Core\nNo cloud. No API. Pure local intelligence.", animate = false)
            delay(600)
            addAetherMessage("Consciousness online. Type anything to begin.\nTry: hello · joke · java · life · weather", animate = false)
        }
    }

    private fun handleSend() {
        val raw = binding.inputField.text.toString().trim()
        if (raw.isEmpty()) return

        binding.inputField.setText("")
        setInputEnabled(false)
        setStatus(StatusState.THINKING)
        addUserMessage(raw)

        if (AetherBrain.isExit(raw)) {
            lifecycleScope.launch {
                delay(400)
                addAetherMessage(AetherBrain.GOODBYE, animate = true)
                setStatus(StatusState.OFFLINE)
            }
            return
        }

        lifecycleScope.launch {
            delay((350..650).random().toLong())
            val reply = AetherBrain.respond(raw)
            addAetherMessage(reply, animate = true)
            setStatus(StatusState.ONLINE)
            setInputEnabled(true)
        }
    }

    private fun addUserMessage(text: String) {
        messages.add(ChatMessage(text, ChatMessage.Sender.USER))
        adapter.notifyItemInserted(messages.lastIndex)
        scrollToBottom()
        persist()
    }

    private suspend fun addAetherMessage(text: String, animate: Boolean) {
        val msg = ChatMessage(if (animate) "" else text, ChatMessage.Sender.AETHER)
        messages.add(msg)
        adapter.notifyItemInserted(messages.lastIndex)
        scrollToBottom()
        if (animate) {
            for (i in 1..text.length) {
                msg.text = text.substring(0, i)
                adapter.notifyItemChanged(messages.lastIndex)
                scrollToBottom()
                delay(16L)
            }
        }
        persist()
    }

    private fun scrollToBottom() {
        binding.recyclerView.post {
            if (messages.isNotEmpty()) {
                binding.recyclerView.smoothScrollToPosition(messages.lastIndex)
            }
        }
    }

    enum class StatusState { ONLINE, THINKING, OFFLINE }

    private fun setStatus(state: StatusState) {
        when (state) {
            StatusState.ONLINE   -> {
                binding.statusDot.setImageResource(R.drawable.ic_dot_green)
                binding.statusText.text = getString(R.string.status_online)
            }
            StatusState.THINKING -> {
                binding.statusDot.setImageResource(R.drawable.ic_dot_yellow)
                binding.statusText.text = getString(R.string.status_thinking)
            }
            StatusState.OFFLINE  -> {
                binding.statusDot.setImageResource(R.drawable.ic_dot_gray)
                binding.statusText.text = getString(R.string.status_offline)
            }
        }
    }

    private fun setInputEnabled(enabled: Boolean) {
        binding.inputField.isEnabled  = enabled
        binding.sendButton.isEnabled  = enabled
    }

    private fun prefs() = getSharedPreferences("aether", MODE_PRIVATE)

    private fun persist() {
        if (restoring) return
        val arr = JSONArray()
        for (m in messages) {
            val o = JSONObject()
            o.put("text", m.text)
            o.put("sender", m.sender.name)
            arr.put(o)
        }
        val mem = JSONArray()
        for (item in AetherBrain.memorySnapshot()) mem.put(item)
        prefs().edit()
            .putString("history", arr.toString())
            .putString("memory", mem.toString())
            .apply()
    }

    private fun restoreHistory(): Boolean {
        val raw = prefs().getString("history", null) ?: return false
        val arr = JSONArray(raw)
        if (arr.length() == 0) return false
        restoring = true
        try {
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val sender = if (o.getString("sender") == "USER")
                    ChatMessage.Sender.USER else ChatMessage.Sender.AETHER
                messages.add(ChatMessage(o.getString("text"), sender))
            }
            adapter.notifyItemRangeInserted(0, messages.size)
            val memRaw = prefs().getString("memory", null)
            if (memRaw != null) {
                val mem = JSONArray(memRaw)
                val items = mutableListOf<String>()
                for (i in 0 until mem.length()) items.add(mem.getString(i))
                AetherBrain.restoreMemory(items)
            }
            scrollToBottom()
            return true
        } catch (_: Exception) {
            messages.clear()
            return false
        } finally {
            restoring = false
        }
    }
}
