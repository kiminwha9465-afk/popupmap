(function () {
    'use strict';

    const STORAGE_KEY = 'popupmap-bookmarks';
    let _category = null;
    let _bmMode   = false;

    /* ── Bookmark CRUD ──────────────────────────────── */
    function _load() {
        try { return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]'); }
        catch { return []; }
    }

    function _save(list) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
    }

    function isBookmarked(id) {
        return _load().includes(String(id));
    }

    function toggleBookmark(id) {
        const bm  = _load();
        const key = String(id);
        const idx = bm.indexOf(key);
        const added = idx < 0;
        if (added) bm.push(key); else bm.splice(idx, 1);
        _save(bm);
        _syncIcons(key, added);
        _syncBadge();
        if (_bmMode) _apply();
        return added;
    }

    /* ── Filter ─────────────────────────────────────── */
    function _filtered() {
        const all = window.storesData || [];
        let list  = all;
        if (_bmMode) {
            const bm = _load();
            list = list.filter(s => bm.includes(String(s.id)));
        }
        if (_category) {
            list = list.filter(s => s.category === _category);
        }
        return list;
    }

    function setCategory(cat) {
        // 같은 버튼 다시 누르면 해제
        _category = (_category === cat && cat !== null) ? null : cat;
        _syncCatBtns();
        _apply();
    }

    function setBookmarkMode(on) {
        _bmMode = on;
        _syncBmMode();
        _apply();
    }

    function clearAll() {
        _category = null;
        _bmMode   = false;
        _syncCatBtns();
        _syncBmMode();
        _apply();
    }

    function _apply() {
        const list = _filtered();

        // 지도 / 달력 업데이트
        window.PopupMap?.setStores(list);
        window.PopupCalendar?.setStores(list);

        // 필터링됨 뱃지
        const filtering = _bmMode || !!_category;
        document.getElementById('calFilterBadge')?.toggleAttribute('hidden', !filtering);
        document.getElementById('mapFilterBadge')?.toggleAttribute('hidden', !filtering);

        // 지도 카운트 업데이트
        const mapCount = document.getElementById('mapCount');
        if (mapCount) mapCount.textContent = list.length + '개';

        _syncIndicator();
        _renderBmPanel(list);
    }

    /* ── UI sync ────────────────────────────────────── */
    function _syncCatBtns() {
        document.querySelectorAll('[data-mid-cat]').forEach(btn => {
            const v = btn.dataset.midCat;
            const active = (v === '' && _category === null) || (v !== '' && v === _category);
            btn.classList.toggle('active', active);
        });
    }

    function _syncIcons(id, bookmarked) {
        document.querySelectorAll(`.bm-btn[data-id="${id}"]`).forEach(btn => {
            btn.classList.toggle('bookmarked', bookmarked);
            btn.title = bookmarked ? '북마크 해제' : '북마크 추가';
            const svg = btn.querySelector('svg');
            if (svg) svg.setAttribute('fill', bookmarked ? 'currentColor' : 'none');
        });
    }

    function _syncAllIcons() {
        const bm = _load();
        document.querySelectorAll('.bm-btn[data-id]').forEach(btn => {
            const marked = bm.includes(String(btn.dataset.id));
            btn.classList.toggle('bookmarked', marked);
            btn.title = marked ? '북마크 해제' : '북마크 추가';
            const svg = btn.querySelector('svg');
            if (svg) svg.setAttribute('fill', marked ? 'currentColor' : 'none');
        });
    }

    function _syncBadge() {
        const n     = _load().length;
        const badge = document.getElementById('bmBadge');
        if (!badge) return;
        badge.textContent = n;
        badge.hidden = n === 0;
    }

    function _syncBmMode() {
        document.getElementById('bmToggle')?.classList.toggle('active', _bmMode);
        const panel = document.getElementById('bmPanel');
        if (panel) panel.hidden = !_bmMode;
    }

    function _syncIndicator() {
        const btn = document.getElementById('filterClear');
        if (btn) btn.classList.toggle('active', _bmMode || !!_category);
    }

    /* ── Bookmark panel ─────────────────────────────── */
    function _renderBmPanel(list) {
        const el = document.getElementById('bmList');
        if (!el) return;

        if (list.length === 0) {
            const msg = _bmMode && !_category
                ? '북마크한 팝업이 없습니다.<br/>카드의 <b>북마크 아이콘</b>을 눌러 추가해보세요.'
                : '해당 카테고리의 북마크가 없습니다.';
            el.innerHTML = `<div class="bm-empty">
                <div class="bm-empty-icon">🔖</div>
                <p>${msg}</p>
            </div>`;
            return;
        }

        el.innerHTML = list.map(s => {
            const sd = (s.startDate || '').replace(/-/g, '.');
            const ed = (s.endDate   || '').replace(/-/g, '.');
            const marked = isBookmarked(s.id);
            return `<a href="/store/${s.id}" class="bm-card">
                <img src="${s.imageUrl}" alt="${s.name}" class="bm-thumb"
                     onerror="this.style.background='#eee';this.removeAttribute('src')"/>
                <div class="bm-info">
                    <p class="bm-brand">${s.brand || ''}</p>
                    <p class="bm-name">${s.name}</p>
                    <p class="bm-date">${sd} ~ ${ed}</p>
                    <p class="bm-region">${s.region || ''} · ${s.category || ''}</p>
                </div>
                <button class="bm-btn ${marked ? 'bookmarked' : ''}" data-id="${s.id}"
                        title="${marked ? '북마크 해제' : '북마크 추가'}"
                        onclick="event.preventDefault(); PopupFilter.toggleBookmark(${s.id});">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24"
                         fill="${marked ? 'currentColor' : 'none'}" stroke="currentColor" stroke-width="2">
                        <path d="m19 21-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"/>
                    </svg>
                </button>
            </a>`;
        }).join('');
    }

    /* ── Init ───────────────────────────────────────── */
    function _init() {
        _syncAllIcons();
        _syncBadge();

        // mid-section 필터는 항상 "전체"로 시작 (헤더 URL과 무관)

        // 달력·지도 전용 카테고리 버튼 이벤트
        document.querySelectorAll('[data-mid-cat]').forEach(btn => {
            btn.addEventListener('click', () => setCategory(btn.dataset.midCat || null));
        });

        // 북마크 토글
        document.getElementById('bmToggle')?.addEventListener('click', () => {
            setBookmarkMode(!_bmMode);
        });

        // 필터 초기화
        document.getElementById('filterClear')?.addEventListener('click', clearAll);

        // 배너 북마크 버튼 (onclick 미사용 버전)
        document.querySelectorAll('.banner-bm').forEach(btn => {
            btn.addEventListener('click', e => {
                e.preventDefault();
                e.stopPropagation();
                toggleBookmark(btn.dataset.id);
            });
        });

        // 랭킹 북마크 버튼
        document.querySelectorAll('.rank-bm').forEach(btn => {
            btn.addEventListener('click', () => toggleBookmark(btn.dataset.id));
        });
    }

    /* ── Public API ─────────────────────────────────── */
    window.PopupFilter = { toggleBookmark, isBookmarked, getBookmarks: _load, setCategory, setBookmarkMode, clearAll };

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', _init);
    } else {
        _init();
    }
})();
