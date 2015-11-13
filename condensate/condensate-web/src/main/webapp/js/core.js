//js code
/*-------------------------------------------*/
/*****Helper Function for User Request*****/
var sendString = "",
    dataSet = "SSMI",
    frequency = "19",
    polarization = "h",
    startDate = "2010-12-12",
    endDate = "2010-12-20";

loctnArray = Array();

/*user request function*/
var anomalyRequest = {
    "dsName": dataSet,
    "dsFreq": frequency,
    "dsPolar": polarization,
    "sDate": startDate,
    "eDate": endDate,
    "locations": loctnArray
};

var anomalyResults = [];

function updateDate() {
    startDate = "" + document.getElementById('startDate').value;
    endDate = "" + document.getElementById('endDate').value;
    anomalyRequest["sDate"] = startDate;
    anomalyRequest["eDate"] = endDate;
}

function changeDataSet(ds) {
    dataSet = ds;
    updateDate();
    anomalyRequest["dsName"] = ds;
};

function changeVariableSelection(variable, selectionList) {
    updateDate();

}

function changeFrequency(freq) {
    frequency = freq;
    updateDate();
    //update Frequency
    anomalyRequest["dsFreq"] = freq;

    if (freq == '91') {
        document.getElementById("freq91").style.color = "black";
        document.getElementById("freq85").style.color = "#B6B6B4";
        document.getElementById("freq37").style.color = "#B6B6B4";
        document.getElementById("freq22").style.color = "#B6B6B4";
        document.getElementById("freq19").style.color = "#B6B6B4";
    } else if (freq == '85') {

        document.getElementById("freq91").style.color = "#B6B6B4";
        document.getElementById("freq85").style.color = "black";
        document.getElementById("freq37").style.color = "#B6B6B4";
        document.getElementById("freq22").style.color = "#B6B6B4";
        document.getElementById("freq19").style.color = "#B6B6B4";
    } else if (freq == '37') {
        document.getElementById("freq91").style.color = "#B6B6B4";
        document.getElementById("freq85").style.color = "#B6B6B4";
        document.getElementById("freq37").style.color = "black";
        document.getElementById("freq22").style.color = "#B6B6B4";
        document.getElementById("freq19").style.color = "#B6B6B4";
    } else if (freq == '22') {
        document.getElementById("freq91").style.color = "#B6B6B4";
        document.getElementById("freq85").style.color = "#B6B6B4";
        document.getElementById("freq37").style.color = "#B6B6B4";
        document.getElementById("freq22").style.color = "black";
        document.getElementById("freq19").style.color = "#B6B6B4";
    } else {
        document.getElementById("freq91").style.color = "#B6B6B4";
        document.getElementById("freq85").style.color = "#B6B6B4";
        document.getElementById("freq37").style.color = "#B6B6B4";
        document.getElementById("freq22").style.color = "#B6B6B4";
        document.getElementById("freq19").style.color = "black";
    }
};

function changePolarization(pol) {
    polarization = pol;
    updateDate();
    anomalyRequest["dsPolar"] = pol;

    if (pol == 'v') {
        document.getElementById("vertical").style.color = "black";
        document.getElementById("horizontal").style.color = "#B6B6B4";
    } else {
        document.getElementById("horizontal").style.color = "black";
        document.getElementById("vertical").style.color = "#B6B6B4";
    }
}

//pass full request to a new page, in localStorage
function sendRequest(anomalyRequest, mode) {
    //remove the current box
    dragNewBox();
    //
    if (mode === 0) {
        updateDate(); //update start/end date based on query
    }
    anomalyRequest["dsFreq"] = "s" + frequency + polarization;
    console.log(anomalyRequest);
    localStorage['userQuery'] = JSON.stringify(anomalyRequest);
    parseRequest(); //send and parse results
    updateDateValues(); //first 10 days from user query
    updatePixels(); //visualize results
};
/*****End of Helper Function for User Reuqest*****/

/*-------------------------------------------*/
/*****Helper Functions for Visualizing User Request*****/
requestReturned = 0;

