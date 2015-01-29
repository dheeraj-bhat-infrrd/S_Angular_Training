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
					<div id="btn-new-survey" class="sb-tab-item sb-tab-active">New Survey</div>
					<div id="btn-choose-template" class="sb-tab-item">Choose Template</div>
				</div>
			</div>
			<div class="sb-content new-survery-content">
				<div class="sb-con-txt">Choose Question Type</div>
				<div class="clearfix sb-tem-select">
					<div class="float-left clearfix sb-sel-item sb-sel-item-range pos-relative">
						<div id="sb-range" class="float-left sb-sel-icn sb-sel-icn-inact sb-sel-icn-inact-range"></div>
						<div class="float-left sb-sel-icn sb-sel-icn-act sb-sel-icn-act-range hide"></div>
						<div class="float-left sb-sel-txt sb-sel-item-range-txt">Range</div>
						<div class="float-left sb-sel-icn-dd sb-sel-item-range-icn"></div>
						<div class="sb-dd-wrapper blue-arrow-bot hide">
							<div id="sb-range-smiles" class="sb-icn-smiles sb-ratings-sel-item sb-dd-item blue-text">Smiles</div>
							<div id="sb-range-star" class="sb-icn-star sb-ratings-sel-item sb-dd-item">Star</div>
							<div id="sb-range-scale" class="sb-icn-scale sb-ratings-sel-item sb-dd-item">Scale</div>
						</div>
					</div>
					<div class="float-left clearfix sb-sel-item">
						<div id="sb-sel-desc" class="float-left sb-sel-icn sb-sel-icn-inact hide"></div>
						<div class="float-left sb-sel-icn sb-sel-icn-act"></div>
						<div class="float-left sb-sel-txt">Descriptive</div>
					</div>
					<div class="float-left clearfix sb-sel-item">
						<div type="mcq" id="sb-sel-mcq" class="float-left sb-sel-icn sb-sel-icn-inact"></div>
						<div type="mcq" class="float-left sb-sel-icn sb-sel-icn-act hide"></div>
						<div class="float-left sb-sel-txt">Multiple Choice</div>
					</div>
				</div>
				<form id="sb-new-question-form">
					<div class="sb-txtarea-wrapper">
						<input type="hidden" id="sb-question-type" name="sb-question-type" value="sb-sel-desc"/>
						<textarea id="sb-question-txt" name="sb-question-txt" class="sb-txtarea" placeholder="Enter Question"></textarea>
					</div>
					<div id="sb-mcq-ans-wrapper" class="sb-txtarea-wrapper hide sb-mcq-ans-wrapper">
						<div id="mcq-ans-container">
							<input name="sb-answers[]" class="sb-inparea" placeholder="Enter option">
							<input name="sb-answers[]" class="sb-inparea" placeholder="Enter option">
						</div>
						<div class="sb-btns-add-rem-wrapper clearfix">
							<div class="sb-ad-rem-btn icn-sb-ad-btn float-right"></div>
							<div class="sb-ad-rem-btn icn-sb-rem-btn hide float-right"></div>
						</div>
					</div>
					<div class="sb-btn-wrapper clearfix">
						<div id="sb-question-add" class="btn-sb-add btn-sb">Add Question</div>
					</div>
				</form>
				
				<div id="sb-ques-wrapper" class="sb-ques-wrapper">
					<div class="sb-quests-error"></div>
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
							<div class="sb-q-txt-1" q-type="rating">Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun </div>
							<textarea class="sb-q-txt-1 sb-txt-ar"></textarea>
							<div class="sb-q-txt-2 clearfix">
								<div class="float-left sb-stars icn-full-star"></div>
								<div class="float-left sb-stars sb-icn-smiles hide"></div>
								<div class="float-left sb-stars sb-icn-scale hide"></div>
							</div>
							<div class="sb-ans-rat-wrapper">
								<div class="sb-dd-wrapper-or">
									<div type="smiles" class="sb-icn-smiles sb-dd-item sb-dd-item-or sb-dd-item-ans blue-text">Smiles</div>
									<div type="star" class="sb-icn-star sb-dd-item sb-dd-item-or sb-dd-item-ans">Star</div>
									<div type="scale" class="sb-icn-scale sb-dd-item sb-dd-item-or sb-dd-item-ans">Scale</div>
								</div>
							</div>
						</div>
						<div class="float-right sb-q-item-btns clearfix">
							<div class="float-left sb-q-btn sb-btn-reorder-up"></div>
							<div class="float-left sb-q-btn sb-btn-reorder-down"></div>
							<div class="float-left sb-q-btn sb-btn-delete"></div>
							<div class="float-left sb-q-btn sb-btn-cancel hide"></div>
							<div class="float-left sb-q-btn sb-btn-edit"></div>
							<div class="float-left sb-q-btn sb-btn-save hide"></div>
						</div>
					</div>
					<div class="sb-item-row clearfix">
						<div class="float-left sb-q-item-no">(1)</div>
						<div class="float-left sb-q-item-chk">
							<div class="sb-q-chk sb-q-chk-no"></div>
							<div class="sb-q-chk sb-q-chk-yes hide"></div>
						</div>
						<div class="float-left sb-q-item-txt">
							<div class="sb-q-txt-1" q-type="objective">Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun </div>
							<textarea class="sb-q-txt-1 sb-txt-ar"></textarea>
							<div class="sb-ans-mc-wrapper">
								<div class="sb-ans-mc-item q-ans-obj-1">Answer 1</div>
								<div class="sb-ans-mc-item q-ans-obj-2">Answer 2</div>
								<div class="sb-ans-mc-item q-ans-obj-3">Answer 3</div>
								<input class="q-ans-obj-txt q-ans-obj-1-txt">
								<input class="q-ans-obj-txt q-ans-obj-2-txt">
								<input class="q-ans-obj-txt q-ans-obj-3-txt">
							</div> 
						</div>
						<div class="float-right sb-q-item-btns clearfix">
							<div class="float-left sb-q-btn sb-btn-reorder-up"></div>
							<div class="float-left sb-q-btn sb-btn-reorder-down"></div>
							<div class="float-left sb-q-btn sb-btn-delete"></div>
							<div class="float-left sb-q-btn sb-btn-cancel hide"></div>
							<div class="float-left sb-q-btn sb-btn-edit"></div>
							<div class="float-left sb-q-btn sb-btn-save hide"></div>
						</div>
					</div>
					<div class="sb-item-row clearfix">
						<div class="float-left sb-q-item-no">(1)</div>
						<div class="float-left sb-q-item-chk">
							<div class="sb-q-chk sb-q-chk-no"></div>
							<div class="sb-q-chk sb-q-chk-yes hide"></div>
						</div>
						<div class="float-left sb-q-item-txt">
							<div class="sb-q-txt-1" q-type="descriptive">Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun </div>
							<textarea class="sb-q-txt-1 sb-txt-ar"></textarea>
						</div>
						<div class="float-right sb-q-item-btns clearfix">
							<div class="float-left sb-q-btn sb-btn-reorder-up"></div>
							<div class="float-left sb-q-btn sb-btn-reorder-down"></div>
							<div class="float-left sb-q-btn sb-btn-delete"></div>
							<div class="float-left sb-q-btn sb-btn-cancel hide"></div>
							<div class="float-left sb-q-btn sb-btn-edit"></div>
							<div class="float-left sb-q-btn sb-btn-save hide"></div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="sb-content choose-survery-content hide">
				<div id="sb-template-wrapper" class="sb-ques-wrapper">
					<div class="sb-item-row sb-item-row-header clearfix">
						<div class="float-left sb-q-item-no"></div>
						<div class="float-left sb-q-item-chk"></div>
						<div class="float-left sb-q-item-txt text-center pos-relative">
							<span class="sb-q-header-txt">Select Template</span>
						</div>
