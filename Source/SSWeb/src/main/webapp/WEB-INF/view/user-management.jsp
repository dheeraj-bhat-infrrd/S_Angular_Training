<div class="hm-header-main-wrapper">
    <div class="container">
        <div class="hm-header-row clearfix">
            <div class="float-left hm-header-row-left">User Management</div>
        </div>
    </div>
</div>
<div id="hm-main-content-wrapper" class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
    <div class="container">
        <div class="um-top-container">
            <div class="um-header">User Details</div>
            <div class="clearfix um-panel-content">
                <div class="row">
                    <div class="um-top-row cleafix">
                        <div class="clearfix um-top-form-wrapper">
                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
                                <div class="hm-item-row clearfix">
                                    <div class="um-item-row-left text-right">Frist Name</div>
                                    <div class="um-item-row-icon"></div>
                                    <div class="hm-item-row-right um-item-row-right">
                                        <input type="text" class="um-item-row-txt" placeholder="First Name">
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
                                <div class="hm-item-row clearfix">
                                    <div class="um-item-row-left text-right">Last Name</div>
                                    <div class="um-item-row-icon"></div>
                                    <div class="hm-item-row-right um-item-row-right">
                                        <input type="text" class="um-item-row-txt" placeholder="Last Name">
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
                                <div class="hm-item-row clearfix">
                                    <div class="um-item-row-left text-right">Email ID</div>
                                    <div class="um-item-row-icon icn-tick"></div>
                                    <div class="hm-item-row-right um-item-row-right">
                                        <input type="text" class="um-item-row-txt" placeholder="Email ID">
                                    </div>
                                </div>
                                <div class="hm-item-row clearfix">
                                    <div class="um-item-row-left text-right">Assign To</div>
                                    <div class="um-item-row-icon icn-tick"></div>
                                    <div class="hm-item-row-right um-item-row-right">
                                        <input type="text" class="um-item-row-txt" placeholder="Assign To">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="clearfix um-top-tag-wrapper margin-bottom-25">
                            <div class="um-tag-row clearfix">
                                <div class="um-tag-item-wrapper clearfix float-left">
                                    <div class="um-tag-item-txt float-left">Branch 1</div>
                                    <div class="um-tag-item-icn float-left"></div>
                                </div>
                                <div class="um-tag-item-wrapper clearfix float-left">
                                    <div class="um-tag-item-txt float-left">Branch 2</div>
                                    <div class="um-tag-item-icn float-left"></div>
                                </div>
                            </div>
                        </div>
                        <div class="clearfix um-top-status-wrapper margin-bottom-25">
                            <div class="um-top-status-title float-left">Status</div>
                            <div class="um-top-status-text float-left">Active</div>
                            <div id="icn-status-green" class="hide um-top-status-text um-status-icon icn-status-green float-left"></div>
                            <div id="icn-status-red" class="um-top-status-text um-status-icon icn-status-red float-left"></div>
                            <div class="um-top-status-text um-status-icon icn-person-remove float-left"></div>
                        </div>
                    </div>
                    <div class="um-bottom-row cleafix">
                        
                    </div>
                </div>
            </div>
        </div>
        <div class="um-bottom-container">
            <div class="um-header">Browse Users</div>
            <div class="clearfix um-panel-content um-bottom-content">
                <table class="um-table">
                    <thead>
                        <tr>
                            <td class="col-username">Username</td>
                            <td class="col-email">Email ID</td>
                            <td class="col-loanoff">Loan Officer</td>
                            <td class="col-status"></td>
                            <td class="col-remove"></td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="col-username um-table-content">john doe</td>
                            <td class="col-email um-table-content">john@doe.com</td>
                            <td class="col-loanoff um-table-content clearfix">
                                <div class="float-left tm-table-tick-icn icn-right-tick"></div>
                            </td>
                            <td class="col-status um-table-content clearfix">
                                <div class="tm-table-status-icn icn-green-col float-left"></div>
                                <div class="tm-table-status-icn icn-green-brown float-left hide"></div>
                            </td>
                            <td class="col-remove um-table-content clearfix">
                                <div class="tm-table-remove-icn icn-remove-user float-left"></div>
                            </td>
                        </tr>
                    </tbody>
                </table>    
            </div>
        </div>
    </div>
</div>
<script>
    $(document).ready(function(){
    	$(document).attr("title", "User Management");
    });
</script>