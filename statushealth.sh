#!/bin/bash
echo "check ECS service exists"
for((i=0;i<=10;i++))
    do
        status=$(aws ecs describe-services --cluster $2 --services $1 --query 'failures[0].reason' --output text)

        if [[ "${status}" == "MISSING" ]]; then
                echo "ecs service $1 missing in $2"
                sleep 30
        else
                echo "ecs service $1 running in $2"
                exit
    fi
    done
