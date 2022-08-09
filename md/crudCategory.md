### 需求一、新增category

#### 1. 前端

须填信息：name、avatar（非必填）、description。

按钮设计：新增

avatar需上传图片。

#### 2. 接口

| 方法                    | post            |
| ----------------------- | --------------- |
| url                     | backend/newcate |
| Authorization（请求头） | token           |

参数形式如下：

```json
{
    "name":"新建分类",
    "avatar":"url",
    "description":"这是新建的分类"
}
```

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully create new category"
}
```

### 需求二、删除category

#### 1. 前端

按钮设计：删除

#### 2. 后端删除逻辑

- 如果删除的是“未分类”类别，直接返回fail，禁止删除
- 查询文章表，将原分类下所有文章的分类置为未分类（类别）
- 删除原分类

#### 3. 接口

| 方法                    | post             |
| ----------------------- | ---------------- |
| url                     | backend/delcate  |
| Authorization（请求头） | token            |
| 参数                    | cateId（String） |

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully delete category"
}
```

### 需求三、修改category

#### 1. 前端

按钮设计：修改

#### 2. 后端修改逻辑

直接修改

#### 3. 接口

| 方法                    | post           |
| ----------------------- | -------------- |
| url                     | backend/upcate |
| Authorization（请求头） | token          |
| 参数                    | 如下           |

参数：

```json
{
    "id":"1001",
    "avatar":null,
    "categoryName":"修改分类",
    "description":"nothing 123"
}
```

返回值：

```json
{
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully update category"
}
```

