function MarkerClustering(options) {
    this.map = options.map;
    this.markers = options.markers || [];
    this.minClusterSize = options.minClusterSize || 2;
    this.maxZoom = options.maxZoom || 13;
    this.gridSize = options.gridSize || 100;
    this.icons = options.icons || [];
    this.stylingFunction = options.stylingFunction || function() {};
    this.onClusterClick = options.onClusterClick || null;
    this._clusterMarkers = [];
    this._listener = null;

    if (this.map) {
        this._listener = naver.maps.Event.addListener(this.map, 'idle', this._redraw.bind(this));
        this._redraw();
    }
}

MarkerClustering.prototype.setMap = function(map) {
    if (this._listener) { naver.maps.Event.removeListener(this._listener); this._listener = null; }
    this._clear();
    this.map = map;
    if (map) {
        this._listener = naver.maps.Event.addListener(map, 'idle', this._redraw.bind(this));
        this._redraw();
    }
};

MarkerClustering.prototype._clear = function() {
    this._clusterMarkers.forEach(m => m.setMap(null));
    this._clusterMarkers = [];
};

MarkerClustering.prototype._redraw = function() {
    if (!this.map) return;
    this._clear();

    const zoom = this.map.getZoom();
    if (zoom > this.maxZoom) {
        this.markers.forEach(m => m.setMap(this.map));
        return;
    }

    this.markers.forEach(m => m.setMap(null));

    const proj = this.map.getProjection();
    const items = this.markers.map(m => ({
        marker: m,
        pt: proj.fromCoordToOffset(m.getPosition()),
        used: false
    }));

    items.forEach((item, i) => {
        if (item.used) return;
        const group = [item.marker];
        item.used = true;

        items.forEach((other, j) => {
            if (i === j || other.used) return;
            const dx = item.pt.x - other.pt.x;
            const dy = item.pt.y - other.pt.y;
            if (Math.sqrt(dx * dx + dy * dy) <= this.gridSize) {
                group.push(other.marker);
                other.used = true;
            }
        });

        if (group.length < this.minClusterSize) {
            group.forEach(m => m.setMap(this.map));
            return;
        }

        let lat = 0, lng = 0;
        group.forEach(m => { lat += m.getPosition().lat(); lng += m.getPosition().lng(); });
        lat /= group.length; lng /= group.length;

        const icon = this.icons[0];
        const cm = new naver.maps.Marker({
            position: new naver.maps.LatLng(lat, lng),
            map: this.map,
            icon: { content: icon.content, size: icon.size, anchor: icon.anchor },
            zIndex: 200
        });

        this.stylingFunction(cm, group.length);

        naver.maps.Event.addListener(cm, 'click', () => {
            if (typeof this.onClusterClick === 'function') {
                this.onClusterClick(group);
            } else {
                this.map.setZoom(Math.min(this.map.getZoom() + 3, this.maxZoom + 1));
                this.map.setCenter(new naver.maps.LatLng(lat, lng));
            }
        });

        this._clusterMarkers.push(cm);
    });
};
