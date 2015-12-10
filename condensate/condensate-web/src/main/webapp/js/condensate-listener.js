//====================================================
//
//
//		onClick events for Ratio 
//
//
//====================================================
function selcTypeByDayOnClinck(ele) {
	// alert(ele.value);
	document.getElementById("datePicker").style.visibility = "visible";
	document.getElementById("timePicker").style.visibility = "hidden";
	document.getElementById("submtIter").disabled = false;
	document.getElementById("submtAgg").onclick = ajaxAggregateDailyRequest;
}

function selcTypeByMonthOnClinck(ele) {
	// alert(ele.value);
	document.getElementById("datePicker").style.visibility = "hidden";
	document.getElementById("timePicker").style.visibility = "visible";
	document.getElementById("submtIter").disabled = true;
	document.getElementById("submtAgg").onclick = ajaxAggregateMonthlyRequest;
}

function chkFreqOnClick(ele, anomalyRequest) {
	anomalyRequest["chkFreq"] = ele.value;
	updateDsFreq(anomalyRequest);
	// alert(anomalyRequest["dsFreq"]);
}

function chkPolartnOnClick(ele, anomalyRequest) {
	anomalyRequest["chkPolar"] = ele.value;
	updateDsFreq(anomalyRequest);
	// alert(anomalyRequest["dsFreq"]);
}

function updateDsFreq(anomalyRequest) {
	anomalyRequest["dsFreq"] = "s" + anomalyRequest["chkFreq"]
			+ anomalyRequest["chkPolar"];
}

// ====================================================
//
//
// onClick events for Button
//
//
// ====================================================
function submtIterOnClick() {
	gAnomalyRepo = [];
	ajaxIterRequestIntial();
}

function createTimelineSlider() {
	lEndDate = new Date(Date.parse(anomalyRequest["eDate"]));
	initialValue = 0;
	min = 0;
	max = (lEndDate.getTime() - gCurWindowBegin.getTime())
			/ (24 * 60 * 60 * 1000.0) + 1;
	labelArr = GetDates(gCurWindowBegin, max);

	// udpate labels for slider bar
	$(".sliderTimeline").slider({
		min : min,
		max : max - 1,
		value : 0,
		slide : function(event, ui) {
			updateTimeLinesliderOnSlideChange(event, ui, labelArr);
		},
		change : function(event, ui) {
			updateTimeLinesliderOnSlideChange(event, ui, labelArr);
		}
	});
	$("#timeLineLabel").text(labelArr[initialValue]);
	$("#timeLineLabel").css("margin-left",
			(initialValue - 1) / (max - min) * 100 + "%");
	$("#timeLineLabel").css("left", "-50px");

}

function updateTimeLinesliderOnSlideChange(event, ui, labelArr) {
	$("#timeLineLabel").text(labelArr[ui.value]);
	$("#timeLineLabel").css("margin-left", (ui.value - 0) / (1000) * 100 + "%");
	$("#timeLineLabel").css("left", "-50px");

	offSet = ui.value;
	lDate = new Date(gCurWindowBegin);
	lDate.setDate(gCurWindowBegin.getDate() + offSet);

	lhKey = lHeader + lDate.getUTCFullYear() + "_" + lDate.getUTCMonth() + "_"
			+ lDate.getUTCDate();
	lanomlyArr = gAnomalyRepo[lhKey];
	if (lanomlyArr != null && lanomlyArr.length !== 0) {
		replotMap(lanomlyArr);
	} else {
		ajaxIterRequestConcave(formatDate(lDate));
	}
}

function formatDate(date) {
	var d = new Date(date), month = '' + (d.getMonth() + 1), day = ''
			+ d.getDate(), year = d.getFullYear();

	if (month.length < 2)
		month = '0' + month;
	if (day.length < 2)
		day = '0' + day;

	return [ year, month, day ].join('-');
}

// ====================================================
//
//
// listener for date
//
//
// ====================================================
function updateBeginDate(ele, anomalyRequest) {
	anomalyRequest["sDate"] = ele.value;
	// alert(anomalyRequest["sDate"]);
}

function updateEndDate(ele, anomalyRequest) {
	anomalyRequest["eDate"] = ele.value;
	// alert(anomalyRequest["eDate"]);
}

// ====================================================
//
// listener for slider
//
//
// ====================================================
function updateYearlyRange(anomalyRequest) {
	startYear = $(".sliderYears").slider("values")[0];
	endYear = $(".sliderYears").slider("values")[1];
	anomalyRequest["sYear"] = startYear;
	anomalyRequest["eYear"] = endYear;
	// alert(anomalyRequest["sYear"] + ", " + anomalyRequest["eYear"]);
}

function updateMonthlyRange(anomalyRequest) {
	startMonth = $(".sliderMonths").slider("values")[0] + 1;
	endMonth = $(".sliderMonths").slider("values")[1] + 1;
	anomalyRequest["sMonth"] = startMonth;
	anomalyRequest["eMonth"] = endMonth;
	// alert(anomalyRequest["sMonth"] + ", " + anomalyRequest["eMonth"]);
}

function updateKevinRange(aggregateAnomalyResponse) {
	if (aggregateAnomalyResponse.length !== 0) {
		replotMap(aggregateAnomalyResponse);
	}
}
