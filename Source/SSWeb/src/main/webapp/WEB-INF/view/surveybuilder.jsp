<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">Build Your Customer Feedback</div>
		</div>
	</div>
</div>

<div id="" class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="sb-container container">
		<div class="sb-header">Setup your questions</div>
		<div class="sb-questions-wrapper">
			<div class="sb-tab-header clearfix">
				<div class="sb-tab-wrapper float-left clearfix">
					<div class="sb-tab-item sb-tab-active">New Survey</div>
					<div class="sb-tab-item">Choose Template</div>
				</div>
			</div>
			<div class="sb-content">
				<div class="sb-con-txt">Choose Template</div>
				<div class="clearfix sb-tem-select">
					<div class="float-left clearfix sb-sel-item sb-sel-item-range pos-relative">
						<div class="float-left sb-sel-icn sb-range-sel-icn"></div>
						<div class="float-left sb-sel-txt">Range</div>
						<div class="float-left sb-sel-icn-dd"></div>
						<div class="sb-dd-wrapper blue-arrow-bot hide">
							<div class="sb-icn-smiles sb-dd-item">Smiles</div>
							<div class="sb-icn-star sb-dd-item">Star</div>
							<div class="sb-icn-scale sb-dd-item">Scale</div>
						</div>
					</div>
					<div class="float-left clearfix sb-sel-item">
						<div class="float-left sb-sel-icn sb-sel-icn-inact"></div>
						<div class="float-left sb-sel-icn sb-sel-icn-act hide"></div>
						<div class="float-left sb-sel-txt">Descriptive</div>
					</div>
					<div class="float-left clearfix sb-sel-item">
						<div class="float-left sb-sel-icn sb-sel-icn-inact"></div>
						<div class="float-left sb-sel-icn sb-sel-icn-act hide"></div>
						<div class="float-left sb-sel-txt">Multiple Choice</div>
					</div>
				</div>
				<div class="sb-txtarea-wrapper">
					<textarea id="question-txt" class="sb-txtarea"></textarea>
				</div>
				<div class="sb-btn-wrapper clearfix">
					<div id="btn-add-question" class="btn-sb-add btn-sb">Add More</div>
					<div id="btn-create-survey" class="btn-sb-done btn-sb">Done</div>
				</div>


				<div class="sb-ques-wrapper">
					<div class="sb-item-row sb-item-row-header clearfix">
						<div class="float-left sb-q-item-no"></div>
						<div class="float-left sb-q-item-chk">
							<div class="sb-q-chk sb-q-chk-no sb-icn-pos-adj"></div>
							<div class="sb-q-chk sb-q-chk-yes sb-icn-pos-adj hide"></div>
						</div>
						<div class="float-left sb-q-item-txt text-center pos-relative">
							<span class="sb-q-header-txt">Survey Questions</span>
							<div class="sb-q-header-icons-rem">Remove</div>
						</div>
						<div class="float-right sb-q-item-btns blue-text cursor-pointer view-all-lnk">View All</div>
					</div>
					<div class="sb-item-row clearfix">
						<div class="float-left sb-q-item-no">(1)</div>
						<div class="float-left sb-q-item-chk">
							<div class="sb-q-chk sb-q-chk-no"></div>
							<div class="sb-q-chk sb-q-chk-yes hide"></div>
						</div>
						<div class="float-left sb-q-item-txt">
							<div class="sb-q-txt-1">Lorem ipsum dore it ler aun Lorem
								ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum
								dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it
								ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun
								Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem
								ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum
								dore it ler aun Lorem ipsum dore it ler aun</div>
							<div class="sb-q-txt-2 clearfix">
								<div class="float-left sb-stars icn-full-star"></div>
								<div class="float-left sb-stars icn-full-star"></div>
								<div class="float-left sb-stars icn-full-star"></div>
								<div class="float-left sb-stars icn-half-star"></div>
								<div class="float-left sb-stars icn-no-star"></div>
							</div>
						</div>
						<div class="float-right sb-q-item-btns clearfix">
							<div class="float-left sb-q-btn sb-btn-reorder"></div>
							<div class="float-left sb-q-btn sb-btn-delete"></div>
							<div class="float-left sb-q-btn sb-btn-edit"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<jsp:include page="scripts.jsp" />
<script>
	$(document).ready(function() {
		$(document).attr("title", "Build Survey");

		$('.sb-sel-item-range').click(function() {
			$('.sb-dd-wrapper').slideToggle(200);
		});

		$('.sb-sel-icn-inact').click(function() {
			$(this).hide();
			$(this).parent().find('.sb-sel-icn-act').show();
		});

		$('.sb-sel-icn-act').click(function() {
			$(this).hide();
			$(this).parent().find('.sb-sel-icn-inact').show();
		});

		$('.sb-q-chk-no').click(function() {
			$(this).hide();
			$(this).parent().find('.sb-q-chk-yes').show();
		});

		$('.sb-q-chk-yes').click(function() {
			$(this).hide();
			$(this).parent().find('.sb-q-chk-no').show();
		});
	});
</script>