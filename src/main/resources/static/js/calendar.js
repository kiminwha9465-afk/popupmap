window.PopupCalendar = (() => {
    'use strict';
    const gridEl   = document.getElementById('calGrid');
    const monthEl  = document.getElementById('calMonth');
    const prevBtn  = document.getElementById('calPrev');
    const nextBtn  = document.getElementById('calNext');
    const eventsEl = document.getElementById('calEvents');
    if (!gridEl) return null;

    const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'];
    let _stores     = (window.storesData || []).slice();
    let _year       = new Date().getFullYear();
    let _month      = new Date().getMonth();
    let _selectedDay = null;

    function _parse(str) {
        if (!str) return null;
        const [y, m, d] = str.split('-').map(Number);
        return new Date(y, m - 1, d);
    }

    function _eventDays() {
        const days  = new Set();
        const first = new Date(_year, _month, 1);
        const last  = new Date(_year, _month + 1, 0);
        _stores.forEach(s => {
            const st = _parse(s.startDate);
            const en = _parse(s.endDate);
            if (!st || !en) return;
            const lo = st < first ? new Date(first) : new Date(st);
            const hi = en > last  ? new Date(last)  : new Date(en);
            for (const d = new Date(lo); d <= hi; d.setDate(d.getDate() + 1)) days.add(d.getDate());
        });
        return days;
    }

    function _storesOnDate(day) {
        const t = new Date(_year, _month, day);
        return _stores.filter(s => {
            const st = _parse(s.startDate);
            const en = _parse(s.endDate);
            return st && en && st <= t && t <= en;
        });
    }

    /* 날짜 미선택 시 전체 진행중·예정 팝업 목록 표시 */
    function _renderAll() {
        const today = new Date(); today.setHours(0, 0, 0, 0);
        const active = _stores
            .filter(s => { const en = _parse(s.endDate); return en && en >= today; })
            .sort((a, b) => (_parse(a.startDate) || 0) - (_parse(b.startDate) || 0));

        eventsEl.innerHTML = '';
        if (active.length === 0) {
            eventsEl.innerHTML = '<p class="cal-events-hint">현재 진행 중인 팝업이 없습니다.</p>';
            return;
        }
        active.forEach(s => {
            const st = _parse(s.startDate);
            const today2 = new Date(); today2.setHours(0, 0, 0, 0);
            const upcoming = st && st > today2;
            const statusBg = upcoming ? '#FEF3C7' : '#DCFCE7';
            const statusC  = upcoming ? '#D97706' : '#16A34A';
            const statusTx = upcoming ? '예정' : '진행중';
            const a = document.createElement('a');
            a.href      = `/store/${s.id}`;
            a.className = 'cal-event-item';
            a.innerHTML = `
                <img src="${s.imageUrl}" alt="${s.name}" class="cal-event-thumb"
                     onerror="this.style.background='#eee';this.removeAttribute('src')"/>
                <div style="min-width:0;">
                    <p class="cal-event-name">${s.name}</p>
                    <p class="cal-event-date">${(s.startDate||'').replace(/-/g,'.')} ~ ${(s.endDate||'').replace(/-/g,'.')}</p>
                    <span style="display:inline-block;margin-top:3px;padding:1px 7px;border-radius:8px;
                                 font-size:10px;font-weight:700;background:${statusBg};color:${statusC};">${statusTx}</span>
                </div>`;
            eventsEl.appendChild(a);
        });
    }

    function _renderEvents(day) {
        const stores = _storesOnDate(day);
        eventsEl.innerHTML = '';
        if (stores.length === 0) {
            eventsEl.innerHTML = `<p class="cal-events-hint">${_month + 1}월 ${day}일에 진행 중인 팝업이 없습니다.</p>`;
            return;
        }
        stores.forEach(s => {
            const a = document.createElement('a');
            a.href      = `/store/${s.id}`;
            a.className = 'cal-event-item';
            a.innerHTML = `
                <img src="${s.imageUrl}" alt="${s.name}" class="cal-event-thumb"
                     onerror="this.style.background='#eee';this.removeAttribute('src')"/>
                <div style="min-width:0;">
                    <p class="cal-event-name">${s.name}</p>
                    <p class="cal-event-date">${(s.startDate||'').replace(/-/g,'.')} ~ ${(s.endDate||'').replace(/-/g,'.')}</p>
                </div>`;
            eventsEl.appendChild(a);
        });
    }

    function _renderCal() {
        const today    = new Date();
        const evDays   = _eventDays();
        const firstDay = new Date(_year, _month, 1).getDay();
        const daysInM  = new Date(_year, _month + 1, 0).getDate();
        const prevDays = new Date(_year, _month, 0).getDate();

        monthEl.textContent = `${_year}년 ${_month + 1}월`;
        gridEl.innerHTML = '';

        WEEKDAYS.forEach(w => {
            const el = document.createElement('div');
            el.className = 'cal-weekday';
            el.textContent = w;
            gridEl.appendChild(el);
        });

        for (let i = 0; i < firstDay; i++) {
            const el = document.createElement('div');
            el.className = 'cal-day other-month';
            el.textContent = prevDays - firstDay + 1 + i;
            gridEl.appendChild(el);
        }

        for (let d = 1; d <= daysInM; d++) {
            const el = document.createElement('div');
            el.className = 'cal-day';
            el.textContent = d;
            const isToday = today.getFullYear() === _year && today.getMonth() === _month && today.getDate() === d;
            if (isToday) el.classList.add('today');
            if (evDays.has(d)) el.classList.add('has-event');
            if (_selectedDay === d && today.getFullYear() === _year && today.getMonth() === _month) {
                el.classList.add('selected');
            }
            el.addEventListener('click', () => {
                gridEl.querySelectorAll('.cal-day.selected').forEach(x => x.classList.remove('selected'));
                el.classList.add('selected');
                _selectedDay = d;
                _renderEvents(d);
            });
            gridEl.appendChild(el);
        }

        const total = firstDay + daysInM;
        const rem   = total % 7 === 0 ? 0 : 7 - (total % 7);
        for (let i = 1; i <= rem; i++) {
            const el = document.createElement('div');
            el.className = 'cal-day other-month';
            el.textContent = i;
            gridEl.appendChild(el);
        }
    }

    function setStores(list) {
        _stores = list.slice();
        _renderCal();
        if (_selectedDay) _renderEvents(_selectedDay);
        else _renderAll();
    }

    prevBtn?.addEventListener('click', () => {
        if (--_month < 0) { _month = 11; _year--; }
        _selectedDay = null;
        _renderCal();
        _renderAll();
    });
    nextBtn?.addEventListener('click', () => {
        if (++_month > 11) { _month = 0; _year++; }
        _selectedDay = null;
        _renderCal();
        _renderAll();
    });

    document.getElementById('calToday')?.addEventListener('click', () => {
        const now = new Date();
        _year  = now.getFullYear();
        _month = now.getMonth();
        _selectedDay = now.getDate();
        _renderCal();
        _renderEvents(_selectedDay);
        // 오늘 셀 선택 표시
        gridEl.querySelectorAll('.cal-day').forEach(el => {
            if (Number(el.textContent) === _selectedDay && !el.classList.contains('other-month')) {
                el.classList.add('selected');
            }
        });
    });

    _renderCal();
    _renderAll();
    return { setStores };
})();
