alter table user_ranking_this_month_main add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;

alter table user_ranking_this_month_branch add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;

alter table user_ranking_past_month_main add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;

alter table user_ranking_past_month_branch add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;

alter table user_ranking_past_year_main add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;

alter table user_ranking_past_year_branch add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;

alter table user_ranking_this_year_main add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;

alter table user_ranking_this_year_branch add column `total_questions` int(11) default 0,
add column `answer_sum` int(11) default 0;
