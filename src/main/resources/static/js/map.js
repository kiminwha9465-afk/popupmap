window.PopupMap = (() => {
    'use strict';
    const container = document.getElementById('map');
    if (!container || typeof L === 'undefined') return null;

    const map = L.map(container, { zoomControl: false }).setView([36.5, 127.5], 7);
    L.control.zoom({ position: 'topright' }).addTo(map);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '© OpenStreetMap contributors © CARTO',
        maxZoom: 19
    }).addTo(map);

    const today = new Date(); today.setHours(0, 0, 0, 0);

    /* 클러스터 그룹 — 겹치는 마커를 숫자 배지로 묶음 */
    const _cluster = L.markerClusterGroup({
        maxClusterRadius: 50,
        spiderfyOnMaxZoom: true,
        showCoverageOnHover: false,
        iconCreateFunction(c) {
            const n = c.getChildCount();
            return L.divIcon({
                html: `<div class="mc-icon">${n}</div>`,
                className: 'mc-wrap',
                iconSize: [36, 36]
            });
        }
    });
    map.addLayer(_cluster);

    function _parse(str) {
        if (!str) return null;
        const [y, m, d] = str.split('-').map(Number);
        return new Date(y, m - 1, d);
    }

    function _pinIcon(upcoming) {
        const bg = upcoming ? '#F59E0B' : '#FF4B4B';
        const shadow = upcoming ? 'rgba(245,158,11,.45)' : 'rgba(255,75,75,.45)';
        return L.divIcon({
            html: `<div class="mc-icon" style="background:${bg};box-shadow:0 2px 8px ${shadow};">1</div>`,
            className: 'mc-wrap',
            iconSize: [36, 36],
            iconAnchor: [18, 18],
            popupAnchor: [0, -20]
        });
    }

    function _addMarkers(list) {
        _cluster.clearLayers();
        const bounds = [];

        list.forEach(store => {
            if (!store.latitude || !store.longitude) return;
            const upcoming = _parse(store.startDate) > today;
            const marker   = L.marker([store.latitude, store.longitude], { icon: _pinIcon(upcoming) });

            const sd  = (store.startDate || '').replace(/-/g, '.');
            const ed  = (store.endDate   || '').replace(/-/g, '.');
            const sBg = upcoming ? '#FEF3C7' : '#DCFCE7';
            const sC  = upcoming ? '#D97706' : '#16A34A';
            const sTx = upcoming ? '오픈예정' : '진행중';
            const img = store.imageUrl
                ? `<img src="${store.imageUrl}" alt="${store.name}"
                        style="width:100%;height:90px;object-fit:cover;border-radius:6px;margin-bottom:8px;display:block;"
                        onerror="this.style.display='none'"/>`
                : '';

            marker.bindPopup(`
                <div style="padding:4px 2px;min-width:200px;font-family:-apple-system,sans-serif;line-height:1.5;">
                    ${img}
                    <div style="display:flex;align-items:center;gap:6px;margin-bottom:6px;flex-wrap:wrap;">
                        <span style="padding:2px 8px;border-radius:10px;font-size:11px;font-weight:700;background:${sBg};color:${sC};">${sTx}</span>
                        ${store.category ? `<span style="padding:2px 8px;border-radius:10px;font-size:11px;font-weight:600;background:#F3F4F6;color:#374151;">${store.category}</span>` : ''}
                    </div>
                    <div style="font-size:14px;font-weight:700;color:#1A1A1A;margin-bottom:4px;">${store.name}</div>
                    <div style="font-size:12px;color:#6E6E73;margin-bottom:2px;">${store.address || ''}</div>
                    <div style="font-size:12px;color:#6E6E73;margin-bottom:10px;">${sd} ~ ${ed}</div>
                    <a href="/store/${store.id}" style="font-size:12px;color:#FF4B4B;font-weight:700;text-decoration:none;">자세히 보기 →</a>
                </div>`, { maxWidth: 260 });

            _cluster.addLayer(marker);
            bounds.push([store.latitude, store.longitude]);
        });

        if (bounds.length > 0) {
            try {
                bounds.length === 1
                    ? map.setView(bounds[0], 14)
                    : map.fitBounds(bounds, { padding: [40, 40] });
            } catch (e) {}
        }
    }

    function setStores(list) { _addMarkers(list); }

    _addMarkers(window.storesData || []);

    /* ── 현위치 버튼 ── */
    const locateBtn = document.getElementById('mapLocate');
    let _locationMarker = null;

    locateBtn?.addEventListener('click', () => {
        if (!navigator.geolocation) {
            alert('이 브라우저는 위치 정보를 지원하지 않습니다.');
            return;
        }
        locateBtn.classList.add('locating');
        navigator.geolocation.getCurrentPosition(
            pos => {
                locateBtn.classList.remove('locating');
                const { latitude: lat, longitude: lng } = pos.coords;
                if (_locationMarker) map.removeLayer(_locationMarker);
                _locationMarker = L.circleMarker([lat, lng], {
                    radius: 8, fillColor: '#3B82F6', fillOpacity: 1,
                    color: 'white', weight: 2
                }).addTo(map).bindPopup('현재 위치').openPopup();
                map.setView([lat, lng], 14);
            },
            () => {
                locateBtn.classList.remove('locating');
                alert('위치 정보를 가져올 수 없습니다.\n브라우저 위치 권한을 확인해주세요.');
            },
            { timeout: 8000 }
        );
    });

    return { setStores };
})();
