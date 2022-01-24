[
  {
    "name": "${name}",
    "image": "305949049023.dkr.ecr.ap-south-1.amazonaws.com/${ecr_repo_name}:${tag}",
    "essential": true,
    "memory": ${memory},
    "cpu": ${cpu},
    "logConfiguration": {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "${cloudwatch_group }",
        "awslogs-region": "${region}",
        "awslogs-create-group": "true",
        "awslogs-stream-prefix": "${cloudwatch_group }"
      }
    },
    "environment" : [
      {
        "name" : "CHECKIT_ENV",
        "value" : "${environment}"
      },
      {
        "name": "NODE_ENV",
        "value": "${node_env}"
      }
    ],
    "portMappings": [
      {
        "containerPort": ${port},
        "protocol": "tcp"
      }
    ]
  }
]

