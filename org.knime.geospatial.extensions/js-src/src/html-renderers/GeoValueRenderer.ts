import "./styles.css";

import { wktToGeoJSON } from "@terraformer/wkt";
import L from "leaflet";
import iconRetinaUrl from "leaflet/dist/images/marker-icon-2x.png?url";
import iconUrl from "leaflet/dist/images/marker-icon.png?url";
import shadowUrl from "leaflet/dist/images/marker-shadow.png?url";
import "leaflet/dist/leaflet.css";

// Icon URLs must be overwritten, otherwise no icons will be displayed. See:
// https://github.com/Leaflet/Leaflet/issues/4968#issuecomment-269750768
L.Icon.Default.mergeOptions({
  iconUrl,
  iconRetinaUrl,
  shadowUrl,
});

import { AlertingService, JsonDataService } from "@knime/ui-extension-service";

const INITIAL_MAP_ZOOM = 13;
const INITIAL_MAP_CENTER = [0, 0] as L.LatLngExpression;

export class GeoValueRenderer {
  private jsonDataService!: JsonDataService;
  private readonly map: L.Map | null = null;

  constructor(map: HTMLElement) {
    if (!navigator.onLine) {
      const message =
        "Sorry, we couldn't load the data.\n Check your connection or try again later.";
      AlertingService.getInstance().then((service) => {
        service.sendAlert({
          message,
          type: "error",
          isBlocking: true,
        });
      });
      return;
    }

    this.map = L.map(map);
    L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution:
        '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);
    this.map.setView(INITIAL_MAP_CENTER, INITIAL_MAP_ZOOM);
  }

  async init() {
    if (!this.map) {
      return;
    }
    this.jsonDataService = await JsonDataService.getInstance();
    const initialData = await this.jsonDataService.initialData();

    const geoJSON = wktToGeoJSON(initialData);
    const leafletGeometry = L.geoJSON(geoJSON);
    leafletGeometry.addTo(this.map);
    this.map.fitBounds(leafletGeometry.getBounds());
  }
}
