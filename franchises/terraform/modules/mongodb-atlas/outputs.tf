# terraform/modules/mongodb-atlas/outputs.tf

output "project_id" {
  description = "MongoDB Atlas Project ID"
  value       = mongodbatlas_project.project.id
}

output "cluster_name" {
  description = "MongoDB Cluster Name"
  value       = mongodbatlas_cluster.cluster.name
}

output "cluster_id" {
  description = "MongoDB Cluster ID"
  value       = mongodbatlas_cluster.cluster.cluster_id
}

output "connection_string" {
  description = "MongoDB Connection String (standard)"
  value = replace(
    mongodbatlas_cluster.cluster.connection_strings[0].standard_srv,
    "mongodb+srv://",
    "mongodb+srv://${var.database_username}:${var.database_password}@"
  )
}

output "cluster_state" {
  description = "Current state of the cluster"
  value       = mongodbatlas_cluster.cluster.state_name
}