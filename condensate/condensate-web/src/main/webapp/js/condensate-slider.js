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

// sliderHeatmapOpacity
/*
 * $(".sliderHeatmapOpacity") .slider({ min: 0, max: 1, step: 0.05, value: 1,
 * slide: function( event, ui ){ //$("#markerOpacityText").text( ui.value ); $(
 * heatmapLayer.setOpacity( ui.value ) ); }, change: function( event, ui ){
 * //$("#markerOpacityText").text( ui.value ); $( heatmapLayer.setOpacity(
 * ui.value ) ); } }) .slider("pips", { rest: "label", step: 5 })
 * .on("slidechange", function( event, ui ){ //$("#markerOpacityText").text(
 * ui.value ); $( heatmapLayer.setOpacity( ui.value ) ); $( updateResults(
 * anomalyRequest ) ); });
 */

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
	values : [ startYear, endYear ],
	// this gets a live reading of the value and prints it on the page
	slide : function(event, ui) {
		// $("#yearsText").text( ui.values[0] + " to " + ui.values[1] );
		$(updateResults(anomalyRequest));
	},
	change : function(event, ui) {
		// $("#yearsText").text( ui.values[0] + " to " + ui.values[1] );
		$(updateResults(anomalyRequest));
	}
}).slider("pips", {
	rest : "label",
	step : 3
}).on("slidechange", function(event, ui) {
	// $("#yearsText").text( ui.values[0] + " to " + ui.values[1] );
	$(updateResults(anomalyRequest));
});

// ====================================================
//
// slider for patterns of date input
//
// ====================================================
$(".sliderMonths").slider({
	min : 0,
	max : 11,
	range : true,
	values : [ startMonth, endMonth ],
	slide : function(event, ui) {
		// $("#patternText").text( months[ui.values[0]] + " to " +
		// months[ui.values[1]] );
	},
	change : function(event, ui) {
		// $("#patternText").text( months[ui.values[0]] + " to " +
		// months[ui.values[1]] );
	}

}).slider("pips", {
	rest : "label",
	labels : months
}).on("slidechange", function(event, ui) {
	// $("#patternText").text( months[ui.values[0]] + " to " +
	// months[ui.values[1]] );
	$(updateResults(anomalyRequest));
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
	values : [ 50, 350 ],
	slide : function(event, ui) {
		$(updateResults(anomalyRequest));
	},
	change : function(event, ui) {
		$(updateResults(anomalyRequest));
	}
}).slider("pips", {
	rest : "label",
	step : 3
}).on("slidechange", function(event, ui) {
	$(updateResults(anomalyRequest));
});

// ====================================================
//
// slider for main timeline at bottom of page
//
// ====================================================
$(".sliderTimeline").slider({
	min : sliderTimelineMin,
	max : sliderTimelineMax,
	step : sliderTimelineStep,
	values : [ sliderTimelineMin, sliderTimelineMax ],
	slide : function(event, ui) {
		// $("#timelineText").text( ui.value );
	},
	change : function(event, ui) {
		// $("#timelineText").text( ui.value );
	}
}).slider("pips", {
	rest : "label",
	step : 1
}).on("slidechange", function(event, ui) {
	// $("#timelineText").text( ui.value );
	$(updateResults(anomalyRequest));
});