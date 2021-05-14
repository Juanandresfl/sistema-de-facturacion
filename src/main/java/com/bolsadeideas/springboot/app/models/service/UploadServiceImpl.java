package com.bolsadeideas.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadServiceImpl implements IUploadService {

	@Override
	public Resource load(String filename) throws MalformedURLException {
		Path foto= getPath(filename);
		
		Resource recurso = null;
			 recurso = new UrlResource(foto.toUri());
			 if(!recurso.exists() || !recurso.isReadable()) {
				 throw new RuntimeException("Error no se puede cargar el archivo "+ foto.toString());
			 }
		return recurso;
	}

	@Override
	public String copy(MultipartFile file) throws IOException {
		String uniqueFilename= UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		
		Path rootPath = getPath(uniqueFilename);

		Files.copy(file.getInputStream(), rootPath);  		
	
		return uniqueFilename;
	}

	@Override
	public boolean delete(String filename) {
		Path ruta= getPath(filename);
		File archivo= ruta.toFile();
		
		if(archivo.exists() && archivo.canRead()) {
			if(archivo.delete()) {
				return true;
			}
		}
		return false;
	}

	public Path getPath(String filename) {
		return Paths.get("uploads").resolve(filename).toAbsolutePath();
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get("uploads").toFile());
	}

	@Override
	public void init() throws IOException {
		Files.createDirectory(Paths.get("uploads"));
	}
}
