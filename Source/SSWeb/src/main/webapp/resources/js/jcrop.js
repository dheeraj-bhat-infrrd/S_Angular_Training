var selected_x = 50, selected_y = 0, selected_h = 100, selected_w = 100;
var imageMaxWidth = 470;
var imageMaxHeight = 470;
var ratio = 1;

$(document).on('change', '#prof-image', function() {
	createPopupCanvas();
	initiateJcrop(this);
});

function createPopupCanvas() {
	var canvas = '<img src="" id="target" class="hide" style="position:absoulte;"/>'
			+ '<canvas id="canvas" width="200" height="200" style="overflow:hidden; position:absoulte; display:none;"></canvas>';
	$('#overlay-header').html("Edit image");
	$('#overlay-text').html(canvas).css('position', 'relative');
	$('#overlay-continue').html("Upload");
	$('#overlay-cancel').html("Cancel");

	$('#overlay-main').show();
}

// Function to crop and upload profile image
function initiateJcrop(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();
		reader.onload = function(e) {
			var myImage = new Image();
			myImage.src = e.target.result;
			if (myImage.width > myImage.height) {
				$('#target').width(imageMaxWidth);
			} else {
				$('#target').width(imageMaxWidth);
				$('#target').height(imageMaxHeight);
			}
			$('#target').attr('src', e.target.result);
			$('#target').removeClass('hide');
			
			ratio = $('#target').width() / myImage.width;

			$('#target').Jcrop({
				aspectRatio : 1,
				setSelect : [ 50, 0, 200, 200 ],
				onSelect : updatePreview,
				onChange : updatePreview,
				trackDocument : true
			});
		};
		reader.readAsDataURL(input.files[0]);

		$('#overlay-continue').click(function() {
			showOverlay();
			
			var canvas = $('#target')[0];
			var dataurl = canvas.src;
			if (isNaN(selected_x)) {
				selected_x = 50;
			}
			if (isNaN(selected_y)) {
				selected_y = 50;
			}
			if (isNaN(selected_w)) {
				selected_w = 100;
			}
			if (isNaN(selected_h)) {
				selected_h = 100;
			}
			
			var formData = new FormData();
			formData.append("selected_x", Math.round(selected_x));
			formData.append("selected_y", Math.round(selected_y));
			formData.append("selected_w", Math.round(selected_w));
			formData.append("selected_h", Math.round(selected_h));
			formData.append("width", Math.round($('#target').width()));
			formData.append("height", Math.round($('#target').height()));
			formData.append("imageFileName", $('#prof-image').prop("files")[0].name);
			formData.append("imageBase64", dataurl);
			
			overlayRevert();
			$.ajax({
				url : "updateprofileimage.do",
				type : "POST",
				contentType : false,
				processData : false,
				cache : false,
				data : formData,
				success : function(data) {
					$('#prof-message-header').html(data);

					callAjaxGET("./fetchprofileimage.do", function(data) {
						$('#prof-img-container').html(data);
						var profileImageUrl = $('#prof-image-edit').css("background-image");
						if (profileImageUrl == undefined || profileImageUrl == "none") {
							return;
						}
						adjustImage();
					});

					$('#overlay-toast').html($('#display-msg-div').text().trim());
					showToast();
					loadDisplayPicture();
				},
				complete : function() {
					hideOverlay();
				}
			});
		});
	}
}

function updatePreview(c) {
	if (parseInt(c.w) > 0) {
		var imageObj = $("#target")[0];
		var canvas = $("#canvas")[0];
		var context = canvas.getContext("2d");
		
		ratio = 1;
		canvas.width = c.w;
		canvas.height = c.h;

		selected_x = Math.round(c.x / ratio);
		selected_y = Math.round(c.y / ratio);
		selected_w = Math.round(c.w / ratio);
		selected_h = Math.round(c.h / ratio);
		
		context.drawImage(imageObj, c.x / ratio, c.y / ratio, c.w / ratio, c.h / ratio, 0, 0, canvas.width, canvas.height);
	}
}