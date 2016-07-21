
update USER_PROFILE
 set IS_PRIMARY = 1
where USER_PROFILE.USER_PROFILE_ID IN (
select 
outerQuery.USER_PROFILE_ID
from (
select subQuery.USER_PROFILE_ID,
subQuery.User_ID, subQuery.PROFILES_MASTER_ID
, subQuery.BRANCH_ID,subQuery.IS_PRIMARY
, subQuery.IS_DEFAULT_BY_SYSTEM
, case when @runningUserId = subQuery.USER_ID 
          then @groupedUserId := @groupedUserId + 1
 else @groupedUserId := 1 and @runningUserId := subQuery.USER_ID 
END as ranked_user_id
 from 
(
SELECT up.USER_PROFILE_ID,
up.USER_ID, up.PROFILES_MASTER_ID,up.IS_PRIMARY, br.BRANCH_ID, br.IS_DEFAULT_BY_SYSTEM
 FROM ss_user.USER_PROFILE as up
join ss_user.BRANCH as br 
    on up.BRANCH_ID = br.BRANCH_ID
where up.PROFILES_MASTER_ID IN (3,4)
 order by up.USER_ID
, up.PROFILES_MASTER_ID  desc
, br.IS_DEFAULT_BY_SYSTEM asc
) as subQuery
, (select @runningUserId := 1) as a
, (SELECT @groupedUserId := 0) as b
) as outerQuery
where outerQuery.ranked_user_id = 1);
