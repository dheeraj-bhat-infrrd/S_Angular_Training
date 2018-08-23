<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="admin_header.jsp" />
<div id="main-content"></div>
<jsp:include page="admin_scripts.jsp"/>
<script type="text/javascript">
$(document).ready(function(){
	onpopstate = function(event) {
	    if(location.hash.trim()!=''){
	        historyCallback= true;
	        refreshSupport=true;
	    }
	    retrieveState();
	};

	if(location.hash.trim()!='' ){
	    historyCallback= true;
	    refreshSupport=true;
	    retrieveState();
	    return;
	}

	showMainContent('./adminhierarchy.do');	
});
</script>
<jsp:include page="admin_footer.jsp" />