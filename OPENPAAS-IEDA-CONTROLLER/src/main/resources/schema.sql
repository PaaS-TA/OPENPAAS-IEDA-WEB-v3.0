create database ieda default character set utf8 collate utf8_general_ci;

#ieda 데이터 베이스 생성
use ieda;

SET character_set_client = utf8;
SET character_set_results = utf8;
SET character_set_connection = utf8;
SET character_set_server = utf8;
SET NAMES 'UTF8';
SET CHARACTER SET 'UTF8';

CREATE TABLE ieda_user
(
  user_id                           VARCHAR(255)  NOT NULL,
  user_password                     VARCHAR(255)  NOT NULL,
  user_name                         VARCHAR(255)  NOT NULL,
  email                             VARCHAR(255)  NOT NULL,
  role_id                           INT(11)       NOT NULL,
  init_pass_yn                      CHAR(1)       NOT NULL,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#롤 아이디    ROLE_ID                 INT(11) NOT NULL
#일련 번호    SEQ                     INT(11) NOT NULL
#권한 코드    AUTH_CODE               VARCHAR(255) NOT NULL
#생성자      CREATE_USER_ID          VARCHAR(255) NOT NULL
#생성일자     CREATE_DATE             DATE NOT NULL  SYSDATE
#수정자      UPDATE_USER_ID          VARCHAR(255) NOT NULL
#수정일자     UPDATE_DATE             DATE NOT NULL  SYSDATE

CREATE TABLE ieda_role
(
  role_id                           INT(11)       NOT NULL AUTO_INCREMENT,
  role_name                         VARCHAR(255)  NOT NULL,
  role_description                  VARCHAR(255)  NULL,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (role_id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_role_detail
(
  seq                               INT(11)       NOT NULL,
  role_id                           INT(11)       NOT NULL,
  auth_code                         VARCHAR(255)  NOT NULL,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (role_id, seq)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_common_code
(
  code_idx                          INT(11)       NOT NULL AUTO_INCREMENT,
  code_name                         VARCHAR(255)  NOT NULL,
  code_value                        VARCHAR(255)  NOT NULL,
  code_name_kr                      VARCHAR(255),
  code_description                  VARCHAR(255),
  sort_order                        INT(11)       NOT NULL,
  sub_group_code                    VARCHAR(255),
  usub_group_code                   VARCHAR(255),
  parent_code                       VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (code_idx) ,
  INDEX COMMON_CODE_INDEX (code_value)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_director_config
(
  ieda_director_config_seq          INT(11)       NOT NULL AUTO_INCREMENT,
  current_deployment                VARCHAR(255),
  default_yn                        VARCHAR(255),
  director_cpi                      VARCHAR(255),
  director_name                     VARCHAR(255),
  director_port                     INT(11),
  director_url                      VARCHAR(255),
  director_uuid                     VARCHAR(255),
  director_version                  VARCHAR(255),
  user_id                           VARCHAR(255),
  user_password                     VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  credential_file                   VARCHAR(255),
  PRIMARY KEY (ieda_director_config_seq)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

# public stemcell
CREATE TABLE ieda_public_stemcells
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  download_status                   VARCHAR(255),
  download_link                     VARCHAR(255),
  iaas                              VARCHAR(255),
  stemcell_name                     VARCHAR(255),
  os                                VARCHAR(255),
  os_version                        VARCHAR(255),
  size                              VARCHAR(255),
  stemcell_filename                 VARCHAR(255),
  stemcell_version                  VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#system_release(new)
CREATE TABLE ieda_system_releases
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  release_type                      VARCHAR(255),
  release_name                      VARCHAR(255),
  size                              VARCHAR(255),
  release_filename                  VARCHAR(255),
  download_status                   VARCHAR(255),
  download_link                     VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  iaas                              VARCHAR(255),
  primary key(id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#Manifast
SET FOREIGN_KEY_CHECKS=0 ;
/* Drop Tables */
DROP TABLE IF EXISTS ieda_manifest_template CASCADE;
CREATE TABLE ieda_manifest_template
(
  id                                INT           NOT NULL AUTO_INCREMENT,
  deploy_type                       VARCHAR(255)  NOT NULL,
  iaas_type                         VARCHAR(255)  NOT NULL,
  release_type                      VARCHAR(255)  NOT NULL,
  template_version                  VARCHAR(255)  NOT NULL,
  min_release_version               VARCHAR(255)  NOT NULL,
  common_base_template              VARCHAR(255)  NOT NULL,
  common_job_template               VARCHAR(255)  NOT NULL,
  common_option_template            VARCHAR(255),
  iaas_property_template            VARCHAR(255),
  option_network_template           VARCHAR(255),
  option_resource_template          VARCHAR(255),
  option_etc                        VARCHAR(255),
  meta_template                     VARCHAR(255),
  input_template                    VARCHAR(255),
  input_template_second             VARCHAR(255),
  input_template_third              VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id, deploy_type, iaas_type, min_release_version)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1 ;

#Manifest
SET FOREIGN_KEY_CHECKS=0 ;

DROP TABLE IF EXISTS ieda_manifest CASCADE;
CREATE TABLE ieda_manifest
(
  id                                INT           NOT NULL AUTO_INCREMENT,
  manifest_idx                      INT,
  manifest_file                     VARCHAR(255),
  iaas                              VARCHAR(255),
  deployment_name                   VARCHAR(255),
  description                       VARCHAR(255),
  path                              VARCHAR(255),
  deploy_status                     VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

# BOOTSTRAP
CREATE TABLE ieda_bootstrap
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas_config_id                    INT(11)       NOT NULL,
  iaas_type                         VARCHAR(255)  NOT NULL,
  deployment_name                   VARCHAR(100),
  director_name                     VARCHAR(100),
  credential_key_name               VARCHAR(100),
  ntp                               VARCHAR(100),
  bosh_release                      VARCHAR(100),
  bosh_cpi_release                  VARCHAR(100),
  bosh_bpm_release                  VARCHAR(100),
  bosh_uaa_release                  VARCHAR(100),
  os_conf_release                   VARCHAR(100),
  bosh_cred_hub_release             VARCHAR(100),
  enable_snapshots                  VARCHAR(100),
  snapshot_schedule                 VARCHAR(100),
  subnet_id                         VARCHAR(100),
  network_name                      VARCHAR(100),
  private_static_ip                 VARCHAR(100),
  public_static_ip                  VARCHAR(100),
  subnet_range                      VARCHAR(100),
  subnet_gateway                    VARCHAR(100),
  subnet_dns                        VARCHAR(100),
  public_subnet_id                  VARCHAR(100),
  public_subnet_range               VARCHAR(100),
  public_subnet_gateway             VARCHAR(100),
  public_subnet_dns                 VARCHAR(100),
  stemcell                          VARCHAR(100),
  cloud_instance_type               VARCHAR(100),
  bosh_password                     VARCHAR(255),
  resource_pool_cpu                 VARCHAR(100),
  resource_pool_ram                 VARCHAR(100),
  resource_pool_disk                VARCHAR(100),
  deployment_file                   VARCHAR(255),
  deploy_status                     VARCHAR(100),
  deploy_log                        LONGTEXT,
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  paasta_monitoring_use             VARCHAR(100) NULL,
  paasta_monitoring_agent_release   VARCHAR(100) NULL,
  paasta_monitoring_syslog_release  VARCHAR(100) NULL,
  metric_url                        VARCHAR(100) NULL,
  syslog_address                    VARCHAR(100) NULL,
  syslog_port                       VARCHAR(100) NULL,
  syslog_transport                  VARCHAR(100) NULL,
  PRIMARY KEY (id)	
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;	

# BOSH	
CREATE TABLE ieda_bosh	
(	
  id                                INT(11)       NOT NULL AUTO_INCREMENT,	
  iaas_type                         VARCHAR(255)  NOT NULL,	
  aws_access_key_id                 VARCHAR(255),	
  aws_secret_access_id              VARCHAR(255),	
  aws_region                        VARCHAR(255),
  aws_availability_zone             VARCHAR(255),
  openstack_auth_url                VARCHAR(255),
  openstack_tenant                  VARCHAR(255),
  openstack_user_name               VARCHAR(255),
  openstack_api_key                 VARCHAR(255),
  vcenter_address                   VARCHAR(255),
  vcenter_user                      VARCHAR(255),
  vcenter_password                  VARCHAR(255),
  vcenter_datacenter_name           VARCHAR(255),
  vcenter_vm_folder                 VARCHAR(255),
  vcenter_template_folder           VARCHAR(255),
  vcenter_datastore                 VARCHAR(255),
  vcenter_persistent_datastore      VARCHAR(255),
  vcenter_disk_path                 VARCHAR(255),
  vcenter_clusters                  VARCHAR(255),
  default_security_groups           VARCHAR(100),
  private_key_name                  VARCHAR(100),
  deployment_name                   VARCHAR(100),
  director_uuid                     VARCHAR(100),
  release_version                   VARCHAR(100),
  ntp                               VARCHAR(100),
  director_name                     VARCHAR(100),
  snapshot_schedule                 VARCHAR(100),
  enable_snapshots                  VARCHAR(100),
  deployment_file                   VARCHAR(100),
  deploy_status                     VARCHAR(100),
  task_id                           INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_network
(
  id                                INT(11)       NOT NULL,
  deploy_type                       VARCHAR(100)  NOT NULL,
  seq                               INT(11)       NOT NULL DEFAULT 0,
  net                               VARCHAR(255)  NOT NULL,
  public_static_ip                  VARCHAR(255),
  subnet_range                      VARCHAR(255),
  subnet_gateway                    VARCHAR(255),
  subnet_reserved_from              VARCHAR(255),
  subnet_reserved_to                VARCHAR(255),
  subnet_static_from                VARCHAR(255),
  subnet_static_to                  VARCHAR(255),
  subnet_dns                        VARCHAR(255),
  subnet_id                         VARCHAR(255),
  network_name                      VARCHAR(255),
  cloud_security_groups             VARCHAR(255),
  availability_zone                 VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id, deploy_type, seq)
 ) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#Resource
CREATE TABLE ieda_resource
(
  id                                INT(11)       NOT NULL,
  deploy_type                       VARCHAR(100)  NOT NULL,
  bosh_password                     VARCHAR(255),
  stemcell_name                     VARCHAR(255),
  stemcell_version                  VARCHAR(255),
  small_type_flavor                 VARCHAR(255),
  small_type_cpu                    INT(11),
  small_type_ram                    INT(11),
  small_type_disk                   INT(11),
  medium_type_flavor                VARCHAR(255),
  medium_type_cpu                   INT(11),
  medium_type_ram                   INT(11),
  medium_type_disk                  INT(11),
  large_type_flavor                 VARCHAR(255),
  large_type_cpu                    INT(11),
  large_type_ram                    INT(11),
  large_type_disk                   INT(11),
  runner_type_flavor                VARCHAR(255),
  runner_type_cpu                   INT(11),
  runner_type_ram                   INT(11),
  runner_type_disk                  INT(11),
  runner_instance_number            INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  enable_windows_stemcell           VARCHAR(100) DEFAULT NULL,
  windows_stemcell_name             VARCHAR(255) NULL,
  windows_stemcell_version          VARCHAR(255) NULL,
  windows_cell_instance             VARCHAR(255) NULL,
  PRIMARY KEY (id, deploy_type)
)ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#cf
CREATE TABLE ieda_cf
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(255)  NOT NULL,
  deployment_name                   VARCHAR(100),
  director_uuid                     VARCHAR(100),
  cf_db_type                        VARCHAR(100),
  release_name                      VARCHAR(100),
  release_version                   VARCHAR(100),
  domain                            VARCHAR(100),
  domain_organization               VARCHAR(100),
  country_code                      VARCHAR(255),
  state_name                        VARCHAR(255),
  locality_name                     VARCHAR(255),
  organization_name                 VARCHAR(255),
  unit_name                         VARCHAR(255),
  email                             VARCHAR(255),
  key_file                          VARCHAR(255),
  deployment_file                   VARCHAR(255),
  deploy_status                     VARCHAR(100),
  task_id                           INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  user_add_ssh                      LONGTEXT NULL,
  inception_os_user_name            VARCHAR(255) NULL,
  cf_admin_password                 VARCHAR(255) NULL,
  portal_domain                     VARCHAR(255) NULL,
  paasta_monitoring_use             VARCHAR(100) DEFAULT NULL,
  metric_url                        VARCHAR(255) NULL,
  syslog_address                    VARCHAR(255) NULL,
  syslog_port                       VARCHAR(255) NULL,
  syslog_custom_rule                VARCHAR(255) NULL,
  syslog_fallback_servers           VARCHAR(255) NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#job setting
CREATE table ieda_job_setting (
  id                            INT          NOT NULL AUTO_INCREMENT,
  seq                           INT          NOT NULL,
  deploy_type                   VARCHAR(100) NOT NULL,
  job_id                        VARCHAR(100),
  zone                          VARCHAR(100),
  instances                     INT NOT NULL,
  create_user_id                VARCHAR(255) NOT NULL,
  create_date                   DATE         NOT NULL,
  update_user_id                VARCHAR(255) NOT NULL,
  update_date                   DATE         NOT NULL,
  PRIMARY KEY (id,seq, deploy_type)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#cf deployment job template
CREATE table ieda_cf_job_template (
  id                             INT NOT NULL AUTO_INCREMENT, 
  seq                            INT NOT NULL,
  deploy_type                    VARCHAR(100) NOT NULL,
  job_name                       VARCHAR(100),
  min_release_version            VARCHAR(100),
  max_release_version            VARCHAR(100),
  zone_z1                        VARCHAR(100),
  zone_z2                        VARCHAR(100),
  zone_z3                        VARCHAR(100),
  create_user_id                 VARCHAR(255) NOT NULL,
  create_date                    DATE NOT NULL,
  update_user_id                 VARCHAR(255) NOT NULL,
  update_date                    DATE NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_service_pack
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas                              VARCHAR(255)  NOT NULL,
  deployment_name                   VARCHAR(100),
  deployment_file                   VARCHAR(100),
  deploy_status                     VARCHAR(100),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_iaas_account 
(
  id                          INT(11)      NOT NULL auto_increment,
  iaas_type                   VARCHAR(100) NOT NULL,
  account_name                VARCHAR(100) NOT NULL,
  common_access_endpoint      VARCHAR(255) NULL,
  common_access_user          VARCHAR(255) NULL,
  common_access_secret        VARCHAR(255) NULL,
  openstack_keystone_version  VARCHAR(255) NULL,
  common_tenant               VARCHAR(255) NULL,
  common_project              VARCHAR(255) NULL,
  common_region               VARCHAR(255) NULL,
  openstack_domain            VARCHAR(255) NULL,
  google_json_key             VARCHAR(255) NULL,
  azure_subscription_id       VARCHAR(255) NULL,
  default_yn                  VARCHAR(100) NULL,
  create_user_id              VARCHAR(255) NOT NULL,
  create_date                 DATE         NOT NULL,
  update_user_id              VARCHAR(255) NOT NULL,
  update_date                 DATE NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_iaas_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  account_id                            INT(11)      NOT NULL,
  iaas_config_alias                     VARCHAR(255) NOT NULL,
  common_security_group                 VARCHAR(255) NULL,
  common_keypair_name                   VARCHAR(255) NULL,
  common_keypair_path                   VARCHAR(255) NULL,
  common_region                         VARCHAR(255) NULL,
  common_availability_zone              VARCHAR(255) NULL,
  google_public_key                     LONGTEXT NULL,
  vsphere_vcenter_cluster               VARCHAR(255) NULL,
  vsphere_vcenter_datacenter_name       VARCHAR(255) NULL,
  vsphere_vcenter_datastore             VARCHAR(255) NULL,
  vsphere_vcenter_disk_path             VARCHAR(255) NULL,
  vsphere_vcenter_persistent_datastore  VARCHAR(255) NULL,
  vsphere_vcenter_template_folder       VARCHAR(255) NULL,
  vsphere_vcenter_vm_folder             VARCHAR(255) NULL,
  azure_resource_group                  VARCHAR(255) NULL,
  azure_storage_account_name            VARCHAR(255) NULL,
  azure_ssh_public_key                  LONGTEXT NULL,
  azure_private_key                     VARCHAR(255) NULL,
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_director_credential
(
  id                                    INT(11)      NOT NULL auto_increment,
  director_credential_name              VARCHAR(100),
  director_credential_key_name          VARCHAR(100),
  director_public_ip                    VARCHAR(100),
  director_private_ip                    VARCHAR(100),
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_hybrid_director_config
(
  ieda_director_config_seq          INT(11)  NOT NULL AUTO_INCREMENT,
  current_deployment                VARCHAR(255),
  director_cpi                      VARCHAR(255),
  director_name                     VARCHAR(255),
  director_port                     INT(11),
  director_url                      VARCHAR(255),
  director_uuid                     VARCHAR(255),
  director_version                  VARCHAR(255),
  user_id                           VARCHAR(255),
  user_password                     VARCHAR(255),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  credential_file                   VARCHAR(255),
  director_type                       VARCHAR(100),
  PRIMARY KEY (ieda_director_config_seq)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bootstrap_cpi_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  cpi_name                              VARCHAR(100)  NOT NULL,
  iaas_config_id                        INT(11) NOT NULL,
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_bootstrap_resource_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  resource_config_name                  VARCHAR(100) NOT NULL,
  stemcell_name                         VARCHAR(100) NOT NULL,
  instance_type                         VARCHAR(100) NOT NULL,
  vm_password                           VARCHAR(100),
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bootstrap_credential_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100),
  director_credential_name              VARCHAR(100),
  network_config_name                   VARCHAR(100),
  director_credential_key_name          VARCHAR(100),
  director_public_ip                    VARCHAR(100),
  director_private_ip                   VARCHAR(100),
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bootstrap_config
(
  id                                INT(11)      NOT NULL auto_increment,
  iaas_type                         VARCHAR(100),
  bootstrap_config_name             VARCHAR(100),
  network_config_name               VARCHAR(100),
  cpi_config_name                   VARCHAR(100),
  default_config_name               VARCHAR(100),
  resource_config_name              VARCHAR(100),
  deployment_file                   VARCHAR(255),
  deploy_status                     VARCHAR(100),
  deploy_log                        LONGTEXT,
  create_user_id                    VARCHAR(255) NOT NULL,
  create_date                       DATE         NOT NULL,
  update_user_id                    VARCHAR(255) NOT NULL,
  update_date                       DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bootstrap_network_config (
  id                                INT(11) NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(100) NOT NULL,
  network_config_name               VARCHAR(100) NOT NULL,
  subnet_id                         VARCHAR(100) NOT NULL,
  private_static_ip                 VARCHAR(100) NOT NULL,
  subnet_range                      VARCHAR(100) NOT NULL,
  subnet_gateway                    VARCHAR(100) NOT NULL,
  subnet_dns                        VARCHAR(100) NOT NULL,
  public_static_ip                  VARCHAR(100) NOT NULL,
  create_user_id                    VARCHAR(255) NOT NULL,
  create_date                       DATE NOT NULL,
  update_user_id                    VARCHAR(255) NOT NULL,
  update_date                       DATE NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_bootstrap_default_config (
  id                                INT(11) NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(100) NOT NULL,
  default_config_name               VARCHAR(100) NOT NULL,
  deployment_name                   VARCHAR(100) DEFAULT NULL,
  director_name                     VARCHAR(100) DEFAULT NULL,
  ntp                               VARCHAR(100) DEFAULT NULL,
  credential_key_name               VARCHAR(100) NOT NULL,
  boshRelease                       VARCHAR(100) NOT NULL,
  bosh_cpi_release                  VARCHAR(100) DEFAULT NULL,
  bosh_bpm_release                  VARCHAR(100) DEFAULT NULL,
  uaa_release                       VARCHAR(100) DEFAULT NULL,
  os_conf_release                   VARCHAR(100) DEFAULT NULL,
  cred_hub_release                  VARCHAR(100) DEFAULT NULL,
  enable_snapshots                  VARCHAR(100) DEFAULT NULL,
  snapshot_schedule                 VARCHAR(100) DEFAULT NULL,
  paasta_monitoring_use             VARCHAR(100) DEFAULT NULL,
  syslog_release                    VARCHAR(100) DEFAULT NULL,
  syslog_address                    VARCHAR(100) DEFAULT NULL,
  syslog_port                       VARCHAR(100) DEFAULT NULL,
  syslog_transport                  VARCHAR(100) DEFAULT NULL,
  metric_url                        VARCHAR(100) DEFAULT NULL,
  paasta_monitoring_release         VARCHAR(100) DEFAULT NULL,
  create_user_id                    VARCHAR(255) NOT NULL,
  create_date                       date NOT NULL,
  update_user_id                    VARCHAR(255) NOT NULL,
  update_date                       date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#hb cf deploymnet

CREATE TABLE ieda_hb_cfDeployment_default_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  default_config_name                   VARCHAR(100) NOT NULL,
  cf_deployment_version                 VARCHAR(100) NOT NULL,
  domain                                VARCHAR(100) NOT NULL,
  domain_organization                   VARCHAR(100) NOT NULL,
  cf_db_type                            VARCHAR(100) NOT NULL,
  inception_os_user_name                VARCHAR(255) NULL,
  cf_admin_password                     VARCHAR(255) NULL,
  portal_domain                         VARCHAR(255) NULL,
  paasta_monitoring_use                 VARCHAR(100) DEFAULT NULL,
  metric_url                            VARCHAR(255) NULL,
  syslog_address                        VARCHAR(255) NULL,
  syslog_port                           VARCHAR(255) NULL,
  syslog_custom_rule                    VARCHAR(255) NULL,
  syslog_fallback_servers               VARCHAR(255) NULL,
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_hb_cfDeployment_network_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  public_static_ip                      VARCHAR(100) NOT NULL,                           
  network_name                          VARCHAR(100) NOT NULL,
  subnet_reserved_ip                    VARCHAR(100),
  subnet_static_ip                      VARCHAR(100),
  subnet_id_1                           VARCHAR(100) NOT NULL,
  security_group_1                      VARCHAR(100) NOT NULL,
  subnet_range_1                        VARCHAR(100) NOT NULL,
  subnet_gateway_1                      VARCHAR(100) NOT NULL,
  subnet_dns_1                          VARCHAR(100) NOT NULL,
  subnet_reserved_from_1                VARCHAR(100) NOT NULL,
  subnet_reserved_to_1                  VARCHAR(100) NOT NULL,
  subnet_static_from_1                  VARCHAR(100) NOT NULL,
  subnet_static_to_1                    VARCHAR(100) NOT NULL,
  availability_zone_1                   VARCHAR(100),
  subnet_id_2                           VARCHAR(100),
  security_group_2                      VARCHAR(100),
  subnet_range_2                        VARCHAR(100),
  subnet_gateway_2                      VARCHAR(100),
  subnet_dns_2                          VARCHAR(100),
  subnet_reserved_from_2                VARCHAR(100),
  subnet_reserved_to_2                  VARCHAR(100),
  subnet_static_from_2                  VARCHAR(100),
  subnet_static_to_2                    VARCHAR(100),
  availability_zone_2                   VARCHAR(100),
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_hb_cfDeployment_resource_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  resource_config_name                  VARCHAR(100) NOT NULL,
  stemcell_name                         VARCHAR(100) NOT NULL,
  instance_type_s                       VARCHAR(100) NOT NULL,
  instance_type_m                       VARCHAR(100) NOT NULL,
  instance_type_l                       VARCHAR(100) NOT NULL,
  stemcell_version                      VARCHAR(100) NOT NULL,
  director_id                           VARCHAR(100),
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_hb_cfDeployment_credential_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  credential_config_name                VARCHAR(100) NOT NULL,
  domain                                VARCHAR(100) NOT NULL,
  country_code                          VARCHAR(100) NOT NULL,
  city                                  VARCHAR(100) NOT NULL,
  company                               VARCHAR(100) NOT NULL,
  job_title                             VARCHAR(100) NOT NULL,
  email_address                         VARCHAR(100) NOT NULL,
  key_file_name                         VARCHAR(100) NOT NULL,
  release_name                          VARCHAR(100) NOT NULL,
  release_version                       VARCHAR(100) NOT NULL,
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;


CREATE TABLE ieda_hb_cfDeployment_instance_config
(
  id                                    INT(11)      NOT NULL auto_increment,
  iaas_type                             VARCHAR(100) NOT NULL,
  instance_config_name                  VARCHAR(100) NOT NULL,
  cf_deployment_name                    VARCHAR(100) NOT NULL,
  cf_deployment_version                 VARCHAR(100) NOT NULL,
  adapter                               VARCHAR(100),
  api                                   VARCHAR(100),
  cc_worker                             VARCHAR(100),
  consul                                VARCHAR(100),
  the_database                          VARCHAR(100),
  scheduler                             VARCHAR(100),
  diego_api                             VARCHAR(100),
  diego_cell                            VARCHAR(100),
  doppler                               VARCHAR(100),
  haproxy                               VARCHAR(100),
  log_api                               VARCHAR(100),
  nats                                  VARCHAR(100),
  router                                VARCHAR(100),
  singleton_blobstore                   VARCHAR(100),
  tcp_router                            VARCHAR(100),
  uaa                                   VARCHAR(100),        
  create_user_id                        VARCHAR(255) NOT NULL,
  create_date                           DATE         NOT NULL,
  update_user_id                        VARCHAR(255) NOT NULL,
  update_date                           DATE         NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

CREATE TABLE ieda_hb_cfDeployment
(
  id                                INT(11)       NOT NULL AUTO_INCREMENT,
  iaas_type                         VARCHAR(255)  NOT NULL,
  cf_deployment_config_name         VARCHAR(100),
  network_config_name               VARCHAR(100),
  default_config_name               VARCHAR(100),
  resource_config_name              VARCHAR(100),
  instance_config_name              VARCHAR(100),
  credential_config_name            VARCHAR(100),
  cloud_config_file                 VARCHAR(100),
  deploy_status                     VARCHAR(100),
  task_id                           INT(11),
  create_user_id                    VARCHAR(255)  NOT NULL,
  create_date                       DATE          NOT NULL,
  update_user_id                    VARCHAR(255)  NOT NULL,
  update_date                       DATE          NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARSET=utf8;

#Setting AUTO_INCREMENT
ALTER TABLE ieda_role AUTO_INCREMENT=1000;
ALTER TABLE ieda_common_code AUTO_INCREMENT=1000;
ALTER TABLE ieda_director_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_public_stemcells AUTO_INCREMENT=1000;
ALTER TABLE ieda_system_releases AUTO_INCREMENT=1000;
ALTER TABLE ieda_manifest_template AUTO_INCREMENT=1000;
ALTER TABLE ieda_manifest AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap AUTO_INCREMENT=1000;
ALTER TABLE ieda_cf AUTO_INCREMENT=1000;
ALTER TABLE ieda_service_pack AUTO_INCREMENT=1000;
ALTER TABLE ieda_iaas_account AUTO_INCREMENT=1000;
ALTER TABLE ieda_iaas_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_cf_job_template AUTO_INCREMENT=1000;
ALTER TABLE ieda_director_credential AUTO_INCREMENT=1000;
ALTER TABLE ieda_hybrid_director_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap_credential_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap_cpi_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap_default_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap_network_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap_resource_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_bootstrap_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_hb_cfDeployment_default_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_hb_cfDeployment_network_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_hb_cfDeployment_resource_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_hb_cfDeployment_credential_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_hb_cfDeployment_instance_config AUTO_INCREMENT=1000;
ALTER TABLE ieda_hb_cfDeployment AUTO_INCREMENT=1000;
