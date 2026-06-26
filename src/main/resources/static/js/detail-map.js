(() => {
    const container = document.getElementById('detail-map');
    if (!container || typeof L === 'undefined') return;

    const map = L.map(container, { zoomControl: false }).setView([storeDetail.lat, storeDetail.lng], 15);
    L.control.zoom({ position: 'topright' }).addTo(map);

    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '© OpenStreetMap contributors © CARTO',
        maxZoom: 19
    }).addTo(map);

    const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='32' height='44' viewBox='0 0 32 44'>
        <path d='M16 0C7.163 0 0 7.163 0 16c0 12 16 28 16 28s16-16 16-28C32 7.163 24.837 0 16 0z' fill='%23FF4B4B'/>
        <circle cx='16' cy='16' r='6.5' fill='white'/>
    </svg>`;

    const icon = L.divIcon({
        html: svg,
        className: '',
        iconSize: [32, 44],
        iconAnchor: [16, 44],
        popupAnchor: [0, -44]
    });

    L.marker([storeDetail.lat, storeDetail.lng], { icon })
        .addTo(map)
        .bindPopup(`<div style="padding:4px 2px;font-family:-apple-system,sans-serif;">
            <div style="font-size:13px;font-weight:700;color:#1A1A1A;margin-bottom:3px;">${storeDetail.name}</div>
            <div style="font-size:12px;color:#6E6E73;">${storeDetail.address}</div>
        </div>`)
        .openPopup();
})();
