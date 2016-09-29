<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<jsp:include page="scripts.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Batch Starter Dev Tool</title>
</head>
<script>
	function startLoneWolfJobDetail() {
		$("#startLW").prop('disabled', true);
		$("#startLW").attr('value', 'Running');
		$.ajax({
			url : "/startLoneWolfJobDetail.do",
			type : "POST",
			dataType : "html",
			success : function() {
				$("#startLW").prop('disabled', false);		
				$("#startLW").attr('value', 'Run');
			},
			complete : function() {

			},
			error : function(e) {

			}
		});
	}
</script>
<body>
	<div width="700px">
		<table width="100%">
			<tr>
				<td width="50%"><label>Fetch data from LoneWolf</label></td>
				<td width="25%"><input id="startLW" type="button" value="Run"
					onclick="startLoneWolfJobDetail()"></td>
				<td width="25%"></td>
			</tr>
		</table>
	</div>
</body>
</html>