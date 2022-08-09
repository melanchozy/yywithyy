### 需求一、新增tag

#### 1. 前端

须填信息：name

按钮设计：新增

#### 2. 接口

| 方法                    | post           |
| ----------------------- | -------------- |
| url                     | backend/newtag |
| Authorization（请求头） | token          |
| 参数                    | name(String)   |

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": {
        "id": 1660016597248,
        "tagName": "新建标签1"
    }
}
```

### 需求二、删除tag

#### 1. 前端

须填信息：name

按钮设计：删除

#### 2. 后端逻辑

- 如果删除的是“未知标签”类别，直接返回fail，禁止删除
- 查询文章-标签表，将要删除的标签全替换为“未知标签”
- 删除原标签

#### 3. 接口

| 方法                    | post           |
| ----------------------- | -------------- |
| url                     | backend/deltag |
| Authorization（请求头） | token          |
| 参数                    | tagId(String)  |

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully delete tag"
}
```

### 需求三、修改tag

#### 1. 前端

须填信息：name

按钮设计：修改

#### 2. 后端逻辑

- 直接修改

#### 3. 接口

| 方法                    | post          |
| ----------------------- | ------------- |
| url                     | backend/uptag |
| Authorization（请求头） | token         |
| 参数                    | 如下          |

参数：

```json
{
    "id":"1555887813844652034",
    "name":"修改标签"
}
```

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully update tag"
}
```

