#!/bin/bash
for (( i=1; i<10; i++ ))
do
aws ecs describe-services --cluster SASTech-Devops-Preprod --services uat-usermanagement --region ap-south-1 | grep message | head -n 1 | grep steady
if [ $? -eq 0 ]
then
echo "server alive"
break
else
echo "server dead or can not ping."
sleep 45s
fi
done
