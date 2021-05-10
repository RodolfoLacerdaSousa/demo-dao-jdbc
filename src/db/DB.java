package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {
	
	private static Connection conn = null;
	
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl"); //pega a url la no connector
				conn = DriverManager.getConnection(url, props); //conecta com o banco de dados intanciando 1 objeto do tipo Connection
		
			}
			catch (SQLException e) { //isso foi feito pq a SQLexception eh uma excessao do tipo Excetpiton, sendo assim obrigado a tratar.. mas colocando ela como DbExcetpion, que é uma RunTimeExcetpiton, nao fica mais obrigado a tratar.
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}
	
	public static void closeConnection() {
		if (conn != null) {
			try {
			conn.close();
			}
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	
	private static Properties loadProperties() {
		try(FileInputStream fs = new FileInputStream("db.properties")){
			Properties props = new Properties();
			props.load(fs); //faz a leitura do arquivo properties apontado pelo inputStream Fs e vai guardar os dados dentro do fs
			return props;
		}
		catch (IOException e) {
				throw new DbException(e.getMessage());
		}
	}
	
	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
	
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

}
