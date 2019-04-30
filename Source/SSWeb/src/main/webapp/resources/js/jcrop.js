var selected_x = 50, selected_y = 0, selected_h = 100, selected_w = 100;
var imageMaxWidth = 470;
var imageMaxHeight = 470;
var ratio = 1;

$(document).on('change', '#prof-image', function() {
	overlayRevert();
	createPopupCanvas();
	initiateJcrop(this, false,false);
});


$(document).on('change', '#rep-prof-image', function() {
	overlayRevert();
	createPopupCanvas();
	initiateJcrop(this, true,false);
});

$(document).on('change', '#v-ed-prof-image', function() {
	overlayRevert();
	createPopupCanvas();
	initiateJcrop(this, false,true);
});

function initiatePopupForImgFix(){
	profImg = $('#prof-image-edit').attr('src');
	createPopupCanvasForImgFix();
	initiateJcropForImgFix(profImg,true);
}

function createPopupCanvasForImgFix() {
	var canvas = '<img src="" id="target" class="hide jcrop-img-upload" style="position:absoulte;"/>'
			+ '<canvas id="canvas" style="overflow:hidden; position:absoulte; display:none;"></canvas>';
	$('.ss-prof-img-popup-cropper').html(canvas).css('position', 'relative');
}

function initiateJcropForImgFix(profImg,forNewDashboard,forMultiSelect) {

		var reader = new FileReader();
		var myImage = new Image();
		myImage.src = profImg;
		
		$('#target').attr('src', profImg);
		$('#target').removeClass('hide');
			
		$('#target').load(function() {
			$('#target').attr('data-original-width',myImage.width);
			$('#target').attr('data-original-height',myImage.height);
			
			if (myImage.width > myImage.height && myImage.width > imageMaxWidth) {
				// landscape image
				$('#target').width(imageMaxWidth);
			}
			else if (myImage.width <= myImage.height && myImage.height > imageMaxHeight) {
				// portrait image
				$('#target').height(imageMaxHeight);
			}
			/*else {
				$('#target').width(imageMaxWidth);
				$('#target').height(imageMaxHeight);
		}*/
		
			
			ratio = $('#target').width() / myImage.width;

			$('#target').Jcrop({
				aspectRatio : 1,
				setSelect : [ 50, 50, 300, 300 ],
				onSelect : updatePreview,
				onChange : updatePreview,
				trackDocument : true
			});
		});
			
		$('.ss-prof-img-popup-confirm').click(function(e) {
			e.stopImmediatePropagation();
			e.preventDefault();
			e.stopPropagation();
			
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
			
			
			var originalWidth = $('#target').attr('data-original-width');
			var boxWidth = $('#target').width();
			
			var boxToOriginalRatio = originalWidth / boxWidth;
			
			var formData = new FormData();
			formData.append("selected_x", Math.round(selected_x * boxToOriginalRatio));
			formData.append("selected_y", Math.round(selected_y * boxToOriginalRatio));
			formData.append("selected_w", Math.round(selected_w * boxToOriginalRatio));
			formData.append("selected_h", Math.round(selected_h * boxToOriginalRatio));
			formData.append("width", Math.round($('#target').width() * boxToOriginalRatio));
			formData.append("height", Math.round($('#target').height() * boxToOriginalRatio));
			formData.append("imageFileName", forNewDashboard == true ? $('#rep-prof-container').attr('data-prof-name') :"");
			formData.append("imageBase64", dataurl);
			formData.append("forNewDashboard",forNewDashboard == true ? true : false );
			formData.append("forQuickEdits", false);
			formData.append("userIdForQuickEdits","");
			formData.append("forProfileImageFix",true );
			
			$.ajax({
				url : "updateprofileimage.do",
				type : "POST",
				contentType : false,
				processData : false,
				cache : false,
				data : formData,
				success : callBackOnProfileImageUpload,
				complete : function() {
					if( forNewDashboard == true ){
						$('#rep-prof-image').val('');
					} else {
						$('#prof-image').val('');
					}
					
					$('.ss-prof-img-fix-popup').hide();
					$('.ss-prof-img-popup-cropper').html('');
					$('#overlay-toast').html('Updated profile image successfully.');
					showToast();
				},
				error : function(e) {
					$('#overlay-toast').html('Failed to Update profile image.');
					showToast();
				}
			});
		});
}

