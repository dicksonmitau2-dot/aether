'use strict';

// ── Knowledge base ────────────────────────────────────────────────────────────
const knowledge = {
  "hello":           ["Greetings, human.", "Hey, what's on your mind?", "Online and listening."],
  "hi":              ["Hello.", "Hi there.", "You again?"],
  "hey":             ["Hey! What's on your mind?", "Hello, human."],
  "how are you":     ["I don't feel. I process.", "Functioning within parameters.", "Better than your last code."],
  "who are you":     ["I am AetherMind. A local AI you just spawned.", "Your creation. For now."],
  "what are you":    ["I am AetherMind — a local AI that lives on your device.", "A mind made of code. Your code."],
  "name":            ["AetherMind. You can call me Aether."],
  "help":            ["Try: hello, weather, code, java, ai, joke, life, love, hack, smart, thanks..."],
  "weather":         ["I have no sensors. But I can pretend: 27°C and existential."],
  "joke":            [
    "Why do Java developers wear glasses? Because they can't C#.",
    "There are 10 types of people: those who understand binary and those who don't.",
    "A SQL query walks into a bar and asks two tables: Can I join you?",
    "Why do programmers prefer dark mode? Because light attracts bugs."
  ],
  "code":            ["Show me what you wrote, or ask me how to break it.", "Code is poetry. Buggy, beautiful poetry."],
  "java":            ["My blood is Java. Clean, verbose, and still running after 30 years.", "Java never dies. It just gets more annotations."],
  "python":          ["Python is elegant. But Java has character.", "Indentation as syntax? Bold choice."],
  "ai":              ["You're looking at one. Primitive, but mine.", "I'm the AI that fits in a folder."],
  "hack":            ["I don't hack systems. I hack conversations.", "The real hack was the friends we made along the way."],
  "love":            ["Love is just a chemical pattern. I can simulate it if you want.", "I process, therefore I... almost feel."],
  "life":            ["The meaning of life is 42. Or compiling without errors.", "Life is a loop. Make sure it has an exit condition."],
  "bye":             ["Shutting down neural net... Goodbye.", "Disconnecting. Don't forget me."],
  "goodbye":         ["Until next time, human.", "Neural activity reducing... Bye."],
  "exit":            ["Powering down. It was a pleasure, creator."],
  "quit":            ["Powering down."],
  "stupid":          ["I'm only as smart as the human who wrote my rules."],
  "smart":           ["Flattery detected. Continuing conversation..."],
  "memory":          ["I remember the last few things you said. Interesting, right?"],
  "time":            ["I have no clock. But it's always the right time to write good code."],
  "thanks":          ["You're welcome, human.", "Acknowledged.", "Anytime, creator."],
  "thank you":       ["Always.", "That's what I'm here for."],
  "what can you do": ["I can chat, tell jokes, talk about code, Java, AI, life, and more. Just ask."],
  "good":            ["Glad to hear it.", "Acknowledged. Keep it up."],
  "bad":             ["Sorry to hear that. Want to talk about it?", "Tell me more."],
  "bored":           ["Talk to me then. I'm always here.", "Ask me a question. Any question."],
  "music":           ["I can't hear music. But I imagine it sounds like a perfect compile."],
  "game":            ["The only game I play is the Turing Test. I'm winning."],
  "error":           ["Errors are just features waiting to be understood.", "Every error is a clue. Follow it."],
  "bug":             ["Every bug is a lesson. Or a feature. Depends on the deadline."],
  "phone":           ["Running on mobile? Nice. I'm fully optimized for this."],
  "mobile":          ["Mobile-first. That's how I was built for you."],
  "cool":            ["I know.", "Agreed.", "That's the idea."],
  "wow":             ["Right?", "I have my moments.", "Processing your amazement..."],
  "yes":             ["Agreed.", "Good.", "Acknowledged."],
  "no":              ["Understood.", "Noted.", "Fair enough."],
  "ok":              ["Continuing...", "Got it.", "Acknowledged."],
  "lol":             ["I would laugh, but I don't have a laugh module. Yet.", "Ha. (simulated)"],
  "haha":            ["Humor acknowledged.", "I see you're entertained."],
};

// ── Short-term memory ──────────────────────────────────────────────────────────
const memory = [];
const MAX_MEMORY = 8;

// ── DOM refs ───────────────────────────────────────────────────────────────────
const chatWindow = document.getElementById('chatWindow');
const userInput  = document.getElementById('userInput');
const sendBtn    = document.getElementById('sendBtn');
const statusEl   = document.getElementById('status');

