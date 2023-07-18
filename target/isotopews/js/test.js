var sampleFields = Array();

function selectSheet() {
}

function toggleSelection(id) {
	let position = -1;
	if ((position = searchField($("#" + id).html())) < 0) {
		sampleFields.push($("#" + id).html());
		$("#" + id).attr("style", "background:yellow");
	}
	else {
		$("#" + id).removeAttr("style");
		sampleFields.splice(position, 1);
	}
}

function searchField(field) {
	for (let i = 0; i < sampleFields.length; i++) {
		if (sampleFields[i] == field)
			return i;
	}
	return -1;
}

function submitSamples() {
	if (sampleFields.length > 0) {
		let payLoad = JSON.stringify(sampleFields);
		console.log(payLoad);
		$.ajax({
			type: "POST",
			url: "fragments/receiver-xslx-fragment.jsp",
			data: payLoad,
			success: function() {
				$("#step-one").hide();
				$("#step-two").show();
			},
		});
	}
}