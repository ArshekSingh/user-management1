variable "environment" {
    type= string
    default= "uat"
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
    default="user-deployment"
}
variable "memory" {
    type= number
    default=512
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
    default="user-management"
}
variable "api_health"{
    type=string
    default="/user/actuator/health"
}
variable "vpc_id" {
    type= string
    default="vpc-0ef1c909227de196b"
}
variable "common_tags" {
    type= string
    default="user-common"
}
variable "containers_min" {
    type= number
    default=1
}
variable "containers_max" {
    type= number
    default=3
}
variable "clustername" {
    type= string
    default="SASTech-Devops-Preprod"
}
variable "lb_listener_arn"{
    type = string
    default="arn:aws:elasticloadbalancing:ap-south-1:305949049023:listener/app/Devops-ALB/e1dd7790c5b34474/2b8b3ad1ea006326"
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
    default="user-latest"
}
