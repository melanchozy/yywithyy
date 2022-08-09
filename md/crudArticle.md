### 需求一、查询Article

#### 1. 前端

展示信息：文章标题、简述、时间（可优化设置）、分类（调查询接口 category 单选）、**置顶**（修改数值）

分页展示

#### 2. 后端逻辑

- 文章表增加字段 is_delete
- 设置该文章的 is_delete 字段为 1

#### 3. 接口

| 方法                    | post                       |
| ----------------------- | -------------------------- |
| url                     | backend/articles           |
| Authorization（请求头） | token                      |
| 参数                    | page（int），pageSize(int) |

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": [
        {
            "id": 111,
            "title": "文章标题1",
            "summary": "文章简述1",
            "authorId": 100000001,
            "bodyId": 12,
            "categoryId": 10086,
            "createDate": 1659433871610,
            "isDelete": 0,
            "weight": 0
        },
        {
            "id": 1659434396808,
            "title": "文章标题",
            "summary": "文章简述",
            "authorId": 100000001,
            "bodyId": 1659434397182,
            "categoryId": 10086,
            "createDate": 1659433811609,
            "isDelete": 1,
            "weight": 1
        }
    ]
}
```



### 需求二、逻辑删除Article

#### 1. 前端

按钮设计：删除

#### 2. 后端逻辑

- 文章表增加字段 is_delete
- 设置该文章的 is_delete 字段为 1

#### 3. 接口

| 方法                    | post              |
| ----------------------- | ----------------- |
| url                     | backend/ldelart   |
| Authorization（请求头） | token             |
| 参数                    | articleId(String) |

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully Ldelete article"
}
```



### 需求三、恢复被逻辑删除的Article

#### 1. 前端

按钮设计：恢复

#### 2. 后端逻辑

- 设置该文章的 is_delete 字段为 0

#### 3. 接口

| 方法                    | post              |
| ----------------------- | ----------------- |
| url                     | backend/reart     |
| Authorization（请求头） | token             |
| 参数                    | articleId(String) |

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully Ldelete article"
}
```

### 需求四、彻底删除Article

#### 1. 前端

按钮设计：彻底删除（点击弹出对话框确认是否确定删除）

#### 2. 后端逻辑

- 删除文章标签表该文章-标签对应记录
- 删除文章body表记录
- 删除文章表记录

#### 3. 接口

| 方法                    | post              |
| ----------------------- | ----------------- |
| url                     | backend/delart    |
| Authorization（请求头） | token             |
| 参数                    | articleId(String) |

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully delete Article"
}
```

### 需求五、修改Article

#### 1. 前端

按钮设计：修改

能修改的信息：文章标题、简述、时间（可优化设置）、分类（调查询接口 category 单选）、**置顶**（修改数值）

#### 2. 后端逻辑

- 新建字段 weight
- 更新

#### 3. 接口

| 方法                    | post          |
| ----------------------- | ------------- |
| url                     | backend/upart |
| Authorization（请求头） | token         |
| 参数                    | 如下          |

参数：

```json
{
    "id": 1659434396808,
    "title": "标题",
    "summary": "文章简述",
    "categoryId": 10086,
    "createDate": 1659433811609,
    "weight": 2323
}
```

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully update article"
}
```

