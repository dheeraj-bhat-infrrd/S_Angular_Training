<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left hr-dsh-adj-lft">Dashboard</div>
            <div class="float-right header-right clearfix hr-dsh-adj-rt">
                <div class="float-left hr-txt1">View As</div>
                <div id="hr-txt2" class="float-left hr-txt2">Agent</div>
                <div id="hr-dd-wrapper" class="hr-dd-wrapper hide">
                    <div class="hr-dd-item">Temp</div>
                    <div class="hr-dd-item">Temp</div>
                    <div class="hr-dd-item">Temp</div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="dash-wrapper-main">
    <div class="dash-container container">
        
        <div class="dash-top-info">
            <div class="row row-dash-top-adj">
                <div class="float-right dash-main-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
                    <div class="dsh-graph-wrapper">
                        <div class="dsh-g-wrap dsh-g-wrap-1">
                            <div class="dsh-graph-item dsh-graph-item-1">
                                <div id="dg-img-1" class="dsh-graph-img"></div>
                                <div id="" class="dsh-graph-num">400</div>
                                <div id="" class="dsh-graph-txt dsh-graph-txt-1">Social Post In Last 30 Days</div>
                            </div>
                        </div>
                        <div class="dsh-g-wrap dsh-g-wrap-2">
                            <div class="dsh-graph-item dsh-graph-item-1">
                                <div id="dg-img-2" class="dsh-graph-img"></div>
                                <div id="" class="dsh-graph-num">3000</div>
                                <div id="" class="dsh-graph-txt dsh-graph-txt-2">Total Surveys Sent</div>
                            </div>
                        </div>
                        <div class="dsh-g-wrap dsh-g-wrap-3">
                            <div class="dsh-graph-item dsh-graph-item-1">
                                <div id="dg-img-3" class="dsh-graph-img"></div>
                                <div id="" class="dsh-graph-num">4/5</div>
                                <div id="" class="dsh-graph-txt dsh-graph-txt-3">Survey Score Over Last 30 Days</div>
                            </div>
                        </div>
                        <div class="dsh-g-wrap dsh-g-wrap-4">
                            <div class="dsh-graph-item dsh-graph-item-1">
                                <div id="dg-img-4" class="dsh-graph-img dsh-graph-img-4"></div>
                                <div id="" class="dsh-graph-num dsh-graph-num-4 person-img"></div>
                                <div id="" class="dsh-graph-txt dsh-graph-txt-4">Profile Completed</div>
                                <div class="dsg-g-rbn"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="float-left dash-main-left col-lg-6 col-md-6 col-sm-6 col-xs-12">
                    <div class="dash-left-txt-wrapper">
                        <div class="dsh-name-wrapper">
                            <div class="dsh-txt-1">Anna Thomas</div>
                            <div class="dsh-txt-2">Managing Broker at</div>
                            <div class="dsh-txt-3">Prudential Utah Elite Real Estate</div>
                        </div>
                        <div class="dsh-star-wrapper clearfix">
                            <div class="float-left dsh-star-item sq-full-star"></div>
                            <div class="float-left dsh-star-item sq-full-star"></div>
                            <div class="float-left dsh-star-item sq-full-star"></div>
                            <div class="float-left dsh-star-item no-star"></div>
                            <div class="float-left dsh-star-item no-star"></div>
                            <div class="float-right dsh-rating-item">3/5</div>
                        </div>
                        <div class="dsh-btn-complete">Complete Your Profile</div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="dash-stats-wrapper bord-bot-dc clearfix">
            <div class="float-left stats-left clearfix">
                <div class="dash-sub-head">Survey Status</div>
                <div class="clearfix dash-sel-wrapper">
                    <div class="float-left dash-sel-lbl">Choose</div>
                    <select class="float-left dash-sel-item">
                        <option value="volvo">Region</option>
                        <option value="saab">Saab</option>
                        <option value="mercedes">Mercedes</option>
                        <option value="audi">Audi</option>
                    </select>
                </div>
                <div class="clearfix dash-sel-wrapper">
                    <div class="float-left dash-sel-lbl">Duration</div>
                    <select class="float-left dash-sel-item">
                        <option value="volvo">30 Days</option>
                        <option value="saab">Saab</option>
                        <option value="mercedes">Mercedes</option>
                        <option value="audi">Audi</option>
                    </select>
                </div>
            </div>
            <div class="float-left stats-right">
                <div class="clearfix stat-icns-wrapper">
                    <div class="float-left stat-icn-lbl">No. of surveys sent</div>
                    <div class="float-left stat-icns-item clearfix">
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-img stat-icn-img-green"></div>
                        <div class="float-left stat-icn-txt-rt">1K</div>
                    </div>
                </div>
                <div class="clearfix stat-icns-wrapper">
                    <div class="float-left stat-icn-lbl">No. of surveys clicked</div>
                    <div class="float-left stat-icns-item clearfix">
                        <div class="float-left stat-icn-img stat-icn-img-blue"></div>
                        <div class="float-left stat-icn-img stat-icn-img-blue"></div>
                        <div class="float-left stat-icn-img stat-icn-img-blue"></div>
                        <div class="float-left stat-icn-img stat-icn-img-blue"></div>
                        <div class="float-left stat-icn-img stat-icn-img-blue"></div>
                        <div class="float-left stat-icn-img stat-icn-img-blue"></div>
                        <div class="float-left stat-icn-img stat-icn-img-blue"></div>
                        <div class="float-left stat-icn-txt-rt">70%</div>
                    </div>
                </div>
                <div class="clearfix stat-icns-wrapper">
                    <div class="float-left stat-icn-lbl">No. of surveys completed</div>
                    <div class="float-left stat-icns-item clearfix">
                        <div class="float-left stat-icn-img stat-icn-img-yellow"></div>
                        <div class="float-left stat-icn-img stat-icn-img-yellow"></div>
                        <div class="float-left stat-icn-img stat-icn-img-yellow"></div>
                        <div class="float-left stat-icn-img stat-icn-img-yellow"></div>
                        <div class="float-left stat-icn-img stat-icn-img-yellow"></div>
                        <div class="float-left stat-icn-img stat-icn-img-yellow"></div>
                        <div class="float-left stat-icn-txt-rt">85%</div>
                    </div>
                </div>
                <div class="clearfix stat-icns-wrapper">
                    <div class="float-left stat-icn-lbl">No. of social posts</div>
                    <div class="float-left stat-icns-item clearfix">
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-img stat-icn-img-red"></div>
                        <div class="float-left stat-icn-txt-rt">80%</div>
                    </div>
                </div>