function parseRequest(e) {
    console.log("making request....");

    ajaxHandle = $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: "./anomaly/ajaxRetrvAnomaly.do",
        data: localStorage["userQuery"],
        dataType: 'json',
        async: true,
        success: function(response) {
            console.log("________________ajax success!_________________");
            if ($.isEmptyObject(response)) {
                alert('No Anomaly Found, Try Another Query');
            } else {
                anomalyResults = response;
                requestReturned = 1;
                updatePixels();
            }
        }
    });
}

var months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
var activeMonth = new Date().getMonth();
var save = 0;
var arrDates = [];

function updateDateValues() {
    sDate = JSON.parse(localStorage.userQuery)["sDate"];
    eDate = JSON.parse(localStorage.userQuery)["eDate"];

    startDate = new Date(sDate);
    slideDate = startDate;
    cDateNum = startDate;
    arrDates = GetDates(startDate, 10);
    //udpate labels for slider bar
    $(".slider").slider({
        min: 0,
        max: 9,
        value: 0
    }).slider("pips", {
        rest: "label",
        labels: arrDates
    });
}

function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

function changeDateRange(num) {
    var currentDate = new Date(slideDate.getDate());
    currentDate.setDate;
    slideDate.setDate(currentDate.getTime() + num);

    startDate = formatDate(slideDate); //update start date
    var currentDate = new Date(startDate);
    currentDate.setDate(slideDate.getDate() + 10);
    endDate = formatDate(currentDate); //update end date, need simplify code

    arrDates = GetDates(slideDate, 10); // next 10 days
    $(".slider").slider({
        min: 0,
        max: 9,
        value: 0
    }).slider("pips", {
        rest: "label",
        labels: arrDates
    })

    //send new query to server, no data cached so far, always query new data
    anomalyRequest["sDate"] = startDate;
    anomalyRequest["eDate"] = endDate;
    sendRequest(anomalyRequest, 1);
}

// find the next set of days
function GetDates(startDate, daysToAdd) {
    arrDates = [];
    for (var i = 1; i <= daysToAdd; i++) {
        var currentDate = new Date(startDate);
        currentDate.setDate(startDate.getDate() + i);
        arrDates.push(currentDate.getDate() + " " + months[currentDate.getMonth()] + " " + currentDate.getFullYear());
    }
    return arrDates;
}

function updatePixels() {
    var k, j = 0;
    var foo = [];

    var sliderValue = 0;

    if ((requestReturned == 1) && (anomalyResults.length !== 0)) {
        sliderValue = $(".slider").slider("value"); // which selection is highlighted
        timeSeconds = (slideDate.getTime() + sliderValue * 24 * 60 * 60 * 1000);

        slDate = new Date(0);
        slDate.setUTCSeconds((slideDate.getTime() + (sliderValue + 1) * 24 * 60 * 60 * 1000) / 1000);
        aReq = new Date(0);
        // should be zero!

        // if there is nothing, then what?
        aReq.setUTCSeconds(anomalyResults[0]["date"] / 1000);

        console.log("slideDate: " + slDate.getDate() + " " + (slDate.getMonth() + 1) + " " + slDate.getFullYear());
        console.log("requestDate: " + aReq.getDate() + " " + (aReq.getMonth() + 1) + " " + aReq.getFullYear());

        markers3.clearMarkers();

        for (k = 0; k < anomalyResults.length; k++) {
            foo = anomalyResults[k];
            num = foo["date"] / 1000;
            d = new Date(0); // set date to epoch
            d.setUTCSeconds(num);

            // if the slider day matches any of the json anomalies
            if (d.getDate() == slDate.getDate() && d.getMonth() == slDate.getMonth() && d.getFullYear() == slDate.getFullYear()) {

                longi = foo["longi"];
                lati = foo["lati"];

                var location = new OpenLayers.LonLat(longi, lati).transform(
                    new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
                    new OpenLayers.Projection("EPSG:3031") // to Spherical Mercator
                );
                var size = new OpenLayers.Size(10, 10);
                var offset = new OpenLayers.Pixel(-size.w / 2, -size.h / 2);
                var icon = new OpenLayers.Icon('images/redMarker.png', size, offset);
                markers3.addMarker(new OpenLayers.Marker(location, icon.clone()));

            }
        } // end loop through anomalyResults json
    } else { // if request returned is 1
        console.log("...either waiting for the data or something went wrong?!");
    }
}

