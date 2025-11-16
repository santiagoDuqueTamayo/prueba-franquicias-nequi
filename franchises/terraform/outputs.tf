# terraform/outputs.tf

output "project_id" {
  description = "MongoDB Atlas Project ID"
  value       = module.mongodb_atlas.project_id
}

output "cluster_name" {
  description = "MongoDB Cluster Name"
  value       = module.mongodb_atlas.cluster_name
}

output "connection_string" {
  description = "MongoDB Connection String"
  value       = module.mongodb_atlas.connection_string
  sensitive   = true
}

output "database_username" {
  description = "Database Username"
  value       = var.database_username
}

output "database_password" {
  description = "Database Password"
  value       = random_password.db_password.result
  sensitive   = true
}

output "database_name" {
  description = "Database Name"
  value       = var.database_name
}

# Output formateado para Spring Boot
output "spring_boot_config" {
  description = "Configuration for Spring Boot application.yml"
  value = <<-EOT
    spring:
      data:
        mongodb:
          uri: ${module.mongodb_atlas.connection_string}
          database: ${var.database_name}
  EOT
  sensitive = true
}