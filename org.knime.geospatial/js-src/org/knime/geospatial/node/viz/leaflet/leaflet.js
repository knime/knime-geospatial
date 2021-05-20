window.leafletNamespace = (function () {
    var geojsonMarkerOptions = {
        radius: 8,
        fillColor: "#ff7800",
        color: "#000",
        weight: 1,
        opacity: 1,
        fillOpacity: 0.8
    };

    var Leaflet = function () {
        this._representation = null;
        this._value = null;
        this._map = null;
        this._canvasRenderer = L.canvas({ padding: 0.5 });
        this._latLongPoints = [];
        this._geoObjects = [];
    };

    Leaflet.prototype.getPolygoneBounds = function () {
        var polygons = []
        this._map.eachLayer(function (layer) {
            if (layer instanceof L.Polygon && !(layer instanceof L.Rectangle)) {
                // polygons.push(layer.getLatLngs()) //Returns an array of arrays of geographical points in each polygon.
                polygons.push(layer.getBounds()) //Returns a GeoJSON representation of the polygon (GeoJSON Polygon Feature).
            }
        })
        return polygons
    };

    Leaflet.prototype.init = function (representation, value) {
        debugger;
        this._representation = representation;
        this._value = value;
        // ------------------------ Code Editor --------------------------------
        // let codeContainer = document.createElement('div');
        // codeContainer.id = 'codeContainer';
        // document.body.appendChild(codeContainer);
        // codeContainer.style.height = '500px';
        // debugger;
        
        // const editor = monaco.editor.create(document.getElementById("codeContainer"), {
        //     theme: 'vs-dark',
        //     wordWrap: 'on',
        //     value: ['function x() {', '\tconsole.log("Hello world!");', '}'].join('\n'),
		// 		language: 'javascript'
        // });
        // --------------------------------------------------------

        let refreshButton = document.createElement('button');
        refreshButton.innerHTML = 'Reset Viewport';
        refreshButton.onclick = () => {
            this._map.flyTo([this._representation.centerLat, this._representation.centerLong], this._representation.zoomLevel);
        }
        document.body.appendChild(refreshButton);

        let fitBounds = document.createElement('button');
        fitBounds.innerHTML = 'Fit Bounds';
        fitBounds.onclick = () => {
            console.log(this.getPolygoneBounds());
            let minNorthEast;
            let maxSouthWest;
            this._geoObjects.forEach((geoObject) => {
                let bounds = geoObject.getBounds();
                if (!minNorthEast && !maxSouthWest) {
                    minNorthEast = {_northEast: bounds._northEast};
                    maxSouthWest = {_southWest: bounds._southWest};
                }
                debugger;
                if (bounds._northEast.lat > minNorthEast._northEast.lat) {
                    minNorthEast._northEast.lat = bounds._northEast.lat;
                }
                if (bounds._northEast.lng > minNorthEast._northEast.lng) {
                    minNorthEast._northEast.lng = bounds._northEast.lng;
                }
                if (bounds._southWest.lat < maxSouthWest._southWest.lat) {
                    maxSouthWest._southWest.lat = bounds._southWest.lat;
                }
                if (bounds._southWest.lng < maxSouthWest._southWest.lng) {
                    maxSouthWest._southWest.lng = bounds._southWest.lng;
                }
            });
            var southWest = L.latLng(maxSouthWest._southWest.lat, maxSouthWest._southWest.lng),
                northEast = L.latLng(minNorthEast._northEast.lat, minNorthEast._northEast.lng),
                finalBounds = L.latLngBounds(southWest, northEast);
            this._map.fitBounds(finalBounds);
        };
        document.body.appendChild(fitBounds);

        let mapContainer = document.createElement('div');
        mapContainer.id = 'mapContainer';
        mapContainer.style.height = '500px';
        document.body.appendChild(mapContainer);
        this._map = L.map('mapContainer').setView([this._representation.centerLat, this._representation.centerLong], this._representation.zoomLevel);

        L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw', {
            maxZoom: 18,
            attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' +
                'Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
            id: 'mapbox/light-v9',
            tileSize: 512,
            zoomOffset: -1
        }).addTo(this._map);

        this._representation.table.rows.forEach(row => {
            let geoJson = JSON.parse(row.data[0]);
            let self = this;
            this._geoObjects.push(L.geoJSON(geoJson, {
                renderer: this._canvasRenderer,
                pointToLayer: function (feature, latlng) {
                    self._latLongPoints.push(latlng);
                    if (feature.properties.style) {
                        geojsonMarkerOptions = feature.properties.style;
                    }
                    return L.circleMarker(latlng, geojsonMarkerOptions);
                },
                // Popup
                onEachFeature: function (feature, layer) {
                    let tooltip = '<h2><b>' + feature.id + '</b></h2>';
                    Object.keys(feature.properties).forEach(property => {
                        tooltip += '<b>' + property + ': </b>' + feature.properties[property] + '<br>';    
                    });
                    layer.bindPopup(tooltip);
                }
            }).addTo(this._map));
        });
    };

    Leaflet.prototype.getComponentValue = function () {
        this._value.center = this._map.getCenter();
        this._value.centerLatValue = this._value.center.lat;
        this._value.centerLongValue = this._value.center.lng;
        this._value.zoomLevel = this._map.getZoom();
        debugger;
        return this._value;
    };

    Leaflet.prototype.validate = function () {
        return true;
    };
    return new Leaflet();
})();
