#!/bin/bash
for (( i=1; i<=10; i++ ))
do
aws ecs describe-services --cluster $2 --services $1 --region ap-south-1 | grep message | head -n 1 | grep steady
if [ $? -eq 0 ]
then
echo "ecs service $1 is running on $2 "
else
echo "Waiting for service $1 to be steady"
sleep 45s
fi
if [ i >= 1 ] 
then 
echo "Service $1 is not available in $2 Please check logs"
fi
done

