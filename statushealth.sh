  DEPLOYMENT_SUCCESS="false"
  every=5
  i=0
  TIMEOUT=60
  echo "Waiting for service deployment to complete..."
  while [ $i -lt $TIMEOUT ]
  do
          status=$(aws ecs describe-services --cluster $2 --services $1 --region ap-south-1 && grep message && head -n 1 && grep steady)
    # Wait to see if more than 1 deployment stays running
    # If the wait time has passed, we need to roll back
    if ( ${status} -eq 0);then
      echo "ecs service $1 running in $2"
      DEPLOYMENT_SUCCESS="true"
      # Exit the loop.
      i=$TIMEOUT
    else
      sleep $every
      i=$(( $i + $every ))
    fi
  done

  if [[ "${DEPLOYMENT_SUCCESS}" != "true" ]]; then
    echo "ecs service $1 missing in $2"
    fi
    exit 1
