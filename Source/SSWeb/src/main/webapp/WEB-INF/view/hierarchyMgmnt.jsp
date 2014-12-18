<jsp:include page="header.jsp"/>

<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left">Build Your Company Hierarchy</div>
            <div class="float-right hm-header-row-right">EDIT COMPANY</div>
        </div>
    </div>
</div>

<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
        
        <div class="hm-content clearfix padding-001 hide">
            <div class="hm-top-panel">
                <div class="hm-top-panel-header">
                    <div class="hm-item-header padding-0150 clearfix">
                        <div class="hm-item-header-left float-left">Create Branch</div>
                        <div class="hm-item-header-right icn-plus float-right"></div>
                    </div>
                </div>
                <div class="hm-top-panel-content clearfix">
                    <div class="clearfix">
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                            <div class="hm-item-row clearfix">
                                <div class="hm-item-row-left text-right">Branch Name</div>
                                <div class="hm-item-row-right">
                                    <input type="text" class="hm-item-row-txt" placeholder="New Region">
                                </div>
                            </div>
                            <div class="hm-item-row clearfix">
                                <div class="hm-item-row-left text-right">Select Region</div>
                                <div class="hm-item-row-right z-in-9 pos-relative">
                                    <div class="hm-item-row-txt cursor-pointer" id="hm-item-dd"></div>
                                    <div class="hm-dd-wrapper hide" id="hm-dd-wrapper">
                                        <div class="hm-dd-item">Sample text</div>
                                        <div class="hm-dd-item">Sample text</div>
                                        <div class="hm-dd-item">Sample text</div>
                                        <div class="hm-dd-item">Sample text</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
                            <div class="hm-item-row clearfix">
                                <div class="hm-item-row-left text-right">Address Line1</div>
                                <div class="hm-item-row-right">
                                    <input type="text" class="hm-item-row-txt" placeholder="Address Line1">
                                </div>
                            </div>
                            <div class="hm-item-row clearfix">
                                <div class="hm-item-row-left text-right">Address Line2</div>
                                <div class="hm-item-row-right">
                                    <input type="text" class="hm-item-row-txt" placeholder="Address Line2">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="hm-btn-outer margin-bottom-25 margin-top-5 clearfix">
                        <div class="clearfix hm-btn-wrapper hm-btn-wrapper-fix margin-0-auto">
                            <div class="float-left add-curve-btn cursor-pointer">Add Branch Admin</div>
                            <div class="float-left icn-save cursor-pointer margin-right-0"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="hm-bottom-panel margin-bottom-25">
                <div class="hm-bottom-header">
                    <div class="hm-sub-header clearfix">
                        <div class="float-left hm-sub-header-left">Existing Region</div>
                        <div class="float-right">
                            <div class="clearfix hm-sub-search-wrapper">
                                <div class="float-left">
                                    <input class="hm-sub-search-txt" placeholder="Search Region"> 
                                </div>
                                <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="hm-bottom-panel-content clearfix">
                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-bottom-panel-item padding-right-30">
                        <div class="hm-sub-item clearfix">
                            <div class="float-left hm-sub-item-left">New Region Two</div>
                            <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                        </div>
                        <div class="hm-sub-item clearfix">
                            <div class="float-left hm-sub-item-left">New Region Two</div>
                            <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                        </div>
                        <div class="hm-sub-item clearfix">
                            <div class="float-left hm-sub-item-left">New Region Two</div>
                            <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                        </div>
                    </div>
                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-bottom-panel-item padding-left-30">
                        <div class="hm-sub-item clearfix">
                            <div class="float-left hm-sub-item-left">New Region Two</div>
                            <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                        </div>
                        <div class="hm-sub-item clearfix">
                            <div class="float-left hm-sub-item-left">New Region Two</div>
                            <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                        </div>
                        <div class="hm-sub-item clearfix">
                            <div class="float-left hm-sub-item-left">New Region Two</div>
                            <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="hm-content clearfix padding-001">
            <div class="float-left hm-content-left padding-right-0 col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <div class="hm-left-item">
                    <div class="hm-item-header clearfix">
                        <div class="hm-item-header-left float-left">Create Region</div>
                        <div class="hm-item-header-right icn-plus float-right"></div>
                    </div>
                    <div class="hm-item-content">
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-left text-right">New Region</div>
                            <div class="hm-item-row-right">
                                <input type="text" class="hm-item-row-txt" placeholder="New Region">
                            </div>
                        </div>
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-left text-right">Address Line1</div>
                            <div class="hm-item-row-right">
                                <input type="text" class="hm-item-row-txt" placeholder="Address Line1">
                            </div>
                        </div>
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-left text-right">Address Line2</div>
                            <div class="hm-item-row-right">
                                <input type="text" class="hm-item-row-txt" placeholder="Address Line2">
                            </div>
                        </div>
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-right">
                                <div class="clearfix hm-btn-wrapper">
                                    <div class="float-left add-curve-btn cursor-pointer">Add Region Admin</div>
                                    <div class="float-left icn-save cursor-pointer"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="hm-left-item hm-left-item-bottom no-border">
                    <div class="hm-sub-header clearfix">
                        <div class="float-left hm-sub-header-left">Existing Region</div>
                        <div class="float-right">
                            <div class="clearfix hm-sub-search-wrapper">
                                <div class="float-left">
                                    <input class="hm-sub-search-txt" placeholder="Search Region"> 
                                </div>
                                <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
                            </div>
                        </div>
                    </div>
                    <div class="hm-sub-item clearfix">
                        <div class="float-left hm-sub-item-left">New Region Two</div>
                        <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                    </div>
                    <div class="hm-sub-item clearfix">
                        <div class="float-left hm-sub-item-left">New Region Two</div>
                        <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                    </div>
                    <div class="hm-sub-item clearfix">
                        <div class="float-left hm-sub-item-left">New Region Two</div>
                        <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                    </div>
                </div>
            </div>
            
            <div class="float-left hm-content-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
                <div class="hm-right-item">
                    <div class="hm-item-header clearfix">
                        <div class="hm-item-header-left float-left">Create Branch</div>
                        <div class="hm-item-header-right icn-plus float-right"></div>
                    </div>
                    <div class="hm-item-content">
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-left text-right">Branch Name</div>
                            <div class="hm-item-row-right">
                                <input type="text" class="hm-item-row-txt" placeholder="New Region">
                            </div>
                        </div>
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-left text-right">Select Region</div>
                            <div class="hm-item-row-right pos-relative">
                                <div class="hm-item-row-txt cursor-pointer" id="hm-item-dd"></div>
                                <div class="hm-dd-wrapper hide" id="hm-dd-wrapper">
                                    <div class="hm-dd-item">Sample text</div>
                                    <div class="hm-dd-item">Sample text</div>
                                    <div class="hm-dd-item">Sample text</div>
                                    <div class="hm-dd-item">Sample text</div>
                                </div>
                            </div>
                        </div>
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-left text-right">Address Line1</div>
                            <div class="hm-item-row-right">
                                <input type="text" class="hm-item-row-txt" placeholder="Address Line1">
                            </div>
                        </div>
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-left text-right">Address Line2</div>
                            <div class="hm-item-row-right">
                                <input type="text" class="hm-item-row-txt" placeholder="Address Line2">
                            </div>
                        </div>
                        <div class="hm-item-row clearfix">
                            <div class="hm-item-row-right">
                                <div class="clearfix hm-btn-wrapper">
                                    <div class="float-left add-curve-btn cursor-pointer">Add Branch Admin</div>
                                    <div class="float-left icn-save cursor-pointer"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="hm-right-item hm-left-item-bottom no-border padding-left-25">
                    <div class="hm-sub-header clearfix">
                        <div class="float-left hm-sub-header-left">Existing Region</div>
                        <div class="float-right">
                            <div class="clearfix hm-sub-search-wrapper">
                                <div class="float-left">
                                    <input class="hm-sub-search-txt" placeholder="Search Region"> 
                                </div>
                                <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
                            </div>
                        </div>
                    </div>
                    <div class="hm-sub-item clearfix">
                        <div class="float-left hm-sub-item-left">New Region Two</div>
                        <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                    </div>
                    <div class="hm-sub-item clearfix">
                        <div class="float-left hm-sub-item-left">New Region Two</div>
                        <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                    </div>
                    <div class="hm-sub-item clearfix">
                        <div class="float-left hm-sub-item-left">New Region Two</div>
                        <div class="float-right icn-remove cursor-pointer hm-item-height-adjust"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<jsp:include page="footer.jsp"/>