<jsp:include page="header.jsp"/>

<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left">Build Your Customer Feedback</div>
<!--
            <a class="float-right hm-header-row-right hm-rt-btn-lnk" href="javascript:showMainContent('./showcompanysettings.do')">
            	EDIT COMPANY
            </a>
-->
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
                    <div id="btn-choose-survey" class="sb-tab-item">Choose Template</div>
                </div>
            </div>
            <div class="sb-content new-survery-content">
                <div class="sb-con-txt">Choose Template</div>
                <div class="clearfix sb-tem-select">
                    <div class="float-left clearfix sb-sel-item sb-sel-item-range pos-relative">
                        <div class="float-left sb-sel-icn sb-sel-icn-inact sb-sel-icn-inact-range"></div>
                        <div class="float-left sb-sel-icn sb-sel-icn-act sb-sel-icn-act-range hide"></div>
                        <div class="float-left sb-sel-txt sb-sel-item-range-txt">Range</div>
                        <div class="float-left sb-sel-icn-dd sb-sel-item-range-icn"></div>
                        <div class="sb-dd-wrapper blue-arrow-bot hide">
                            <div class="sb-icn-smiles sb-ratings-sel-item sb-dd-item blue-text">Smiles</div>
                            <div class="sb-icn-star sb-ratings-sel-item sb-dd-item">Star</div>
                            <div class="sb-icn-scale sb-ratings-sel-item sb-dd-item">Scale</div>
                        </div>
                    </div>
                    <div class="float-left clearfix sb-sel-item">
                        <div class="float-left sb-sel-icn sb-sel-icn-inact hide"></div>
                        <div class="float-left sb-sel-icn sb-sel-icn-act"></div>
                        <div class="float-left sb-sel-txt">Descriptive</div>
                    </div>
                    <div class="float-left clearfix sb-sel-item">
                        <div type="mcq" class="float-left sb-sel-icn sb-sel-icn-inact"></div>
                        <div type="mcq" class="float-left sb-sel-icn sb-sel-icn-act hide"></div>
                        <div class="float-left sb-sel-txt">Multiple Choice</div>
                    </div>
                </div>
                <div class="sb-txtarea-wrapper">
                    <textarea class="sb-txtarea" placeholder="Enter Question"></textarea>
                </div>
                <div id="sb-mcq-ans-wrapper" class="sb-txtarea-wrapper hide sb-mcq-ans-wrapper">
                    <div id="mcq-ans-container">
                        <input class="sb-inparea" placeholder="Enter option">
                        <input class="sb-inparea" placeholder="Enter option">
                    </div>
                    <div class="sb-btns-add-rem-wrapper clearfix">
                        <div class="sb-ad-rem-btn icn-sb-ad-btn float-right"></div>
                        <div class="sb-ad-rem-btn icn-sb-rem-btn hide float-right"></div>
                    </div>
                </div>
                
                
                <div class="sb-btn-wrapper clearfix">
                    <div class="btn-sb-add btn-sb">Add Question</div>
<!--                    <div class="btn-sb-done btn-sb">Done</div>-->
                </div>
                 
                <div class="sb-ques-wrapper">
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
                                <div class="sb-ans-mc-item q-ans-obj-4">Answer 4</div>
                                <div class="sb-ans-mc-item q-ans-obj-5">Answer 5</div>
                                <input class="q-ans-obj-txt q-ans-obj-1-txt">
                                <input class="q-ans-obj-txt q-ans-obj-2-txt">
                                <input class="q-ans-obj-txt q-ans-obj-3-txt">
                                <input class="q-ans-obj-txt q-ans-obj-4-txt">
                                <input class="q-ans-obj-txt q-ans-obj-5-txt">
                            </div> 
                        </div>
                        <div class="float-right sb-q-item-btns clearfix">
                            <div class="float-left sb-q-btn sb-btn-reorder-up"></div>
                            <div class="float-left sb-q-btn sb-btn-reorder-down"></div>
                            <div class="float-left sb-q-btn sb-btn-delete"></div>
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
                            <div class="float-left sb-q-btn sb-btn-edit"></div>
                            <div class="float-left sb-q-btn sb-btn-save hide"></div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="sb-content choose-survery-content hide">
                <div class="sb-ques-wrapper">
                    <div class="sb-item-row sb-item-row-header clearfix">
                        <div class="float-left sb-q-item-no"></div>
                        <div class="float-left sb-q-item-chk"></div>
                        <div class="float-left sb-q-item-txt text-center pos-relative">
                            <span class="sb-q-header-txt">Select Template</span>
                        </div>
<!--                        <div class="float-right sb-q-item-btns blue-text cursor-pointer view-all-lnk">View All</div>-->
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
                                            <div class="sb-ans-mc-item q-ans-obj-4">Answer 4</div>
                                            <div class="sb-ans-mc-item q-ans-obj-5">Answer 5</div>
                                            <input class="q-ans-obj-txt q-ans-obj-1-txt">
                                            <input class="q-ans-obj-txt q-ans-obj-2-txt">
                                            <input class="q-ans-obj-txt q-ans-obj-3-txt">
                                            <input class="q-ans-obj-txt q-ans-obj-4-txt">
                                            <input class="q-ans-obj-txt q-ans-obj-5-txt">
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

<jsp:include page="scripts.jsp"/>

