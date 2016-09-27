package com.realtech.socialsurvey.core.starter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;

public class ReMaxPartitonBatch extends QuartzJobBean
{
    
    private static final Logger LOG = LoggerFactory.getLogger( ReMaxPartitonBatch.class );
    private OrganizationManagementService organizationManagementService;
    private void initializeDependencies( JobDataMap jobMap )
    {
        organizationManagementService = (OrganizationManagementService) jobMap.get( "organizationManagementService" );
    }


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {

          final String SS_USERS = "http://172.30.0.139:8983/solr/ss-users/";
          final String SS_BRANCHES = "http://172.30.0.139:8983/solr/ss-branches/";
          final String SS_REGIONS = "http://172.30.0.139:8983/solr/ss-regions/";

        
        LOG.debug("main method started to trim the first and last name of users in solr");
        String solrUserURL = SS_USERS;
        String solrBranchURL = SS_BRANCHES;
        String solrRegionURL = SS_REGIONS;
        
        int maxRow = 10000;
        String SOLR_EDIT_REPLACE = "set";
        
        
        String regionIdsTennessee = "(76 77 81 82 84 87 90 97 98 99 101 103)";
        String companyIdTennessee = "411";
        String regionIdsOhio = "(79 83 85 86 96 100 316)";
        String companyIdOhio = "412";
        String regionIdsKentucky = "(92 94 104 318 319)";
        String companyIdKentucky = "413";
        
        String branchIdsForDefaultRegionTennessee= "(294 295 296 297 302 307 308 310 312 314 320 321 327 331 334 335 336 337 341 342 345 346 347 348 349 357 358 359 366 373 383 390 395 396 397 609 956 1010 1320 1321)";
        String defaultRegionIdTennessee = "332";
        String branchIdsForDefaultRegionOhio = "(398 405 407 408 412 414 415 416 421 434)";
        String defaultRegionIdOhio = "333";
        String branchIdsForDefaultRegionKentucky = "(277 280 281 282 283 286 287 288 290 300 301 305 313 316 318 329 332 343 352 355 360 363 365 368 369 379 391 394 906 1006 1071)";
        String defaultRegionIdKentucky = "334";
        
        
        String userIdsTennessee = "(1012 1020 1021 1022 1023 1024 1028 1029 1053 1054 1057 1060 1076 1077 1079 1080 1081 1082 1191 1204 1206 1219 1221 1225 1232 1240 1243 1254 1266 1269 1329 1333 1334 1346 1351 1359 1362 1363 1364 1365 1366 1367 1373 1376 1377 1407 1412 1430 1431 1432 1457 1458 1472 1490 1494 1495 1496 1501 1502 1503 1511 1512 1513 1518 1540 1549 1550 1564 1565 1566 1570 1574 1575 1576 1587 1588 1604 1607 1609 1610 1611 1612 1613 1614 1615 1616 1617 1618 1619 1620 1621 1625 1626 1627 1635 1642 1647 1657 1658 1659 1661 1662 1663 1683 1696 1697 1698 1699 1715 1717 1722 1736 1743 1744 1745 1747 1750 1751 1752 1753 1758 1769 1774 1775 1779 1784 1791 1796 1797 1799 1800 1808 1826 1828 1829 1830 1838 1840 1841 1842 1843 1844 1845 1847 1879 1880 1899 1900 1901 1902 1903 1904 1905 1906 1907 1908 1909 1910 1911 1936 1971 1977 1978 1979 1988 1989 1992 1993 2004 2007 2008 2015 2016 2023 2024 2025 2036 2037 2039 2042 2043 2050 2051 2052 2053 2054 2057 2058 2059 2061 2072 2073 2074 2075 2076 2077 2078 2100 2104 2105 2106 2107 2125 2136 2181 2195 2196 2218 2219 2220 2222 2223 2225 2226 2227 2228 2230 2231 2232 2233 2234 2235 2236 2237 2238 2240 2241 2242 2243 2244 2246 2247 2249 2250 2251 2278 2279 2280 2281 2289 2290 2291 2292 2293 2294 2299 2300 2308 2311 2312 2332 2333 2335 2342 2343 2344 2345 2347 2348 2349 2350 2354 2384 2385 2409 2410 2418 2421 2423 2428 2435 2448 2449 2450 2451 2452 2453 2454 2455 2456 2457 2458 2462 2466 2472 2484 2485 2486 2494 2495 2496 2504 2510 2514 2517 2518 2528 2530 2531 2534 2541 2543 2545 2547 2548 2549 2559 2577 2578 2579 2589 2590 2593 2594 2610 2616 2619 2624 2625 2626 2629 2633 2635 2636 2638 2642 2644 2646 2651 2657 2668 2669 2671 2673 2674 2676 2677 2678 2682 2687 2688 2691 2693 2695 2696 2702 2704 2706 2707 2710 2711 2713 2715 2716 2717 2724 2725 2727 2734 2736 2737 2740 2741 2744 2750 2751 2752 2754 2755 2757 2762 2765 2766 2769 2771 2773 2776 2778 2782 2790 2792 2797 2799 2800 2801 2802 2806 2811 2814 2816 2820 2823 2837 2843 2847 2849 2850 2851 2852 2853 2862 2863 2864 2866 2867 2869 2871 2872 2877 2878 2882 2887 2888 2889 2893 2894 2895 2896 2897 2899 2907 2908 2911 2915 2916 2919 2920 2922 2929 2931 2943 2946 2947 2952 2954 2955 2961 2964 2965 2966 2967 2968 2973 2974 2975 2976 2977 2978 2982 2986 2988 2989 2993 2995 2996 3010 3014 3018 3020 3024 3028 3030 3032 3033 3034 3035 3038 3043 3047 3048 3049 3053 3061 3062 3065 3089 3090 3091 3092 3095 3103 3104 3105 3109 3113 3115 3116 3119 3122 3123 3129 3133 3138 3142 3145 3149 3150 3152 3159 3160 3161 3162 3167 3172 3175 3176 3186 3187 3190 3194 3195 3198 3201 3202 3203 3206 3208 3213 3218 3220 3227 3229 3230 3245 3247 3249 3253 3258 3264 3266 3267 3270 3275 3276 3284 3288 3289 3301 3302 3305 3315 3318 3319)";
        String userIdsTennessee2 = "( 3323 3326 3331 3338 3347 3349 3351 3356 3357 3358 3359 3360 3364 3366 3367 3369 3371 3386 3393 3405 3406 3407 3409 3412 3413 3418 3419 3422 3426 3429 3430 3432 3433 3435 3437 3438 3441 3448 3452 3453 3455 3460 3461 3462 3464 3465 3466 3471 3472 3475 3476 3479 3481 3483 3485 3487 3489 3491 3492 3498 3504 3508 3509 3510 3511 3512 3514 3532 3533 3534 3537 3542 3543 3544 3545 3547 3552 3558 3559 3564 3575 3580 3581 3583 3584 3585 3586 3602 3613 3618 3619 3637 3645 3646 3653 3654 3657 3660 3663 3676 3680 3682 3685 3688 3690 3691 3692 3695 3699 3703 3705 3706 3709 3714 3718 3733 3742 3748 3750 3752 3759 3765 3769 3772 3773 3774 3775 3777 3778 3783 3785 3793 3795 3797 3798 3799 3803 3805 3807 3811 3818 3831 3836 3838 3840 3842 3843 3846 3847 3848 3850 3854 3861 3862 3864 3867 3870 3871 3877 3879 3884 3887 3888 3889 3890 3894 3896 3900 3901 3903 3906 3907 3910 3911 3915 3918 3927 3928 3929 3931 3933 3936 3940 3943 3953 3958 3964 3965 3970 3973 3977 3981 3983 3984 3985 3986 3987 3988 3990 4001 4004 4005 4006 4010 4011 4015 4022 4024 4026 4027 4028 4030 4034 4035 4042 4044 4050 4052 4058 4060 4061 4062 4063 4071 4076 4081 4087 4088 4093 4097 4100 4103 4106 4107 4108 4109 4111 4115 4123 4127 4130 4134 4135 4139 4140 4141 4144 4146 4152 4154 4160 4162 4164 4168 4176 4177 4179 4180 4181 4184 4190 4193 4194 4195 4196 4198 4199 4202 4206 4207 4211 4217 4222 4232 4239 4240 4244 4250 4262 4263 4267 4268 4270 4271 4277 4285 4291 4298 4303 4304 4308 4311 4313 4314 4317 4324 4345 4347 4348 4349 4350 4354 4355 4356 4362 4363 4367 4370 4373 4377 4378 4379 4380 4383 4384 4385 4386 4388 4391 4393 4397 4398 4399 4401 4403 4405 4411 4412 4414 4415 4420 4421 4427 4433 4434 4439 4446 4452 4453 4454 4458 4462 4468 4474 4476 4477 4481 4490 4494 4521 4600 5429 5433 5434 5435 5436 5437 5443 5549 5582 5583 5616 5644 5646 5652 5655 5656 5658 5659 5660 5661 5662 5663 5664 5665 5666 5667 5668 5669 5670 5671 5735 6467 6490 6493 6503 6504 6505 6506 6524 6558 6603 6666 6667 6668 6669 6670 6674 6675 6677 6678 6679 6713 6747 6755 6756 6757 6758 6759 6938 6950 6951 6952 6953 6954 6955 6956 6957 6958 6959 6960 6961 6962 6963 7055 7059 7196 7197 7208 7237 7258 7259 7270 7351 7530 7544 7545 7818 7839 7840 7841 7842 7845 8047 8051 8053 8056 8057 8066 8085 8087 8088 8094 8095 8097 8098 8099 8100 8101 8138 8140 8141 8142 8143 8144 8145 8146 8147 8148 8149 8150 8151 8152 8153 8154 8155 8156 8157 8158 8159 8160 8161 8162 8163 8165 8166 8167 8169 8199 8201 8202 8203 8204 8205 8206 8208 8209 8210 8211 8212 8213 8214 8215 8216 8217 8218 8219 8220 8221 8232 8233 8234 8235 8236 8255 8258 8259 8260 8261 8262 8263 8264 8265 8266 8267 8268 8269 8270 8271 8272 8273 8274 8275 8276 8278 8287 8288 8289 8293 8294 8295 8296 8297 8299 8300 8301 8302 8303 8306 8307 8308 8316 8317 8318 8319 8320 8321 8322 8323 8324 8325 8342 8343 8345 8346 8347 8348 8349 8350 8351 8363 8366 8380 8382 8383 8384 8391 8392 8393 8400 8401 8403 8404 8405 8406 8407 8408 8409 8419 8420 8422 8626 8627 8628 8629 8630 8631 8632 8633 8634 8635 8636 9154 9155 9156 9157 9482 9483 9613)";
        
        
        String userIdsOhio = "(1083 1084 1085 1086 1087 1088 1089 1091 1098 1099 1100 1101 1102 1103 1207 1212 1218 1220 1228 1233 1251 1264 1302 1303 1304 1305 1306 1307 1309 1310 1311 1312 1313 1314 1315 1316 1317 1318 1322 1323 1424 1426 1428 1429 1433 1448 1459 1460 1461 1463 1470 1499 1500 1525 1526 1537 1538 1551 1584 1585 1599 1644 1666 1668 1676 1679 1687 1688 1694 1700 1701 1702 1712 1727 1728 1729 1732 1737 1739 1741 1748 1759 1760 1761 1762 1764 1778 1785 1802 1873 1915 1916 1917 1918 1919 1920 1921 1929 1931 1932 1933 1934 1935 1975 1976 1983 1984 1985 1986 1987 1990 1991 1996 1997 2038 2084 2085 2089 2164 2197 2198 2199 2200 2201 2202 2204 2205 2206 2207 2208 2209 2217 2261 2262 2263 2264 2265 2266 2267 2268 2277 2287 2288 2304 2305 2351 2355 2361 2399 2406 2407 2420 2445 2470 2471 2480 2500 2501 2502 2511 2513 2520 2552 2566 2568 2571 2592 2652 2653 2675 2689 2721 2729 2770 2784 2789 2804 2805 2810 2861 2874 2876 2886 2900 2903 2904 2921 2935 2936 2941 3002 3021 3022 3025 3031 3054 3073 3077 3084 3101 3118 3139 3143 3146 3151 3154 3168 3184 3192 3193 3207 3211 3222 3236 3237 3240 3241 3260 3272 3282 3291 3311 3312 3313 3336 3341 3381 3389 3390 3392 3394 3395 3398 3399 3414 3424 3439 3469 3480 3484 3496 3500 3501 3528 3531 3536 3539 3540 3541 3550 3567 3568 3582 3608 3673 3686 3719 3737 3738 3756 3768 3776 3835 3837 3851 3869 3891 3893 3899 3967 4003 4025 4037 4039 4041 4048 4068 4070 4072 4073 4079 4092 4098 4101 4102 4105 4136 4158 4170 4242 4243 4258 4289 4293 4318 4340 4359 4361 4389 4413 4424 4430 4459 4469 4475 4497 4498 4500 4501 4504 4512 4515 5058 7018 7340 7348 7349 7350 7542 7806 7807 7808 7809 7810 7811 7814 7815 7816 7817 7819 7820 7821 7822 7823 7824 7825 7826 7827 7831 7832 7833 7834 7835 7837 8637 8638 8639 8641 8652 8730)";
        String userIdsKentucky = "(1019 1025 1026 1027 1030 1031 1032 1033 1034 1035 1036 1037 1038 1039 1040 1041 1043 1044 1045 1046 1047 1048 1049 1050 1051 1052 1055 1056 1058 1059 1061 1062 1063 1064 1065 1066 1067 1068 1069 1070 1071 1072 1073 1074 1075 1092 1093 1094 1095 1096 1097 1205 1211 1215 1222 1224 1234 1237 1239 1245 1246 1249 1252 1256 1258 1259 1262 1263 1267 1270 1320 1321 1330 1331 1332 1335 1336 1337 1338 1339 1340 1341 1342 1343 1344 1345 1347 1348 1350 1352 1353 1354 1355 1356 1357 1358 1360 1361 1369 1370 1371 1372 1374 1380 1381 1392 1397 1398 1399 1400 1401 1402 1403 1405 1408 1410 1411 1419 1420 1421 1422 1423 1425 1451 1452 1453 1454 1455 1466 1467 1493 1497 1498 1509 1510 1529 1542 1543 1544 1545 1547 1548 1558 1579 1581 1582 1589 1596 1597 1598 1631 1632 1633 1634 1636 1637 1638 1639 1640 1641 1643 1646 1648 1649 1664 1665 1667 1691 1692 1693 1713 1716 1719 1720 1725 1730 1731 1755 1756 1770 1781 1782 1788 1789 1790 1803 1804 1805 1806 1807 1812 1813 1814 1822 1823 1824 1825 1827 1831 1832 1833 1834 1835 1836 1846 1850 1851 1881 1884 1885 1888 1889 1891 1894 1895 1896 1897 1912 1913 1914 1922 1923 1924 1925 1926 1927 1928 1938 2010 2012 2013 2027 2028 2029 2030 2031 2041 2055 2056 2080 2090 2091 2108 2109 2116 2137 2138 2139 2140 2141 2142 2143 2144 2146 2147 2148 2149 2150 2151 2152 2153 2156 2157 2158 2159 2160 2162 2163 2165 2166 2167 2168 2169 2170 2171 2172 2173 2174 2175 2176 2177 2178 2179 2180 2182 2183 2184 2185 2186 2187 2188 2295 2323 2324 2326 2329 2330 2331 2336 2337 2338 2339 2340 2341 2381 2382 2393 2396 2397 2404 2408 2412 2414 2415 2424 2434 2436 2438 2441 2442 2444 2447 2463 2464 2465 2468 2497 2498 2503 2507 2508 2512 2516 2519 2522 2525 2529 2535 2537 2538 2542 2546 2550 2557 2558 2562 2564 2570 2572 2573 2582 2585 2591 2596 2600 2601 2609 2623 2631 2639 2645 2654 2664 2665 2672 2683 2692 2694 2700 2701 2705 2720 2730 2739 2748 2749 2764 2780 2781 2788 2794 2795 2796 2813 2822 2848 2857 2858 2860 2868 2879 2881 2905 2923 2925 2933 2945 2962 2991 2992 3003 3004 3012 3027 3060 3066 3074 3085 3086 3087 3093 3106 3132 3140 3144 3157 3163 3164 3169 3178 3180 3183 3199 3209 3210 3212 3216 3228 3250 3252 3254 3263 3265 3271 3277 3281 3290 3292 3293 3294 3295 3309 3317 3321 3330 3332 3337 3344 3355 3368 3370 3373 3374 3375 3391 3396 3397 3408 3417 3425 3427 3434 3442 3445 3447 3456 3463 3468 3474 3482 3494 3495 3497 3499 3502 3513 3516 3518 3524 3526 3549 3551 3553 3555 3561 3562 3565 3570 3571 3574 3576 3579 3587 3588 3591 3592 3593 3598 3603 3604 3607 3612 3617 3635 3643 3644 3652 3656 3659 3662 3675 3679 3684 3687 3689 3694 3700 3704 3707 3708 3710 3711 3715 3716 3717 3720 3722 3723 3726 3727 3728 3731 3735 3736 3741 3743 3744 3745 3746 3747 3751 3757 3760 3761 3762 3763 3766 3767 3771 3781 3782 3786 3787 3788 3790 3796 3800 3812 3814 3826 3828 3834 3844 3855 3878 3880 3881 3882 3883 3885 3897 3898 3905 3913 3917 3919 3920 3921 3922 3923 3924 3932 3937 3938 3939 3941 3942 3945 3946 3947 3951 3952 3963 3966 3968 3974 3976 3978 3982 3991 3993 3997 3999 4016 4021 4023 4031 4032 4033 4036 4045 4051 4059 4064 4066 4069 4110 4112 4113 4114 4116 4120 4121 4122 4126 4128 4147 4153 4174 4186 4204 4209 4210 4212 4218 4219 4231 4233 4234 4236 4249 4253 4254 4255 4256 4257 4266 4273 4274 4275 4276 4278 4283 4286 4287 4290 4294 4297 4299 4300 4302 4307 4309 4322 4323 4326 4327 4331 4333 4334 4335 4336 4357 4358 4365 4366 4371 4372 4376 4381 4387 4396 4402 4404 4409 4426 4437 4438 4440 4441 4442 4449 4457 4467 4482 4489 4511 4514 4519 4520 4575 4587 4588 4589 4609 4665 4666 4667 4787 4890 5448 6230 6303 6304 6305 6491 6494 6497 6498 6499 6508 6564 6600 6609 6931 6932 6933 6934 6935 6940 6941 6942 6943 6944 6945 6946 7244 7245 7246 7302 7303 7305 7306 7307 7308 7309 7310 7311 7803 7804 7805 7812 7813 7843 8058 8059 8060 8061 8062 8063 8064 8065 8103 8105 8106 8107 8108 8109 8110 8111 8112 8113 8114 8115 8131 8132 8133 8134 8135 8136 8137 8223 8224 8226 8227 8229 8237 8238 8256 8257 8280 8285 8286 8290 8291 8292 8298 8304 8305 8326 8327 8328 8335 8337 8338 8339 8352 8353 8354 8355 8356 8357 8358 8359 8360 8361 8362 8364 8365 8368 8369 8370 8371 8373 8374 8378 8379 8381 8387 8389 8390 8394 8395 8396 8397 8398 8399 8402 8410 8411 8412 8416 8417 8423 8424 8427 8428 8622 8623 8870 9473 9666)";
        

        try {
            
            //update company in region
          //Tennessee   TN   411
            
            LOG.debug("update company in region");
            
            SolrServer solrServer = new HttpSolrServer( solrRegionURL );
            SolrQuery solrQuery = new SolrQuery( CommonConstants.REGION_ID_SOLR + ":" + regionIdsTennessee );
            solrQuery.setRows( maxRow );
            QueryResponse response = solrServer.query( solrQuery );
            SolrDocumentList result = response.getResults();

            if ( result != null ) {
                for ( SolrDocument document : result ) {
                    Long regionId = (Long) document.getFieldValue( CommonConstants.REGION_ID_SOLR );

                    SolrInputDocument ipDocument = new SolrInputDocument();
                    ipDocument.setField( CommonConstants.REGION_ID_SOLR, regionId );
                    Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                    firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdTennessee );
                    ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                    solrServer.add( ipDocument );
                    solrServer.commit();

                }
            }
            
            //update company in region
            //Ohio  OH   412
              
               solrServer = new HttpSolrServer( solrRegionURL );
               solrQuery = new SolrQuery( CommonConstants.REGION_ID_SOLR + ":" + regionIdsOhio );
              solrQuery.setRows( maxRow );
               response = solrServer.query( solrQuery );
               result = response.getResults();

              if ( result != null ) {
                  for ( SolrDocument document : result ) {
                      Long regionId = (Long) document.getFieldValue( CommonConstants.REGION_ID_SOLR );

                      SolrInputDocument ipDocument = new SolrInputDocument();
                      ipDocument.setField( CommonConstants.REGION_ID_SOLR, regionId );
                      Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                      firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdOhio );
                      ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                      solrServer.add( ipDocument );
                      solrServer.commit();

                  }
              }
              
              
              //update company in region
              //Kentucky
              
