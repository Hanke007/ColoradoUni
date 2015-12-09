//====================================================
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
