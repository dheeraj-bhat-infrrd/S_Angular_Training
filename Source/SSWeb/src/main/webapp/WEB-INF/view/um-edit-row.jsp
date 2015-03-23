
    <div class="v-um-edit-wrapper clearfix">
        <div class="v-edit-lft col-lg-6 col-md-6 col-sm-6 col-xs-12">
            <div class="v-edit-row clearfix">
                <div class="float-left v-ed-lbl">User Name</div>
                <div class="float-left v-ed-txt-sm">
                    <input class="v-ed-txt-item" placeholder="First Name" value="${firstName}">
                </div>
                <div class="float-left v-ed-txt-sm v-ed-txt-sm-adj">
                    <input class="v-ed-txt-item" placeholder="Last Name" value="${lastName}">
                </div>
            </div>
            <div class="v-edit-row clearfix">
                <div class="float-left v-ed-lbl">Email Address</div>
                <div class="float-left v-ed-txt">
                    <input class="v-ed-txt-item" placeholder="Email Address" value="${emailId}">
                </div>
            </div>
            <div class="v-edit-row clearfix">
                <div class="float-left v-ed-lbl">Assign To</div>
                <div class="float-left v-ed-txt pos-relative">
                    <input class="v-ed-txt-item v-ed-txt-dd" placeholder="Email Address">
                    <div class="clearfix hide v-ed-dd-wrapper">
                        <div class="clearfix v-ed-dd-item">One</div>
                        <div class="clearfix v-ed-dd-item">Two</div>
                        <div class="clearfix v-ed-dd-item">Three</div>
                    </div>
                </div>
            </div>
            <div class="v-edit-row clearfix">
                <div class="float-left v-ed-lbl">Select Office</div>
                <div class="float-left v-ed-txt pos-relative">
                    <input class="v-ed-txt-item v-ed-txt-dd" placeholder="">
                    <div class="clearfix hide v-ed-dd-wrapper">
                        <div class="clearfix v-ed-dd-item">One</div>
                        <div class="clearfix v-ed-dd-item">Two</div>
                        <div class="clearfix v-ed-dd-item">Three</div>
                    </div>
                </div>
            </div>
            <div class="v-edit-row clearfix">
                <div class="float-left v-ed-lbl">Select Role</div>
                <div class="float-left v-ed-txt pos-relative">
                    <input class="v-ed-txt-item v-ed-txt-dd" placeholder="">
                    <div class="clearfix hide v-ed-dd-wrapper">
                        <div class="clearfix v-ed-dd-item">One</div>
                        <div class="clearfix v-ed-dd-item">Two</div>
                        <div class="clearfix v-ed-dd-item">Three</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="v-edit-rt col-lg-6 col-md-6 col-sm-6 col-xs-12">
            <div class="v-edt-tbl-wrapper">
                <table class="v-edt-tbl">
                    <tr class="v-edt-tbl-header">
                        <td class="v-edt-tbl-assign-to">Assigned To</td>
                        <td class="v-edt-tbl-role">Role</td>
                        <td class="v-edt-tbl-status">Status</td>
                        <td class="v-edt-tbl-rem"></td>
                    </tr>
                    <tr class="v-edt-tbl-row">
                        <td class="v-edt-tbl-assign-to">Northern Providential</td>
                        <td class="v-edt-tbl-role">User</td>
                        <td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-on"></td>
                        <td class="v-edt-tbl-rem v-edt-tbl-icn v-icn-rem-user"></td>
                    </tr>
                    <tr class="v-edt-tbl-row">
                        <td class="v-edt-tbl-assign-to">Providential Utah</td>
                        <td class="v-edt-tbl-role">Admin</td>
                        <td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-off"></td>
                        <td class="v-edt-tbl-rem v-edt-tbl-icn v-icn-rem-user"></td>
                    </tr>
                </table>
            </div>
        </div>
        <div class="v-edt-btn-sav">Save Changes</div>
    </div>
