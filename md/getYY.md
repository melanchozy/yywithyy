> 需求：获取首页yy信息 

#### 1. 新增`yy`表 

```mysql
 CREATE TABLE `yyblog`.`yy_yy`(
     `id` BIGINT NOT NULL COMMENT 'yy_id',
     `name` VARCHAR(12) COMMENT 'name',
     `github` VARCHAR(30) COMMENT 'github',
     `email` VARCHAR(20) COMMENT 'email',
     `post` VARCHAR(10) COMMENT 'position',
     `gender` VARCHAR(2) COMMENT 'gender',
     `aboutme` VARCHAR(50) COMMENT 'introduce',  PRIMARY KEY (`id`) 
 ) ENGINE=INNODB CHARSET=utf8 COLLATE=utf8_general_ci;
```

#### 2. 新增对应实体类 

```java
package com.yy.blog.dao.pojo; 
import lombok.Data;
@Data 
public class Yy {
    private Long id;
    private String name;
    private String gender;
    private String github;
    private String email;
    private String post;
    @TableField(value = "aboutme")
    private String aboutMe; 
}
```

#### 3. 后端逻辑实现

- 先查 redis， 有则返回结果。

- 没查到再去数据库中查找返回结果并存入 redis。 

#### 4. 接口

| 方法 | get      |
| ---- | -------- |
| url  | users/yy |
| 参数 | 无       |

返回值： 

```json
{    
    "success": true,
    "code": 200,
    "msg": "success",
    "data": [
        {
            "id": 100000001,
            "name": "yy1",
            "gender": "男",
            "github": "https://github.com/melanchozy",
            "email": "wbszzy@qq.com",
            "post": "后端",
            "aboutMe": "love life，love yy."
        },
        {            
            "id": 10000000002,
            "name": "yy",
            "gender": null,
            "github": null,
            "email": null,
            "post": null,
            "aboutMe": null
        }
    ]
}
```

