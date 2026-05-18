/* ============================================================
   FloowRoom — Frontend SPA
   Vanilla JS · comunicação com a API REST via fetch + JWT
   ============================================================ */

'use strict';

// ─── Config ─────────────────────────────────────────────────
const API = '/api';
let token  = localStorage.getItem('floowroom_token') || null;
let me     = JSON.parse(localStorage.getItem('floowroom_user') || 'null');

// Dados em memória (preenchidos nas chamadas de listagem)
let salas      = [];
let pessoas    = [];
let pessoaTipos = [];
let eventoTipos = [];

// ─── Utils ───────────────────────────────────────────────────
const $ = (sel, ctx = document) => ctx.querySelector(sel);
const $$ = (sel, ctx = document) => [...ctx.querySelectorAll(sel)];

function fmt(dt) {
  if (!dt) return '—';
  const d = new Date(dt);
  return d.toLocaleString('pt-BR', { day:'2-digit', month:'2-digit', year:'numeric',
    hour:'2-digit', minute:'2-digit' });
}

function fmtDate(d) {
  if (!d) return '—';
  return new Date(d + 'T00:00').toLocaleDateString('pt-BR');
}

function initials(name) {
  return (name || '?').split(' ').slice(0,2).map(n => n[0]).join('').toUpperCase();
}

// ─── Toast ───────────────────────────────────────────────────
function toast(msg, type = 'info') {
  const icons = { success: '✅', error: '❌', info: 'ℹ️' };
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = `<span>${icons[type]}</span><span>${msg}</span>`;
  $('#toast-container').appendChild(el);
  setTimeout(() => el.remove(), 4000);
}

// ─── API helper ──────────────────────────────────────────────
async function api(path, opts = {}) {
  const headers = { 'Content-Type': 'application/json', ...(opts.headers || {}) };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch(API + path, { ...opts, headers });
  if (res.status === 401) { logout(); return null; }
  if (res.status === 204 || res.headers.get('content-length') === '0') return true;
  const json = await res.json();
  if (!res.ok) throw new Error(json.mensagem || JSON.stringify(json.erros) || 'Erro desconhecido');
  return json;
}

// ─── Auth ────────────────────────────────────────────────────
async function login() {
  const loginVal = $('#inp-login').value.trim();
  const senhaVal = $('#inp-senha').value.trim();
  const errEl    = $('#login-error');
  errEl.style.display = 'none';

  try {
    const data = await api('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ login: loginVal, senha: senhaVal })
    });
    if (!data) return;
    token = data.token;
    me    = { usuarioId: data.usuarioId, nome: data.nome, login: data.login };
    localStorage.setItem('floowroom_token', token);
    localStorage.setItem('floowroom_user', JSON.stringify(me));
    showApp();
  } catch (e) {
    errEl.textContent = e.message || 'Login ou senha inválidos';
    errEl.style.display = 'block';
  }
}

function logout() {
  token = null; me = null;
  localStorage.removeItem('floowroom_token');
  localStorage.removeItem('floowroom_user');
  $('#app').style.display = 'none';
  $('#login-screen').style.display = 'flex';
  $('#inp-login').value = '';
  $('#inp-senha').value = '';
}

function showApp() {
  $('#login-screen').style.display = 'none';
  $('#app').style.display = 'flex';
  // Preenche info do usuário
  $('#user-name').textContent = me.nome;
  $('#user-login').textContent = me.login;
  $('#user-avatar').textContent = initials(me.nome);
  navigate('dashboard');
  loadDominio();
}

// ─── Navigation ──────────────────────────────────────────────
function navigate(page) {
  $$('.page').forEach(p => p.classList.remove('active'));
  $$('.nav-item').forEach(n => n.classList.remove('active'));
  $(`#page-${page}`)?.classList.add('active');
  $(`[data-page="${page}"]`)?.classList.add('active');
  switch (page) {
    case 'dashboard':   loadDashboard(); break;
    case 'salas':       loadSalas(); break;
    case 'pessoas':     loadPessoas(); break;
    case 'agenda':      loadAgenda(); break;
    case 'disponibilidade': loadSalasSelect(); break;
  }
}

