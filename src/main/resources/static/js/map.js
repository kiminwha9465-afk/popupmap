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
    let _markers = [];
    let _openPopup = null;

    function _parse(str) {
        if (!str) return null;
        const [y, m, d] = str.split('-').map(Number);
        return new Date(y, m - 1, d);
    }

    function _icon(upcoming) {
        const color = upcoming ? '#F59E0B' : '#FF4B4B';
        const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='28' height='38' viewBox='0 0 28 38'>
            <path d='M14 0C6.268 0 0 6.268 0 14c0 10.5 14 24 14 24s14-13.5 14-24C28 6.268 21.732 0 14 0z' fill='${color}'/>
            <circle cx='14' cy='14' r='5.5' fill='white'/>
        </svg>`;
        return L.divIcon({
            html: svg,
            className: '',
            iconSize: [28, 38],
            iconAnchor: [14, 38],
            popupAnchor: [0, -38]
        });
    }

    function _clearMarkers() {
        _markers.forEach(m => map.removeLayer(m));
        _markers = [];
    }

    function _addMarkers(list) {
        const bounds = [];

        list.forEach(store => {
            if (!store.latitude || !store.longitude) return;
            const upcoming = _parse(store.startDate) > today;
            const marker = L.marker([store.latitude, store.longitude], { icon: _icon(upcoming) });

            const sd = (store.startDate || '').replace(/-/g, '.');
            const ed = (store.endDate   || '').replace(/-/g, '.');
            const sBg = upcoming ? '#FEF3C7' : '#DCFCE7';
            const sC  = upcoming ? '#D97706' : '#16A34A';
            const sTx = upcoming ? '오픈예정' : '진행중';

            marker.bindPopup(`<div style="padding:4px 2px;min-width:190px;font-family:-apple-system,sans-serif;line-height:1.5;">
                <span style="display:inline-block;margin-bottom:6px;padding:2px 8px;border-radius:10px;
                             font-size:11px;font-weight:700;background:${sBg};color:${sC};">${sTx}</span>
                <div style="font-size:14px;font-weight:700;color:#1A1A1A;margin-bottom:4px;">${store.name}</div>
                <div style="font-size:12px;color:#6E6E73;margin-bottom:2px;">${store.address || ''}</div>
                <div style="font-size:12px;color:#6E6E73;margin-bottom:10px;">${sd} ~ ${ed}</div>
                <a href="/store/${store.id}" style="font-size:12px;color:#FF4B4B;font-weight:700;text-decoration:none;">자세히 보기 →</a>
            </div>`, { maxWidth: 240 });

            marker.addTo(map);
            _markers.push(marker);
            bounds.push([store.latitude, store.longitude]);
        });

        if (bounds.length > 0) {
            try {
                if (bounds.length === 1) {
                    map.setView(bounds[0], 14);
                } else {
                    map.fitBounds(bounds, { padding: [40, 40] });
                }
            } catch (e) {}
        }
    }

    function setStores(list) {
        _clearMarkers();
        _addMarkers(list);
    }

    _addMarkers(window.storesData || []);

    return { setStores };
})();
