'use strict';

const STORAGE_KEY = 'aethermind.session.v1';
const GOODBYE = 'Neural activity ceasing... Goodbye, creator.';

let knowledge = {};
let fallbacks = [];
let memory = [];
let maxMemory = 8;
let ready = false;

const chatWindow = document.getElementById('chatWindow');
const userInput  = document.getElementById('userInput');
const sendBtn    = document.getElementById('sendBtn');
const statusEl   = document.getElementById('status');

function phraseMatch(input, key) {
  const escaped = key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return new RegExp('\\b' + escaped + '\\b', 'i').test(input);
}

function isExit(raw) {
  const s = raw.trim().toLowerCase();
  return s === 'exit' || s === 'quit';
}

function think(input) {
  const lower = input.toLowerCase();
  const keys = Object.keys(knowledge).sort((a, b) => b.length - a.length);
  for (const key of keys) {
    if (phraseMatch(lower, key)) {
      const list = knowledge[key];
      return list[Math.floor(Math.random() * list.length)];
    }
  }
  if (/\b(remember|earlier|before)\b/.test(lower)) {
    if (memory.length > 1) {
      return `I remember you said: "${memory[memory.length - 2]}"`;
    }
    return 'My short-term memory is still empty.';
  }
  return fallbacks[Math.floor(Math.random() * fallbacks.length)];
}

function remember(lower) {
  memory.push(lower);
  if (memory.length > maxMemory) memory.shift();
}

async function loadKnowledge() {
  const urls = ['knowledge.json', '../shared/knowledge.json'];
  let lastErr = null;
  for (const url of urls) {
    try {
      const res = await fetch(url);
      if (!res.ok) throw new Error(url + ' HTTP ' + res.status);
      return await res.json();
    } catch (err) {
      lastErr = err;
    }
  }
  throw lastErr || new Error('knowledge.json not found');
}

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
    persist();
    return Promise.resolve();
  }

  return new Promise((resolve) => {
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
        persist();
        resolve();
      }
    }, speed);
  });
}

function handleSend() {
  if (!ready) return;
  const raw = userInput.value.trim();
  if (!raw) return;

  userInput.value = '';
  setInputEnabled(false);

  if (!isExit(raw)) remember(raw.toLowerCase());
  appendBubble('user', raw);

  if (isExit(raw)) {
    setStatus('offline');
    setTimeout(async () => {
      await appendBubble('ai', GOODBYE, true);
    }, 400);
    return;
  }
  setStatus('thinking');
  const reply = think(raw);

  setTimeout(async () => {
    await appendBubble('ai', reply, true);
    setStatus('online');
    setInputEnabled(true);
    userInput.focus();
  }, 350 + Math.random() * 200);
}

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

function persist() {
  if (!ready) return;
  const rows = [...chatWindow.querySelectorAll('.msg-row')].map((row) => ({
    who: row.classList.contains('user') ? 'user' : 'ai',
    text: row.querySelector('.msg-bubble').textContent
  }));
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({ rows, memory }));
  } catch (_) { /* quota / private mode */ }
}

function restoreHistory() {
  let saved;
  try {
    saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null');
  } catch (_) {
    return;
  }
  if (!saved || !Array.isArray(saved.rows) || saved.rows.length === 0) return;
  for (const row of saved.rows) {
    appendBubble(row.who === 'user' ? 'user' : 'ai', row.text, false);
  }
  if (Array.isArray(saved.memory)) {
    memory = saved.memory.slice(-maxMemory);
  }
}

userInput.addEventListener('keydown', e => {
  if (e.key === 'Enter') handleSend();
});

document.querySelectorAll('.boot-hint span').forEach(el => {
  el.addEventListener('click', () => {
    userInput.value = el.textContent;
    handleSend();
  });
});

if (/iPhone|iPad|iPod/.test(navigator.userAgent)) {
  userInput.addEventListener('focus', () => {
    setTimeout(scrollBottom, 350);
  });
}

setInputEnabled(false);
loadKnowledge()
  .then((data) => {
    fallbacks = Array.isArray(data.fallbacks) ? data.fallbacks : ['Hmm. Rephrase that?'];
    maxMemory = Number(data.memorySize) || 8;
    knowledge = {};
    for (const t of data.topics || []) {
      if (t && t.key && Array.isArray(t.replies) && t.replies.length) {
        knowledge[String(t.key).toLowerCase()] = t.replies;
      }
    }
    restoreHistory();
    ready = true;
    setInputEnabled(true);
    userInput.focus();
  })
  .catch(() => {
    appendBubble(
      'ai',
      'Could not load knowledge.json. From the repo root run ./run-web.sh (or python3 -m http.server 8080 and open /web/).',
      false
    );
    setStatus('offline');
  });