<!--						<div class="float-right sb-q-item-btns blue-text cursor-pointer view-all-lnk">View All</div>-->
					</div>
					<div class="sb-item-row clearfix">
						<div class="float-left sb-q-item-no">(1)</div>
						<div class="float-left sb-q-item-txt sb-q-item-txt-or">
							<div class="sb-q-txt-1" q-type="rating">Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun </div>
							<div class="sb-template-q-wrapper hide">
								<ul class="sb-ul">
									<li class="sb-q-template-item">
										<div class="sb-q-txt-1" q-type="objective">Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun </div>
										<div class="sb-ans-mc-wrapper">
											<div class="sb-ans-mc-item q-ans-obj-1">Answer 1</div>
											<div class="sb-ans-mc-item q-ans-obj-2">Answer 2</div>
											<div class="sb-ans-mc-item q-ans-obj-3">Answer 3</div>
										</div> 
									</li>
									<li class="sb-q-template-item">
										<div class="sb-q-txt-1" q-type="rating">Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun Lorem ipsum dore it ler aun </div>
										<div class="sb-q-txt-2 clearfix">
											<div class="float-left sb-stars icn-full-star"></div>
											<div class="float-left sb-stars sb-icn-smiles hide"></div>
											<div class="float-left sb-stars sb-icn-scale hide"></div>
										</div>
									</li>
								</ul>
								<div class="sb-btn-choose">Copy Template</div>
							</div>
						</div>
						<div class="float-left sb-q-item-chk">
							<div class="sb-ct-exp cursor-pointer"></div>
							<div class="sb-ct-close cursor-pointer hide"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function(){
	$(document).attr("title", "Build Survey");
	
	$('#sb-ques-wrapper').html('');
	$('#sb-template-wrapper').html('');
	loadActiveSurvey();
});
</script>