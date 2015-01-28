<jsp:include page="header.jsp"/>

<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left">Customer Feedback Survey</div>
        </div>
    </div>
</div>


<div id="" class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
        <div class="sq-ques-wrapper">
            <div quest-no="1" class="sq-quest-item">
                <div class="sq-top-img"></div>
                <div class="sq-main-txt">lorema ipsum lorema ipsum lorema ipsum</div>
                <div class="sq-bord-bot-sm"></div>
                <div class="sq-ques">
                    <i><span class="sq-ques-txt">lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler.</span></i>
                </div>
                <div class="sq-rat-wrapper">
                    <div class="sq-star-wrapper clearfix">
                        <div star-no="1" class="sq-star"></div>
                        <div star-no="2" class="sq-star"></div>
                        <div star-no="3" class="sq-star"></div>
                        <div star-no="4" class="sq-star"></div>
                        <div star-no="5" class="sq-star"></div>
                    </div>
                </div>
                <div class="sq-skip-main">
                    <div class="sq-skip-wrapper clearfix">
                        <div class="float-left sq-skip-chk st-checkbox-on hide"></div>
                        <div class="float-left sq-skip-chk st-checkbox-off"></div>
                        <div class="float-left sq-skip-txt"><i>Skip this question</i></div>
                    </div>
                </div>
                <div class="sq-np-wrapper clearfix">
                    <div class="float-left sq-np-item sq-np-item-prev sq-np-item-disabled">&lt;&lt;&nbsp;&nbsp;&nbsp;Previous</div>
                    <div class="float-left sq-np-item sq-np-item-next">Next&nbsp;&nbsp;&nbsp;&gt;&gt;</div>
                </div>
                <div class="sq-btn-wrapper">
                    <div class="sq-btn-continue">Continue</div>
                </div>
            </div>
            <div quest-no="2" class="sq-quest-item hide">
                <div class="sq-top-img"></div>
                <div class="sq-main-txt">lorema ipsum lorema ipsum lorema ipsum</div>
                <div class="sq-bord-bot-sm"></div>
                <div class="sq-ques">
                    <i><span class="sq-ques-txt">lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler.</span></i>
                </div>
                <div class="sq-rat-wrapper">
                    <div class="sq-star-wrapper clearfix">
                        <div star-no="1" class="sq-star"></div>
                        <div star-no="2" class="sq-star"></div>
                        <div star-no="3" class="sq-star"></div>
                        <div star-no="4" class="sq-star"></div>
                        <div star-no="5" class="sq-star"></div>
                    </div>
                </div>
                <div class="sq-skip-main">
                    <div class="sq-skip-wrapper clearfix">
                        <div class="float-left sq-skip-chk st-checkbox-on hide"></div>
                        <div class="float-left sq-skip-chk st-checkbox-off"></div>
                        <div class="float-left sq-skip-txt"><i>Skip this question</i></div>
                    </div>
                </div>
                <div class="sq-np-wrapper clearfix">
                    <div class="float-left sq-np-item sq-np-item-prev">&lt;&lt;&nbsp;&nbsp;&nbsp;Previous</div>
                    <div class="float-left sq-np-item sq-np-item-next">Next&nbsp;&nbsp;&nbsp;&gt;&gt;</div>
                </div>
                <div class="sq-btn-wrapper">
                    <div class="sq-btn-continue">Continue</div>
                </div>
            </div>
            <div quest-no="3" class="sq-quest-item hide">
                <div class="sq-top-img"></div>
                <div class="sq-main-txt">lorema ipsum lorema ipsum lorema ipsum</div>
                <div class="sq-bord-bot-sm"></div>
                <div class="sq-ques">
                    <i><span class="sq-ques-txt">lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler. lorem ipsum dore it ler.</span></i>
                </div>
                <div class="sq-rat-wrapper">
                    <div class="sq-star-wrapper clearfix">
                        <div star-no="1" class="sq-star"></div>
                        <div star-no="2" class="sq-star"></div>
                        <div star-no="3" class="sq-star"></div>
                        <div star-no="4" class="sq-star"></div>
                        <div star-no="5" class="sq-star"></div>
                    </div>
                </div>
                <div class="sq-skip-main">
                    <div class="sq-skip-wrapper clearfix">
                        <div class="float-left sq-skip-chk st-checkbox-on hide"></div>
                        <div class="float-left sq-skip-chk st-checkbox-off"></div>
                        <div class="float-left sq-skip-txt"><i>Skip this question</i></div>
                    </div>
                </div>
                <div class="sq-np-wrapper clearfix">
                    <div class="float-left sq-np-item sq-np-item-prev">&lt;&lt;&nbsp;&nbsp;&nbsp;Previous</div>
                    <div class="float-left sq-np-item sq-np-item-next sq-np-item-disabled">Next&nbsp;&nbsp;&nbsp;&gt;&gt;</div>
                </div>
                <div class="sq-btn-wrapper">
                    <div class="sq-btn-continue">Continue</div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="scripts.jsp"/>

<script>
    $(document).ready(function(){
        
        $('.sq-star').click(function(){
            $(this).parent().find('.sq-star').removeClass('sq-full-star');
            var starVal = $(this).attr('star-no');
            $(this).parent().find('.sq-star').each(function( index ) {
                if(index < starVal){
                    $(this).addClass('sq-full-star');
                }
            });
        });
        
        $('.st-checkbox-on').click(function(){
            $(this).hide();
            $(this).parent().find('.st-checkbox-off').show();
        });
        
        $('.st-checkbox-off').click(function(){
            $(this).hide();
            $(this).parent().find('.st-checkbox-on').show();
        });
        
        $('.sq-np-item-next').click(function(){
            if(!$(this).hasClass('sq-np-item-disabled')){
                var qNo = $(this).parent().parent().attr('quest-no');
                var nextQ = parseInt(qNo) + 1;
                $(this).parent().parent().hide();
                $(this).parent().parent().parent().find('div[quest-no="'+nextQ+'"]').show();
            }
        });
        
        $('.sq-np-item-prev').click(function(){
            if(!$(this).hasClass('sq-np-item-disabled')){
                var qNo = $(this).parent().parent().attr('quest-no');
                var nextQ = parseInt(qNo) - 1;
                $(this).parent().parent().hide();
                $(this).parent().parent().parent().find('div[quest-no="'+nextQ+'"]').show();
            }
        });
        
    });
</script>

<jsp:include page="footer.jsp"/>