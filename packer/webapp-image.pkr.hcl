packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

variable "project_id" {
  description = "The GCP project ID"
  type        = string
  default     = "brian-csye6225"
}

variable "zone" {
  description = "The GCP zone"
  type        = string
  default     = "us-east1-c"
}

variable "source_image_family" {
  description = "The source image family"
  type        = string
  default     = "centos-stream-8"
}

variable "ssh_username" {
  description = "The SSH username"
  type        = string
  default     = "centos"
}

variable "network" {
  description = "The network to run in"
  type        = string
  default     = "default"
}


source "googlecompute" "webapp-image" {
  project_id          = "${var.project_id}"
  source_image_family = "${var.source_image_family}"
  ssh_username        = "${var.ssh_username}"
  zone                = "${var.zone}"
  network             = "${var.network}"
}

build {
  sources = ["source.googlecompute.webapp-image"]

  provisioner "shell" {
    script = "scripts/update.sh"
  }

  provisioner "shell" {
    script = "scripts/install_java.sh"
  }

  provisioner "shell" {
    script = "scripts/install_mysql.sh"
  }

}