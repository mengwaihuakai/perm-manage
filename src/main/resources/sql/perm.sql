DROP TABLE
IF EXISTS perm_role_permission;

DROP TABLE
IF EXISTS perm_user_role;

DROP TABLE
IF EXISTS perm_user;

DROP TABLE
IF EXISTS perm_role;

DROP TABLE
IF EXISTS perm_permission;

DROP TABLE
IF EXISTS monitor_log;

DROP TABLE
IF EXISTS perm_shiro_session;


CREATE TABLE `perm_user` (
	`id` INT (11) NOT NULL AUTO_INCREMENT,
	`account` VARCHAR (50) DEFAULT '' COMMENT '账号',
	`password` VARCHAR (255) DEFAULT '' COMMENT '密码',
	`status` INT DEFAULT 0 COMMENT '是否冻结',
	`last_login_time` TIMESTAMP NULL DEFAULT NULL COMMENT '最后一次登录时间',
	`last_login_ip` varchar(20) CHARACTER SET latin1 DEFAULT NULL,
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	UNIQUE KEY (account),
	index account_index(account)
) ENGINE = INNODB DEFAULT CHARSET = 'utf8' COMMENT '用户表';


CREATE TABLE `perm_role` (
	`id` INT (11) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
	`name` VARCHAR (100) DEFAULT '' COMMENT '角色名称',
	 `code` VARCHAR (100) DEFAULT '' COMMENT '角色代码',
	`status` INT DEFAULT 0 COMMENT '角色状态',
	`create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT '角色表';


CREATE TABLE `perm_permission` (
	`id` INT (11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
	`name` VARCHAR (200) DEFAULT '' COMMENT '权限名称',
	`code` VARCHAR (100) DEFAULT '' COMMENT '权限代码--菜单名称代码:菜单操作代码',
	`father_code` VARCHAR (100) DEFAULT '' COMMENT '父菜单名称代码',
	`father_name` VARCHAR (100) DEFAULT '' COMMENT '父菜单名称',
	`status` INT DEFAULT 0 COMMENT '权限状态',
	`create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`),
	UNIQUE KEY `code` (`code`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT '权限表';


CREATE TABLE `perm_role_permission` (
	`role_id` INT (11) NOT NULL COMMENT '角色ID',
	`permission_id` INT (11) NOT NULL COMMENT '权限ID',
	`create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (`role_id`, `permission_id`),
	CONSTRAINT `prp_role_id` FOREIGN KEY (`role_id`) REFERENCES `perm_role` (`id`),
	CONSTRAINT `prp_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `perm_permission` (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT '角色权限表';


CREATE TABLE `perm_user_role` (
	`user_id` INT (11) NOT NULL COMMENT '用户ID',
	`role_id` INT (11) NOT NULL COMMENT '角色ID',
	`create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (user_id, role_id),
	CONSTRAINT `pur_role_id` FOREIGN KEY (`role_id`) REFERENCES `perm_role` (`id`),
	CONSTRAINT `pur_user_id` FOREIGN KEY (`user_id`) REFERENCES `perm_user` (`id`)
) ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT '角色用户表';


CREATE TABLE `monitor_log` (
	`id` BIGINT (20) NOT NULL AUTO_INCREMENT COMMENT 'id',
	`log_type` VARCHAR (50) DEFAULT NULL COMMENT '日志类型',
	`page` VARCHAR (50) DEFAULT NULL COMMENT '页面',
	`page_url` VARCHAR (50) DEFAULT NULL COMMENT '页面资源',
	`operate_type` VARCHAR (50) DEFAULT NULL COMMENT '操作类型',
	`object_type` VARCHAR (50) DEFAULT NULL COMMENT '对象类型',
	`object_id` VARCHAR (50) DEFAULT NULL COMMENT '对象Id',
	`content` text COMMENT '内容',
	`operator_name` VARCHAR (50) DEFAULT NULL COMMENT '操作员姓名',
	`operator_account` VARCHAR (50) DEFAULT NULL COMMENT '操作员账号',
	`system_ip` VARCHAR (50) DEFAULT NULL COMMENT 'ip',
	`user_agent` VARCHAR (200) DEFAULT NULL COMMENT 'UA',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	KEY `query_index` (
		`log_type`,
		`object_type`,
		`object_id`
	)
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8 COMMENT '日志表';


CREATE TABLE `perm_shiro_session` (
	`id` VARCHAR (255) NOT NULL,
	`session` TEXT,
	`startTimestamp` Date,
	`lastAccessTime` Date,
	`timeout` BIGINT,
	`host` VARCHAR (255),
	CONSTRAINT pk_sessions PRIMARY KEY (id)
) ENGINE = INNODB DEFAULT CHARSET = 'utf8' COMMENT 'session表';

insert into perm_permission(name,code,father_code,father_name,status)values('User Manage','perm_user','perm_manage','Permission',0);
insert into perm_permission(name,code,father_code,father_name,status)values('Role Manege','perm_role','perm_manage','Permission',0);
insert into perm_permission(name,code,father_code,father_name,status)values('Log Summary','log_summary','log_summary','Log Summary',1);


insert into perm_user (id, account, password, status) values (1, 'admin', '88212f91e2e9cf36981a91b6c518af5c', 0);
insert into perm_role (id, name, code, status) values (1, 'admin', 'admin', 0);
insert into perm_user_role (user_id, role_id) values (1,1);
insert into perm_role_permission (permission_id, role_id) values (1,1);
insert into perm_role_permission (permission_id, role_id) values (2,1);
insert into perm_role_permission (permission_id, role_id) values (3,1);


/*offer页面*/
INSERT INTO `perm_permission` (`name`, `code`, `father_code`, `father_name`, `status`) VALUES ('Offer', 'offer', 'subscribe', 'Subscribe', '0');
insert into perm_role_permission (permission_id, role_id) values (4,1);
