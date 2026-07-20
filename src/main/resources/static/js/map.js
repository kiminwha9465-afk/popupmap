window.PopupMap = (() => {
    'use strict';
    const container = document.getElementById('map');
    if (!container || typeof kakao === 'undefined') return null;

    const map = new kakao.maps.Map(container, {
        center: new kakao.maps.LatLng(36.5, 127.5),
        level: 12
    });
    map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);

    const today = new Date(); today.setHours(0, 0, 0, 0);

    const clusterer = new kakao.maps.MarkerClusterer({
        map,
        averageCenter: true,
        minLevel: 5,
        styles: [{
            width: '38px', height: '38px',
            background: '#FF4B4B',
            border: '3px solid white',
            borderRadius: '50%',
            color: '#fff',
            textAlign: 'center',
            lineHeight: '32px',
            fontWeight: '700',
            fontSize: '13px',
            boxShadow: '0 2px 8px rgba(255,75,75,.45)'
        }]
    });

    function _parse(str) {
        if (!str) return null;
        const [y, m, d] = str.split('-').map(Number);
        return new Date(y, m - 1, d);
    }

    function _markerImage(upcoming) {
        const color = upcoming ? '%23F59E0B' : '%23FF4B4B';
        const svg = `data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='32' height='44' viewBox='0 0 32 44'><path d='M16 0C7.163 0 0 7.163 0 16c0 12 16 28 16 28s16-16 16-28C32 7.163 24.837 0 16 0z' fill='${color}'/><circle cx='16' cy='16' r='6.5' fill='white'/></svg>`;
        return new kakao.maps.MarkerImage(svg, new kakao.maps.Size(32, 44), {
            offset: new kakao.maps.Point(16, 44)
        });
    }

    let _markers = [];
    let _activeIW = null;

    function _addMarkers(list) {
        clusterer.clear();
        _markers = [];
        if (_activeIW) { _activeIW.close(); _activeIW = null; }

        const bounds = new kakao.maps.LatLngBounds();
        const valid = list.filter(s => s.latitude && s.longitude);

        valid.forEach(store => {
            const upcoming = _parse(store.startDate) > today;
            const pos = new kakao.maps.LatLng(store.latitude, store.longitude);
            const marker = new kakao.maps.Marker({ position: pos, image: _markerImage(upcoming) });

            const sd  = (store.startDate || '').replace(/-/g, '.');
            const ed  = (store.endDate   || '').replace(/-/g, '.');
            const sBg = upcoming ? '#FEF3C7' : '#DCFCE7';
            const sC  = upcoming ? '#D97706' : '#16A34A';
            const sTx = upcoming ? '오픈예정' : '진행중';
            const img = store.imageUrl
                ? `<img src="${store.imageUrl}" style="width:100%;height:90px;object-fit:cover;border-radius:6px;margin-bottom:8px;display:block;" onerror="this.style.display='none'"/>`
                : '';

            const iw = new kakao.maps.InfoWindow({
                content: `<div style="padding:12px;min-width:220px;max-width:260px;font-family:-apple-system,sans-serif;line-height:1.5;">
                    ${img}
                    <div style="display:flex;align-items:center;gap:6px;margin-bottom:6px;flex-wrap:wrap;">
                        <span style="padding:2px 8px;border-radius:10px;font-size:11px;font-weight:700;background:${sBg};color:${sC};">${sTx}</span>
                        ${store.category ? `<span style="padding:2px 8px;border-radius:10px;font-size:11px;font-weight:600;background:#F3F4F6;color:#374151;">${store.category}</span>` : ''}
                    </div>
                    <div style="font-size:14px;font-weight:700;color:#1A1A1A;margin-bottom:4px;">${store.name}</div>
                    <div style="font-size:12px;color:#6E6E73;margin-bottom:2px;">${store.address || ''}</div>
                    <div style="font-size:12px;color:#6E6E73;margin-bottom:10px;">${sd} ~ ${ed}</div>
                    <a href="/store/${store.id}" style="font-size:12px;color:#FF4B4B;font-weight:700;text-decoration:none;">자세히 보기 →</a>
                </div>`,
                removable: true
            });

            kakao.maps.event.addListener(marker, 'click', () => {
                if (_activeIW) _activeIW.close();
                iw.open(map, marker);
                _activeIW = iw;
            });

            _markers.push(marker);
            bounds.extend(pos);
        });

        clusterer.addMarkers(_markers);

        if (valid.length === 1) {
            map.setCenter(new kakao.maps.LatLng(valid[0].latitude, valid[0].longitude));
            map.setLevel(5);
        } else if (valid.length > 1) {
            map.setBounds(bounds);
        }
    }

    function setStores(list) { _addMarkers(list); }
    _addMarkers(window.storesData || []);

    /* 현위치 버튼 */
    const locateBtn = document.getElementById('mapLocate');
    let _locOverlay = null;

    locateBtn?.addEventListener('click', () => {
        if (!navigator.geolocation) { alert('이 브라우저는 위치 정보를 지원하지 않습니다.'); return; }
        locateBtn.classList.add('locating');
        navigator.geolocation.getCurrentPosition(
            pos => {
                locateBtn.classList.remove('locating');
                const latlng = new kakao.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
                if (_locOverlay) _locOverlay.setMap(null);
                _locOverlay = new kakao.maps.CustomOverlay({
                    position: latlng,
                    content: '<div style="width:14px;height:14px;border-radius:50%;background:#3B82F6;border:2px solid white;box-shadow:0 0 0 4px rgba(59,130,246,.3);transform:translate(-50%,-50%);"></div>',
                    xAnchor: 0.5,
                    yAnchor: 0.5,
                    zIndex: 10
                });
                _locOverlay.setMap(map);
                map.setCenter(latlng);
                map.setLevel(4);
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