<script>
    $(document).ready(function(){
        
        $('.sb-ct-exp').click(function(){
            $(this).hide();
            $('.sb-ct-close').show();
            $(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideDown(350);
        });
        
        $('.sb-ct-close').click(function(){
            $(this).hide();
            $('.sb-ct-exp').show();
            $(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideUp(350);
        });
        
        $('#btn-new-survey').click(function(){
            $('.sb-tab-item').removeClass('sb-tab-active');
            $(this).addClass('sb-tab-active');
            $('.sb-content').hide();
            $('.new-survery-content').show();
        });
        
        $('#btn-choose-survey').click(function(){
            $('.sb-tab-item').removeClass('sb-tab-active');
            $(this).addClass('sb-tab-active');
            $('.sb-content').hide();
            $('.choose-survery-content').show();
        });
        
        $('.sb-sel-item-range-txt, .sb-sel-item-range-icn').click(function(e){
            e.stopPropagation();
            $('.sb-dd-wrapper').slideToggle(200);
        });
        
        $('.sb-ratings-sel-item').click(function(e){
            e.stopPropagation();
            $('.sb-ratings-sel-item').removeClass('blue-text');
            $(this).addClass('blue-text');
            $('.sb-sel-icn-act').hide();
            $('.sb-sel-icn-inact').show();
            $('.sb-sel-icn-inact-range').hide();
            $('.sb-sel-icn-act-range').show();
        });
        
        $('.sb-sel-icn-inact').click(function(){
            $('.sb-sel-icn-act').hide();
            $('.sb-sel-icn-inact').show();
            $(this).hide();
            $(this).parent().find('.sb-sel-icn-act').show();
            if($(this).attr('type') == 'mcq'){
                $('.sb-mcq-ans-wrapper').show();
            }else{
                $('.sb-mcq-ans-wrapper').hide();
            }
        });
        
        $('.sb-sel-icn-act').click(function(){
//            $('.sb-sel-icn-act').hide();
//            $('.sb-sel-icn-inact').show();
//            $(this).hide();
//            $(this).parent().find('.sb-sel-icn-inact').show();
//            if($(this).attr('type') == 'mcq'){
//                $('.sb-mcq-ans-wrapper').hide();
//            }
        });

        $('.sb-q-chk-no').click(function(){
            $(this).hide();
            $(this).parent().find('.sb-q-chk-yes').show();
        });
        
        $('.sb-q-chk-yes').click(function(){
            $(this).hide();
            $(this).parent().find('.sb-q-chk-no').show();
        });
        
        var selectedRating = "";
        $('.sb-btn-edit').click(function(){
            $('.sb-ans-mc-wrapper').css('padding','0px');
            $(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html());
            
            if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "objective"){
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1').html());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2').html());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3').html());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4').html());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5').html());
                $('.sb-ans-mc-item').hide();
                $('.q-ans-obj-txt').show();
            }else if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "rating"){
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').show();
                $(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').hide();
            }
            
            $(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').hide();
            $(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').show();
            $(this).next('.sb-btn-save').show();
            $(this).parent().find('.sb-btn-delete').hide();
            $(this).parent().find('.sb-btn-cancel').show();
            $(this).hide();
        });
        
        $('.sb-btn-save').click(function(){
            $('.sb-ans-mc-wrapper').css('padding','0 10px');
            $(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html($(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val());
            
            if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "objective"){
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1-txt').val());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2-txt').val());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3-txt').val());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4-txt').val());
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5-txt').val());
                $('.sb-ans-mc-item').show();
                $('.q-ans-obj-txt').hide();
            }else if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "rating"){
                $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').hide();
                $(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').show();
            }
            
            if(selectedRating == "smiles"){
                $('.sb-q-txt-2').find('.sb-stars').hide();
                $('.sb-q-txt-2').find('.sb-icn-smiles').show();
            }else if(selectedRating == "star"){
                $('.sb-q-txt-2').find('.sb-stars').hide();
                $('.sb-q-txt-2').find('.icn-full-star').show();
            }else if(selectedRating == "scale"){
                $('.sb-q-txt-2').find('.sb-stars').hide();
                $('.sb-q-txt-2').find('.sb-icn-scale').show();
            }
            
            $(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').show();
            $(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').hide();
            $(this).prev('.sb-btn-edit').show();
            $(this).parent().find('.sb-btn-delete').show();
            $(this).parent().find('.sb-btn-cancel').hide();
            $(this).hide();
        });
        
        $('.sb-btn-cancel').click(function(){
//            $(this).parent().find('.sb-btn-delete').show();
//            $(this).hide();
            $(this).parent().find('.sb-btn-save').click();
        });
        
        $('body').on('click','.sb-dd-item-ans',function(){
            selectedRating = $(this).attr('type');
            $('.sb-dd-item-ans').removeClass('blue-text');
            $(this).addClass('blue-text');
        });
        
        $('body').click(function(){
            if($('.sb-dd-wrapper').css('display') == "block"){
                $('.sb-dd-wrapper').slideToggle(200);
            }
        });
        
//        Adding new option for MCQ
        $('.icn-sb-ad-btn').click(function(){
            $('#mcq-ans-container').append('<input class="sb-inparea" placeholder="Enter option">');
            var choiceLen = $('.sb-inparea').length;
            if(choiceLen > 2){
                $('.icn-sb-rem-btn').show();
            }
        });
        
        $('.icn-sb-rem-btn').click(function(){
            var choiceLen = $('.sb-inparea').length;
            if(choiceLen > 2){
                $('.sb-inparea').each(function(){
                    if($('.sb-inparea').index(this) > choiceLen - 2){
                        $(this).remove();
                    }
                });
            }
        });
        
    });
</script>

<jsp:include page="footer.jsp"/>