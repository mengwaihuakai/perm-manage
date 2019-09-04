package com.solid.subscribe.web.perm.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by ZOOMY on 2019/1/17.
 */
@Table(name="perm_shiro_session")
@Data
public class ShiroSession implements Serializable {
    @Id
    private String id;
    private String session;
    private Date startTimestamp;
    private Date lastAccessTime;
    private Long timeout;
    private String host;

}
