package com.mmall.service.impl;import com.google.common.collect.Lists;import com.mmall.service.IFileService;import com.mmall.util.FTPUtil;import lombok.extern.slf4j.Slf4j;import org.apache.commons.lang3.StringUtils;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.stereotype.Service;import org.springframework.web.multipart.MultipartFile;import java.io.File;import java.io.IOException;import java.util.UUID;/** * @author chenqiang * @create 2020-07-01 17:19 */@Service@Slf4jpublic class FileServiceImpl implements IFileService {    @Override    public String upload(MultipartFile file, String path) {        String originalFilename = file.getOriginalFilename();        String fileExtensionName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;        log.info("开始上传文件，上传文件的文件名：{}，上传的路径{}",uploadFileName,path);        File fileDir = new File(path);        if(!fileDir.exists()){            fileDir.setWritable(true);            fileDir.mkdirs();        }        File targetFile = new File(path,uploadFileName);        try {            //上传文件到tomcat下            file.transferTo(targetFile);            //上传文件到FTP服务器            FTPUtil.uploadFile(Lists.newArrayList(targetFile));            //删除tomcat下的文件            targetFile.delete();        } catch (IOException e) {            e.printStackTrace();        }        return targetFile.getName();    }}