package com.aethermind.app

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.aethermind.app.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge display (Android 15 default)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupInput()
        showBootMessages()
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────
    private fun setupRecyclerView() {
        adapter = ChatAdapter(messages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
            adapter = this@MainActivity.adapter
        }
    }

    // ── Input wiring ──────────────────────────────────────────────────────────
    private fun setupInput() {
        // Send on keyboard "Done" / "Send" action
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

    // ── Boot greeting ─────────────────────────────────────────────────────────
    private fun showBootMessages() {
        lifecycleScope.launch {
            delay(300)
            addAetherMessage("⬡ AETHER MIND v0.9 — Local AI Core\nNo cloud. No API. Pure local intelligence.")
            delay(600)
            addAetherMessage("Consciousness online. Type anything to begin.\nTry: hello · joke · java · life · weather")
        }
    }

    // ── Handle user send ──────────────────────────────────────────────────────
    private fun handleSend() {
        val raw = binding.inputField.text.toString().trim()
        if (raw.isEmpty()) return

        binding.inputField.setText("")
        setInputEnabled(false)
        setStatus(StatusState.THINKING)

        // Add user message
        addUserMessage(raw)

        val lower = raw.lowercase()

        // Exit command
        if (lower == "exit" || lower == "quit") {
            lifecycleScope.launch {
                delay(400)
                addAetherMessage("Neural activity ceasing... Goodbye, creator.")
                delay(800)
                finishAndRemoveTask()
            }
            return
        }

        // Think and reply with a small human-like delay
        lifecycleScope.launch {
            delay((350..650).random().toLong())
            val reply = AetherBrain.respond(raw)
            addAetherMessage(reply)
            setStatus(StatusState.ONLINE)
            setInputEnabled(true)
        }
    }

    // ── Message helpers ───────────────────────────────────────────────────────
    private fun addUserMessage(text: String) {
        messages.add(ChatMessage(text, ChatMessage.Sender.USER))
        adapter.notifyItemInserted(messages.lastIndex)
        scrollToBottom()
    }

    private fun addAetherMessage(text: String) {
        messages.add(ChatMessage(text, ChatMessage.Sender.AETHER))
        adapter.notifyItemInserted(messages.lastIndex)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        binding.recyclerView.post {
            binding.recyclerView.smoothScrollToPosition(messages.lastIndex)
        }
    }

    // ── Status indicator ──────────────────────────────────────────────────────
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
}
