package br.com.posjava;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateUtil {

	public static EntityManagerFactory factory = null;

	static { // sempre que a classe for chamada será executado o metodo init
		init();
	}

	private static void init() {
		try {

			if (factory == null) {
				factory = Persistence.createEntityManagerFactory("pos-java");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static EntityManager getEntityManager() {
		return factory.createEntityManager(); // prove persistencia
	}

	public static Object getPrimaryKey(Object entity) { // Retorna a primary key
		return factory.getPersistenceUnitUtil().getIdentifier(entity);
	}

}
