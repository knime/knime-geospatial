window.leafletNamespace = (function () {
    let geojsonFeature = {
        "type": "Feature",
        "properties": {
            "name": "KNIME Office",
            "amenity": "KNIME KNOFFICE",
            "popupContent": "Best company ever"
        },
        "geometry": {
            "type": "Point",
            "coordinates": [9.169613, 47.671207]
        }
    };
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
    };

    Leaflet.prototype.init = function (representation, value) {
        debugger;
        this._representation = representation;
        this._value = value;
        // ------------------------ Code Editor --------------------------------
        let codeContainer = document.createElement('div');
        codeContainer.id = 'codeContainer';
        document.body.appendChild(codeContainer);
        codeContainer.style.height = '500px';
        debugger;
        
        const editor = monaco.editor.create(document.getElementById("codeContainer"), {
            theme: 'vs-dark',
            wordWrap: 'on',
            value: ['function x() {', '\tconsole.log("Hello world!");', '}'].join('\n'),
				language: 'javascript'
        });
        // --------------------------------------------------------

        let refreshButton = document.createElement('button');
        refreshButton.innerHTML = 'Reset Viewport';
        refreshButton.onclick = () => {
            this._map.flyTo([this._representation.centerLat, this._representation.centerLong], this._representation.zoomLevel);
        }
        document.body.appendChild(refreshButton);

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
            let geoJson = JSON.parse(row.data[0].split('|')[1]);
            L.geoJSON(geoJson, {
                renderer: this._canvasRenderer,
                pointToLayer: function (feature, latlng) {
                    return L.circleMarker(latlng, geojsonMarkerOptions);
                },
                // Popup
                onEachFeature: function (feature, layer) {
                    debugger;
                    layer.bindPopup("<b>" + feature.properties?.name + '</b><br />'
                    + feature.properties?.popupContent);
                }
            }).addTo(this._map);
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
