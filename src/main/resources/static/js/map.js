window.PopupMap = (() => {
    'use strict';
    const container = document.getElementById('map');
    if (!container || typeof naver === 'undefined') return null;

    const map = new naver.maps.Map(container, {
        center: new naver.maps.LatLng(36.5, 127.5),
        zoom: 7,
        zoomControl: true,
        zoomControlOptions: { position: naver.maps.Position.RIGHT_CENTER }
    });

    const today = new Date(); today.setHours(0, 0, 0, 0);

    function _parse(str) {
        if (!str) return null;
        const [y, m, d] = str.split('-').map(Number);
        return new Date(y, m - 1, d);
    }

    function _markerIcon(upcoming) {
        const color = upcoming ? '%23F59E0B' : '%23FF4B4B';
        const svg = `data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='32' height='44' viewBox='0 0 32 44'><path d='M16 0C7.163 0 0 7.163 0 16c0 12 16 28 16 28s16-16 16-28C32 7.163 24.837 0 16 0z' fill='${color}'/><circle cx='16' cy='16' r='6.5' fill='white'/></svg>`;
        return {
            url: svg,
            size: new naver.maps.Size(32, 44),
            anchor: new naver.maps.Point(16, 44)
        };
    }

    let _markers = [];
    let _activeIW = null;
    let _clusterer = null;

    function _addMarkers(list) {
        _markers.forEach(m => m.setMap(null));
        _markers = [];
        if (_clusterer) { _clusterer.setMap(null); _clusterer = null; }
        if (_activeIW) { _activeIW.close(); _activeIW = null; }

        const bounds = new naver.maps.LatLngBounds();
        const valid = list.filter(s => s.latitude && s.longitude);

        valid.forEach(store => {
            const upcoming = _parse(store.startDate) > today;
            const pos = new naver.maps.LatLng(store.latitude, store.longitude);
            const marker = new naver.maps.Marker({
                position: pos,
                map: map,
                icon: _markerIcon(upcoming)
            });

            const sd  = (store.startDate || '').replace(/-/g, '.');
            const ed  = (store.endDate   || '').replace(/-/g, '.');
            const sBg = upcoming ? '#FEF3C7' : '#DCFCE7';
            const sC  = upcoming ? '#D97706' : '#16A34A';
            const sTx = upcoming ? '오픈예정' : '진행중';
            const img = store.imageUrl
                ? `<img src="${store.imageUrl}" style="width:100%;height:90px;object-fit:cover;border-radius:6px;margin-bottom:8px;display:block;" onerror="this.style.display='none'"/>`
                : '';

            const iw = new naver.maps.InfoWindow({
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
                borderWidth: 0,
                disableAnchor: true,
                backgroundColor: 'white',
                pixelOffset: new naver.maps.Point(0, -10)
            });

            naver.maps.Event.addListener(marker, 'click', () => {
                if (_activeIW) _activeIW.close();
                iw.open(map, marker);
                _activeIW = iw;
            });

            _markers.push(marker);
            bounds.extend(pos);
        });

        if (typeof MarkerClustering !== 'undefined') {
            _clusterer = new MarkerClustering({
                minClusterSize: 2,
                maxZoom: 13,
                map: map,
                markers: _markers,
                disableClickZoom: false,
                gridSize: 120,
                icons: [{
                    content: '<div style="cursor:pointer;width:38px;height:38px;line-height:38px;font-size:13px;color:white;text-align:center;font-weight:700;background:#FF4B4B;border-radius:50%;border:3px solid white;box-shadow:0 2px 8px rgba(255,75,75,.45);">COUNT</div>',
                    size: new naver.maps.Size(38, 38),
                    anchor: new naver.maps.Point(19, 19)
                }],
                stylingFunction: (clusterMarker, count) => {
                    clusterMarker.getElement().querySelector('div').textContent = count;
                }
            });
        }

        if (valid.length === 1) {
            map.setCenter(new naver.maps.LatLng(valid[0].latitude, valid[0].longitude));
            map.setZoom(14);
        } else if (valid.length > 1) {
            map.fitBounds(bounds);
        }
    }

    naver.maps.Event.addListener(map, 'click', () => {
        if (_activeIW) { _activeIW.close(); _activeIW = null; }
    });

    function setStores(list) { _addMarkers(list); }
    _addMarkers(window.storesData || []);

    const locateBtn = document.getElementById('mapLocate');
    let _locMarker = null;

    locateBtn?.addEventListener('click', () => {
        if (!navigator.geolocation) { alert('이 브라우저는 위치 정보를 지원하지 않습니다.'); return; }
        locateBtn.classList.add('locating');
        navigator.geolocation.getCurrentPosition(
            pos => {
                locateBtn.classList.remove('locating');
                const latlng = new naver.maps.LatLng(pos.coords.latitude, pos.coords.longitude);
                if (_locMarker) _locMarker.setMap(null);
                _locMarker = new naver.maps.Marker({
                    position: latlng,
                    map: map,
                    icon: {
                        content: '<div style="width:14px;height:14px;border-radius:50%;background:#3B82F6;border:2px solid white;box-shadow:0 0 0 4px rgba(59,130,246,.3);transform:translate(-50%,-50%);"></div>',
                        size: new naver.maps.Size(14, 14),
                        anchor: new naver.maps.Point(7, 7)
                    },
                    zIndex: 10
                });
                map.setCenter(latlng);
                map.setZoom(15);
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
