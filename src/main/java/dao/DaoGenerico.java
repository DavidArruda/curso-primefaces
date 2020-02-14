package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.posjava.HibernateUtil;

public class DaoGenerico<E> {
	
	private EntityManager entityManager = HibernateUtil.getEntityManager();
	
	public void salvar(E entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.persist(entidade);
		transaction.commit();
	}

	public E updateMerge(E entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		E entidadeSalva = entityManager.merge(entidade);
		transaction.commit();
		
		return entidadeSalva;
	}
	
	public E pesquisar(E entidade) {

		Object id = HibernateUtil.getPrimaryKey(entidade);

		@SuppressWarnings("unchecked")
		E e = (E) entityManager.find(entidade.getClass(), id);

		return e;
	}

	public E pesquisar2(Long id, Class<E> entidade) {
		entityManager.clear();
		E e = (E) entityManager.find(entidade, id);

		return e;
	}
	
	public E pesquisar3(Long id, Class<E> entidade) {
		EntityManager entityManager = HibernateUtil.getEntityManager();

		@SuppressWarnings("unchecked")
		E e = (E) entityManager.createQuery("from" + entidade.getSimpleName() + "where id = " + id).getSingleResult();

		return e;
	}

	public void deletar(E entidade) throws Exception{

		Object id = HibernateUtil.getPrimaryKey(entidade);

		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		entityManager
				.createNativeQuery(
						"delete from " + entidade.getClass().getSimpleName().toLowerCase() + " where id =" + id)
				.executeUpdate(); // faz o delete

		transaction.commit(); // Grava alteração no banco
	}
	
	public void deletePorId(E entidade) {
		
		
		Object id = HibernateUtil.getPrimaryKey(entidade);
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		entityManager.createQuery("delete from " + entidade.getClass().getName() + " where id = " + id).executeUpdate();
		
		transaction.commit();
	}
	

	@SuppressWarnings("unchecked")
	public List<E> listar(Class<E> entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		List<E> lista = entityManager.createQuery("from " + entidade.getName()).getResultList();

		transaction.commit();

		return lista;
	}

	// Para acessar os recursos de persistencia em qualquer lugar da aplicação
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
