 #!/bin/bash 
  DEPLOYMENT_SUCCESS=1
  i=0
  every=1
  #echo "Waiting for service deployment to complete..."
while [ $i -lt 10 ]
    do
    NUM_DEPLOYMENTS=$(aws ecs describe-services --services $1 --cluster $2 | jq "[.services[].deployments[]] | length")
    if [ $NUM_DEPLOYMENTS -eq 1 ]; then
    # Wait to see if more than 1 deployment stays running
    # If the wait time has passed, we need to roll back 
      echo "ecs service $1 running in $2"
      DEPLOYMENT_SUCCESS=0
      break
      # Exit the loop.
      else
      echo "Waiting for service $1 to be steady"
      sleep 30
      i=$(($i + $every))
    fi
  done

  if [[ $DEPLOYMENT_SUCCESS -eq 1 ]]; then
    echo "ecs service $1 missing in $2"
    fi
    exit 1
