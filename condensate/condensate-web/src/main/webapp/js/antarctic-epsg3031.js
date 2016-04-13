window.onload = function() {

	// definitions
	proj4.defs("EPSG:3031", "+proj=stere +lat_0=-90 +lat_ts=-71 +lon_0=0 +k=1 +x_0=0 +y_0=0 +datum=WGS84 +units=m +no_defs");
	//ol.proj.get("EPSG:3031").setExtent([-3929786, -3929786, 3923000, 3923674]);//[-4194304, -4194304, 4194304, 4194304]);
	ol.proj.get("EPSG:3031").setExtent([-4194304, -4194304, 4194304, 4194304]);
	
	// for outputting the mouse position
	mousePositionControl = new ol.control.MousePosition({
		coordinateFormat: ol.coordinate.createStringXY(2),
		projection: 'EPSG:4326',
		//projection: 'EPSG:3031',
		//displayProjection: "EPSG:4326",
		className: 'custom-mouse-position',					// comment the following two lines to have the mouse position
		target: document.getElementById('mouse-position'),	// be placed within the map.
		undefinedHTML: '&nbsp;'
	});

	// create the map
	map = new ol.Map({
		controls: ol.control.defaults().extend([mousePositionControl]), // for displaying the mouse
		view: new ol.View({
			maxResolution: 8192.0,
			projection: ol.proj.get("EPSG:3031"),
			extent: [-4194304, -4194304, 4194304, 4194304], // original
			center: [0, 0],
			zoom: 1,
			maxZoom: 11,
		}),
		target: "map",
		renderer: ["canvas","dom"],
	});
	
	// create layer for binary layers of water, land, and permanent glacier
	sourceLandWater = new ol.source.WMTS({
		url: "//map1{a-c}.vis.earthdata.nasa.gov/wmts-antarctic/wmts.cgi",
		layer: "SCAR_Land_Water_Map", // jpeg 500m
		//extent: [-3929786, -3929786, 3923000, 3923674],//[-4194304, -4194304, 4194304, 4194304],
		extent: [-4194304, -4194304, 4194304, 4194304],
		format: "image/png",
		//projection: "EPSG:3031",
		matrixSet: "EPSG3031_250m",
		tileGrid: new ol.tilegrid.WMTS({
			origin: [-4194304, 4194304],
			resolutions: [
				8192.0,
				4096.0,
				2048.0,
				1024.0,
				512.0,
				256.0
			],
			matrixIds: [0, 1, 2, 3, 4, 5],
			tileSize: 512
		})
	});
	layerLandWater = new ol.layer.Tile({source: sourceLandWater});
	map.addLayer(layerLandWater);
	
	// https://wiki.earthdata.nasa.gov/display/GIBS/GIBS+Available+Imagery+Products#expand-EarthatNight1Product
	// coastline: SCAR antarctic digital database
	// add picture-like image layer
	sourceBaseMap = new ol.source.WMTS({
		opacity: 0.1,
		url: "//map1{a-c}.vis.earthdata.nasa.gov/wmts-antarctic/wmts.cgi",
		//layer: "MODIS_Terra_CorrectedReflectance_TrueColor", // png 250m
		//layer: "BlueMarble_ShadedRelief_Bathymetry", // jpeg 500m
		layer: "BlueMarble_ShadedRelief", // jpeg 500m
		extent: [-4194304, -4194304, 4194304, 4194304],
		format: "image/jpeg",
		matrixSet: "EPSG3031_500m",
		tileGrid: new ol.tilegrid.WMTS({
			origin: [-4194304, 4194304],
			resolutions: [
				8192.0,
				4096.0,
				2048.0,
				1024.0,
				512.0,
				256.0
			],
			matrixIds: [0, 1, 2, 3, 4, 5],
			tileSize: 512
		})
	});
	layerBaseMap = new ol.layer.Tile({source: sourceBaseMap});
	map.addLayer(layerBaseMap);


	// add layer with outlines of coasts and permanent glacier boundaries
	sourceCoastlines = new ol.source.WMTS({
		url: "//map1{a-c}.vis.earthdata.nasa.gov/wmts-antarctic/wmts.cgi",
		layer: "Coastlines", // jpeg 500m
		extent: [-4194304, -4194304, 4194304, 4194304],
		format: "image/png",
		matrixSet: "EPSG3031_250m",
		tileGrid: new ol.tilegrid.WMTS({
			origin: [-4194304, 4194304],
			resolutions: [
				8192.0,
				4096.0,
				2048.0,
				1024.0,
				512.0,
				256.0
			],
			matrixIds: [0, 1, 2, 3, 4, 5],
			tileSize: 512
		})
	});
	layerCoastlines = new ol.layer.Tile({source: sourceCoastlines});
	map.addLayer(layerCoastlines);
	
////////////////////////////////////
/*
	// SMAP_L3_Active_AM_Freeze_Thaw
	sourceFreezeThaw = new ol.source.WMTS({
		url: "//map1{a-c}.vis.earthdata.nasa.gov/wmts-antarctic/wmts.cgi",
		layer: "SMAP_L3_Active_AM_Freeze_Thaw",
		extent: [-4194304, -4194304, 4194304, 4194304],
		format: "image/png",
		matrixSet: "EPSG3031_1km",
		tileGrid: new ol.tilegrid.WMTS({
			origin: [-4194304, 4194304],
			resolutions: [
				8192.0,
				4096.0,
				2048.0,
				1024.0,
				512.0,
				256.0
			],
			matrixIds: [0, 1, 2, 3, 4, 5],
			tileSize: 512
		})
	});
	layerFreezeThaw = new ol.layer.Tile({source: sourceFreezeThaw});
	map.addLayer(layerFreezeThaw);
*/

/*
	// MODIS_Terra_Sea_Ice and MODIS_Aqua_Sea_Ice
	sourceSeaIce = new ol.source.WMTS({
		url: "//map1{a-c}.vis.earthdata.nasa.gov/wmts-antarctic/wmts.cgi",
		layer: "MODIS_Aqua_Sea_Ice",
		extent: [-4194304, -4194304, 4194304, 4194304],
		format: "image/png",
		matrixSet: "EPSG3031_1km",
		tileGrid: new ol.tilegrid.WMTS({
			origin: [-4194304, 4194304],
			resolutions: [
				8192.0,
				4096.0,
				2048.0,
				1024.0,
				512.0,
				256.0
			],
			matrixIds: [0, 1, 2, 3, 4, 5],
			tileSize: 512
		})
	});
	layerSeaIce = new ol.layer.Tile({source: sourceSeaIce});
	map.addLayer(layerSeaIce);
*/

/*
	// MODIS_Aqua_Snow_Cover and MODIS_Terra_Snow_Cover
	sourceSnowCover = new ol.source.WMTS({
		//resource --> https://github.com/nasa-gibs/worldview-options-eosdis/blob/master/release/gc/gibs-arctic.xml
		url: "//map1{a-c}.vis.earthdata.nasa.gov/wmts-antarctic/wmts.cgi",
		layer: "MODIS_Terra_Snow_Cover",
		extent: [-4194304, -4194304, 4194304, 4194304],
		format: "image/png",
		matrixSet: "EPSG3031_500m",
		tileGrid: new ol.tilegrid.WMTS({
			origin: [-4194304, 4194304],
			resolutions: [
				8192.0,
				4096.0,
				2048.0,
				1024.0,
				512.0,
				256.0
			],
			matrixIds: [0, 1, 2, 3, 4, 5],
			tileSize: 512
		})
	});
	layerSnowCover = new ol.layer.Tile({source: sourceSnowCover});
	map.addLayer(layerSnowCover);
*/
	
	// add layer with latitude and longitude lines
	sourceGraticule = new ol.source.WMTS({
		url: "//map1{a-c}.vis.earthdata.nasa.gov/wmts-antarctic/wmts.cgi",
		layer: "Graticule", // jpeg 500m
		extent: [-4194304, -4194304, 4194304, 4194304],
		format: "image/png",
		matrixSet: "EPSG3031_250m",
		tileGrid: new ol.tilegrid.WMTS({
			origin: [-4194304, 4194304],
			resolutions: [
				8192.0,
				4096.0,
				2048.0,
				1024.0,
				512.0,
				256.0
			],
			matrixIds: [0, 1, 2, 3, 4, 5],
			tileSize: 512
		})
	});
	layerGraticule = new ol.layer.Tile({source: sourceGraticule});
	map.addLayer(layerGraticule);
	
/////////////////////////////////////
	
		// Create an image layer, <http://www.acuriousanimal.com/thebookofopenlayers3/chapter02_04_image_layer.html>
	// add layer with the climatology
	imageLayer = new ol.layer.Image({
		opacity: 0.5,
		source: new ol.source.ImageStatic({
			url: 'images/oct.png',
			//url: 'images/adjust2.png',
			imageSize: [632, 664],
			//imageSize: [1528, 1587],
			// extent [minx, miny, maxx, maxy]
			//imageExtent: [ -3938333, -3938333, 4175000, 4175000], <-- working okay with 1528 x 1590
			imageExtent: [ -3950000, -3950000, 3700000, 4100000],
			//projection: map.getView().getProjection()
			projection: 'EPSG:4326'
			
			//imageExtent: [-3929786, -2929786, -2029786, 13523674]
			//imageExtent: [-4194304, -4194304, -4194304, 4194304]//
			
			//imageExtent: [-3929786, -3929786, 4194304, 3923674] // not sure why last number is 43...
			//imageExtent: [-4000000, -3700000, -3000000, 4100000]
			// [1] [2] 40-moves right side right [3] [4]
		})//,
		//extent: [-4194304, -4194304, 4194304, 4194304]
	});
	visible: true;
	map.addLayer(imageLayer);
	
	
	// add layer for a heatmap of anomaly points
/*
	heatmapLayer = new ol.layer.Heatmap({
		opacity: 1.0,
		source: new ol.source.Vector({
			url: 'data/cities.json',
			format: new ol.format.GeoJSON()
		})
	});
	map.addLayer(heatmapLayer);
*/

	// for drawing the selection box
	source = new ol.source.Vector();
	vector = new ol.layer.Vector({
		source: source,
		//projection: 'EPSG:3031',
		style: new ol.style.Style({
			fill: new ol.style.Fill({
				color: 'rgba(255, 255, 255, 0.2)'
			}),
			stroke: new ol.style.Stroke({
				color: '#ffffff',
				width: 2
			}),
			image: new ol.style.Circle({
				radius: 7,
				fill: new ol.style.Fill({
					color: '#ffffff'
				})
			})
		})
	});
	map.addLayer(vector);


// for drawing anomalies
	vectorSource = new ol.source.Vector(); //create empty vec
	//create the style
	iconStyle = new ol.style.Style({
		image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
			anchor: [.01, .1],
			anchorXUnits: 'fraction',
			anchorYUnits: 'fraction',
			src: 'images/greenMarker.jpg'
		}))
	});
	//add the feature vector to the layer vector, and apply a style to whole layer
	vectorLayer = new ol.layer.Vector({
		opacity: 0.25,
		source: vectorSource,
		style: iconStyle
	});

// for drawing aggregate results	
	redVectorSource = new ol.source.Vector();
	blueVectorSource = new ol.source.Vector();
	//create the style for hot and then cold
	redIconStyle = new ol.style.Style({
		image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
			anchor: [1, 1],
			anchorXUnits: 'fraction',
			anchorYUnits: 'fraction',
			src: 'images/redMarker.jpg'
		}))
	});
	blueIconStyle = new ol.style.Style({
		image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({
			anchor: [1, 1],//anchor: [.01, .1],
			anchorXUnits: 'fraction',
			anchorYUnits: 'fraction',
			src: 'images/blueMarker.jpg'
		}))
	});
	
	redVectorLayer = new ol.layer.Vector({
		opacity: 0.25,
		source: redVectorSource,
		style: redIconStyle
	});
	blueVectorLayer = new ol.layer.Vector({
		opacity: 0.25,
		source: blueVectorSource,
		style: blueIconStyle
	});

};