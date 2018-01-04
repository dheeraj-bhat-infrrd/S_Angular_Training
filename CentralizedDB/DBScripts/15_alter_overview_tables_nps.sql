alter table overview_user add column (
  `nps_score` decimal(10,2),
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_detractors_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_passives_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_promoters` int(11) NOT NULL DEFAULT '0',
  `nps_promoter_percentage` decimal(10,2) NOT NULL DEFAULT '0.00'
);

alter table overview_branch add column (
  `nps_score` decimal(10,2),
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_detractors_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_passives_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_promoters` int(11) NOT NULL DEFAULT '0',
  `nps_promoter_percentage` decimal(10,2) NOT NULL DEFAULT '0.00'
);

alter table overview_region add column (
  `nps_score` decimal(10,2),
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_detractors_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_passives_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_promoters` int(11) NOT NULL DEFAULT '0',
  `nps_promoter_percentage` decimal(10,2) NOT NULL DEFAULT '0.00'
);


alter table overview_company add column (
  `nps_score` decimal(10,2),
  `nps_detractors` int(11) NOT NULL DEFAULT '0',
  `nps_detractors_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_passives` int(11) NOT NULL DEFAULT '0',
  `nps_passives_percentage` decimal(10,2) NOT NULL DEFAULT '0.00',
  `nps_promoters` int(11) NOT NULL DEFAULT '0',
  `nps_promoter_percentage` decimal(10,2) NOT NULL DEFAULT '0.00'
);