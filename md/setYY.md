> 需求：修改首页yy信息 

#### 1. 功能设计 - 修改功能（本模块只需设计修改功能） 

#### 2. 前端逻辑实现 

- 先通过 users/yy 接口获取信息并展示（除id之外）
-  展示的信息均可修改
- 点击**修改按钮**提交修改

#### 3. 后端逻辑实现 

- 将该接口纳入登录拦截，登录用户才可使用

- 修改数据库数据
- 将 redis 中对应数据删除

 #### 4. 接口 

| url                            | backend/yy           |
| ------------------------------ | -------------------- |
| 方法                           | post                 |
| 参数                           |                      |
| Authorization(RequestHeader中) | token                |
| yyParams(RequestBody中)        | 如下参数(id为long型) |

请求参数： 

```json
{
    "id": 100000001,
    "name": "zzy",
    "gender": "男",
    "github": "https://github.com/melanchozy",
    "email": "",
    "post": "后端",
    "aboutMe": "love life，love yy." 
} 
```

返回值：

```json
{    
    "success": true,
    "code": 200,
    "msg": "success",
    "data": "Successfully modified data" } 
```