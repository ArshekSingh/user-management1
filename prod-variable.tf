variable "environment" {
    type= string
    default= "prod"
}
variable "aws_region" {
    type= string
    default="ap-south-1"
}
variable "ecr_repo_name" {
    type= string
    default="sastech-devops-repository"
}
variable "deployment_tag" {
    type= string
    default="usermanagement-deployment"
}
variable "memory" {
    type= number
    default=2048
}
variable "cpu" {
    type= number
    default= 1024
}
variable "port" {
    type= number
    default=8080
}
variable "service" {
    type= string
    default="usermanagement"
}
variable "servicetg" {
    type= string
    default="ums"
}
variable "api_health"{
    type=string
    default="/ums/actuator/health"
}
variable "vpc_id" {
    type= string
    default="vpc-03f2eebc6fb8a5784"
}
variable "common_tags" {
    type= string
    default="usermanagement-common"
}
variable "containers_min" {
    type= number
    default=1
}
variable "containers_max" {
    type= number
    default=2
}
variable "clustername" {
    type= string
    default="prod-sas-ecs"
}
variable "lb_listener_arn"{
    type = string
    default="arn:aws:elasticloadbalancing:ap-south-1:305949049023:listener/app/sastech-svcl-prod-alb/39b5a698ac5d9cdf/57b90b675e845a07"
}
variable "service_role"{
    type= string
    default= "arn:aws:iam::305949049023:role/ecs-service-autoscale-role"
}
variable "task_role"{
    type= string
    default= "arn:aws:iam::305949049023:role/ecs-test-task-role"
}
variable "tag"{ 
    type= string
    default="usermanagement-latest"
}