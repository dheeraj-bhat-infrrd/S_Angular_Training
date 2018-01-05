alter table survey_stats_report add column (
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_promoters` int(11) NOT NULL DEFAULT '0'
);

alter table survey_stats_report_user add column (
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_promoters` int(11) NOT NULL DEFAULT '0'
);

alter table survey_stats_report_company add column (
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_promoters` int(11) NOT NULL DEFAULT '0'
);

alter table survey_stats_report_region add column (
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_promoters` int(11) NOT NULL DEFAULT '0'
);