<!--
                <div class="clearfix stat-icns-wrapper">
                    <div class="float-left stat-icn-lbl">No. of social posts</div>
                    <div class="float-left stat-icns-item clearfix">
                        <div class="progress">
                            <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 40%">
                                <span class="sr-only">40% Complete (success)</span>
                            </div>
                        </div>
                        <div class="float-left stat-icn-txt-rt">80%</div>
                    </div>
                </div>
-->
            </div>
        </div>
        
        <div class="dash-stats-wrapper bord-bot-dc clearfix">
            <div class="float-left stats-left clearfix">
                <div class="dash-sub-head">Utilization over time</div>
                <div class="clearfix dash-sel-wrapper">
                    <div class="float-left dash-sel-lbl">Choose</div>
                    <select class="float-left dash-sel-item">
                        <option value="volvo">Region</option>
                        <option value="saab">Saab</option>
                        <option value="mercedes">Mercedes</option>
                        <option value="audi">Audi</option>
                    </select>
                </div>
                <div class="clearfix dash-sel-wrapper">
                    <div class="float-left dash-sel-lbl">Duration</div>
                    <select class="float-left dash-sel-item">
                        <option value="volvo">20 Days</option>
                        <option value="saab">Saab</option>
                        <option value="mercedes">Mercedes</option>
                        <option value="audi">Audi</option>
                    </select>
                </div>
            </div>
            <div class="float-left stats-right stats-right-adj">
                <div class="util-graph-wrapper">
                    <div id="util-gph-item" class="util-gph-item">
                    </div>
                    <div class="util-gph-legend clearfix">
                        <div class="util-gph-legend-item">No of surveys sent<span class="lgn-col-item lgn-col-grn"></span></div>
                        <div class="util-gph-legend-item">No of surveys clicked<span class="lgn-col-item lgn-col-blue"></span></div>
                        <div class="util-gph-legend-item">No of surveys completed<span class="lgn-col-item lgn-col-yel"></span></div>
                        <div class="util-gph-legend-item">No of social posts<span class="lgn-col-item lgn-col-red"></span></div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="dash-panels-wrapper">
            <div class="row">
                <div class="dash-panel-left col-lg-4 col-md-4 col-sm-4 col-xs-12">
                    <div class="dash-lp-header">Incomplete Surveys</div>
                    <div class="dash-lp-item clearfix">
                        <div class="float-left dash-lp-txt">Divina Couture <span>12th Dec</span></div>
                        <div class="float-right dash-lp-rt-img"></div>
                    </div>
                    <div class="dash-lp-item clearfix">
                        <div class="float-left dash-lp-txt">tam Fedrick <span>12th Dec</span></div>
                        <div class="float-right dash-lp-rt-img"></div>
                    </div>
                    <div class="dash-lp-item clearfix">
                        <div class="float-left dash-lp-txt">Jerome Mickelson <span>12th Dec</span></div>
                        <div class="float-right dash-lp-rt-img"></div>
                    </div>
                    <div class="dash-lp-item clearfix">
                        <div class="float-left dash-lp-txt">Lisbeth Barkan <span>12th Dec</span></div>
                        <div class="float-right dash-lp-rt-img"></div>
                    </div>
                    <div class="dash-lp-item clearfix">
                        <div class="float-left dash-lp-txt">Al Chowdhury <span>12th Dec</span></div>
                        <div class="float-right dash-lp-rt-img"></div>
                    </div>
                    <div class="dash-btn-sur-data">Incomplete Survery Data</div>
                </div>
                <div class="dash-panel-right col-lg-8 col-md-8 col-sm-8 col-xs-12 resp-adj">
                    <div class="people-say-wrapper rt-content-main rt-content-main-adj">
                        <div class="main-con-header clearfix pad-bot-10-resp">
                            <div class="float-left dash-ppl-say-lbl"><span class="ppl-say-txt-st">What people say</span> about Anna Thomas</div>
                            <div class="float-right dash-btn-dl-sd">Download Survery Data</div>
                        </div>
                        <div class="ppl-review-item">
                            <div class="ppl-header-wrapper clearfix">
                                <div class="float-left ppl-header-left">
                                    <div class="ppl-head-1">Matt &amp; Gina Conelly - Lehi, UT</div>
                                    <div class="ppl-head-2">12<sup>th</sup> Sept 2014</div>
                                </div>
                                <div class="float-right ppl-header-right">
                                    <div class="st-rating-wrapper maring-0 clearfix">
                                        <div class="rating-star icn-full-star"></div>
                                        <div class="rating-star icn-full-star"></div>
                                        <div class="rating-star icn-half-star"></div>
                                        <div class="rating-star icn-no-star"></div>
                                        <div class="rating-star icn-no-star"></div>
                                    </div>
                                </div>
                            </div>
                            <div class="ppl-content">Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las. Anna is a managing broker at Sntiner lorenm ipsim dore et ie las.</div>
                            <div class="ppl-share-wrapper clearfix">
                                <div class="float-left blue-text ppl-share-shr-txt">Share</div>
                                <div class="float-left icn-share icn-plus-open" style="display: block;"></div>
                                <div class="float-left clearfix ppl-share-social hide" style="display: none;">
                                    <div class="float-left ppl-share-icns icn-fb"></div>
                                    <div class="float-left ppl-share-icns icn-twit"></div>
                                    <div class="float-left ppl-share-icns icn-lin"></div>
                                    <div class="float-left ppl-share-icns icn-yelp"></div>
                                </div>
                                <div class="float-left icn-share icn-remove icn-rem-size hide" style="display: none;"></div>
                            </div>
                        </div>
                        <div class="profile-addl-links clearfix">
                            <span class="p-a-l-item">100 additional reviews not recommended</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
    </div>
