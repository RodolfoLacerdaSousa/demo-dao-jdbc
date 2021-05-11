package application;

import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
	
		DepartmentDao depdao = DaoFactory.createDepartmentDao();
		
		System.out.println("===== Test 1: department findById =====");
		Department dep = depdao.findById(3);
		System.out.println(dep);
		
		
		System.out.println();
		System.out.println("===== Test 2: deparment findAll =====");
		List<Department> list = depdao.findAll();
		
		for (Department obj : list) {
			System.out.println(obj);
		}
		
		System.out.println();
		System.out.println("===== Test 3: deparment insert =====");
		Department newDep = new Department(null, "Food");
		depdao.insert(newDep);
		System.out.println("Inserted! New id = " + newDep.getId());
		
		System.out.println();
		System.out.println("===== Test 4: department update =====");
		dep = depdao.findById(3);
		dep.setName("Plant");
		depdao.update(dep);
		System.out.println("Update completed!");
		
		System.out.println();
		System.out.println("===== Test 2: department delete =====");
		System.out.print("Enter id for delete test: ");
		int id = sc.nextInt();
		depdao.deleteById(id);
		System.out.println("Delete completed");
		sc.close();
	}

}
