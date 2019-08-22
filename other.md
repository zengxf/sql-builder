# 其他说明
- [发布参考](https://segmentfault.com/a/1190000018026290)
- 发布测试 `gradle publishToMavenLocal`
  - `*.pom` 文件不会生成在 `libs` 下面
  - `*-javadoc.jar` 里面是空的
- 正式发布 `gradle publish`


## 问题
- 私钥不要用 `-a` 输出，用二进制就行

### 发布出错
- 如：`xx Could not write to resource xx`
- 去下面搜上传的 Key
  - http://pool.sks-keyservers.net
  - http://keyserver.ubuntu.com
- 但手动上传了也搜不出，**但手动上传后能发布了**
  - 可用 Kleopatra 导出公钥文件

### 找不到相关按钮
- 在 https://oss.sonatype.org 找不到，是因为没登录
- 点击 `Log In` 登录即可

## gradle.properties 示例
```
sonatypeUsername=u
sonatypePassword=p

signing.keyId=0026AA85
signing.password=xx
signing.secretKeyRingFile=L:/key/secret.gpg
```