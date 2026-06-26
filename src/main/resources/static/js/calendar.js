window.PopupCalendar = (() => {
    'use strict';
    const gridEl   = document.getElementById('calGrid');
    const monthEl  = document.getElementById('calMonth');
    const prevBtn  = document.getElementById('calPrev');
    const nextBtn  = document.getElementById('calNext');
    const eventsEl = document.getElementById('calEvents');
    if (!gridEl) return null;

    const WEEKDAYS = ['일', '월', '화', '수', '목', '금', '토'];
    let _stores = (window.storesData || []).slice();
    let _year   = new Date().getFullYear();
    let _month  = new Date().getMonth();

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
            if (isToday)       el.classList.add('today');
            if (evDays.has(d)) el.classList.add('has-event');
            el.addEventListener('click', () => {
                gridEl.querySelectorAll('.cal-day.selected').forEach(x => x.classList.remove('selected'));
                el.classList.add('selected');
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
                <img src="${s.imageUrl}" alt="${s.name}" class="cal-event-thumb"/>
                <div>
                    <p class="cal-event-name">${s.name}</p>
                    <p class="cal-event-date">${(s.startDate||'').replace(/-/g,'.')} ~ ${(s.endDate||'').replace(/-/g,'.')}</p>
                </div>`;
            eventsEl.appendChild(a);
        });
    }

    function _resetEvents() {
        eventsEl.innerHTML = '<p class="cal-events-hint">날짜를 선택하면 팝업스토어 일정을 확인할 수 있어요.</p>';
    }

    function setStores(list) {
        _stores = list.slice();
        _renderCal();
        _resetEvents();
    }

    prevBtn?.addEventListener('click', () => {
        if (--_month < 0) { _month = 11; _year--; }
        _renderCal(); _resetEvents();
    });
    nextBtn?.addEventListener('click', () => {
        if (++_month > 11) { _month = 0; _year++; }
        _renderCal(); _resetEvents();
    });

    _renderCal();
    return { setStores };
})();
