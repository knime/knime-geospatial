window.leafletNamespace = (function () {
    var geojsonMarkerOptions = {
        fillColor: "#ff7800",
        color: "#0078A8",
    };

    var Leaflet = function () {
        this._representation = null;
        this._value = null;
        this._map = null;
        this._canvasRenderer = L.canvas({ padding: 0.5 });
        this._latLongPoints = [];
        this._geoObjects = [];
    };

    Leaflet.prototype.getPolygoneBounds = function() {
        var polygons = []
        this._map.eachLayer(function (layer) {
            if (layer instanceof L.Polygon && !(layer instanceof L.Rectangle)) {
                // polygons.push(layer.getLatLngs()) //Returns an array of arrays of geographical points in each polygon.
                polygons.push(layer.getBounds()) //Returns a GeoJSON representation of the polygon (GeoJSON Polygon Feature).
            }
        })
        return polygons
    };

    Leaflet.prototype.showHideTooltip = function (hide) {
        this._map.eachLayer(function (layer) {

            if (!hide) {
                layer.unbindTooltip();
            } else {
                if (layer.feature){
                    let tooltip = '<h2><b>' + layer.feature.id + '</b></h2>';
                    Object.keys(layer.feature.properties).forEach(property => {
                        tooltip += '<b>' + property + ': </b>' + layer.feature.properties[property] + '<br>';    
                    });
                    layer.bindTooltip(tooltip);
                }
            }
        })
        return polygons
    }

    Leaflet.prototype.init = function (representation, value) {
        this._representation = representation;
        this._value = value;
        knimeService.subscribeToSelection(representation.table.id, this.handleSelection);

        let refreshButton = document.createElement('button');
        refreshButton.innerHTML = 'Reset Viewport';
        refreshButton.onclick = () => {
            this._map.flyTo([this._representation.centerLat, this._representation.centerLong], this._representation.zoomLevel);
        }
        document.body.appendChild(refreshButton);

        let fitBounds = document.createElement('button');
        fitBounds.innerHTML = 'Fit Bounds';
        fitBounds.onclick = () => {
           this.fitToBounds();
        };
        document.body.appendChild(fitBounds);

        this._toggleTooltip = document.createElement('input');
        this._toggleTooltip.type = 'checkbox'
        this._toggleTooltip.checked = false;
        this._toggleTooltip.id = 'toggleTooltip';
        this._toggleTooltip.onchange = (e) => {
            this.showHideTooltip(e.target.checked);
        }
        document.body.appendChild(this._toggleTooltip);

        var label = document.createElement('label')
        label.htmlFor = 'toggleTooltip';
        document.body.appendChild(document.createTextNode('Toggle Tooltip'));

        let mapContainer = document.createElement('div');
        mapContainer.id = 'mapContainer';
        mapContainer.style.height = 'calc(100% - 25px)';
        document.body.style.height = '100%';
        document.body.parentElement.style.height = '100%';
        document.body.appendChild(mapContainer);
        this._map = L.map('mapContainer').setView([this._representation.centerLat, this._representation.centerLong], this._representation.zoomLevel);

        L.tileLayer(this._representation.mapProvider, {
            maxZoom: 18,
            attribution: this._representation.mapAttribution
        }).addTo(this._map);

        this._representation.table.rows.forEach(row => {
            let geoJson = JSON.parse(row.data[0]);
            geoJson.rowId = row.rowKey;
            let self = this;
            this._geoObjects.push(L.geoJSON(geoJson, {
                renderer: this._canvasRenderer,
                style: function(feature) {
                    if (feature.properties.style  && !feature.properties.style.color) {
                        feature.properties.style.color = '#0078A8';
                    }
                    return feature.properties.style ? feature.properties.style : geojsonMarkerOptions;
                },
                pointToLayer: function (feature, latlng) {
                    self._latLongPoints.push(latlng);
                    let tempGeoStyle = geojsonMarkerOptions;
                    if (feature.properties.style) {
                        tempGeoStyle = feature.properties.style;
                    }
                    return L.circleMarker(latlng, tempGeoStyle);
                },
            }).addTo(this._map).on('click', function(e) {
                if (!e.layer.feature.selected) {
                    knimeService.addRowsToSelection(leafletNamespace._representation.table.id, [e.layer.feature.rowId]);
                    e.layer.feature.selected = true;
                    e.layer.setStyle({
                        color: 'yellow'
                    });
                } else {
                    knimeService.removeRowsFromSelection(leafletNamespace._representation.table.id, [e.layer.feature.rowId]);
                    e.layer.feature.selected = false;
                    e.layer.setStyle(e.layer.feature.properties.style ? e.layer.feature.properties.style : geojsonMarkerOptions);
                }
            }));
        });

        this.fitToBounds();
    };

    Leaflet.prototype.getComponentValue = function () {
        this._value.center = this._map.getCenter();
        this._value.centerLatValue = this._value.center.lat;
        this._value.centerLongValue = this._value.center.lng;
        this._value.zoomLevel = this._map.getZoom();
        return this._value;
    };

    Leaflet.prototype.fitToBounds = function() {
        let minNorthEast;
        let maxSouthWest;
        this._geoObjects.forEach((geoObject) => {
            let bounds = geoObject.getBounds();
            if (!minNorthEast && !maxSouthWest) {
                minNorthEast = {_northEast: bounds._northEast};
                maxSouthWest = {_southWest: bounds._southWest};
            }
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

    Leaflet.prototype.validate = function () {
        return true;
    };

    Leaflet.prototype.handleSelection = function(selectionEvent) {
        leafletNamespace._map.eachLayer(function (layer) {
            if (selectionEvent.changeSet.added) {
                selectionEvent.changeSet.added.forEach(addedRow => {
                    if (layer.feature?.rowId === addedRow) {
                        layer.setStyle({
                            color: 'yellow'
                        });
                    }
                })
            }
            if (selectionEvent.changeSet.removed) {
                selectionEvent.changeSet.removed.forEach(removedRow => {
                    if (layer.feature?.rowId === removedRow) {
                        layer.setStyle(layer.feature.properties.style ? layer.feature.properties.style : geojsonMarkerOptions);
                    }
                })
            }
        });
    };

    return new Leaflet();
})();