function createPopupCanvas() {
	var canvas = '<img src="" id="target" class="hide jcrop-img-upload" style="position:absoulte;"/>'
			+ '<canvas id="canvas" style="overflow:hidden; position:absoulte; display:none;"></canvas>';
	$('#overlay-header').html("Edit image");
	$('#overlay-text').html(canvas).css('position', 'relative');
	$('#overlay-continue').html("Upload");
	$('#overlay-cancel').html("Cancel");

	$('#overlay-main').show();
}

// Function to crop and upload profile image
function initiateJcrop(input, forNewDashboard, forQuickEdits) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();
		reader.onload = function(e) {
			var myImage = new Image();
			myImage.src = e.target.result;
			
			$('#target').attr('src', e.target.result);
			$('#target').removeClass('hide');
			
			$('#target').load(function() {
				$('#target').attr('data-original-width',myImage.width);
				$('#target').attr('data-original-height',myImage.height);
				
				if (myImage.width > myImage.height && myImage.width > imageMaxWidth) {
					// landscape image
					$('#target').width(imageMaxWidth);
				}
				else if (myImage.width <= myImage.height && myImage.height > imageMaxHeight) {
					// portrait image
					$('#target').height(imageMaxHeight);
				}
				/*else {
					$('#target').width(imageMaxWidth);
					$('#target').height(imageMaxHeight);
				}*/
				
				
				ratio = $('#target').width() / myImage.width;
	
				$('#target').Jcrop({
					aspectRatio : 1,
					setSelect : [ 50, 50, 300, 300 ],
					onSelect : updatePreview,
					onChange : updatePreview,
					trackDocument : true
				});
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
			
			
			var originalWidth = $('#target').attr('data-original-width');
			var boxWidth = $('#target').width();
			
			var boxToOriginalRatio = originalWidth / boxWidth;
			
			var formData = new FormData();
			formData.append("selected_x", Math.round(selected_x * boxToOriginalRatio));
			formData.append("selected_y", Math.round(selected_y * boxToOriginalRatio));
			formData.append("selected_w", Math.round(selected_w * boxToOriginalRatio));
			formData.append("selected_h", Math.round(selected_h * boxToOriginalRatio));
			formData.append("width", Math.round($('#target').width() * boxToOriginalRatio));
			formData.append("height", Math.round($('#target').height() * boxToOriginalRatio));
			formData.append("imageFileName", forNewDashboard == true ? $('#rep-prof-image').prop("files")[0].name : (forQuickEdits == true ?$('#v-ed-prof-image').prop("files")[0].name :$('#prof-image').prop("files")[0].name));
			formData.append("imageBase64", dataurl);
			formData.append("forNewDashboard",forNewDashboard == true ? true : false );
			formData.append("forQuickEdits", forQuickEdits == true ? true : false);
			formData.append("userIdForQuickEdits",$('#selected-userid-hidden').val());
			formData.append("forProfileImageFix",false );
			
			overlayRevert();
			$.ajax({
				url : "updateprofileimage.do",
				type : "POST",
				contentType : false,
				processData : false,
				cache : false,
				data : formData,
				success : callBackOnProfileImageUpload,
				complete : function() {
					hideOverlay();
					if(forQuickEdits == true){
						$('#v-ed-prof-image').val('');
					}else if( forNewDashboard == true ){
						$('#rep-prof-image').val('');
					} else {
						$('#prof-image').val('');
					}
				},
				error : function(e) {
					if(e.status == 504) {
						redirectToLoginPageOnSessionTimeOut(e.status);
						return;
					}
					redirectErrorpage();
				}
			});
		});
		
		$('#overlay-cancel').off('click');
		$('#overlay-cancel').click(function() {
			$('#v-ed-prof-image').val('');
			$('#rep-prof-image').val('');
			$('#prof-image').val('');
			
			$('#overlay-continue').unbind('click');
			$('#overlay-cancel').unbind('click');
			overlayRevert();
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

$(document).on('change', '#ms-prof-image', function() {
	overlayRevert();
	createPopupCanvasForMultiSelect();
	initiateJcropForMultiSelect(this, false);
});

function createPopupCanvasForMultiSelect() {
	var canvas = '<img src="" id="target" class="hide jcrop-img-upload" style="position:absoulte;"/>'
			+ '<canvas id="canvas" style="overflow:hidden; position:absoulte; display:none;"></canvas>';
	$('#ms-prof-img-cropper').html(canvas).css('position', 'relative');
	$('#ms-prof-pic-cont').show();
	
	$('#ms-prof-pic-cont').show();
	$('#ms-assign-popup').show();
	$('#ms-assign-popup-hdr').html('Bulk Profile Image Upload');
}

function initiateJcropForMultiSelect(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();
		reader.onload = function(e) {
			var myImage = new Image();
			myImage.src = e.target.result;
			
			$('#target').attr('src', e.target.result);
			$('#target').removeClass('hide');
			
			$('#target').load(function() {
				$('#target').attr('data-original-width',myImage.width);
				$('#target').attr('data-original-height',myImage.height);
				
				if (myImage.width > myImage.height && myImage.width > imageMaxWidth) {
					// landscape image
					$('#target').width(imageMaxWidth);
				}
				else if (myImage.width <= myImage.height && myImage.height > imageMaxHeight) {
					// portrait image
					$('#target').height(imageMaxHeight);
				}
				/*else {
					$('#target').width(imageMaxWidth);
					$('#target').height(imageMaxHeight);
				}*/
				
				
				ratio = $('#target').width() / myImage.width;
	
				$('#target').Jcrop({
					aspectRatio : 1,
					setSelect : [ 50, 50, 300, 300 ],
					onSelect : updatePreview,
					onChange : updatePreview,
					trackDocument : true
				});
			});
		};
		reader.readAsDataURL(input.files[0]);

		$('#ms-assign-popup-assign').off('click');
		$('#ms-assign-popup-assign').click(function() {
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
			
			var selectedUsers = $('#ms-user-data').data('selectedUsers');
			
			if(selectedUsers.length <= 0){
				$('#overlay-toast').html('No Users Selected');
				showToast();
				return;
			}
			
			$('#ms-overlay-loader').show();
			
			var userIdList = [];
			for(var i=0;i<selectedUsers.length;i++){
				userIdList.push(selectedUsers[i].userId);
			}
			
			var originalWidth = $('#target').attr('data-original-width');
			var boxWidth = $('#target').width();
			
			var boxToOriginalRatio = originalWidth / boxWidth;
			
			var manageTeamBulkRequest = {
					selectedX : Math.round(selected_x * boxToOriginalRatio),
					selectedY : Math.round(selected_y * boxToOriginalRatio),
		    		selectedW : Math.round(selected_w * boxToOriginalRatio),
		    		selectedH : Math.round(selected_h * boxToOriginalRatio),
		    		resizeWidth : Math.round($('#target').width() * boxToOriginalRatio),
		    		resizeHeight : Math.round($('#target').height() * boxToOriginalRatio),
		    		imageBase64 : dataurl,
		    		imageFileName : $('#ms-prof-image').prop("files")[0].name,
		    		userIds : userIdList
			}
			
			var action = 'profImgUpload';
			
			$.ajax({
				url : "/users/uploadprofilepic.do",
				type : "POST",
				contentType: "application/json",
				dataType : "json",
				data : JSON.stringify(manageTeamBulkRequest),
				async : false,
				success : function(data){showMSSuccessPopup(data,action)},
				complete : function() {
					hideMSProfImgPopup();
					$('#ms-overlay-loader').hide();
					actionCompleteCallback();
				},
				error : function(e) {
					if(e.status == 504) {
						redirectToLoginPageOnSessionTimeOut(e.status);
						return;
					}
					redirectErrorpage();
					hideMSProfImgPopup();
				}
			});
		});
		
		$(document).off('click','#ms-assign-popup-cancel');
		$(document).on('click','#ms-assign-popup-cancel',function(e){
			e.stopPropagation();
			hideMSProfImgPopup();
		});
	}
}