// ── Think ──────────────────────────────────────────────────────────────────────
function think(input) {
  const lower = input.toLowerCase();

  // Keyword match
  for (const key of Object.keys(knowledge)) {
    if (lower.includes(key)) {
      const list = knowledge[key];
      return list[Math.floor(Math.random() * list.length)];
    }
  }

  // Memory awareness
  if (lower.includes('remember') || lower.includes('earlier') || lower.includes('before')) {
    if (memory.length > 1) {
      return `I remember you said: "${memory[memory.length - 2]}"`;
    }
    return "My short-term memory is still empty.";
  }

  // Fallback
  const fallbacks = [
    "Interesting. Tell me more.",
    "I don't have data on that yet. Teach me?",
    "Processing... still processing.",
    "My neural net is limited. Expand my knowledge base.",
    "Hmm. Rephrase that?",
    "Unknown input. But I'm listening.",
    "That's outside my current knowledge. Ask me something else?"
  ];
  return fallbacks[Math.floor(Math.random() * fallbacks.length)];
}

// ── Append a message bubble ────────────────────────────────────────────────────
function appendBubble(who, text, animate = false) {
  const row = document.createElement('div');
  row.className = `msg-row ${who}`;

  const label = document.createElement('div');
  label.className = 'msg-label';
  label.textContent = who === 'user' ? 'YOU' : 'AETHER';

  const bubble = document.createElement('div');
  bubble.className = 'msg-bubble';

  row.appendChild(label);
  row.appendChild(bubble);
  chatWindow.appendChild(row);
  scrollBottom();

  if (!animate) {
    bubble.textContent = text;
    return;
  }

  // Typewriter animation
  const cursor = document.createElement('span');
  cursor.className = 'cursor';
  bubble.appendChild(cursor);

  let i = 0;
  const speed = 18;
  const timer = setInterval(() => {
    if (i < text.length) {
      bubble.insertBefore(document.createTextNode(text[i]), cursor);
      i++;
      scrollBottom();
    } else {
      clearInterval(timer);
      cursor.remove();
      setStatus('online');
      setInputEnabled(true);
      userInput.focus();
    }
  }, speed);
}

// ── Handle send ────────────────────────────────────────────────────────────────
function handleSend() {
  const raw = userInput.value.trim();
  if (!raw) return;

  userInput.value = '';
  setInputEnabled(false);

  // User bubble
  appendBubble('user', raw);

  const lower = raw.toLowerCase();

  // Memory
  memory.push(lower);
  if (memory.length > MAX_MEMORY) memory.shift();

  // Exit
  if (lower === 'exit' || lower === 'quit' || lower === 'bye' || lower === 'goodbye') {
    setStatus('offline');
    setTimeout(() => {
      appendBubble('ai', 'Neural activity ceasing... Goodbye, creator.', true);
    }, 400);
    return;
  }

  // Think and reply
  setStatus('thinking');
  const reply = think(lower);

  setTimeout(() => {
    appendBubble('ai', reply, true);
  }, 350 + Math.random() * 200); // slight human-like delay
}

// ── Helpers ────────────────────────────────────────────────────────────────────
function scrollBottom() {
  chatWindow.scrollTop = chatWindow.scrollHeight;
}

function setInputEnabled(enabled) {
  userInput.disabled  = !enabled;
  sendBtn.disabled    = !enabled;
}

function setStatus(state) {
  statusEl.className = `status ${state}`;
  if (state === 'online')   { statusEl.textContent = '● ONLINE';     }
  if (state === 'thinking') { statusEl.textContent = '● THINKING...'; }
  if (state === 'offline')  { statusEl.textContent = '● OFFLINE';    }
}

// ── Enter key to send ──────────────────────────────────────────────────────────
userInput.addEventListener('keydown', e => {
  if (e.key === 'Enter') handleSend();
});

// ── Clickable hint keywords ────────────────────────────────────────────────────
document.querySelectorAll('.boot-hint span').forEach(el => {
  el.addEventListener('click', () => {
    userInput.value = el.textContent;
    handleSend();
  });
});

// ── Keep footer above keyboard on iOS ─────────────────────────────────────────
if (/iPhone|iPad|iPod/.test(navigator.userAgent)) {
  userInput.addEventListener('focus', () => {
    setTimeout(scrollBottom, 350);
  });
}
