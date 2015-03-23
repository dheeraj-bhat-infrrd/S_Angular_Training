<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<table class="v-um-tbl">
	<tr class="u-tbl-header">
		<td class="v-tbl-uname"><spring:message
				code="label.usermanagement.username.key" /></td>
		<td class="v-tbl-email"><spring:message code="label.emailid.key" /></td>
		<td class="v-tbl-rgn-adm text-center">Region</br/>Admin
		</td>
		<td class="v-tbl-of-adm text-center">Office<br />Admin
		</td>
		<td class="v-tbl-ln-of text-center">Loan<br />Officer
		</td>
		<td class="v-tbl-mail"></td>
		<td class="v-tbl-online"></td>
		<td class="v-tbl-rem"></td>
		<td class="v-tbl-edit"></td>
	</tr>
	<tr class="u-tbl-row">
		<td class="v-tbl-uname">Annalisa Detrick</td>
		<td class="v-tbl-email">annalisa@detrick.com</td>
		<td class="v-tbl-rgn-adm v-tbl-icn v-icn-tick"></td>
		<td class="v-tbl-of-adm v-tbl-icn v-icn-tick"></td>
		<td class="v-tbl-ln-of v-tbl-icn v-icn-tick"></td>
		<td class="v-tbl-mail v-tbl-icn v-icn-fmail"></td>
		<td class="v-tbl-online v-tbl-icn v-icn-onl v-icn-onl-off"></td>
		<td class="v-tbl-rem v-tbl-icn v-icn-rem-user"></td>
		<td class="v-tbl-edit v-tbl-icn v-icn-edit-user"></td>
	</tr>
	<tr class="u-tbl-row">
		<td class="v-tbl-uname">Annalisa Detrick</td>
		<td class="v-tbl-email">annalisa@detrick.com</td>
		<td class="v-tbl-rgn-adm v-tbl-icn v-icn-tick"></td>
		<td class="v-tbl-of-adm v-tbl-icn v-icn-tick"></td>
		<td class="v-tbl-ln-of v-tbl-icn v-icn-tick"></td>
		<td class="v-tbl-mail v-tbl-icn v-icn-fmail"></td>
		<td class="v-tbl-online v-tbl-icn v-icn-ofl v-icn-onl-off"></td>
		<td class="v-tbl-rem v-tbl-icn v-icn-rem-user"></td>
		<td class="v-tbl-edit v-tbl-icn v-icn-edit-user"></td>
	</tr>
	<!--
                <tr class="u-tbl-row u-tbl-row-sel">
                        
                </tr>
-->
</table>