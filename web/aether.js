'use strict';

const STORAGE_KEY = 'aethermind.session.v1';
const DATABASE_NAME = 'aethermind.storage.v1';
const DATABASE_VERSION = 1;
const SESSION_STORE = 'sessions';
const GOODBYE = 'Neural activity ceasing... Goodbye, creator.';
const ONLINE_SEARCH = 'https://en.wikipedia.org/w/api.php?action=query&list=search&format=json&origin=*&srlimit=1&srsearch=';
const ONLINE_PAGE = 'https://en.wikipedia.org/w/api.php?action=query&prop=extracts|pageimages&piprop=thumbnail&pithumbsize=640&exintro=1&explaintext=1&redirects=1&format=json&origin=*&pageids=';

let knowledge = {};
let fallbacks = [];
let memory = [];
let maxMemory = 1000;
let ready = false;
let storageDb = null;

const chatWindow = document.getElementById('chatWindow');
const userInput  = document.getElementById('userInput');
const micBtn     = document.getElementById('micBtn');
const voiceBtn   = document.getElementById('voiceBtn');
const sendBtn    = document.getElementById('sendBtn');
const statusEl   = document.getElementById('status');

const Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
const recognition = Recognition ? new Recognition() : null;
let listening = false;
let voiceEnabled = localStorage.getItem('aethermind.voice.v1') !== 'off';

function phraseMatch(input, key) {
  const escaped = key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  return new RegExp('\\b' + escaped + '\\b', 'i').test(input);
}

function isExit(raw) {
  const s = raw.trim().toLowerCase();
  return s === 'exit' || s === 'quit';
}

function calculate(expression) {
  const tokens = expression.replace(/,/g, '').match(/\d+(?:\.\d+)?|[()+\-*/%]/g);
  if (!tokens || tokens.join('') !== expression.replace(/\s+/g, '').replace(/,/g, '')) return null;
  let index = 0;

  function parsePrimary() {
    if (tokens[index] === '(') {
      index++;
      const value = parseAdditive();
      if (tokens[index] !== ')') throw new Error('missing closing parenthesis');
      index++;
      return value;
    }
    const value = Number(tokens[index++]);
    if (!Number.isFinite(value)) throw new Error('invalid number');
    return value;
  }

  function parseMultiplicative() {
    let value = parsePrimary();
    while (['*', '/', '%'].includes(tokens[index])) {
      const operator = tokens[index++];
      const right = parsePrimary();
      if (operator === '*') value *= right;
      if (operator === '/') value /= right;
      if (operator === '%') value %= right;
    }
    return value;
  }

  function parseAdditive() {
    let value = parseMultiplicative();
    while (['+', '-'].includes(tokens[index])) {
      const operator = tokens[index++];
      const right = parseMultiplicative();
      value = operator === '+' ? value + right : value - right;
    }
    return value;
  }

  try {
    const value = parseAdditive();
    if (index !== tokens.length || !Number.isFinite(value)) return null;
    return Number.isInteger(value) ? String(value) : String(Number(value.toFixed(8)));
  } catch (_) {
    return null;
  }
}

