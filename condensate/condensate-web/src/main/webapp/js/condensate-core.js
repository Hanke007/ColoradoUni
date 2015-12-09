/* file contains various methods for interacting with the page */

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
// add anomalies to the map
function updateMap() {
	var k, j = 0; // loop vars
	var foo = []; // each entry on list

	// wait for response
	if ((requestReturned == 1) && (anomalyResponse.length !== 0)) {
		// need to reset request returned at some point
		firstAnomaly = new Date(0);
		firstAnomaly.setUTCSeconds(anomalyResponse[0]["date"] / 1000);

		secondAnomaly = new Date(0);
		secondAnomaly
				.setUTCSeconds(anomalyResponse[anomalyResponse.length - 1]["date"] / 1000);

		console.log("first anomaly: " + firstAnomaly);
		console.log("last anomaly: " + secondAnomaly);
		console.log("...with " + anomalyResponse.length + " anomalies.")

		source.clear();

		// create a bunch of icons and add to source vector
		for (var k = 0; k < anomalyResponse.length; k++) {

			if (k == 0) {
				vectorSource.clear();
			}
			foo = anomalyResponse[k];
			longi = foo["longi"];
			lati = foo["lati"];

			var locations = ol.proj.transform([ longi, lati ], 'EPSG:4326',
					'EPSG:3031');

			var iconFeature = new ol.Feature({
				// geometry: new ol.geom.Point(ol.proj.transform([lati,
				// longi],'EPSG:4326','EPSG:3031')),
				geometry : new ol.geom.Point(locations)
			// ,
			// name: 'Null Island',
			// population: 400,
			// rainfall: 500
			});
			vectorSource.addFeature(iconFeature);
		}

		map.addLayer(vectorLayer);
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

		source.clear();

		for (var k = 0; k < aggregateAnomalyResponse.length; k++) {

			if (k == 0) {
				redVectorSource.clear();
				blueVectorSource.clear();
			}
			foo = aggregateAnomalyResponse[k];
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
			console.log("threshold: greater than "
					+ $(".sliderThreshold").slider("values")[0]
					+ ", and less than "
					+ $(".sliderThreshold").slider("values")[1]);
			if (mean > $(".sliderThreshold").slider("values")[0] * 10
					&& mean < $(".sliderThreshold").slider("values")[1] * 10) {
				redVectorSource.addFeature(iconFeature);
			} else {
				blueVectorSource.addFeature(iconFeature);
			}
		}

		map.addLayer(redVectorLayer);
		map.addLayer(blueVectorLayer);
		requestReturned = 0; // reset for next time

	} else { // if request returned is 1
		console
				.log("...waiting for the data or the length of aggregate anomaly request is zero?!");
	}
} // end updateMapAggregate

// //////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////

var months = [ "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep",
		"oct", "nov", "dec" ];
var frequency = "19";
var polarization = "h";
var startDate = "2010-12-12";
var endDate = "2010-12-20";

// bounding box for request
var loctnArray = Array();
loctnArray[0] = {
	"longitude" : -39.3,
	"latitude" : -42.5
};
loctnArray[1] = {
	"longitude" : -41.4,
	"latitude" : 136
};
boxCoordinates = loctnArray;

var startYear = 1992;
var endYear = 1994;
var startMonth = 7; // 7 means august
var endMonth = 9; // 9 means october

anomalyRequest = {
	"chkPolar" : "v",
	"chkFreq" : "19",
	"dsName" : "SSMI",
	"dsFreq" : "s19v",
	"sDate" : "2002-02-03",
	"eDate" : "2002-02-23",
	"sMonth" : startMonth,
	"eMonth" : endMonth,
	"sYear" : startYear,
	"eYear" : endYear,
	"locations" : loctnArray
};

// //////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////
// http://jsfiddle.net/jfhartsock/cM3ZU/
Date.prototype.addDays = function(days) {
	var dat = new Date(this.valueOf())
	dat.setDate(dat.getDate() + days);
	return dat;
}
function getDates(startDate, stopDate) {
	var dateArray = new Array();
	var currentDate = startDate;
	while (currentDate <= stopDate) {
		dateArray.push(currentDate)
		currentDate = currentDate.addDays(1);
	}
	return dateArray;
}

