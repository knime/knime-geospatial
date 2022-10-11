# -*- coding: utf-8 -*-
# ------------------------------------------------------------------------
#  Copyright by KNIME AG, Zurich, Switzerland
#  Website: http://www.knime.com; Email: contact@knime.com
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License, Version 3, as
#  published by the Free Software Foundation.
#
#  This program is distributed in the hope that it will be useful, but
#  WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, see <http://www.gnu.org/licenses>.
#
#  Additional permission under GNU GPL version 3 section 7:
#
#  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
#  Hence, KNIME and ECLIPSE are both independent programs and are not
#  derived from each other. Should, however, the interpretation of the
#  GNU GPL Version 3 ("License") under any applicable laws result in
#  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
#  you the additional permission to use and propagate KNIME together with
#  ECLIPSE with only the license terms in place for ECLIPSE applying to
#  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
#  license terms of ECLIPSE themselves allow for the respective use and
#  propagation of ECLIPSE together with KNIME.
#
#  Additional permission relating to nodes for KNIME that extend the Node
#  Extension (and in particular that are based on subclasses of NodeModel,
#  NodeDialog, and NodeView) and that only interoperate with KNIME through
#  standard APIs ("Nodes"):
#  Nodes are deemed to be separate and independent programs and to not be
#  covered works.  Notwithstanding anything to the contrary in the
#  License, the License does not apply to Nodes, you are not required to
#  license Nodes under the License, and you are granted a license to
#  prepare and propagate Nodes, in each case even if such Nodes are
#  propagated with or for interoperation with KNIME.  The owner of a Node
#  may freely choose the license terms applicable to such Node, including
#  when such Node is propagated with or for interoperation with KNIME.
# ------------------------------------------------------------------------

"""
PythonValueFactory implementations for geospatial KNIME types.

@author Carsten Haubold, KNIME GmbH, Konstanz, Germany
@author Jonas Klotz, KNIME GmbH, Berlin, Germany
"""
import knime_types as kt


class GeoValue:
    def __init__(self, wkb, crs):
        self.wkb = wkb
        self.crs = crs

    def to_dict(self):
        return {
            "wkb": self.wkb,
            "crs": self.crs,
        }

    def to_shapely(self):
        """Return the Shapely geometry, but ignore the crs"""
        from shapely import wkb

        return wkb.loads(self.wkb)

    def __str__(self):
        from shapely import wkb

        return f"GeoValue(wkb={wkb.loads(self.wkb)}, crs={self.crs})"


class GeoValueFactory(kt.PythonValueFactory):
    def __init__(self):
        kt.PythonValueFactory.__init__(self, GeoValue)

    def decode(self, storage):
        if storage is None:
            return None
        return GeoValue(storage["0"], storage["1"])

    def encode(self, value):
        if value is None:
            return None
        return {"0": value.wkb, "1": value.crs}


def _knime_value_factory(name):
    return '{"value_factory_class":"' + name + '"}'


_geo_logical_types = [
    _knime_value_factory("org.knime.geospatial.core.data.cell.GeoCell$ValueFactory"),
    _knime_value_factory(
        "org.knime.geospatial.core.data.cell.GeoPointCell$ValueFactory"
    ),
    _knime_value_factory(
        "org.knime.geospatial.core.data.cell.GeoLineCell$ValueFactory"
    ),
    _knime_value_factory(
        "org.knime.geospatial.core.data.cell.GeoPolygonCell$ValueFactory"
    ),
    _knime_value_factory(
        "org.knime.geospatial.core.data.cell.GeoMultiPointCell$ValueFactory"
    ),
    _knime_value_factory(
        "org.knime.geospatial.core.data.cell.GeoMultiLineCell$ValueFactory"
    ),
    _knime_value_factory(
        "org.knime.geospatial.core.data.cell.GeoMultiPolygonCell$ValueFactory"
    ),
    _knime_value_factory(
        "org.knime.geospatial.core.data.cell.GeoCollectionCell$ValueFactory"
    ),
]

_shapely_type_to_value_factory = {
    "Point": "org.knime.geospatial.core.data.cell.GeoPointCell$ValueFactory",
    "LineString": "org.knime.geospatial.core.data.cell.GeoLineCell$ValueFactory",
    "Polygon": "org.knime.geospatial.core.data.cell.GeoPolygonCell$ValueFactory",
    "MultiPoint": "org.knime.geospatial.core.data.cell.GeoMultiPointCell$ValueFactory",
    "MultiLineString": "org.knime.geospatial.core.data.cell.GeoMultiLineCell$ValueFactory",
    "MultiPolygon": "org.knime.geospatial.core.data.cell.GeoMultiPolygonCell$ValueFactory",
    "GeometryCollection": "org.knime.geospatial.core.data.cell.GeoCollectionCell$ValueFactory"
    # There are more types in shapely like LinearRing, etc.
    # If we want to support these, we need corresponding ValueFactories on the Java side.
}


@kt.register_from_pandas_column_converter
class FromGeoPandasColumnConverter(kt.FromPandasColumnConverter):
    # these warnings will be suppressed by the warning manager
    warnings_to_suppress = ["Geometry column does not contain geometry."]

    def can_convert(self, dtype) -> bool:
        return hasattr(dtype, "name") and dtype.name == "geometry"

    def convert_column(
        self, data_frame: "pandas.dataframe", column_name: str
    ) -> "pandas.Series":
        import pandas as pd
        import geopandas
        import pyarrow as pa
        import knime_arrow_pandas as kap

        column = data_frame[column_name]
        geo_column = geopandas.GeoSeries(column)

        crs = None
        if geo_column.crs:  # highest crs level
            crs = str(geo_column.crs)
        elif data_frame.crs:  # else second-highest crs level
            crs = str(data_frame.crs)

        wkbs = geo_column.to_wkb()

        # extract the most specific type from the data and decide which value factory to use
        most_specific_value_factory = (
            "org.knime.geospatial.core.data.cell.GeoCell$ValueFactory"
        )
        geom_types = set(geo_column.geom_type)
        if len(geom_types) == 1:
            geom_type = geom_types.pop()

            most_specific_value_factory = _shapely_type_to_value_factory[geom_type]

        # how do we get the data into pandas/pyarrow from wkb???
        dtype = kap.PandasLogicalTypeExtensionType(
            storage_type=pa.struct([("0", pa.large_binary()), ("1", pa.string())]),
            logical_type=_knime_value_factory(most_specific_value_factory),
            converter=GeoValueFactory(),
        )

        return pd.Series(
            [GeoValue(w, crs) for w in wkbs], dtype=dtype, index=column.index
        )


@kt.register_to_pandas_column_converter
class ToGeoPandasColumnConverter(kt.ToPandasColumnConverter):
    def can_convert(self, dtype):
        import knime_arrow_types as kat

        return (
            isinstance(dtype, kat.LogicalTypeExtensionType)
            and dtype.logical_type in _geo_logical_types
        )

    def convert_column(
        self, data_frame: "pandas.dataframe", column_name: str
    ) -> "pandas.Series":
        import geopandas

        column = data_frame[column_name]
        # TODO: handle missing values
        crs_set = set()
        wkb_lst = []
        for value in column:
            if value is not None and not (value.crs is None and value.wkb is None):
                crs_set.add(value.crs)
                wkb_lst.append(value.wkb)
                if len(crs_set) != 1:
                    raise ValueError(
                        f"Can only work with exactly one reference frame in one column, but got {crs_set}"
                    )
            else:
                wkb_lst.append(None)
        crs = crs_set.pop()

        return geopandas.GeoSeries.from_wkb(
            wkb_lst,
            crs=crs,
        )
