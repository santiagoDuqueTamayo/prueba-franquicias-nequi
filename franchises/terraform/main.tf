# terraform/main.tf

# ============================================
# Random Password for Database User
# ============================================
resource "random_password" "db_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

# ============================================
# MongoDB Atlas Module
# ============================================
module "mongodb_atlas" {
  source = "./modules/mongodb-atlas"

  # Organization
  org_id = var.mongodb_atlas_org_id

  # Project
  project_name = var.project_name
  environment  = var.environment

  # Cluster
  cluster_name    = var.cluster_name
  cluster_region  = var.cluster_region
  mongodb_version = var.mongodb_version

  # Database User
  database_name     = var.database_name
  database_username = var.database_username
  database_password = random_password.db_password.result
}