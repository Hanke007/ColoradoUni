var minYear = 1987;
var maxYear = 2014;
var sliderTimelineMin = minYear;
var sliderTimelineMax = maxYear;
var sliderTimelineStep = 1;
var sliderTimelineValue = 1;

// change opacity of the layer
$(".sliderLayerOpacity").slider({
	min : 0,
	max : 1,
	step : 0.05,
	value : 0.5,
	slide : function(event, ui) {
		// $("#layerOpacityText").text( ui.value );
		$(layerBaseMap.setOpacity(ui.value));
	},
	change : function(event, ui) {
		// $("#layerOpacityText").text( ui.value );
		$(layerBaseMap.setOpacity(ui.value));
	}
}).slider("pips", {
	rest : "label",
	step : 5
}).on("slidechange", function(event, ui) {
	// $("#layerOpacityText").text( ui.value );
	$(layerBaseMap.setOpacity(ui.value));
	$(updateResults(anomalyRequest));
});

// change opacity of the markers
$(".sliderMarkerOpacity").slider({
	min : 0,
	max : 1,
	step : 0.05,
	value : 0.75,
	slide : function(event, ui) {
		// $("#markerOpacityText").text( ui.value );
		$(imageLayer.setOpacity(ui.value));
	},
	change : function(event, ui) {
		// $("#markerOpacityText").text( ui.value );
		$(imageLayer.setOpacity(ui.value));
	}
}).slider("pips", {
	rest : "label",
	step : 5
}).on("slidechange", function(event, ui) {
	// $("#markerOpacityText").text( ui.value );
	$(imageLayer.setOpacity(ui.value));
	$(updateResults(anomalyRequest));
});

// slider hot and cold anomalies
$(".sliderAnomalyOpacity").slider({
	min : 0,
	max : 1,
	step : 0.05,
	value : 0.25,
	slide : function(event, ui) {
		// $("#markerOpacityText").text( ui.value );
		$(redVectorLayer.setOpacity(ui.value));
		$(blueVectorLayer.setOpacity(ui.value));
		$(vectorLayer.setOpacity(ui.value));
	},
	change : function(event, ui) {
		// $("#markerOpacityText").text( ui.value );
		$(redVectorLayer.setOpacity(ui.value));
		$(blueVectorLayer.setOpacity(ui.value));
		$(vectorLayer.setOpacity(ui.value));
	}
}).slider("pips", {
	rest : "label",
	step : 5
}).on("slidechange", function(event, ui) {
	// $("#markerOpacityText").text( ui.value );
	$(redVectorLayer.setOpacity(ui.value));
	$(blueVectorLayer.setOpacity(ui.value));
	$(vectorLayer.setOpacity(ui.value));
	$(updateResults(anomalyRequest));
});

// ====================================================
//
// user input of the date
//
// ====================================================
$(".sliderYears").slider({
	min : minYear,
	max : maxYear,
	range : true,
	step : 1,
	values : [ gStartYear, gEndYear ],
	slide : function(event, ui) {
		updateYearlyRange(aggregateAnomalyResponse);
	},
	change : function(event, ui) {
		updateYearlyRange(aggregateAnomalyResponse);
	}
}).slider("pips", {
	rest : "label",
	step : 3
});

// ====================================================
//
// slider for Month of date input
//
// ====================================================
$(".sliderMonths").slider({
	min : 0,
	max : 11,
	range : true,
	values : [ gStartMonth, gEndMonth ],
	slide : function(event, ui) {
		updateMonthlyRange(aggregateAnomalyResponse);
	},
	change : function(event, ui) {
		updateMonthlyRange(aggregateAnomalyResponse);
	}
}).slider("pips", {
	rest : "label",
	labels : gMonths
});

// ====================================================
//
// thresholds for the results to return
//
// ====================================================
$(".sliderVal").slider({
	min : 50,
	max : 350,
	range : true,
	step : 10,
	values : [ gStartKevin, gEndKevin ],
	slide : function(event, ui) {
		updateKevinRange(aggregateAnomalyResponse);
	},
	change : function(event, ui) {
		updateKevinRange(aggregateAnomalyResponse);
	}
}).slider("pips", {
	rest : "label",
	step : 3
});

// ====================================================
//
// slider for main timeline at bottom of page
//
// ====================================================

// find the next set of days
function GetDates(startDate, daysToAdd) {
	arrDates = [];
	for (var i = 1; i <= daysToAdd; i++) {
		var currentDate = new Date(startDate);
		currentDate.setDate(currentDate.getDate() + i);
		arrDates.push(gMonths[currentDate.getMonth()] + " "
				+ currentDate.getDate() + " " + currentDate.getFullYear());
	}
	return arrDates;
}
