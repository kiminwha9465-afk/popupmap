(() => {
    const container = document.getElementById('detail-map');
    if (!container || typeof kakao === 'undefined') return;

    const pos = new kakao.maps.LatLng(storeDetail.lat, storeDetail.lng);
    const map = new kakao.maps.Map(container, { center: pos, level: 4 });
    map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);

    const svg = `data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='32' height='44' viewBox='0 0 32 44'><path d='M16 0C7.163 0 0 7.163 0 16c0 12 16 28 16 28s16-16 16-28C32 7.163 24.837 0 16 0z' fill='%23FF4B4B'/><circle cx='16' cy='16' r='6.5' fill='white'/></svg>`;
    const marker = new kakao.maps.Marker({
        position: pos,
        image: new kakao.maps.MarkerImage(svg, new kakao.maps.Size(32, 44), {
            offset: new kakao.maps.Point(16, 44)
        })
    });
    marker.setMap(map);

    const iw = new kakao.maps.InfoWindow({
        content: `<div style="padding:8px 12px;font-family:-apple-system,sans-serif;">
            <div style="font-size:13px;font-weight:700;color:#1A1A1A;margin-bottom:3px;">${storeDetail.name}</div>
            <div style="font-size:12px;color:#6E6E73;">${storeDetail.address}</div>
        </div>`,
        removable: false
    });
    iw.open(map, marker);
})();
