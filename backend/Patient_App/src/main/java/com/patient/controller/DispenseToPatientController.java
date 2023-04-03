package com.patient.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.patient.Entity.DispenseToPatient;
import com.patient.Entity.LastInjectionScreen;
import com.patient.Entity.Patient;
import com.patient.Entity.Product;
import com.patient.Entity.Serial;
import com.patient.Entity.UsageOverLastMonths;
import com.patient.ServiceImpl.DispenceToPatientServiceImpl;
import com.patient.ServiceImpl.PatientServiceImpl;
@RestController
@CrossOrigin
public class DispenseToPatientController {
	
	@Autowired
	private DispenceToPatientServiceImpl dispenceToPatientServiceImpl;
	
	@Autowired
	private DispenceToPatientServiceImpl dispenceToServiceImpl;
	
	
	
	@GetMapping("/getProductBySerialNo/{serialNo}")
	public ResponseEntity<Serial> getProductBySerialNo(@PathVariable Integer serialNo) {
		Serial p=dispenceToPatientServiceImpl.getProductBySerialNo(serialNo);
		return new ResponseEntity<Serial>(p,HttpStatus.OK);
	}
	
	@PostMapping("/createDispense")
	public ResponseEntity<DispenseToPatient> createDispense(@RequestBody DispenseToPatient dispenceToPatient){
		System.out.print(dispenceToPatient);
		DispenseToPatient dp = dispenceToPatientServiceImpl.createDispence(dispenceToPatient);
		return new ResponseEntity<DispenseToPatient>(dp,HttpStatus.CREATED);
//		System.out.print(false)
	}
	
	
	@GetMapping("getPatientsByName/{patientName}")
	public ResponseEntity<List<Patient>> getPatientsByName(@PathVariable String patientName){
		
		List<Patient> patientList=dispenceToServiceImpl.getPatientsByName(patientName);
		return new ResponseEntity<List<Patient>>(patientList,HttpStatus.OK);
	}
	
	@PostMapping("/addDispense/{Id}/{nurseId}/{physicianId}/{productId}/{serialId}/{locationId}/{injectionSite}")
	private ResponseEntity<DispenseToPatient> addDispense(@PathVariable Integer Id,@PathVariable Integer nurseId,@PathVariable Integer physicianId,@PathVariable Integer productId,@PathVariable Integer serialId,@PathVariable Integer locationId,@PathVariable String injectionSite) {
		DispenseToPatient dispensetoPatient=dispenceToServiceImpl.addDispense(Id,nurseId,physicianId,productId,serialId,locationId,injectionSite);
		return new ResponseEntity<DispenseToPatient>(dispensetoPatient,HttpStatus.CREATED);
	}
	
	@GetMapping("/getAllDispense")
	public List<LastInjectionScreen> getAllDispense(){
		List<LastInjectionScreen> l = dispenceToServiceImpl.getAllDispense();
		return l;
	}
	
	@GetMapping("/getAllDispensedDose")
	public List<UsageOverLastMonths> getAllDispensedDose()
	{
		List<UsageOverLastMonths> list = dispenceToServiceImpl.getAllUsedDoses();
		return list;
	}
	
	
	

}