function logicReply(input) {
  const normalized = input.toLowerCase().replace(/[?]/g, '').trim();
  const percent = normalized.match(/(?:what is )?(\d+(?:\.\d+)?)% (?:of|from) (\d+(?:\.\d+)?)/);
  if (percent) {
    const percentage = Number(percent[1]);
    const base = Number(percent[2]);
    return `${percentage}% of ${base} is ${Number((percentage * base / 100).toFixed(8))}.`;
  }

  const conversion = normalized.match(/(?:convert )?(\d+(?:\.\d+)?)\s*(km|kilometers?|mi|miles?|meters?|m|feet|ft|kg|kilograms?|lb|pounds?)\s+(?:to|into)\s+(km|kilometers?|mi|miles?|meters?|m|feet|ft|kg|kilograms?|lb|pounds?)/);
  if (conversion) {
    const value = Number(conversion[1]);
    const from = conversion[2];
    const to = conversion[3];
    const units = { km: 'km', kilometers: 'km', kilometer: 'km', mi: 'mi', miles: 'mi', mile: 'mi', meters: 'm', meter: 'm', m: 'm', feet: 'ft', foot: 'ft', ft: 'ft', kg: 'kg', kilograms: 'kg', kilogram: 'kg', lb: 'lb', pounds: 'lb', pound: 'lb' };
    const normalizedFrom = units[from];
    const normalizedTo = units[to];
    const factors = { km: 1000, mi: 1609.344, m: 1, ft: 0.3048, kg: 1, lb: 0.45359237 };
    const compatible = (['km', 'mi', 'm', 'ft'].includes(normalizedFrom) && ['km', 'mi', 'm', 'ft'].includes(normalizedTo)) || (['kg', 'lb'].includes(normalizedFrom) && ['kg', 'lb'].includes(normalizedTo));
    if (compatible) {
      const result = value * factors[normalizedFrom] / factors[normalizedTo];
      return `${value} ${normalizedFrom} is ${Number(result.toFixed(6))} ${normalizedTo}.`;
    }
  }

  const comparison = normalized.match(/is (.+?) (greater than|less than|equal to) (.+)/);
  if (comparison) {
    const left = calculate(comparison[1].trim());
    const right = calculate(comparison[3].trim());
    if (left !== null && right !== null) {
      const a = Number(left);
      const b = Number(right);
      const result = comparison[2] === 'greater than' ? a > b : comparison[2] === 'less than' ? a < b : a === b;
      return `${left} is ${result ? '' : 'not '}${comparison[2]} ${right}.`;
    }
  }

  const expression = normalized
    .replace(/^(what is|calculate|compute|solve)\s+/, '')
    .replace(/\bmultiplied by\b/g, '*')
    .replace(/\btimes\b/g, '*')
    .replace(/\bdivided by\b/g, '/')
    .replace(/\bplus\b/g, '+')
    .replace(/\bminus\b/g, '-')
    .replace(/\bmod(?:ulo)?\b/g, '%')
    .trim();
  if (/^[\d\s()+*/%.\-]+$/.test(expression) && /\d/.test(expression)) {
    const answer = calculate(expression);
    if (answer !== null) return `The answer is ${answer}.`;
  }

  if (/^(what is )?(the )?time$/.test(normalized)) {
    return `The local time is ${new Intl.DateTimeFormat([], { timeStyle: 'short' }).format(new Date())}.`;
  }
  if (/^(what day is it|what is today's date|what is the date today)$/.test(normalized)) {
    return `Today is ${new Intl.DateTimeFormat([], { dateStyle: 'full' }).format(new Date())}.`;
  }
  return null;
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

function localReply(input) {
  const logic = logicReply(input);
  if (logic) return logic;
  const lower = input.toLowerCase();
  const keys = Object.keys(knowledge).sort((a, b) => b.length - a.length);
  for (const key of keys) {
    if (phraseMatch(lower, key)) {
      const list = knowledge[key];
      return list[Math.floor(Math.random() * list.length)];
    }
  }
  if (/\b(remember|earlier|before)\b/.test(lower)) {
    const searchable = lower.replace(/\b(remember|earlier|before)\b/g, '').trim();
    const previous = memory.slice(0, -1).reverse();
    const match = searchable
      ? previous.find(line => line.includes(searchable.split(/\s+/)[0]))
      : previous[0];
    if (match) return `I remember you said: "${match}"`;
    return 'My short-term memory is still empty.';
  }
  return null;
}

function openStorage() {
  if (!('indexedDB' in window)) return Promise.resolve(null);
  return new Promise(resolve => {
    const request = indexedDB.open(DATABASE_NAME, DATABASE_VERSION);
    request.onupgradeneeded = () => {
      request.result.createObjectStore(SESSION_STORE);
    };
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => resolve(null);
  });
}

function readStoredSession() {
  if (!storageDb) return Promise.resolve(null);
  return new Promise(resolve => {
    const request = storageDb.transaction(SESSION_STORE).objectStore(SESSION_STORE).get('current');
    request.onsuccess = () => resolve(request.result || null);
    request.onerror = () => resolve(null);
  });
}

function saveStoredSession(session) {
  if (!storageDb) return;
  const transaction = storageDb.transaction(SESSION_STORE, 'readwrite');
  transaction.objectStore(SESSION_STORE).put(session, 'current');
}

async function onlineReply(input) {
  const search = await fetch(ONLINE_SEARCH + encodeURIComponent(input));
  if (!search.ok) throw new Error('online search failed');
  const searchData = await search.json();
  const result = searchData.query && searchData.query.search && searchData.query.search[0];
  if (!result) return null;

  const page = await fetch(ONLINE_PAGE + result.pageid);
  if (!page.ok) throw new Error('online article failed');
  const pageData = await page.json();
  const article = pageData.query && pageData.query.pages && pageData.query.pages[result.pageid];
  const extract = article && article.extract ? article.extract.trim() : '';
  if (!extract) return null;
  const shortened = extract.length > 700 ? `${extract.slice(0, 697).trim()}...` : extract;
  return {
    text: `${shortened}\n\nSource: Wikipedia — ${result.title}`,
    image: article.thumbnail ? article.thumbnail.source : null,
    title: result.title
  };
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

function appendBubble(who, text, animate = false, imageUrl = null, imageTitle = '') {
  const row = document.createElement('div');
  row.className = `msg-row ${who}`;

  const label = document.createElement('div');
  label.className = 'msg-label';
  label.textContent = who === 'user' ? 'YOU' : 'AETHER';

  const bubble = document.createElement('div');
  bubble.className = 'msg-bubble';

  if (imageUrl) {
    const image = document.createElement('img');
    image.className = 'answer-image';
    image.src = imageUrl;
    image.alt = imageTitle;
    image.loading = 'lazy';
    bubble.appendChild(image);
  }

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

function speak(text) {
  if (!voiceEnabled || !('speechSynthesis' in window)) return;
  window.speechSynthesis.cancel();
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.rate = 1;
  utterance.pitch = 1;
  utterance.onstart = () => setStatus('speaking');
  utterance.onend = () => {
    if (ready && !listening) setStatus('online');
  };
  window.speechSynthesis.speak(utterance);
}

async function handleSend() {
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
      speak(GOODBYE);
    }, 400);
    return;
  }
  setStatus('thinking');
  const local = localReply(raw);
  let reply = local ? { text: local, image: null, title: '' } : null;
  if (!reply) {
    setStatus('searching');
    try {
      reply = await onlineReply(raw);
    } catch (_) {
      reply = null;
    }
  }
  reply = reply || { text: fallbacks[Math.floor(Math.random() * fallbacks.length)], image: null, title: '' };

  setTimeout(async () => {
    await appendBubble('ai', reply.text, true, reply.image, reply.title);
    speak(reply.text);
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
  if (micBtn) micBtn.disabled = !enabled || !recognition;
}

function setStatus(state) {
  statusEl.className = `status ${state}`;
  if (state === 'online')   { statusEl.textContent = '● ONLINE';     }
  if (state === 'thinking') { statusEl.textContent = '● THINKING...'; }
  if (state === 'searching') { statusEl.textContent = '● SEARCHING ONLINE...'; }
  if (state === 'speaking') { statusEl.textContent = '● SPEAKING...'; }
  if (state === 'listening') { statusEl.textContent = '● LISTENING...'; }
  if (state === 'offline')  { statusEl.textContent = '● OFFLINE';    }
}

function updateVoiceButton() {
  voiceBtn.textContent = voiceEnabled ? '\u{1f50a}' : '\u{1f507}';
  voiceBtn.setAttribute('aria-label', voiceEnabled ? 'Turn voice replies off' : 'Turn voice replies on');
  voiceBtn.title = voiceEnabled ? 'Turn voice replies off' : 'Turn voice replies on';
}

function toggleListening() {
  if (!recognition || !ready) return;
  if (listening) {
    recognition.stop();
    return;
  }
  try {
    recognition.start();
  } catch (_) {
    setStatus('online');
  }
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
  saveStoredSession({ rows, memory, savedAt: Date.now() });
}

async function restoreHistory() {
  const stored = await readStoredSession();
  let saved;
  if (stored) {
    saved = stored;
  } else {
    try {
      saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null');
    } catch (_) {
      return;
    }
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

voiceBtn.addEventListener('click', () => {
  voiceEnabled = !voiceEnabled;
  localStorage.setItem('aethermind.voice.v1', voiceEnabled ? 'on' : 'off');
  if (!voiceEnabled && 'speechSynthesis' in window) window.speechSynthesis.cancel();
  updateVoiceButton();
});

micBtn.addEventListener('click', toggleListening);

if (recognition) {
  recognition.continuous = false;
  recognition.interimResults = false;
  recognition.lang = document.documentElement.lang || 'en-US';
  recognition.onstart = () => {
    listening = true;
    micBtn.classList.add('active');
    setStatus('listening');
  };
  recognition.onresult = event => {
    const transcript = event.results[0][0].transcript.trim();
    if (transcript) {
      userInput.value = transcript;
      handleSend();
    }
  };
  recognition.onerror = () => setStatus('online');
  recognition.onend = () => {
    listening = false;
    micBtn.classList.remove('active');
    if (ready && statusEl.classList.contains('listening')) setStatus('online');
  };
} else {
  micBtn.disabled = true;
  micBtn.title = 'Voice input is not supported by this browser';
  micBtn.setAttribute('aria-label', 'Voice input unavailable');
}

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
updateVoiceButton();
loadKnowledge()
  .then(async (data) => {
    fallbacks = Array.isArray(data.fallbacks) ? data.fallbacks : ['Hmm. Rephrase that?'];
    maxMemory = Number(data.memorySize) || 1000;
    knowledge = {};
    for (const t of data.topics || []) {
      if (t && t.key && Array.isArray(t.replies) && t.replies.length) {
        knowledge[String(t.key).toLowerCase()] = t.replies;
      }
    }
    storageDb = await openStorage();
    await restoreHistory();
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
