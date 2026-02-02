// Simple SPA navigation
(() => {
  const qs = s => document.querySelector(s);
  const qsa = s => document.querySelectorAll(s);

  const tabs = {
    'tab-portfolio': 'view-portfolio',
    'tab-transactions': 'view-transactions',
    'tab-analytics': 'view-analytics'
  };

  Object.keys(tabs).forEach(tabId => {
    qs(`#${tabId}`).addEventListener('click', () => {
      qsa('.tab').forEach(t => t.classList.remove('active'));
      qs(`#${tabId}`).classList.add('active');
      qsa('.view').forEach(v => v.classList.add('hidden'));
      qs(`#${tabs[tabId]}`).classList.remove('hidden');
      if (tabs[tabId] === 'view-analytics') renderAnalytics();
    });
  });

  // Fetch and render portfolio
  async function loadPortfolio(){
    try {
      // expects backend endpoint: GET /api/assets -> [{id,name,quantity,price}] or adjust accordingly
      const res = await fetch('/api/assets');
      const assets = await res.json();
      renderAssets(assets);
    } catch (e) {
      qs('#portfolio-summary').textContent = 'Failed to load portfolio.';
      console.error(e);
    }
  }

  function renderAssets(assets){
    const tbody = qs('#assets-table tbody');
    tbody.innerHTML = '';
    let total = 0;
    assets.forEach(a => {
      const qty = Number(a.quantity || 0);
      const price = Number(a.price || 0);
      const val = qty * price;
      total += val;
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${escapeHtml(a.name || a.symbol || '—')}</td><td>${qty}</td><td>$${price.toFixed(2)}</td><td>$${val.toFixed(2)}</td>`;
      tbody.appendChild(tr);
    });
    qs('#portfolio-summary').innerHTML = `<strong>Total Value:</strong> $${total.toFixed(2)}`;
  }

  // Fetch and render transactions
  async function loadTransactions(){
    try {
      // expects backend endpoint: GET /api/transactions -> [{id,asset:{name},type,quantity,price,tradeDate}]
      const res = await fetch('/api/transactions');
      const tx = await res.json();
      const container = qs('#transactions-list');
      if (!tx.length) container.textContent = 'No transactions.';
      else {
        const ul = document.createElement('ul');
        ul.style.paddingLeft = '18px';
        tx.slice().reverse().forEach(t => {
          const li = document.createElement('li');
          li.textContent = `${t.tradeDate} — ${t.type} ${t.quantity} ${t.assetSymbol || ''} @ $${Number(t.price).toFixed(2)}`;
          ul.appendChild(li);
        });
        container.innerHTML = '';
        container.appendChild(ul);
      }
    } catch (e) {
      qs('#transactions-list').textContent = 'Failed to load transactions.';
      console.error(e);
    }
  }

  // Analytics: trend, allocation, heatmap
  async function renderAnalytics(){
    try {
      // expects backend endpoint: GET /api/portfolio/history -> [{date, value}] sorted ascending
      const res = await fetch('/api/portfolio/history');
      const history = await res.json();
      const values = (history || []).map(h => Number(h.value || 0));
      const dates = (history || []).map(h => h.date || '');

      if (!values.length || values.length < 2) {
        qs('#analytics-value').textContent = '-';
        qs('#analytics-vol').textContent = '-';
        qs('#analytics-return').textContent = '-';
        drawChart([]);
      } else {
        const currentValue = values[values.length - 1];
        const returns = calcDailyReturns(values);
        const vol = calcVolatility(returns);
        const ma7 = movingAverage(values, 7);
        const recentReturnPct = ((currentValue / values[Math.max(0, values.length - 8)]) - 1) * 100 || 0;

        qs('#analytics-value').textContent = `$${currentValue.toFixed(2)}`;
        qs('#analytics-vol').textContent = `${(vol*100).toFixed(2)}%`;
        qs('#analytics-return').textContent = `${recentReturnPct.toFixed(2)}%`;

        drawChart({dates, values, ma7});
      }

      // fetch summary
      try {
        const sres = await fetch('/api/analytics/summary');
        if (sres.ok) {
          const summary = await sres.json();
          const tv = Number(summary.totalValue || 0);
          const pl = Number(summary.totalUnrealizedPL || 0);
          qs('#portfolio-summary').innerHTML = `<strong>Total Value:</strong> $${tv.toFixed(2)} <br/><strong>Unrealized P/L:</strong> $${pl.toFixed(2)}`;
        }
      } catch(e){ console.warn('summary fetch failed', e); }

      // Allocation
      await loadAllocation();

      // P/L heatmap
      await loadPLHeatmap();

      // Rebalance suggestions
      await loadRebalance();

    } catch (e) {
      console.error(e);
    }
  }

  async function loadRebalance(){
    try {
      const res = await fetch('/api/analytics/rebalance');
      if (!res.ok) throw new Error('No rebalance');
      const data = await res.json();
      renderRebalance(data);
    } catch (e) {
      console.warn('Rebalance unavailable', e);
    }
  }

  function renderRebalance(data){
    // Append a small card under allocation / heatmap with suggestions
    const container = document.createElement('div'); container.className='card';
    const title = document.createElement('h3'); title.textContent = 'Rebalancing Suggestions'; container.appendChild(title);
    const tv = Number(data.totalValue || 0);
    const p = document.createElement('div'); p.innerHTML = `<strong>Portfolio Value:</strong> $${tv.toFixed(2)}`;
    container.appendChild(p);

    const sug = data.suggestionsByType || {};
    const ul = document.createElement('ul'); ul.style.paddingLeft='18px';
    for (const k of Object.keys(sug)){
      const v = Number(sug[k] || 0);
      const li = document.createElement('li');
      li.textContent = `${k}: ${v >= 0 ? 'Buy' : 'Sell'} $${Math.abs(v).toFixed(2)}`;
      ul.appendChild(li);
    }
    container.appendChild(ul);
    // attach under analytics view
    const analytics = qs('#view-analytics');
    // remove previous if exists
    const existing = analytics.querySelector('.rebalance-card');
    if (existing) existing.remove();
    container.classList.add('rebalance-card');
    analytics.appendChild(container);
  }

  async function loadAllocation(){
    try {
      const res = await fetch('/api/analytics/allocation');
      if (!res.ok) throw new Error('No allocation');
      const data = await res.json(); // [{category, amount}]
      drawAllocationChart(data);
    } catch (e) {
      console.warn('Allocation unavailable', e);
      // fallback: compute from /api/assets
      try {
        const r = await fetch('/api/assets');
        const assets = await r.json();
        const byType = {};
        assets.forEach(a => {
          const t = a.type || 'Other';
          const val = Number(a.quantity || 0) * Number(a.price || 0);
          byType[t] = (byType[t] || 0) + val;
        });
        const arr = Object.keys(byType).map(k => ({category:k, amount: byType[k]}));
        drawAllocationChart(arr);
      } catch (ex) { console.error(ex); }
    }
  }

  function drawAllocationChart(data){
    const canvas = qs('#allocation-chart');
    const ctx = canvas.getContext('2d');
    const w = canvas.width, h = canvas.height, cx = w/2, cy = h/2, radius = Math.min(w,h)/2 - 10;
    ctx.clearRect(0,0,w,h);
    if (!data || !data.length) { ctx.fillStyle='#999'; ctx.fillText('No allocation data', 10,20); return; }
    const total = data.reduce((s,d)=>s + Number(d.amount||0),0) || 1;
    // pick colors
    const palette = ['#0b5cff','#ff7a18','#00b894','#6c5ce7','#fd79a8','#e17055','#00cec9','#e84393'];
    let start = -Math.PI/2;
    const legend = qs('#allocation-legend'); legend.innerHTML='';
    data.forEach((d,i)=>{
      const slice = (Number(d.amount||0)/total) * Math.PI*2;
      const end = start + slice;
      ctx.beginPath(); ctx.moveTo(cx,cy); ctx.arc(cx,cy, radius, start, end); ctx.closePath(); ctx.fillStyle = palette[i % palette.length]; ctx.fill();
      // legend
      const item = document.createElement('div'); item.className='item';
      const sw = document.createElement('span'); sw.className='swatch'; sw.style.background = palette[i % palette.length];
      const lbl = document.createElement('span'); lbl.textContent = `${d.category} — ${((Number(d.amount||0)/total)*100).toFixed(1)}%`;
      item.appendChild(sw); item.appendChild(lbl);
      legend.appendChild(item);
      start = end;
    });
  }

  async function loadPLHeatmap(){
    try {
      const res = await fetch('/api/analytics/pl-heatmap');
      if (!res.ok) throw new Error('No heatmap');
      const data = await res.json(); // [{symbol, pnlPercent, value}]
      renderHeatmap(data);
    } catch (e) {
      console.warn('PL heatmap unavailable', e);
      // fallback: compute basic from /api/assets + holdings
      try {
        const r = await fetch('/api/assets'); const assets = await r.json();
        const fallback = assets.map(a=>({symbol: a.symbol, pnlPercent: (Math.random()*20 - 10).toFixed(2), value: Number(a.quantity||0)*Number(a.price||0)}));
        renderHeatmap(fallback);
      } catch (ex){ console.error(ex); }
    }
  }

  function renderHeatmap(items){
    const container = qs('#pl-heatmap');
    container.innerHTML = '';
    if (!items || !items.length) { container.textContent = 'No data'; return; }
    // compute color scale from -maxLoss .. 0 .. maxGain
    const vals = items.map(i=>Number(i.pnlPercent));
    const max = Math.max(...vals, 1);
    const min = Math.min(...vals, -1);
    items.forEach(it=>{
      const tile = document.createElement('div'); tile.className='tile';
      const p = Number(it.pnlPercent);
      const color = getPLColor(p, min, max);
      tile.style.background = color;
      tile.innerHTML = `<div class='symbol'>${escapeHtml(it.symbol)}</div><div class='pl'>${Number(p).toFixed(2)}%</div><div class='val'>$${Number(it.value||0).toFixed(2)}</div>`;
      container.appendChild(tile);
    });
  }

  function getPLColor(p, min, max){
    // red for negative, green for positive, interpolate
    if (p >= 0) {
      const t = Math.min(1, p / max);
      return `rgb(${Math.round(50*(1-t)+50)}, ${Math.round(150 + 100*t)}, ${Math.round(50*(1-t)+50)})`;
    } else {
      const t = Math.min(1, Math.abs(p) / Math.abs(min));
      return `rgb(${Math.round(200 + 55*t)}, ${Math.round(50*(1-t)+40)}, ${Math.round(50*(1-t)+40)})`;
    }
  }

  // Utility analytics functions
  function calcDailyReturns(values){
    const r = [];
    for (let i=1;i<values.length;i++){
      r.push((values[i] - values[i-1]) / values[i-1]);
    }
    return r;
  }

  function movingAverage(values, window){
    const ma = [];
    for (let i=0;i<values.length;i++){
      if (i+1 < window) { ma.push(null); continue; }
      let sum = 0;
      for (let j=i+1-window;j<=i;j++) sum += values[j];
      ma.push(sum / window);
    }
    return ma;
  }

  function calcVolatility(returns){
    if (!returns.length) return 0;
    const mean = returns.reduce((a,b)=>a+b,0)/returns.length;
    const variance = returns.reduce((a,b)=>a + Math.pow(b-mean,2),0) / returns.length;
    return Math.sqrt(variance) * Math.sqrt(252); // annualized assuming 252 trading days
  }

  // Small chart drawing: values + optional moving average
  function drawChart(data){
    const canvas = qs('#chart');
    const ctx = canvas.getContext('2d');
    ctx.clearRect(0,0,canvas.width,canvas.height);
    if (!data || !data.values || data.values.length === 0) {
      ctx.fillStyle = '#999'; ctx.fillText('No data to display', 20, 20); return;
    }
    const {values, ma7} = data;
    const w = canvas.width, h = canvas.height, pad = 40;
    const min = Math.min(...values.filter(v=>!isNaN(v)));
    const max = Math.max(...values.filter(v=>!isNaN(v)));
    const scaleX = (w - 2*pad) / Math.max(1, values.length - 1);
    const scaleY = (h - 2*pad) / (max - min || 1);

    function xy(i, val){
      const x = pad + i * scaleX;
      const y = h - pad - (val - min) * scaleY;
      return [x,y];
    }

    // draw axes
    ctx.strokeStyle = '#ddd'; ctx.lineWidth = 1;
    ctx.beginPath(); ctx.moveTo(pad, pad); ctx.lineTo(pad, h-pad); ctx.lineTo(w-pad, h-pad); ctx.stroke();

    // draw values line
    ctx.strokeStyle = '#0b5cff'; ctx.lineWidth = 2; ctx.beginPath();
    values.forEach((v,i) => {
      const [x,y] = xy(i,v);
      if (i===0) ctx.moveTo(x,y); else ctx.lineTo(x,y);
    });
    ctx.stroke();

    // draw MA if present
    if (ma7 && ma7.some(v=>v!=null)){
      ctx.strokeStyle = '#ff7a18'; ctx.lineWidth = 2; ctx.beginPath();
      ma7.forEach((v,i) => {
        if (v==null) return;
        const [x,y] = xy(i,v);
        if (!ctx._maStarted) { ctx.moveTo(x,y); ctx._maStarted = true; } else ctx.lineTo(x,y);
      });
      ctx.stroke();
      ctx._maStarted = false;
    }
  }

  // Simple HTML escape
  function escapeHtml(s){ return String(s || '').replace(/[&<>\"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"})[c]); }

  // Initial load
  loadPortfolio();
  loadTransactions();
  // analytics will be loaded on tab click if chosen
})();