// ─── Domínio (tipos) ─────────────────────────────────────────
async function loadDominio() {
  const [pt, et] = await Promise.all([
    api('/dominio/pessoa-tipos'),
    api('/dominio/evento-tipos')
  ]);
  pessoaTipos = pt || [];
  eventoTipos = et || [];
  renderTipoSelects();
}

function renderTipoSelects() {
  const ptOpts = pessoaTipos.map(t =>
    `<option value="${t.pessoaTipoId}">${t.nome}</option>`).join('');
  $$('.sel-pessoa-tipo').forEach(s =>
    s.innerHTML = `<option value="">— Selecione —</option>` + ptOpts);

  const etOpts = eventoTipos.map(t =>
    `<option value="${t.eventoTipoId}">${t.nome}</option>`).join('');
  $$('.sel-evento-tipo').forEach(s =>
    s.innerHTML = `<option value="">— Selecione —</option>` + etOpts);
}

// ─── Dashboard ───────────────────────────────────────────────
async function loadDashboard() {
  const [s, p, a] = await Promise.all([
    api('/salas'), api('/pessoas'), api('/agenda')
  ]);
  salas   = s || [];
  pessoas = p || [];
  const agendas = a || [];

  $('#stat-salas').textContent   = salas.length;
  $('#stat-pessoas').textContent = pessoas.length;
  $('#stat-agenda').textContent  = agendas.length;

  // Agendamentos de hoje
  const hoje = new Date().toDateString();
  const hoje_count = agendas.filter(a => new Date(a.datahoraInicio).toDateString() === hoje).length;
  $('#stat-hoje').textContent = hoje_count;

  // Lista dos próximos 5 agendamentos
  const proximos = agendas
    .filter(a => new Date(a.datahoraInicio) >= new Date())
    .sort((a,b) => new Date(a.datahoraInicio) - new Date(b.datahoraInicio))
    .slice(0, 5);

  const tbody = $('#dash-proximos');
  if (proximos.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state" style="padding:30px;text-align:center;color:var(--muted)">Nenhum agendamento próximo</td></tr>`;
    return;
  }
  tbody.innerHTML = proximos.map(a => `
    <tr>
      <td><span class="badge badge-primary">Sala ${a.salaNumero}</span></td>
      <td>${a.pessoaNome || '—'}</td>
      <td>${a.eventoTipoNome || '—'}</td>
      <td>${fmt(a.datahoraInicio)}</td>
    </tr>`).join('');
}

// ─── Salas ───────────────────────────────────────────────────
async function loadSalas() {
  salas = await api('/salas') || [];
  renderSalas();
}

function renderSalas(filter = '') {
  const tbody = $('#tbl-salas');
  let rows = salas.filter(s =>
    !filter || String(s.numero).includes(filter));
  if (rows.length === 0) {
    tbody.innerHTML = `<tr><td colspan="4" class="empty-state" style="padding:40px;text-align:center;color:var(--muted)">Nenhuma sala encontrada</td></tr>`;
    return;
  }
  tbody.innerHTML = rows.map(s => `
    <tr>
      <td>${s.salaId}</td>
      <td><span class="badge badge-primary">Sala ${s.numero}</span></td>
      <td>${s.atualizadoPorNome || '—'}</td>
      <td>
        <div class="actions-cell">
          <button class="btn btn-icon" title="Editar" onclick="openSalaModal(${s.salaId})">✏️</button>
          <button class="btn btn-icon danger" title="Excluir" onclick="deleteSala(${s.salaId}, ${s.numero})">🗑️</button>
        </div>
      </td>
    </tr>`).join('');
}

function openSalaModal(id = null) {
  const sala = id ? salas.find(s => s.salaId === id) : null;
  $('#modal-sala-title').textContent = sala ? `Editar Sala ${sala.numero}` : 'Nova Sala';
  $('#inp-sala-numero').value = sala?.numero || '';
  $('#modal-sala').dataset.id = id || '';
  openModal('modal-sala');
}

