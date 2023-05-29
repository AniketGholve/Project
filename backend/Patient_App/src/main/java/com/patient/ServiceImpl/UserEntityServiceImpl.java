package com.patient.ServiceImpl;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.patient.Entity.UserEntity;
import com.patient.Repo.UserEntityRepo;
import com.patient.Service.UserEntityService;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
@Service
public class UserEntityServiceImpl implements UserEntityService {
	
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserEntityRepo userEntityRepo;
	
	@Autowired
	private Keycloak keycloak;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserEntityServiceImpl(Keycloak keycloak) {
		this.keycloak=keycloak;
	}
	

	@Override
	public UserEntity findByCustomUsername(String username) {
		// TODO Auto-generated method stub
		Query q=entityManager.createNativeQuery("select id,password,role,username,dateof_birth,first_name,last_name,phone_no from user_entity where username=?");
		q.setParameter(1, username);
		UserEntity u=(UserEntity) q.getSingleResult();
		return u;
	}

	@Override
	public Integer addUser(UserEntity userEntity) {
		// TODO Auto-generated method stub
		System.out.println(userEntity.toString());
		String role = userEntity.getRole();
		String arr[] = { "CLP", "ELP", "ALP", "MLP" };
		boolean result = Arrays.asList(arr).contains(role);
		if (result) {
			userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
			userEntity.setActive(true);
			userEntity.setDeleted(false);
			userEntityRepo.save(userEntity);
			
			//create user in keycloak
			String password = userEntity.getPassword();
			
			UserRepresentation userRepresentation = new UserRepresentation();
			userRepresentation.setUsername(userEntity.getUsername());
			CredentialRepresentation credential = new CredentialRepresentation();
			credential.setType(CredentialRepresentation.PASSWORD);
			credential.setValue(password);
			credential.setTemporary(false);
			userRepresentation.setCredentials(Collections.singletonList(credential));
			userRepresentation.setEnabled(true);
			
			keycloak.realm("InventoryManagementSystem").users().create(userRepresentation);
			
			RoleRepresentation roleRepresentation = keycloak.realm("InventoryMnagementSystem").roles().get(role).toRepresentation();
			
			keycloak.realm("InventoryManagementSystem").users().get(userRepresentation.getUsername()).roles().realmLevel().add(Arrays.asList(roleRepresentation));
			
			keycloak.realm("InventoryManagementSystem").users().create(userRepresentation);
			
			return 1;
			
			
		}
		return -1;
		
	}

	@Override
	public UserEntity editUser(UserEntity userEntity) {
		// TODO Auto-generated method stub
		System.out.println("user");
		System.out.println(userEntity.toString());
		
		UserEntity u = userEntityRepo.findByUsername(userEntity.getUsername());
		//UserEntity u=userEntityServiceImpl.findByCustomUsername(user.getUsername());
		System.out.println(u.toString());
		if(userEntity.getFirstName() != null) {
			u.setFirstName(userEntity.getFirstName());
			System.out.println(userEntity.getFirstName());	
		}
		if (userEntity.getLastName()!= null) {
			u.setLastName(userEntity.getLastName());
		}
		if (userEntity.getDateofBirth() != null) {
			u.setDateofBirth(userEntity.getDateofBirth());
		}
		if (userEntity.getPhoneNo() != null) {
			u.setPhoneNo(userEntity.getPhoneNo());
		}
		if (userEntity.getPassword() != null) {
//			u.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		}
		if (userEntity.getUsername() != null) {
			u.setUsername(userEntity.getUsername());
		}
		if (userEntity.getRole() != null) {
			u.setRole(userEntity.getRole());
		}
		
		userEntityRepo.save(u);
		return u;
		
	}

	@Override
	public void deleteUser(int id) {
		// TODO Auto-generated method stub
		userEntityRepo.deleteById(id);	
	}

	@Override
	public UserEntity findUserById(int id) {
		// TODO Auto-generated method stub
		UserEntity u = userEntityRepo.findById(id).orElseThrow();
		return u;
	}

}
