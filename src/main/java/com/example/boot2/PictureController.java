package com.example.boot2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片接口
 *
 */
@RequestMapping("/picture")
@RestController
public class PictureController {
	private static final Logger logger=LoggerFactory.getLogger(PictureController.class);
	
	@Value("${picDirPath}")
	private String picDirPath;//存放图片的本地文件夹地址，从配置文件读取
	
	/**
	 * 图片上传
	 */
	@PostMapping("/upload")
	public Map<String,String> uploadPicture(@RequestParam("file")MultipartFile file){
		Map<String,String> map=new HashMap<>();
		//上传
		String fileName = PictureController.upload(file, picDirPath);
		
		//生成结果并返回
		map.put("imgUrl","/picture/preview/"+fileName);
		return map;
	}
	
	
	/**
	 * 图片预览
	 */
	@GetMapping("/preview/{fileName}")
	public void previewPicture(@PathVariable("fileName")String fileName,HttpServletRequest request,
			HttpServletResponse response) {
		String filePath=picDirPath+File.separator+fileName;
		InputStream fileStream = null;
		OutputStream outputStream = null;
		try {
			fileStream = new FileInputStream(filePath);
			response.setContentLength(fileStream.available());
			response.setContentType("image/jpeg");
			outputStream = response.getOutputStream();
			byte buff[] = new byte[1024];
			int length = 0;
			while ((length = fileStream.read(buff)) > 0) {
				outputStream.write(buff, 0, length);
			}
			outputStream.flush();
		} catch (Exception e) {
			logger.error("图片预览时出现异常，异常信息:\n",e);
		}
	}
	
	
	
	/**
	 * 上传方法
	 */
	private static String upload(MultipartFile file, String fileDirPath){
		try {
			// 判断是否存在对应的文件夹
			File fileDir = new File(fileDirPath);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			
			//文件类型
			String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1,
					file.getOriginalFilename().length());
			//UUID
			String uuid = UUID.randomUUID().toString();
			
			String fileName = uuid+"."+fileType;//文件名称
			
			//保存到本地
			Path path=Paths.get(fileDirPath,fileName);
			Files.write(path, file.getBytes());
			return fileName;
		} catch (IOException e) {
			logger.error("处理文件上传时出现异常，异常信息:\n",e);
		}
		
		return null;
	}
}
