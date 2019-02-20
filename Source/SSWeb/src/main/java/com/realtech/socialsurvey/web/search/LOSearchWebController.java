package com.realtech.socialsurvey.web.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.realtech.socialsurvey.core.entities.ZipCodeLookup;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.searchengine.impl.SearchEngineManagementServicesImpl;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.BotRequestUtils;

@Controller
public class LOSearchWebController {

	private static final Logger LOG = Logger.getLogger(LOSearchWebController.class);

	@Autowired
	private SearchEngineManagementServicesImpl searchEngineManagementServicesImpl;

	@Autowired
	private OrganizationManagementService organizationManagementService;

	@Autowired
	private BotRequestUtils botRequestUtils;

	@RequestMapping(value = { "/{vertical}/{entity}/{location}" }, method = RequestMethod.GET)
	public String losearch(Model model, HttpServletRequest request, @PathVariable("entity") String entity,
			@PathVariable("vertical") String vertical, @PathVariable("location") String location) {
		LOG.info("Method showSearchEnginePage() called from SearchEngineWebController with uuid and location ");
		entity = entity.replaceAll("-", " ");
		location = location.replaceAll("-", " ");
		// String url = request.getRequestURI();
		String[] citystate = null;
		if (location.contains("_")) {
			citystate = location.split("_");
			location = citystate[0];
		}
		model.addAttribute("location", location);

		model.addAttribute("entity", entity);
		if (vertical.contains("&amp")) {
			String url = request.getRequestURI();
			String[] requestparams = url.substring(url.indexOf("top"), url.length()).split("/");
			vertical = requestparams[1].replaceAll("&amp;", "&");
		}
		vertical = vertical.replaceAll("-", " ");
		model.addAttribute("vertical", vertical);
		if(citystate != null && citystate.length > 1) {
			model.addAttribute("title", vertical + " " + entity + " " + location + ", " + citystate[1]);
		} else {
			model.addAttribute("title", vertical + " " + entity + " " + location);
		}
		
		if (botRequestUtils.checkBotRequest(request.getHeader(BotRequestUtils.USER_AGENT_HEADER))) {
			LOG.info("Invoked by BOT");
			if (location != null && !StringUtils.isNumeric(location) && !location.contains("_")) {
				String statecode = organizationManagementService.getStateCodeByStateName(location);
				model.addAttribute("stateCode", statecode);
			}
			return JspResolver.SEARCH_ENGINE_NO_SCRIPT;
		} else {
			if (location != null && !location.isEmpty()) {
				if (StringUtils.isNumeric(location)) {
					List<ZipCodeLookup> zipCodeLookupList = searchEngineManagementServicesImpl
							.getSuggestionForNearMe(location, 0, 15, false);
					if (zipCodeLookupList != null && zipCodeLookupList.size() > 0) {
						model.addAttribute("latitude", zipCodeLookupList.get(0).getLatitude());
						model.addAttribute("longitude", zipCodeLookupList.get(0).getLongitude());
					}
				} else if (citystate != null) {
					model.addAttribute("cityName", location);
					if (citystate.length > 1 && citystate[1] != null) {
						model.addAttribute("stateCode", citystate[1]);
					}
				} else {
					String statecode = organizationManagementService.getStateCodeByStateName(location);
					model.addAttribute("stateCode", statecode);
				}
				model.addAttribute("isSeoSearch", true);
			} else {
				model.addAttribute("isSeoSearch", false);
			}

			return JspResolver.SEARCH_ENGINE;
		}

	}
}
