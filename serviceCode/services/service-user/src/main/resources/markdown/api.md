# api文档说明
接口请求统一使用Post;
Content-Type:application/json;




header
|参数|说明|
|--|--|
|token|token|
|lang|语言： （zh：中文，默认）,目前暂支持中文|


## 返回结果结构
```json
{
  "code": -1,
  "data": T,
  "msg": "xxx",
  "success": false
}
```

### 返回code
|code|说明|
|--|--|
|1|成功,通用|
|-1|错误,通用|
|203|token失效|


### 其它说明
为了兼容浏览器js(避免long精度缺失),返回结果的long类型都转为string类型;

接口文档自动生成的字段解析long和int都当作int处理了,需要真正调用接口看返回结果去区分.