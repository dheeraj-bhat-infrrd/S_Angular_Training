CURRENTDATE=`date +"%Y%m%d_%H%M%S"`
echo Current Date and Time is: ${CURRENTDATE}

sh /home/ec2-user/prod/pdi-ce-5.4.0.1-130/data-integration/kitchen.sh -file=/home/ec2-user/prod/ETLs/centralized_etls/FinalJob.kjb -logfile=/home/ec2-user/prod/log/centralized_${CURRENTDATE}.txt -level=Basic && sh /home/ec2-user/prod/pdi-ce-5.4.0.1-130/data-integration/kitchen.sh -file=/home/ec2-user/prod/ETLs/reporting_etls/ReportingJobMain.kjb -logfile=/home/ec2-user/prod/log/reporting_${CURRENTDATE}.txt -level=Basic