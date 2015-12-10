//*******************************************************************************
//																				*
//																				*
//						Global Variables										*
//																				*
//																				*
//*******************************************************************************
var gMonths = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
		"Oct", "Nov", "Dec" ];

// bounding box for request
var gLoctnArray = Array();
gLoctnArray[0] = {
	"longitude" : -39.3,
	"latitude" : -42.5
};
gLoctnArray[1] = {
	"longitude" : -41.4,
	"latitude" : 136
};

var gStartDate = "2010-12-12";
var gEndDate = "2010-12-20";
var gStartYear = 1992; // invoke in condensate-slider
var gEndYear = 1994; // invoke in condensate-slider
var gStartMonth = 7; // invoke in condensate-slider; 7 means august
var gEndMonth = 9; // invoke in condensate-slider; 9 means october
var gStartKevin = 160; // invoke in condensate-slider
var gEndKevin = 240; // invoke in condensate-slider

var DATE_STEP_CONST = 8;
var gCurWindowBegin;

var gAnomalyRepo = [];
var aggregateAnomalyResponse = [];

var anomalyRequest = {
	"chkPolar" : "v",
	"chkFreq" : "19",
	"dsName" : "SSMI",
	"dsFreq" : "s19v",
	"sDate" : gStartDate,
	"eDate" : gEndDate,
	"sMonth" : gStartMonth,
	"eMonth" : gEndMonth,
	"sYear" : gStartYear,
	"eYear" : gEndYear,
	"locations" : gLoctnArray
};

// //////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////
// http://jsfiddle.net/jfhartsock/cM3ZU/
Date.prototype.addDays = function(days) {
	var dat = new Date(this.valueOf())
	dat.setDate(dat.getDate() + days);
	return dat;
}

// update labels of timeline pips

// *************************************************************************************
//
//
// ajax module
//
//
// *************************************************************************************
var requestReturned = 0;
var lHeader;
var aggregateAnomalyResponse = [];

// iterate results
function ajaxIterRequestIntial(e) {
	lHeader = anomalyRequest["dsName"] + "_" + anomalyRequest["dsFreq"] + "_"
			+ JSON.stringify(anomalyRequest["locations"]) + "_";

	ajaxHandle = $
			.ajax({
				type : "POST",
				contentType : "application/json; charset=utf-8",
				url : "./anomaly/ajaxRetrvAnomaly.do",
				// data : localStorage["userQuery"], // don't need localstorage
				// anymore
				data : JSON.stringify(anomalyRequest),
				dataType : 'json',
				async : true,
				success : function(response) {
					console.log("ajax success!________________ajax success!");
					console.log("respones: " + response);

					if (response.length !== 0) {
						requestReturned = 1;
						gCurWindowBegin = new Date(0);
						gCurWindowBegin
								.setUTCSeconds(response[0]["date"] / 1000)
						updateMapAndFillCache(response, gCurWindowBegin);

						createTimelineSlider();
						document.getElementById("timelineBox").style.visibility = "visible";
					}
				}
			});
}

function ajaxIterRequestConcave(lsDate) {
	lAmlyRequest = JSON.parse(JSON.stringify(anomalyRequest));
	lAmlyRequest["sDate"] = lsDate;
	ajaxHandle = $.ajax({
		type : "POST",
		contentType : "application/json; charset=utf-8",
		url : "./anomaly/ajaxRetrvAnomaly.do",
		// data : localStorage["userQuery"], // don't need localstorage
		// anymore
		data : JSON.stringify(lAmlyRequest),
		dataType : 'json',
		async : true,
		success : function(response) {
			if (response != null && response.length !== 0) {
				requestReturned = 1;
				lwBegin = new Date(0);
				lwBegin.setUTCSeconds(response[0]["date"] / 1000)
				updateMapAndFillCache(response, lwBegin);
			}
		}
	});
}

// aggregate results
function ajaxAggregateDailyRequest(e) {
	console.log("making aggregate request....");

	ajaxHandle = $.ajax({
		type : "POST",
		contentType : "application/json; charset=utf-8",
		url : "./anomaly/ajaxRetrvYearlyAggAnomaly.do",
		// to query monthly, just change the string
		// url :
		// "http://localhost:8080/condensate-web/anomaly/ajaxRetrvYearlyAggAnomaly.do",
		data : JSON.stringify(anomalyRequest),
		dataType : 'json',
		async : true,
		success : function(response) {
			console.log("ajax success! ajax success!");
			console.log("respones: " + response);
			aggregateAnomalyResponse = response;
			requestReturned = 1;
			updateMapAggregate();
		}
	});
}

// aggregate results
function ajaxAggregateMonthlyRequest(e) {
	console.log("making aggregate request....");

	ajaxHandle = $.ajax({
		type : "POST",
		contentType : "application/json; charset=utf-8",
		url : "./anomaly/ajaxRetrvMonthlyAggAnomaly.do",
		// to query monthly, just change the string
		// url :
		// "http://localhost:8080/condensate-web/anomaly/ajaxRetrvYearlyAggAnomaly.do",
		data : JSON.stringify(anomalyRequest),
		dataType : 'json',
		async : true,
		success : function(response) {
			console.log("ajax success! ajax success!");
			console.log("respones: " + response);
			aggregateAnomalyResponse = response;
			requestReturned = 1;
			updateMapAggregate();
		}
	});
}

// *************************************************************************************
//
//
// Open Layer 3.0
//
//
// *************************************************************************************
// add anomalies to the map

