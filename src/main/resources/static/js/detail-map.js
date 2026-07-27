(() => {
    const container = document.getElementById('detail-map');
    if (!container || typeof naver === 'undefined') return;

    const pos = new naver.maps.LatLng(storeDetail.lat, storeDetail.lng);
    const map = new naver.maps.Map(container, {
        center: pos,
        zoom: 16,
        zoomControl: true,
        zoomControlOptions: { position: naver.maps.Position.RIGHT_CENTER }
    });

    const svg = `data:image/svg+xml,<svg xmlns='http://www.w3.org/2000/svg' width='32' height='44' viewBox='0 0 32 44'><path d='M16 0C7.163 0 0 7.163 0 16c0 12 16 28 16 28s16-16 16-28C32 7.163 24.837 0 16 0z' fill='%23FF4B4B'/><circle cx='16' cy='16' r='6.5' fill='white'/></svg>`;

    const marker = new naver.maps.Marker({
        position: pos,
        map: map,
        icon: {
            url: svg,
            size: new naver.maps.Size(32, 44),
            anchor: new naver.maps.Point(16, 44)
        }
    });

    const iw = new naver.maps.InfoWindow({
        content: `<div style="padding:8px 12px;font-family:-apple-system,sans-serif;">
            <div style="font-size:13px;font-weight:700;color:#1A1A1A;margin-bottom:3px;">${storeDetail.name}</div>
            <div style="font-size:12px;color:#6E6E73;">${storeDetail.address}</div>
        </div>`,
        borderWidth: 0,
        disableAnchor: true,
        backgroundColor: 'white'
    });
    iw.open(map, marker);
})();
