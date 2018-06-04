<%@ page language="java"  contentType="text/html; charset=UTF-8" %>

<html>
<body>
<<<<<<< HEAD
<h2>Hello World 我是tomcat1................</h2>
=======
<h2>Hello World1-tomcat1!</h2>
>>>>>>> cd5de35ad5b89501bbe185d86d47434607c4c23c
springmvc上传文件
<form name="form1" action="/small/manager/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="springmvc上传文件" />
</form>

富文本图片上传文件
<form name="form2" action="/small/manager/product/manager/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" value="富文本图片上传文件" />
</form>
</body>
</html>