/*****End of Helper Functions for Visualizing User Request*****/

/*-------------------------------------------*/
/*****Helper Functions and Variables for Map*****/
var vectors;
var box;
var transform;
var maxOpacity = 1.0;
var minOpacity = 0.0;

Proj4js.defs["EPSG:4326"] = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";
Proj4js.defs["EPSG:3031"] = "+proj=stere +lat_0=-90 +lat_ts=-71 +lon_0=0 +k=1 +x_0=0 +y_0=0 +ellps=WGS84 +datum=WGS84 +units=m +no_defs";
Proj4js.defs["EPSG:900913"] = "+proj=merc +a=6378137 +b=6378137 +lat_ts=0.0 +lon_0=0.0 +x_0=0.0 +y_0=0 +k=1.0 +units=m +nadgrids=@null +no_defs";

function changeOpacity(byOpacity) {
    graphic.setOpacity(byOpacity);
}

//Note: Update to be able to control opacity for different channels
function changeOpacityMarkers(byOpacity) {
    markers3.setOpacity(byOpacity);
}

function endDrag(bbox) {
    var bounds = bbox.getBounds();
    vertices = bbox.getVertices();
    // coordinate transformation
    topLeftVertice = new OpenLayers.LonLat(vertices[0]["x"], vertices[0]["y"])
        .transform(new OpenLayers.Projection("EPSG:3031"), new OpenLayers.Projection("EPSG:4326"));
    bottomRightVertice = new OpenLayers.LonLat(vertices[2]["x"], vertices[2]["y"])
        .transform(new OpenLayers.Projection("EPSG:3031"), new OpenLayers.Projection("EPSG:4326"));

    setBounds(bounds);
    drawBox(bounds);
    box.deactivate();
    document.getElementById("bbox_drag_instruction").style.display = 'none';
    document.getElementById("bbox_adjust_instruction").style.display = 'block';
}

function dragNewBox() {
    box.activate();
    console.log("drag box");
    transform.deactivate(); //The remove the box with handles
    vectors.destroyFeatures();
    document.getElementById("bbox_drag_instruction").style.display = 'block';
    document.getElementById("bbox_adjust_instruction").style.display = 'none';
    setBounds(null);
}

function boxResize(event) {
    setBounds(event.feature.geometry.bounds);
}

function drawBox(bounds) {
    var feature = new OpenLayers.Feature.Vector(bounds.toGeometry());
    vectors.addFeatures(feature);
    transform.setFeature(feature);
}

function toPrecision(zoom, value) {
    var decimals = Math.pow(10, Math.floor(zoom / 3));
    return Math.round(value * decimals) / decimals;
}

function setBounds(bounds) {
    if (bounds == null) {
        document.getElementById("bbox_result").innerHTML = "";
    } else {

        b = bounds.clone().transform(map.getProjectionObject(), new OpenLayers.Projection("EPSG:4326"))
        minlon = toPrecision(map.getZoom(), b.left);
        minlat = toPrecision(map.getZoom(), b.bottom);
        maxlon = toPrecision(map.getZoom(), b.right);
        maxlat = toPrecision(map.getZoom(), b.top);

        document.getElementById("bbox_result").innerHTML =
            "<br>[top left] lat: " + topLeftVertice["lat"].toPrecision(3) + ", lon: " + topLeftVertice["lon"].toPrecision(3) + ",<br>" +
            "[bottom right] lat:" + bottomRightVertice["lat"].toPrecision(3) + ", lon: " + bottomRightVertice["lon"].toPrecision(3);

        //user input region
        loctnArray = Array();
        loctnArray[0] = {
            "longitude": topLeftVertice["lon"].toPrecision(3),
            "latitude": topLeftVertice["lat"].toPrecision(3)
        };
        loctnArray[1] = {
            "longitude": bottomRightVertice["lon"].toPrecision(3),
            "latitude": bottomRightVertice["lat"].toPrecision(3)
        };

        anomalyRequest["locations"] = loctnArray;
    }
}

