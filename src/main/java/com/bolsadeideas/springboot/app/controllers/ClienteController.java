package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadService uploadService;
	
//	se coloca el .+ para que se pase el nombre completo del archivo con la extension
	@RequestMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		
		Resource recurso=null;
		
		try {
//			se obtiene la imagen a traves del objeto inyectado
			recurso=uploadService.load(filename);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() +"\"")
				.body(recurso);
	}
	
	@RequestMapping(value="ver/{id}")
	public String ver(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash){
		Cliente cliente = clienteService.findOne(id);
		if(cliente == null) {
			flash.addFlashAttribute("error", "Cliente no encontrado");
			return "redirect:/listar";
		}
		model.put("cliente",cliente);
		model.put("titulo", "Detalle cliente");
		return "ver";
	}

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name="page", defaultValue="0") int page,Model model) {
		
		
		Pageable pageRequest= PageRequest.of(page, 4);
		
		Page<Cliente> clientes= clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender= new PageRender<>("/listar", clientes);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}
	
	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {

		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de Cliente");
		return "form";
	}
	
	@RequestMapping(value="/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model,RedirectAttributes flash) {
		
		Cliente cliente = null;	
		if(id > 0) {
			cliente = clienteService.findOne(id);
			if(cliente==null) {
				flash.addFlashAttribute("error", "el cliente no existe");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "el ID no puede ser 0");
			return "redirect:/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "form";
	}
	
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,@RequestParam("file") MultipartFile foto ,RedirectAttributes flash,SessionStatus status) {
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Crear Cliente");
			return "form";
		}
		
		if(!foto.isEmpty()) {
			
			if(cliente.getId()!= null && cliente.getId() > 0  && cliente.getFoto() != null && cliente.getFoto().length() > 0){
				uploadService.delete(cliente.getFoto());
			}
			
			String uniqueFilename = null;
			try {
				uniqueFilename = uploadService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flash.addFlashAttribute("info", "Has subido correctamente la foto '" + uniqueFilename + "'");
			cliente.setFoto(uniqueFilename);
			
		}
		
		String mensaje= (cliente.getId()!=null) ? "Cliente editado con exito" : "Cliente creado con exito";
		
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success",mensaje);
		return "redirect:listar";
	}
	
	@RequestMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,RedirectAttributes flash) {
		
		if(id > 0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute("success", "el cliente se ha eliminado con exito");
			
			
				if(uploadService.delete(cliente.getFoto())) {
					flash.addFlashAttribute("info", "Foto "+cliente.getFoto() + "eliminada con exito");
				}
			}
		return "redirect:/listar";
	}
}