               solrServer = new HttpSolrServer( solrRegionURL );
                 solrQuery = new SolrQuery( CommonConstants.REGION_ID_SOLR + ":" + regionIdsKentucky );
                solrQuery.setRows( maxRow );
                 response = solrServer.query( solrQuery );
                 result = response.getResults();

                if ( result != null ) {
                    for ( SolrDocument document : result ) {
                        Long regionId = (Long) document.getFieldValue( CommonConstants.REGION_ID_SOLR );

                        SolrInputDocument ipDocument = new SolrInputDocument();
                        ipDocument.setField( CommonConstants.REGION_ID_SOLR, regionId );
                        Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                        firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdKentucky );
                        ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                        solrServer.add( ipDocument );
                        solrServer.commit();

                    }
                }
            
            
                
                
                

                
                //update region id in branches
                //Tennessee   TN   411
                  
                  solrServer = new HttpSolrServer( solrBranchURL );
                  solrQuery = new SolrQuery( CommonConstants. BRANCH_ID_SOLR + ":" + branchIdsForDefaultRegionTennessee );
                 solrQuery.setRows( maxRow );
                  response = solrServer.query( solrQuery );
                  result = response.getResults();

                 if ( result != null ) {
                     for ( SolrDocument document : result ) {
                         Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                         SolrInputDocument ipDocument = new SolrInputDocument();
                         ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                         Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                       
                         firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdTennessee );
                         ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                         
                         solrServer.add( ipDocument );
                         solrServer.commit();

                     }
                 }
                 
                 //update region id in branches
                 //Ohio   OH   412
                   
                   solrServer = new HttpSolrServer( solrBranchURL );
                   solrQuery = new SolrQuery( CommonConstants. BRANCH_ID_SOLR + ":" + branchIdsForDefaultRegionOhio );
                  solrQuery.setRows( maxRow );
                   response = solrServer.query( solrQuery );
                   result = response.getResults();

                  if ( result != null ) {
                      for ( SolrDocument document : result ) {
                          Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                          SolrInputDocument ipDocument = new SolrInputDocument();
                          ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                          Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                          firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdOhio );
                          ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                          
                          solrServer.add( ipDocument );
                          solrServer.commit();

                      }
                  }
                  
                  
                  //update region id in branches
                  //Kentucky  KY   413
                    
                    solrServer = new HttpSolrServer( solrBranchURL );
                    solrQuery = new SolrQuery( CommonConstants. BRANCH_ID_SOLR + ":" + branchIdsForDefaultRegionKentucky );
                   solrQuery.setRows( maxRow );
                    response = solrServer.query( solrQuery );
                    result = response.getResults();

                   if ( result != null ) {
                       for ( SolrDocument document : result ) {
                           Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                           SolrInputDocument ipDocument = new SolrInputDocument();
                           ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                           Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                          
                           firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdKentucky );
                           ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                           solrServer.add( ipDocument );
                           solrServer.commit();

                       }
                   }
                
                
                
                
                
                
                
                LOG.debug("update company in branches");

                
            // update company in branches
            //Tennessee   TN   411

             solrServer = new HttpSolrServer( solrBranchURL );
             solrQuery = new SolrQuery( CommonConstants.REGION_ID_SOLR + ":" + regionIdsTennessee );
            solrQuery.setRows( maxRow );
             response = solrServer.query( solrQuery );
             result = response.getResults();

            if ( result != null ) {
                for ( SolrDocument document : result ) {
                    Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                    SolrInputDocument ipDocument = new SolrInputDocument();
                    ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                    Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                    firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdTennessee );
                    ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                    solrServer.add( ipDocument );
                    solrServer.commit();

                }
            }
            
            
            
            
         // update company in branches
            //Ohio  OH   412

             solrServer = new HttpSolrServer( solrBranchURL );
             solrQuery = new SolrQuery( CommonConstants.REGION_ID_SOLR + ":" + regionIdsOhio );
            solrQuery.setRows( maxRow );
             response = solrServer.query( solrQuery );
             result = response.getResults();

            if ( result != null ) {
                for ( SolrDocument document : result ) {
                    Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                    SolrInputDocument ipDocument = new SolrInputDocument();
                    ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                    Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                    firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdOhio );
                    ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                    solrServer.add( ipDocument );
                    solrServer.commit();

                }
            }
            
            
            
         // update company in branches
            //Kentucky  KY   413
             solrServer = new HttpSolrServer( solrBranchURL );
             solrQuery = new SolrQuery( CommonConstants.REGION_ID_SOLR + ":" + regionIdsKentucky );
            solrQuery.setRows( maxRow );
             response = solrServer.query( solrQuery );
             result = response.getResults();

            if ( result != null ) {
                for ( SolrDocument document : result ) {
                    Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                    SolrInputDocument ipDocument = new SolrInputDocument();
                    ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                    Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                    firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdKentucky );
                    ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                    solrServer.add( ipDocument );
                    solrServer.commit();

                }
            }
            
            
            LOG.debug("update region in branches");

            
            
            //update region id in branches
          //Tennessee   TN   411
            
            solrServer = new HttpSolrServer( solrBranchURL );
            solrQuery = new SolrQuery( CommonConstants. BRANCH_ID_SOLR + ":" + branchIdsForDefaultRegionTennessee );
           solrQuery.setRows( maxRow );
            response = solrServer.query( solrQuery );
            result = response.getResults();

           if ( result != null ) {
               for ( SolrDocument document : result ) {
                   Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                   SolrInputDocument ipDocument = new SolrInputDocument();
                   ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                   Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                   firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, defaultRegionIdTennessee );
                   ipDocument.setField( CommonConstants.REGION_ID_SOLR, firstNameEditKeyValues );
                   solrServer.add( ipDocument );
                   solrServer.commit();

               }
           }
           
           //update region id in branches
           //Ohio   OH   412
             
             solrServer = new HttpSolrServer( solrBranchURL );
             solrQuery = new SolrQuery( CommonConstants. BRANCH_ID_SOLR + ":" + branchIdsForDefaultRegionOhio );
            solrQuery.setRows( maxRow );
             response = solrServer.query( solrQuery );
             result = response.getResults();

            if ( result != null ) {
                for ( SolrDocument document : result ) {
                    Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                    SolrInputDocument ipDocument = new SolrInputDocument();
                    ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                    Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                    firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, defaultRegionIdOhio );
                    ipDocument.setField( CommonConstants.REGION_ID_SOLR, firstNameEditKeyValues );
                    solrServer.add( ipDocument );
                    solrServer.commit();

                }
            }
            
            
            //update region id in branches
            //Kentucky  KY   413
              
              solrServer = new HttpSolrServer( solrBranchURL );
              solrQuery = new SolrQuery( CommonConstants. BRANCH_ID_SOLR + ":" + branchIdsForDefaultRegionKentucky );
             solrQuery.setRows( maxRow );
              response = solrServer.query( solrQuery );
              result = response.getResults();

             if ( result != null ) {
                 for ( SolrDocument document : result ) {
                     Long branchId = (Long) document.getFieldValue( CommonConstants.BRANCH_ID_SOLR );

                     SolrInputDocument ipDocument = new SolrInputDocument();
                     ipDocument.setField( CommonConstants.BRANCH_ID_SOLR, branchId );
                     Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                     firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, defaultRegionIdKentucky );
                     ipDocument.setField( CommonConstants.REGION_ID_SOLR, firstNameEditKeyValues );
                     solrServer.add( ipDocument );
                     solrServer.commit();

                 }
             }
           
             
             LOG.debug("update company in users");

             
             // update company id in user 
             //Tennessee    TN   411
               solrServer = new HttpSolrServer( solrUserURL );
               solrQuery = new SolrQuery( CommonConstants. USER_ID_SOLR + ":" + userIdsTennessee );
              solrQuery.setRows( maxRow );
               response = solrServer.query( solrQuery );
               result = response.getResults();

              if ( result != null ) {
                  for ( SolrDocument document : result ) {
                      Long userId = (Long) document.getFieldValue( CommonConstants.USER_ID_SOLR );

                      SolrInputDocument ipDocument = new SolrInputDocument();
                      ipDocument.setField( CommonConstants.USER_ID_SOLR, userId );
                      Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                      firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdTennessee );
                      ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                      solrServer.add( ipDocument );
                      solrServer.commit();

                  }
              }
              
              
              solrServer = new HttpSolrServer( solrUserURL );
              solrQuery = new SolrQuery( CommonConstants. USER_ID_SOLR + ":" + userIdsTennessee2 );
             solrQuery.setRows( maxRow );
              response = solrServer.query( solrQuery );
              result = response.getResults();

             if ( result != null ) {
                 for ( SolrDocument document : result ) {
                     Long userId = (Long) document.getFieldValue( CommonConstants.USER_ID_SOLR );

                     SolrInputDocument ipDocument = new SolrInputDocument();
                     ipDocument.setField( CommonConstants.USER_ID_SOLR, userId );
                     Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                     firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdTennessee );
                     ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                     solrServer.add( ipDocument );
                     solrServer.commit();

                 }
             }
              
              
           // update company id in user 
              //Ohio    OH   412
                solrServer = new HttpSolrServer( solrUserURL );
                solrQuery = new SolrQuery( CommonConstants. USER_ID_SOLR + ":" + userIdsOhio);
               solrQuery.setRows( maxRow );
                response = solrServer.query( solrQuery );
                result = response.getResults();

               if ( result != null ) {
                   for ( SolrDocument document : result ) {
                       Long userId = (Long) document.getFieldValue( CommonConstants.USER_ID_SOLR );

                       SolrInputDocument ipDocument = new SolrInputDocument();
                       ipDocument.setField( CommonConstants.USER_ID_SOLR, userId );
                       Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                       firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdOhio);
                       ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                       solrServer.add( ipDocument );
                       solrServer.commit();

                   }
               }
               
               
            // update company id in user 
               //Kentucky   KY   413
                 solrServer = new HttpSolrServer( solrUserURL );
                 solrQuery = new SolrQuery( CommonConstants. USER_ID_SOLR + ":" + userIdsKentucky);
                solrQuery.setRows( maxRow );
                 response = solrServer.query( solrQuery );
                 result = response.getResults();

                if ( result != null ) {
                    for ( SolrDocument document : result ) {
                        Long userId = (Long) document.getFieldValue( CommonConstants.USER_ID_SOLR );

                        SolrInputDocument ipDocument = new SolrInputDocument();
                        ipDocument.setField( CommonConstants.USER_ID_SOLR, userId );
                        Map<String, String> firstNameEditKeyValues = new HashMap<String, String>();
                        firstNameEditKeyValues.put( SOLR_EDIT_REPLACE, companyIdKentucky);
                        ipDocument.setField( CommonConstants.COMPANY_ID_SOLR, firstNameEditKeyValues );
                        solrServer.add( ipDocument );
                        solrServer.commit();

                    }
                }
                
                LOG.debug("main method started to trim the first and last name of users in solr");
            
        } catch (SolrServerException | IOException e) {
            LOG.error("SolrServerException while updating user record " + e);
        }
        LOG.debug("main method ended");
    
    }


}