</div>

<!-- <jsp:include page="scripts.jsp"/> -->

<script>
    
    window.onload = function onLoad() {
        
        
        
    };
    
    $(document).ready(function(){
    	$(document).attr("title", "Dashboard"); 
        
        $('.icn-plus-open').click(function(){
            $(this).hide();
            $(this).parent().find('.ppl-share-social,.icn-remove').show();
        });
        
        $('.icn-remove').click(function(){
            $(this).hide();
            $(this).parent().find('.ppl-share-social').hide();
            $(this).parent().find('.icn-plus-open').show();
        });
        
        $('#hr-txt2').click(function(e){
            e.stopPropagation();
            $('#hr-dd-wrapper').slideToggle(200);
        });
        
        $('.hr-dd-item').click(function(e){
            e.stopPropagation();
        });
        
        $('body').click(function(){
            $('#hr-dd-wrapper').slideUp(200);
        });
        
        var circle1 = new ProgressBar.Circle('#dg-img-1', {
            color: '#7AB400',
            fill: "rgba(249,249,251, 1)",
            duration: 1500,
            strokeWidth: 4,
            easing: 'easeInOut'
        });

        circle1.animate(.7);
        
        var circle2 = new ProgressBar.Circle('#dg-img-2', {
            color: '#E97F30',
            fill: "rgba(249,249,251, 1)",
            duration: 1500,
            strokeWidth: 4,
            easing: 'easeInOut'
        });

        circle2.animate(.7);
        
        var circle3 = new ProgressBar.Circle('#dg-img-3', {
            color: '#5CC7EF',
            fill: "rgba(249,249,251, 1)",
            duration: 1500,
            strokeWidth: 4,
            easing: 'easeInOut'
        });

        circle3.animate(.7);
        
        var circle4 = new ProgressBar.Circle('#dg-img-4', {
            color: '#7AB400',
            fill: "rgba(249,249,251, 1)",
            duration: 1500,
            strokeWidth: 4,
            easing: 'easeInOut'
        });
        
        circle4.animate(.7);
        
        
        // Graph
//        google.setOnLoadCallback(drawChart);

        drawChart();
        
        function drawChart() {
            var data = google.visualization.arrayToDataTable([
                ['Month', 'No. of surveys sent', 'No. of surveys clicked', 'No. of surveys completed','No. of social posts'],
                ['Jan', 500, 200, 150, 0],
                ['Feb', 550, 200, 200, 50],
                ['Mar', 540, 220, 180, 80],
                ['Apr', 660, 300, 400, 120],
                ['May', 600, 400, 300, 200],
                ['Jun', 560, 500, 100, 230],
                ['Jul', 640, 300, 450, 420],
                ['Aug', 630, 250, 340, 350],
                ['Sep', 560, 450, 270, 500],
                ['Oct', 460, 500, 300, 400],
                ['Nov', 690, 100, 500, 300],
                ['Dec', 700, 200, 400, 540]
            ]);

            var options = {
//                title: 'Company Performance',
                chartArea:{width:'90%',height:'80%'},
                colors:['rgb(28,242,0)','rgb(0,174,239)','rgb(255,242,0)','rgb(255,202,145)'],
                legend: { position: 'none' }
            };

            var chart = new google.visualization.LineChart(document.getElementById('util-gph-item'));

            chart.draw(data, options);
            console.log('draw chart mtd');
        }
        
        var oldConW = $('.container').width();
        var newConW = $('.container').width();
        $(window).resize(function(){
            newConW = $('.container').width();
            if(newConW != oldConW){
                drawChart();
                oldConW = $('.container').width();
            }
        });
        
    });
</script>