// *************************************************************************************
//
//
// Open Layer 3.0
//
//
// *************************************************************************************
function updateMapAndFillCache(anomalyResponse, targetDate) {
	var anomlyArr = []; // each entry on list

	if ((requestReturned == 1) && (anomalyResponse.length !== 0)) {
		// need to reset request returned at some point
		source.clear();

		// build result buffer
		for (var k = 0; k < anomalyResponse.length; k++) {
			if (k == 0) {
				vectorSource.clear();
			}

			iDate = new Date(0);
			iDate.setUTCSeconds(anomalyResponse[k]["date"] / 1000);
			hDateStr = iDate.getUTCFullYear() + "_" + iDate.getUTCMonth() + "_"
					+ iDate.getUTCDate();
			hKey = lHeader + hDateStr;
			hVal = gAnomalyRepo[hKey];
			if (hVal == null) {
				hVal = [];
			}
			hVal.push(anomalyResponse[k]);
			gAnomalyRepo[hKey] = hVal;

			if (targetDate.getUTCFullYear() == iDate.getUTCFullYear()
					&& targetDate.getUTCMonth() == iDate.getUTCMonth()
					&& targetDate.getUTCDate() == iDate.getUTCDate()) {
				anomlyArr.push(anomalyResponse[k]);
			}
		}

		replotMap(anomlyArr);
		requestReturned = 0; // reset for next time

	} else { // if request returned is 1
		console
				.log("...waiting for the data ____or___ the length of anomalyResponse is zero?!");
	}
} // end updateMap

// add anomalies to the map
function updateMapAggregate() {
	var k, j = 0; // loop vars
	var foo = []; // each entry on list

	// wait for response
	if ((requestReturned == 1) && (aggregateAnomalyResponse.length !== 0)) {
		// need to reset request returned at some point
		console.log("...with " + aggregateAnomalyResponse.length
				+ " anomalies.")
		replotMap(aggregateAnomalyResponse);
	} else { // if request returned is 1
		console
				.log("...waiting for the data or the length of aggregate anomaly request is zero?!");
	}
} // end updateMapAggregate

function replotMap(anomlyArr) {
	lwrBound = $(".sliderVal").slider("values")[0] * 10;
	uprBound = $(".sliderVal").slider("values")[1] * 10;
	console.log("threshold: greater than " + lwrBound + ", and less than "
			+ uprBound);

	source.clear();
	for (var k = 0; k < anomlyArr.length; k++) {
		if (k == 0) {
			redVectorSource.clear();
			blueVectorSource.clear();
		}
		foo = anomlyArr[k];
		longi = foo["longi"];
		lati = foo["lati"];
		mean = foo["mean"];
		frequency = foo["frequency"];

		var locations = ol.proj.transform([ longi, lati ], 'EPSG:4326',
				'EPSG:3031');

		var iconFeature = new ol.Feature({
			geometry : new ol.geom.Point(locations)
		});

		// $(".sliderThreshold").slider("values")[1]*10
		if (mean > lwrBound && mean < uprBound) {
			redVectorSource.addFeature(iconFeature);
		} else {
			blueVectorSource.addFeature(iconFeature);
		}
	}

	map.addLayer(redVectorLayer);
	map.addLayer(blueVectorLayer);
	requestReturned = 0; // reset for next time
}

// *************************************************************************************
//
//
// Draw BOX in MAP
//
//
// *************************************************************************************
var draw;

// button with listener to draw a rectangle ['clear' button is hard-coded in
// html]
function buttonDrawRectangle() {
	map.removeInteraction(draw);
	addInteraction();
};

function addInteraction() {
	var locationArray = Array();
	locationArray[0] = {
		"longitude" : -39.3,
		"latitude" : -42.5
	};
	locationArray[1] = {
		"longitude" : -41.4,
		"latitude" : 136
	};

	draw = new ol.interaction.Draw({
		source : source,
		type : 'LineString',
		geometryFunction : function(coordinates, geometry) {
			if (!geometry) {
				geometry = new ol.geom.Polygon(null);
			}
			var start = coordinates[0];
			var end = coordinates[1];
			geometry.setCoordinates([ [ start, [ start[0], end[1] ], end,
					[ end[0], start[1] ], start ] ]);

			locationArray[0]["longitude"] = ol.proj.transform([
					coordinates[0][0], coordinates[0][1] ], 'EPSG:3031',
					'EPSG:4326')[0].toFixed(2);
			locationArray[0]["latitude"] = ol.proj.transform([
					coordinates[0][0], coordinates[0][1] ], 'EPSG:3031',
					'EPSG:4326')[1].toFixed(2);

			locationArray[1]["longitude"] = ol.proj.transform([
					coordinates[1][0], coordinates[1][1] ], 'EPSG:3031',
					'EPSG:4326')[0].toFixed(2);
			locationArray[1]["latitude"] = ol.proj.transform([
					coordinates[1][0], coordinates[1][1] ], 'EPSG:3031',
					'EPSG:4326')[1].toFixed(2);

			console.log("coordinates" + locationArray[0] + ", "
					+ locationArray[1]);

			// use one variable to store coordinates?!
			gLoctnArray = locationArray;
			anomalyRequest["locations"] = gLoctnArray;

			return geometry;
		},
		maxPoints : 2
	});

	// return cursor to user after finishing drawing
	draw.on("drawend", function() {
		// var a = draw.getGeometry().getCoordinates();
		// $('#tempOutput').text(a);
		// console.log("coor:" + draw.getGeometry().getcoordinates());
		map.removeInteraction(draw);
	});

	map.addInteraction(draw);
} // end addInteraction

