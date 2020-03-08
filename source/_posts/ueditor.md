title: Spring MVC整合UEditor
date: 2015-10-05 17:40:56
categories: [UEditor]
tags: [java,UEditor]
---

# 百度编辑器服务端整合
基于Spring MVC的项目中使用百度编辑器，记录备忘。
# 服务端代码：
``` java
/**
     * 初始化百度编辑器，可传入其他参数生成不同的编辑器
     * @param response
     * @param request
     */
    @RequestMapping("/ueditor/init")
    public void initUeditor(HttpServletResponse response,HttpServletRequest request){
        response.setContentType("application/json");
        //配置路径，首先获取webpp根目录绝对路径
        String rootPath = request.getSession().getServletContext().getRealPath("/");
		//将config.json放到与ueditor.config.js同一级的目录下。将ueditor所有文件放入到wapapp-static-ueditor下
		//设置获取服务端配置文件地址修正路径，此路径同时作用于文件上传
        rootPath=rootPath+"static";
        PrintWriter writer=null;
        try {
            String exec = new ActionEnter(request, rootPath).exec();
            writer = response.getWriter();
            writer.write(exec);
            writer.flush();
        } catch (IOException e) {
            logger.error("百度编辑器初始化错误！",e);
        }finally {
            if(writer!=null){
                writer.close();
            }
        }
    }
```
<!-- more -->
## 配置文件说明
> 百度编辑器初始化配置	
	
### ueditor.config.js
2个地方:
1.第22行，URL的设置，表示请求编辑器初始化css，js的请求地址，一般为静态资源映射目录。
> 注意百度编辑器会以他的源码文件夹为根目录自行拼接请求地址，eg：
> 源码结构：
> +utf8-jsp
>  --+third-party
>  --ueditor.config.js
>  --......
> 那么会以“URL+/third-party/...”这种形式请求

web目录结构为：
> +webapp
>  --+static
>     --+ueditor
>        --ueditor.config.js
>        --......

那么URL一行改为：
> var URL = "/static/ueditor/";

2.第34行，serverUrl的设置，表示请求服务器端配置和上传资源的链接地址，百度编辑器所有的请求都只通过这个请求地址，通过action参数进行分发执行不同的功能。
此设置与22行的URL无关。
> 我设置的服务器端请求初始化配置的的地址为“/ueditor/init”，那么将设置为“serverUrl: "/ueditor/init"”。

``` javascript
...
	/*var URL = window.UEDITOR_HOME_URL || getUEBasePath();*/
    var URL = "/static/ueditor/";

    /**
     * 配置项主体。注意，此处所有涉及到路径的配置别遗漏URL变量。
     */
    window.UEDITOR_CONFIG = {

		//为编辑器实例添加一个路径，这个不能被注释
		UEDITOR_HOME_URL: URL

        // 服务器统一请求接口路径
        ,
        //serverUrl: URL + "jsp/controller.jsp"
        serverUrl: "/ueditor/init"
		...
```

### config.json
imageUrlPrefix，图片访问路径前缀，由于百度编辑器默认保存图片的路径是根据webapp根目录绝对路径，然后在后面拼接上”imagePathFormat“路径的方式进行图片的保存并返回此路径对应的访问路径。
一般情况是我们将图片放到了静态资源所在的地方，可能不会。本例”"imagePathFormat": "/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}"“，那么按配置会生成如下的图片保存地址：
> webapp绝对路径+”/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}.{上传的图片的格式后缀}“

然而我们上传的controller是要将静态资源统一放入static目录下。所以访问路径要加个前缀”static”：
> ageUrlPrefix": "/static", /* 图片访问路径前缀 */

到这百度编辑器已经可以显示出来，但图片上传还是不可用。
## 文件图片上传
下载百度java端代码，百度默认使用commons组件，然而我们使用的是Spring MVC，spring对commons组件进行了封装，使得上传后获取不到文件。
代开源码目录upload包下BinaryUploader.java，对“public static final State save(HttpServletRequest request,Map<String, Object> conf)”方法进行改造。
``` java
public static final State save(HttpServletRequest request,
			Map<String, Object> conf) {
		InputStream fileStream = null;
		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}
		try {
			//修改了百度使用原生的commons上传方式
			DefaultMultipartHttpServletRequest multipartRequest=(DefaultMultipartHttpServletRequest)request;
			Iterator<String> fileNames=multipartRequest.getFileNames();
			MultipartFile file=null;
			while (fileNames.hasNext()){
				file=multipartRequest.getFiles(fileNames.next()).get(0);
				fileStream=file.getInputStream();
			}
			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}
			String savePath = (String) conf.get("savePath");
			String originFileName = file.getOriginalFilename();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;
			long maxSize = ((Long) conf.get("maxSize")).longValue();
			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = (String) conf.get("rootPath") + savePath;
			State storageState = StorageManager.saveFileByInputStream(fileStream,
					physicalPath, maxSize);
			fileStream.close();
			if (storageState.isSuccess()) {
				storageState.putInfo("url", PathFormat.format(savePath));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (ClassCastException e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		}catch (IOException e){
			return new BaseState(false, AppInfo.IO_ERROR);
		}
	}
```
确认spring mvc配置文件配置文件上传组件：
``` xml
	<!-- file upload-->
	<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
		<property name="defaultEncoding" value="UTF-8" />
		<!-- 5M -->
		<property name="maxUploadSize" value="52428800" />
	</bean>
```
