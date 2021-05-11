package model.dao.impl; //impl = implementacao

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller"
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime())); //DATA faz assim
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) { //se for >0 significar q ele inseriu o dado
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs); 
			}
			else {
				throw new DbException("Unexpected Error! No rows affected");
			}		
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}	
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime())); //DATA faz assim
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
				
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}	
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"DELETE FROM seller "
					+ "WHERE Id = ?");
			
			st.setInt(1, id);
				
			st.executeUpdate(); //caso o id nao exista, ele vai rodar normal, mas obviamente nao vai deletar nada
								//poderia por um if para lancar um excecao dizendo... mas nao precisa
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}	
		
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			if(rs.next()) { //se existir algo na consulta feita ao banco de dados, vai colocar os valores aqui nos objetos
				//Department
				Department dep = instantiateDepartment(rs);
				
				//Seller
				Seller obj = instatiateSeller(rs, dep);
				
				return obj;	
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs); 
			//nao precisa fechar a conexao conn
		}
	}

	private Seller instatiateSeller(ResultSet rs, Department dep) throws SQLException { //propagou a excecao para o findById
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
			
			rs = st.executeQuery();
			
			List<Seller> list =new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>(); 
			
			//TOP ISSO AQUI
			while(rs.next()) { 			
				Department dep = map.get(rs.getInt("DepartmentId")); 
				
				if(dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep); 
				}
				
				Seller obj = instatiateSeller(rs, dep);	
				list.add(obj);
			}
			return list;	
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs); 
			//nao precisa fechar a conexao conn
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> list =new ArrayList<>();
			
			// controle para nao repetir o departamento, para nao criar 1 departamento para cada objeto q achar no While
			Map<Integer, Department> map = new HashMap<>();  // criou 1 map vazio, e vai guardar dentro desse map qualquer departamento que intanciar
			
			//TOP ISSO AQUI
			while(rs.next()) { //aqui foi WHILE pq pode ter varios resultados, ai enquanto tiver ele vai percorrendo
				
				// Antes de criar o departamento, vai testar se ele ja existe em cada vez q passar no while
				Department dep = map.get(rs.getInt("DepartmentId")); //Vai no Map e tenta buscar com o Get um departamento que tenha o mesmo ID
				
				if(dep == null) { //siginfica q na busca la do MAP nao tinha esse ID ainda... ai sim vai criar 1 novo department
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep); //para salvar no MAP o departamento e assim nao criar de novo.
				}
				
				Seller obj = instatiateSeller(rs, dep);
				list.add(obj);
			}
			return list;	
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs); 
			//nao precisa fechar a conexao conn
		}
	}

}
