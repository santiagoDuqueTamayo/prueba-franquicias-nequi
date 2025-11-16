# terraform/variables.tf

# ============================================
# MongoDB Atlas API Credentials
# ============================================
variable "mongodb_atlas_public_key" {
  description = "MongoDB Atlas Public API Key"
  type        = string
  sensitive   = true
}

variable "mongodb_atlas_private_key" {
  description = "MongoDB Atlas Private API Key"
  type        = string
  sensitive   = true
}

variable "mongodb_atlas_org_id" {
  description = "MongoDB Atlas Organization ID"
  type        = string
}

# ============================================
# Project Configuration
# ============================================
variable "project_name" {
  description = "Name of the MongoDB Atlas project"
  type        = string
  default     = "franchises-project"
}

# ============================================
# Cluster Configuration
# ============================================
variable "cluster_name" {
  description = "Name of the MongoDB cluster"
  type        = string
  default     = "franchises-cluster"
}

variable "cluster_region" {
  description = "AWS region for the cluster"
  type        = string
  default     = "US_EAST_1"

  validation {
    condition     = contains(["US_EAST_1", "US_WEST_2", "EU_WEST_1"], var.cluster_region)
    error_message = "Region must be US_EAST_1, US_WEST_2, or EU_WEST_1"
  }
}

variable "mongodb_version" {
  description = "MongoDB version"
  type        = string
  default     = "7.0"
}

# ============================================
# Database Configuration
# ============================================
variable "database_name" {
  description = "Name of the database"
  type        = string
  default     = "franchises_db"
}

variable "database_username" {
  description = "Database admin username"
  type        = string
  default     = "admin"
}

# ============================================
# Environment
# ============================================
variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "dev"
}