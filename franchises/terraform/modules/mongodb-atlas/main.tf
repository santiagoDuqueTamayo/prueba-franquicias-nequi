# terraform/modules/mongodb-atlas/main.tf

# ============================================
# MongoDB Atlas Project
# ============================================
resource "mongodbatlas_project" "project" {
  name   = var.project_name
  org_id = var.org_id

  tags = {
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

# ============================================
# MongoDB Atlas Cluster (M0 Free Tier)
# ============================================
resource "mongodbatlas_cluster" "cluster" {
  project_id = mongodbatlas_project.project.id
  name       = var.cluster_name

  # Cluster Type
  cluster_type = "REPLICASET"

  # Cloud Provider Settings
  provider_name               = "TENANT"
  backing_provider_name       = "AWS"
  provider_region_name        = var.cluster_region
  provider_instance_size_name = "M0" # Free Tier

  # MongoDB Version
  mongo_db_major_version = var.mongodb_version

  # Auto-scaling (no disponible en M0)
  auto_scaling_disk_gb_enabled = false
}

# ============================================
# Database User
# ============================================
resource "mongodbatlas_database_user" "user" {
  username           = var.database_username
  password           = var.database_password
  project_id         = mongodbatlas_project.project.id
  auth_database_name = "admin"

  roles {
    role_name     = "readWriteAnyDatabase"
    database_name = "admin"
  }

  # Scopes (opcional, para limitar acceso por cluster)
  scopes {
    name = mongodbatlas_cluster.cluster.name
    type = "CLUSTER"
  }
}

# ============================================
# IP Access List (Whitelist)
# ============================================
resource "mongodbatlas_project_ip_access_list" "allow_all" {
  project_id = mongodbatlas_project.project.id
  cidr_block = "0.0.0.0/0" # ⚠️ Solo para desarrollo
  comment    = "Allow access from anywhere (development only)"
}

# Para producción, usa IPs específicas:
# resource "mongodbatlas_project_ip_access_list" "production" {
#   project_id = mongodbatlas_project.project.id
#   ip_address = "203.0.113.45"  # IP de tu servidor
#   comment    = "Production server"
# }