// update labels of timeline pips
function updateDateValues() {

	// generate a list of all days between start and end of input
	var beginningDate = new Date(startDate);
	var endingDate = new Date(endDate);

	dateArray = getDates(beginningDate, endingDate);

	// print out all the dates to the console
	for (i = 0; i < dateArray.length; i++) {
		// console.log( dateArray[i] );
	}

	if ($("#chkMonth").is(':checked')) {
		// months
		$(".sliderTimeline").slider({
			min : 0,
			max : 11,
			step : 1,
			value : 5
		}).slider("pips", {
			rest : "label",
			labels : months
		});
	} else if ($("#chkYear").is(':checked')) {
		// years
		$(".sliderTimeline").slider({
			min : minYear,
			max : maxYear,
			step : 1,
			value : 1
		}).slider("pips", {
			rest : "label",
			step : 1
		});
	} else {
		// days
		$(".sliderTimeline").slider({
			min : 0,
			max : 31,
			step : 1,
			value : 0
		}).slider("pips", {
			rest : "label",
			step : 1
		});
	}
}

// //////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////
// //////////////////////////////////////////////////////////////////////////////////////

var requestReturned = 0;
var anomalyResponse = [];
var aggregateAnomalyResponse = [];

// if mode is '0' parse daily results, if mode is '1' parse aggregate
function sendRequest(anomalyRequest, mode) {

	console.log("anomalyRequest" + anomalyRequest);

	updateDateValues();
	if (mode == 0) {
		ajaxIterRequest();
	} else if (mode == 1) {
		// parseRequestAggregateDaily();
		ajaxAggregateDailyRequest();
	} else {
		console.log("error with request...");
	}

};

function ajaxIterRequest(e) {
	console.log("making request....");

	ajaxHandle = $
			.ajax({
				type : "POST",
				contentType : "application/json; charset=utf-8",
				url : "http://localhost:8080/condensate-web/anomaly/ajaxRetrvAnomaly.do",
				// data : localStorage["userQuery"], // don't need localstorage
				// anymore
				data : JSON.stringify(anomalyRequest),
				dataType : 'json',
				async : true,
				success : function(response) {
					console.log("ajax success!________________ajax success!");
					console.log("respones: " + response);
					anomalyResponse = response;
					requestReturned = 1;
					updateMap();
				}
			});
}

// aggregate results
function ajaxAggregateDailyRequest(e) {
	console.log("making aggregate request....");

	ajaxHandle = $
			.ajax({
				type : "POST",
				contentType : "application/json; charset=utf-8",
				url : "http://localhost:8080/condensate-web/anomaly/ajaxRetrvYearlyAggAnomaly.do",
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

	ajaxHandle = $
			.ajax({
				type : "POST",
				contentType : "application/json; charset=utf-8",
				url : "http://localhost:8080/condensate-web/anomaly/ajaxRetrvMonthlyAggAnomaly.do",
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
// Draw BOX in MAP
//
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

	var geometryFunction;

	function geometryFunction(coordinates, geometry) {
		if (!geometry) {
			geometry = new ol.geom.Polygon(null);
		}
		var start = coordinates[0];
		var end = coordinates[1];
		geometry.setCoordinates([ [ start, [ start[0], end[1] ], end,
				[ end[0], start[1] ], start ] ]);

		locationArray[0]["longitude"] = ol.proj.transform([ coordinates[0][0],
				coordinates[0][1] ], 'EPSG:3031', 'EPSG:4326')[0].toFixed(2);
		locationArray[0]["latitude"] = ol.proj.transform([ coordinates[0][0],
				coordinates[0][1] ], 'EPSG:3031', 'EPSG:4326')[1].toFixed(2);

		locationArray[1]["longitude"] = ol.proj.transform([ coordinates[1][0],
				coordinates[1][1] ], 'EPSG:3031', 'EPSG:4326')[0].toFixed(2);
		locationArray[1]["latitude"] = ol.proj.transform([ coordinates[1][0],
				coordinates[1][1] ], 'EPSG:3031', 'EPSG:4326')[1].toFixed(2);

		console.log("coordinates" + locationArray[0] + ", " + locationArray[1]);

		boxCoordinates = locationArray; // add coordinates to the var
		loctnArray = locationArray;
		// use one variable to store coordinates?!
		anomalyRequest["locations"] = loctnArray;

		return geometry;
	}

	draw = new ol.interaction.Draw({
		source : source,
		type : 'LineString',
		geometryFunction : geometryFunction,
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

