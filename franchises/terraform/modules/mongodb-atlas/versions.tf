# terraform/modules/mongodb-atlas/versions.tf (si existe)

terraform {
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas" # ← Asegurar que sea mongodb/
      version = "~> 1.15"
    }
  }
}