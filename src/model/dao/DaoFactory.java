package model.dao;

import model.dao.impl.SellerDaoJDBC;

public class DaoFactory { //vai instanciar uma implementacao
	
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC();
	}

}
// serve para esconder a implementao do Program, o program so vai conhecer a Interface...
// tbm faz uma injencao de dependencia sem explicitar a implementacao.
