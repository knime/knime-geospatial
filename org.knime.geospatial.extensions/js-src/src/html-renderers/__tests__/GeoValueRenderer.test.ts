import { beforeEach, describe, expect, it, vi } from "vitest";
import L from "leaflet";

import { AlertingService, JsonDataService } from "@knime/ui-extension-service";

import { GeoValueRenderer } from "../GeoValueRenderer";

vi.mock("leaflet", () => {
  const getBoundsMock = vi.fn();
  const addToMock = { addTo: vi.fn() };
  const mapMock = { fitBounds: vi.fn(), setView: vi.fn() };
  return {
    default: {
      geoJSON: vi.fn(() => ({ ...addToMock, getBounds: getBoundsMock })),
      map: vi.fn(() => mapMock),
      tileLayer: vi.fn(() => ({ addTo: vi.fn() })),
      Icon: {
        Default: {
          mergeOptions: vi.fn(),
        },
      },
      Map: {
        prototype: mapMock,
      },
      Layer: {
        prototype: addToMock,
        TileLayer: {
          prototype: {
            addTo: vi.fn(),
          },
        },
      },
    },
  };
});

describe("GeoValueRenderer", () => {
  const createGeoValueRendererAndSpies = (initialData: string) => {
    const addToSpy = vi.spyOn(L.Layer.prototype, "addTo");
    const setViewSpy = vi.spyOn(L.Map.prototype, "setView");
    const fitBoundsSpy = vi.spyOn(L.Map.prototype, "fitBounds");
    const sendAlertMock = vi.fn();
    const map = document.createElement("div");
    map.id = "map";
    JsonDataService.getInstance = vi.fn().mockResolvedValue({
      initialData: vi.fn().mockResolvedValue(initialData),
    });
    AlertingService.getInstance = vi.fn().mockResolvedValue({
      sendAlert: sendAlertMock,
    });
    const geoValueRenderer = new GeoValueRenderer(map);
    return {
      geoValueRenderer,
      addToSpy,
      setViewSpy,
      fitBoundsSpy,
      sendAlertMock,
    };
  };

  beforeEach(() => {
    vi.stubGlobal("navigator", { onLine: true });
  });

  it("adds a tileLayer with attribution to the map during GeoValueRenderer construction", () => {
    const tileLayerSpy = vi.spyOn(L, "tileLayer");
    createGeoValueRendererAndSpies("POINT (1 1)");
    expect(tileLayerSpy).toHaveBeenCalledWith(
      "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
      {
        attribution:
          '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      },
    );
  });

  it("does not add a tileLayer during construction when no connection can be established, but sends an alert", async () => {
    vi.useFakeTimers();
    vi.stubGlobal("navigator", { onLine: false });
    const tileLayerSpy = vi.spyOn(L, "tileLayer");
    const {
      geoValueRenderer,
      sendAlertMock,
      addToSpy,
      setViewSpy,
      fitBoundsSpy,
    } = createGeoValueRendererAndSpies("POINT (1 1)");
    await vi.runAllTimers();

    expect(tileLayerSpy).not.toHaveBeenCalled();
    expect(sendAlertMock).toHaveBeenCalledWith({
      message:
        "Sorry, we couldn't load the data.\n Check your connection or try again later.",
      type: "error",
      isBlocking: true,
    });
    await geoValueRenderer.init();
    expect(addToSpy).not.toHaveBeenCalled();
    expect(setViewSpy).not.toHaveBeenCalled();
    expect(fitBoundsSpy).not.toHaveBeenCalled();
  });

  it.each([
    ["POINT", "(0 0)"],
    ["LINESTRING", "(0 0, 0 1, 1 1)"],
    ["POLYGON", "((0 0, 0 1, 1 1))"],
    ["MULTIPOINT", "((0 0), (1 0), (1 1))"],
    ["MULTILINESTRING", "((0 0, 1 0), (0 1, 1 1))"],
    ["MULTIPOLYGON", "(((0 0, 1 0, 1 1)), ((2 2, 3 2, 3 3)))"],
    [
      "GEOMETRYCOLLECTION",
      "(POINT (0 0), LINESTRING (1 1, 2 2), POLYGON ((3 3, 4 4, 2 3)))",
    ],
  ])(
    "adds a %s WKT to the map, sets the view, and fits the bounds",
    async (type, coordinates) => {
      const INITIAL_MAP_ZOOM = 13;

      const { geoValueRenderer, addToSpy, setViewSpy, fitBoundsSpy } =
        createGeoValueRendererAndSpies(`${type} ${coordinates}`);

      await geoValueRenderer.init();

      expect(addToSpy).toHaveBeenCalled();
      expect(setViewSpy).toHaveBeenCalledWith([0, 0], INITIAL_MAP_ZOOM);
      expect(fitBoundsSpy).toHaveBeenCalled();
    },
  );
});
