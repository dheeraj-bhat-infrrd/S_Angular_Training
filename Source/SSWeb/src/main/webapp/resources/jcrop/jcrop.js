var imageMaxWidth = 470;
var ratio;

function createPopupCanvas() {
	var canvas = '<img src="" id="target" class="hide" style="position:absoulte;"/>'
		+ '<canvas id="canvas" style="overflow:hidden; position:absoulte; display:none;"></canvas>';
	$('#overlay-header').html("Edit image");
	$('#overlay-text').html(canvas).css('position', 'relative');
	$('#overlay-continue').html("Upload");
	$('#overlay-cancel').html("Cancel");

	$('#overlay-main').show();
}

function initiateJcrop(input) {
	if (input.files && input.files[0]) {
		createPopupCanvas();

		var reader = new FileReader();
		reader.onload = function(e) {
			$('#target').attr('src', e.target.result);
			if (typeof ratio === 'undefined') {
				ratio = $('#target').width() / imageMaxWidth;
			}
			$('#target').removeClass('hide');
			$('#target').width(imageMaxWidth);
			
			$('#target').Jcrop({
				aspectRatio : 1,
				setSelect: [ 100, 100, 50, 50 ],
				onSelect: updatePreview,
				onChange: updatePreview
			});
		};
		reader.readAsDataURL(input.files[0]);

		$(document).on('click', '#overlay-continue', function() {
			var dataurl = canvas.toDataURL("image/png");
			$('#prof-image').attr('src', dataurl);
			$('#overlay-main').hide();

			var formData = new FormData();
			formData.append("imageBase64", dataurl);
			formData.append("imageFileName", $('#prof-image').prop("files")[0].name);
			callAjaxPOSTWithTextData("./updateprofileimage.do", callBackOnProfileImageUpload, false, formData);
			
			$('#overlay-continue').unbind('click');
		});
	}
}

function updatePreview(c) {
	if (parseInt(c.w) > 0) {
		var imageObj = $("#target")[0];
		var canvas = $("#canvas")[0];
		var context = canvas.getContext("2d");
		context.drawImage(imageObj, (c.x)*ratio, (c.y)*ratio, (c.w)*ratio, (c.h)*ratio, 0, 0, canvas.width, canvas.height);
	}
}