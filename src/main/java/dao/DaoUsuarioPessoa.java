package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import br.com.posjava.HibernateUtil;
import model.UsuarioPessoa;

public class DaoUsuarioPessoa<E> extends DaoGenerico<UsuarioPessoa> {

	public void removerUsuario(UsuarioPessoa pessoa) throws Exception {
		String sqlTelefone = "delete from telefoneuser where usuariopessoa_id = " + pessoa.getId();
		String sqlEmail = "delete from emailUser where usuariopessoa_id = " + pessoa.getId();
		
		EntityManager entityManager = HibernateUtil.getEntityManager();
		
		EntityTransaction entityTransaction = entityManager.getTransaction();
		
		entityTransaction.begin();
		
		//Apaga os telefones vinculados a esse usuário
		entityManager.createNativeQuery(sqlTelefone).executeUpdate();
		
		//Apaga os E-mails vinculados a esse usuário
		entityManager.createNativeQuery(sqlEmail).executeUpdate();
		
		entityManager.getTransaction().commit();
		entityManager.close();

		super.deletePorId(pessoa);
	}
	
	public void removerUsuarioCascade(UsuarioPessoa pessoa) throws Exception {
		getEntityManager().getTransaction().begin();

		getEntityManager().remove(pessoa);
		
		getEntityManager().getTransaction().commit(); 
	}

	public List<UsuarioPessoa> pesquisar(String campoPesquisa) {
		return getEntityManager().createQuery("from UsuarioPessoa where nome like '%" + campoPesquisa + "%'", UsuarioPessoa.class).getResultList();
	}


}
