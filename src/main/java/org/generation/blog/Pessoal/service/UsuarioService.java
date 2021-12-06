package org.generation.blog.Pessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.generation.blog.Pessoal.model.UserLogin;
import org.generation.blog.Pessoal.model.Usuario;
import org.generation.blog.Pessoal.repository.UsuarioRepository;

@Service

public class UsuarioService {



	@Autowired

	private UsuarioRepository usuarioRepository;



	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);



		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		return Optional.of(usuarioRepository.save(usuario));

	}



	public Optional<Usuario> atualizarUsuario(Usuario usuario) {

		if (usuarioRepository.findById(usuario.getId()).isPresent()) {

			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());



			if (buscaUsuario.isPresent()) {				

				if (buscaUsuario.get().getId() != usuario.getId())

					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			}

			

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.of(usuarioRepository.save(usuario));

		} 

		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!", null);		

	}	

	

	public Optional<UserLogin> logarUsuario(Optional<UserLogin> userLogin) {

		Optional<Usuario> usuario = usuarioRepository.findByUsuario(userLogin.get().getUsuario());



		if (usuario.isPresent()) {

			if (compararSenhas(userLogin.get().getSenha(), usuario.get().getSenha())) {

				userLogin.get().setId(userLogin.get().getId());				

				userLogin.get().setNome(userLogin.get().getNome());

				userLogin.get().setFoto(userLogin.get().getFoto());

				userLogin.get().setSenha(userLogin.get().getSenha());

				userLogin.get().setToken(generatorBasicToken(userLogin.get().getUsuario(), userLogin.get().getSenha()));



				return userLogin;

			}

		}		

		throw new ResponseStatusException(

				HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos!", null);

	}

	

	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String senhaEncoder = encoder.encode(senha);

		return senhaEncoder;

	}

	

	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.matches(senhaDigitada, senhaBanco);		

	}

	

	private String generatorBasicToken(String email, String password) {

		String structure = email + ":" + password;

		byte[] structureBase64 = Base64.encodeBase64(structure.getBytes(Charset.forName("US-ASCII")));

		return "Basic " + new String(structureBase64);

	}



	public @Valid Optional<UserLogin> Logar(@Valid Optional<UserLogin> user) {
		// TODO Auto-generated method stub
		return null;
	}

}