function setupmap() {
    map = new OpenLayers.Map('map', {
        projection: "EPSG:3031",
        displayProjection: "EPSG:4326"
    });

    var lonlat = new OpenLayers.LonLat(0, 0).transform(
        new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
        new OpenLayers.Projection("EPSG:900913") // to Spherical Mercator
    );

    vectors = new OpenLayers.Layer.Vector("Vector Layer", {
        displayInLayerSwitcher: false
    });

    //Note: get better tiles
    var tiles = new OpenLayers.Layer.XYZ(
        'Polar XYZ',
        'http://polar.openstreetmap.de/tiles/${z}/${x}/${y}.png', {
            projection: 'EPSG:3031',
            maxExtent: [-3000000, -3000000, 3000000, 3000000],
            //	attribution: '© <a href="http://www.openstreetmap.org/">OpenStreetMap</a> contributors',
            numZoomLevels: 20,
            transitionEffect: 'resize'
        }
    )
    map.addLayer(tiles);

    // http://dev.openlayers.org/examples/mouse-position.html
    map.addControl(
        new OpenLayers.Control.MousePosition({
            prefix: 'coordinates: ',
            separator: ' | ',
            numDigits: 3,
            emptyString: 'mouse over'
        })
    );

    map.events.register("mousemove", map, function(e) {
        var position = this.events.getMousePosition(e);
        //OpenLayers.Util.getElement("coords").innerHTML = position;
    });

    //Note: for overlaying the climatology image on top of the map
    graphic = new OpenLayers.Layer.Image(
        'SSMI Image	',
        'images/mar.png',
        new OpenLayers.Bounds(-3929786, -3929786, 3923000, 4323674), // not sure why last number is 43...
        new OpenLayers.Size(1, 1), {
            isBaseLayer: false,
            opacity: 0.5
        }
    );
    map.addLayer(graphic);

    // markers that designate the corners of the climatology image
    var markers = new OpenLayers.Layer.Vector("Markers", {
        projection: 'EPSG:4326',
        /* // don't need to plot the markers
        strategies: [new OpenLayers.Strategy.Fixed()],
        	protocol: new OpenLayers.Protocol.HTTP({
        	url: "interesting.json",
        	format: new OpenLayers.Format.GeoJSON()
        })
        */
    });
    map.addLayer(markers);

    var featctl = new OpenLayers.Control.SelectFeature(markers);
    map.addControl(featctl);
    featctl.activate();

    featctl.events.register('featurehighlighted', this, function(e) {
        var pt = e.feature.geometry,
            ll = new OpenLayers.LonLat(pt.x, pt.y);
        map.setCenter(0, 0);
    });

    map.events.register('zoomend', this, function() {
        markers.setVisibility(map.zoom < 8);
    });

    if (!map.getCenter()) {
        map.setCenter(new OpenLayers.LonLat(0, 0), 1); // despite the class name, these are in EPSG:3031, so 0/0 is actually the pole 
    }

    // begin draw box
    var zoom = 1;

    vectors = new OpenLayers.Layer.Vector("Vector Layer", {
        displayInLayerSwitcher: false
    });
    map.addLayer(vectors);

    box = new OpenLayers.Control.DrawFeature(vectors, OpenLayers.Handler.RegularPolygon, {
        handlerOptions: {
            sides: 4,
            snapAngle: 90,
            irregular: true,
            persist: true
        }
    });
    box.handler.callbacks.done = endDrag;
    map.addControl(box);

    transform = new OpenLayers.Control.TransformFeature(vectors, {
        rotate: false,
        irregular: true
    });

    transform.events.register("transformcomplete", transform, boxResize);

    map.addControl(transform);
    map.addControl(box);

    box.activate();
    map.setCenter(lonlat, zoom);

    markers3 = new OpenLayers.Layer.Markers("anomalies");
    map.addLayer(markers3);

};
/*****End of Helper Functions/Variables for Map*****/

/*-------------------------------------------*/
/*****Exectute once document is ready*****/
$(document).ready(function() {
    //set up map
    setupmap();
    //update Initial Date
    updateDateValues();
}); //end of document ready