async function saveSala() {
  const id = $('#modal-sala').dataset.id;
  const payload = { numero: parseInt($('#inp-sala-numero').value) };
  try {
    if (id) {
      await api(`/salas/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      toast('Sala atualizada com sucesso!', 'success');
    } else {
      await api('/salas', { method: 'POST', body: JSON.stringify(payload) });
      toast('Sala criada com sucesso!', 'success');
    }
    closeModal('modal-sala');
    loadSalas();
  } catch (e) { toast(e.message, 'error'); }
}

async function deleteSala(id, numero) {
  if (!confirm(`Excluir Sala ${numero}? Esta ação não pode ser desfeita.`)) return;
  try {
    await api(`/salas/${id}`, { method: 'DELETE' });
    toast('Sala excluída.', 'success');
    loadSalas();
  } catch (e) { toast(e.message, 'error'); }
}

// ─── Pessoas ─────────────────────────────────────────────────
async function loadPessoas() {
  pessoas = await api('/pessoas') || [];
  renderPessoas();
}

function renderPessoas(filter = '') {
  const tbody = $('#tbl-pessoas');
  const rows = pessoas.filter(p =>
    !filter || p.nome.toLowerCase().includes(filter.toLowerCase()) ||
    p.cpf.includes(filter));
  if (rows.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state" style="padding:40px;text-align:center;color:var(--muted)">Nenhuma pessoa encontrada</td></tr>`;
    return;
  }
  tbody.innerHTML = rows.map(p => `
    <tr>
      <td>${p.nome}</td>
      <td><code style="font-size:.82rem;color:var(--accent)">${p.cpf}</code></td>
      <td>${p.telefone || '—'}</td>
      <td>${p.pessoaTipoNome ? `<span class="badge badge-success">${p.pessoaTipoNome}</span>` : '—'}</td>
      <td>
        <div class="actions-cell">
          <button class="btn btn-icon" title="Editar" onclick="openPessoaModal(${p.pessoaId})">✏️</button>
          <button class="btn btn-icon danger" title="Excluir" onclick="deletePessoa(${p.pessoaId}, '${p.nome}')">🗑️</button>
        </div>
      </td>
    </tr>`).join('');
}

function openPessoaModal(id = null) {
  const p = id ? pessoas.find(x => x.pessoaId === id) : null;
  $('#modal-pessoa-title').textContent = p ? `Editar — ${p.nome}` : 'Nova Pessoa';
  $('#inp-pessoa-nome').value       = p?.nome || '';
  $('#inp-pessoa-cpf').value        = p?.cpf  || '';
  $('#inp-pessoa-nascimento').value = p?.nascimento || '';
  $('#inp-pessoa-telefone').value   = p?.telefone || '';
  // Garante que os selects estejam preenchidos
  renderTipoSelects();
  if (p?.pessoaTipoNome) {
    const opt = pessoaTipos.find(t => t.nome === p.pessoaTipoNome);
    if (opt) $('.sel-pessoa-tipo').value = opt.pessoaTipoId;
  } else {
    $('.sel-pessoa-tipo').value = '';
  }
  $('#modal-pessoa').dataset.id = id || '';
  openModal('modal-pessoa');
}

async function savePessoa() {
  const id = $('#modal-pessoa').dataset.id;
  const payload = {
    nome:         $('#inp-pessoa-nome').value.trim(),
    cpf:          $('#inp-pessoa-cpf').value.trim(),
    nascimento:   $('#inp-pessoa-nascimento').value || null,
    telefone:     $('#inp-pessoa-telefone').value.trim() || null,
    pessoaTipoId: $('.sel-pessoa-tipo').value ? parseInt($('.sel-pessoa-tipo').value) : null
  };
  try {
    if (id) {
      await api(`/pessoas/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      toast('Pessoa atualizada!', 'success');
    } else {
      await api('/pessoas', { method: 'POST', body: JSON.stringify(payload) });
      toast('Pessoa cadastrada!', 'success');
    }
    closeModal('modal-pessoa');
    loadPessoas();
  } catch (e) { toast(e.message, 'error'); }
}

async function deletePessoa(id, nome) {
  if (!confirm(`Excluir "${nome}"?`)) return;
  try {
    await api(`/pessoas/${id}`, { method: 'DELETE' });
    toast('Pessoa excluída.', 'success');
    loadPessoas();
  } catch (e) { toast(e.message, 'error'); }
}

// ─── Agenda ──────────────────────────────────────────────────
async function loadAgenda() {
  const agendas = await api('/agenda') || [];
  renderAgenda(agendas);
}

function renderAgenda(agendas, filter = '') {
  const container = $('#agenda-grid');
  const filtered  = agendas.filter(a =>
    !filter ||
    String(a.salaNumero).includes(filter) ||
    (a.pessoaNome || '').toLowerCase().includes(filter.toLowerCase()) ||
    (a.eventoTipoNome || '').toLowerCase().includes(filter.toLowerCase()));

  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="empty-state" style="grid-column:1/-1">
        <div class="empty-icon">📅</div>
        <p>Nenhum agendamento encontrado</p>
      </div>`;
    return;
  }

  container.innerHTML = filtered
    .sort((a,b) => new Date(a.datahoraInicio) - new Date(b.datahoraInicio))
    .map(a => `
    <div class="agenda-card">
      <div class="agenda-card-header">
        <div>
          <div class="agenda-sala">🏠 Sala ${a.salaNumero}</div>
          ${a.eventoTipoNome ? `<span class="badge badge-primary" style="margin-top:4px">${a.eventoTipoNome}</span>` : ''}
        </div>
        <button class="btn btn-icon danger" title="Cancelar" onclick="cancelarAgenda(${a.agendaSalaId})">🗑️</button>
      </div>
      <div class="agenda-pessoa">👤 ${a.pessoaNome || '—'}</div>
      <div class="agenda-time">
        📅 ${fmt(a.datahoraInicio)} → ${fmt(a.datahoraFim)}
      </div>
      ${a.observacao ? `<div class="agenda-obs">💬 ${a.observacao}</div>` : ''}
    </div>`).join('');
}

function openAgendaModal() {
  // Reset
  ['#inp-agenda-sala','#inp-agenda-pessoa','#inp-agenda-evento',
   '#inp-agenda-inicio','#inp-agenda-fim','#inp-agenda-obs']
    .forEach(s => $(s) && ($(s).value = ''));
  // Preenche selects de sala e pessoa
  renderAgendaSalaSelect();
  renderAgendaPessoaSelect();
  renderTipoSelects();
  openModal('modal-agenda');
}

function renderAgendaSalaSelect() {
  const sel = $('#inp-agenda-sala');
  sel.innerHTML = `<option value="">— Selecione a sala —</option>` +
    salas.map(s => `<option value="${s.salaId}">Sala ${s.numero}</option>`).join('');
}

function renderAgendaPessoaSelect() {
  const sel = $('#inp-agenda-pessoa');
  sel.innerHTML = `<option value="">— Selecione a pessoa —</option>` +
    pessoas.map(p => `<option value="${p.pessoaId}">${p.nome}</option>`).join('');
}

async function saveAgenda() {
  const payload = {
    salaId:        parseInt($('#inp-agenda-sala').value),
    pessoaId:      $('#inp-agenda-pessoa').value ? parseInt($('#inp-agenda-pessoa').value) : null,
    eventoTipoId:  $('#sel-agenda-evento').value ? parseInt($('#sel-agenda-evento').value) : null,
    datahoraInicio: $('#inp-agenda-inicio').value || null,
    datahoraFim:    $('#inp-agenda-fim').value    || null,
    observacao:     $('#inp-agenda-obs').value.trim() || null
  };
  if (!payload.salaId || !payload.datahoraInicio || !payload.datahoraFim) {
    toast('Preencha sala, data de início e fim.', 'error'); return;
  }
  try {
    await api('/agenda', { method: 'POST', body: JSON.stringify(payload) });
    toast('Agendamento criado!', 'success');
    closeModal('modal-agenda');
    loadAgenda();
  } catch (e) { toast(e.message, 'error'); }
}

async function cancelarAgenda(id) {
  if (!confirm('Cancelar este agendamento?')) return;
  try {
    await api(`/agenda/${id}`, { method: 'DELETE' });
    toast('Agendamento cancelado.', 'success');
    loadAgenda();
  } catch (e) { toast(e.message, 'error'); }
}

// ─── Disponibilidade ─────────────────────────────────────────
async function loadSalasSelect() {
  if (salas.length === 0) salas = await api('/salas') || [];
  const sel = $('#dispon-sala');
  sel.innerHTML = `<option value="">— Selecione —</option>` +
    salas.map(s => `<option value="${s.salaId}">Sala ${s.numero}</option>`).join('');
}

async function verificarDisponibilidade() {
  const salaId = $('#dispon-sala').value;
  const inicio = $('#dispon-inicio').value;
  const fim    = $('#dispon-fim').value;
  if (!salaId || !inicio || !fim) {
    toast('Preencha sala, início e fim.', 'error'); return;
  }
  try {
    const result = await api(`/agenda/disponibilidade/${salaId}?inicio=${encodeURIComponent(inicio)}&fim=${encodeURIComponent(fim)}`);
    renderDisponibilidade(result || [], inicio, fim);
  } catch (e) { toast(e.message, 'error'); }
}

function renderDisponibilidade(ocupados, inicio, fim) {
  const container = $('#dispon-result');
  if (ocupados.length === 0) {
    container.innerHTML = `
      <div class="slot-card" style="border-color:rgba(16,185,129,.4);background:rgba(16,185,129,.08)">
        <span style="font-size:1.4rem">✅</span>
        <div>
          <div style="font-weight:600;color:var(--success)">Sala Disponível</div>
          <div style="font-size:.8rem;color:var(--muted)">Nenhum agendamento no período selecionado</div>
        </div>
      </div>`;
    return;
  }
  container.innerHTML = `
    <p style="color:var(--muted);font-size:.85rem;margin-bottom:12px">
      ⚠️ ${ocupados.length} período(s) ocupado(s) no intervalo selecionado:
    </p>
    <div class="slot-grid">
      ${ocupados.map(a => `
        <div class="slot-card busy">
          <span style="font-size:1.2rem">🔴</span>
          <div>
            <div style="font-weight:600;color:var(--danger)">Ocupado</div>
            <div style="font-size:.8rem;color:var(--muted)">${fmt(a.datahoraInicio)} → ${fmt(a.datahoraFim)}</div>
            <div style="font-size:.8rem;color:var(--muted)">${a.pessoaNome || ''} ${a.eventoTipoNome ? '· '+a.eventoTipoNome : ''}</div>
          </div>
        </div>`).join('')}
    </div>`;
}

// ─── Tipos de Domínio (gestão rápida) ────────────────────────
async function addPessoaTipo() {
  const nome = prompt('Nome do tipo de pessoa (ex: Professor, Aluno):');
  if (!nome?.trim()) return;
  try {
    await api('/dominio/pessoa-tipos', { method:'POST', body: JSON.stringify({ nome: nome.trim() }) });
    toast('Tipo de pessoa criado!', 'success');
    await loadDominio();
  } catch (e) { toast(e.message, 'error'); }
}

async function addEventoTipo() {
  const nome = prompt('Nome do tipo de evento (ex: Reunião, Aula, Palestra):');
  if (!nome?.trim()) return;
  try {
    await api('/dominio/evento-tipos', { method:'POST', body: JSON.stringify({ nome: nome.trim() }) });
    toast('Tipo de evento criado!', 'success');
    await loadDominio();
  } catch (e) { toast(e.message, 'error'); }
}

// ─── Modal helpers ───────────────────────────────────────────
function openModal(id) {
  $(`#${id}-overlay`)?.classList.add('open');
}
function closeModal(id) {
  $(`#${id}-overlay`)?.classList.remove('open');
}

// ─── Bootstrap ───────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {

  // Enter no login
  $$('#inp-login, #inp-senha').forEach(el =>
    el.addEventListener('keydown', e => e.key === 'Enter' && login()));

  // Navegação do sidebar
  $$('.nav-item[data-page]').forEach(item =>
    item.addEventListener('click', () => navigate(item.dataset.page)));

  // Fechar modais ao clicar fora
  $$('.modal-overlay').forEach(ov =>
    ov.addEventListener('click', e => {
      if (e.target === ov) ov.classList.remove('open');
    }));

  // Busca nas tabelas
  $('#search-salas')?.addEventListener('input', e => renderSalas(e.target.value));
  $('#search-pessoas')?.addEventListener('input', e => renderPessoas(e.target.value));

  // Busca na agenda (re-fetch e filtra no cliente)
  $('#search-agenda')?.addEventListener('input', async e => {
    const agendas = await api('/agenda') || [];
    renderAgenda(agendas, e.target.value);
  });

  // Sessão já ativa?
  if (token && me) {
    showApp();
  } else {
    $('#login-screen').style.display = 'flex';
    $('#app').style.display = 'none';
  }
});
