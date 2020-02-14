package datatablelazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import dao.DaoUsuarioPessoa;
import model.UsuarioPessoa;

public class LazyDataTableModelUserPessoa<T> extends LazyDataModel<UsuarioPessoa> {
	
	private static final long serialVersionUID = 1L;
	
	private DaoUsuarioPessoa<UsuarioPessoa> dao = new DaoUsuarioPessoa<UsuarioPessoa>();
	public List<UsuarioPessoa> list = new ArrayList<UsuarioPessoa>();
	private String sql = "from UsuarioPessoa";
	
	@Override
	public List<UsuarioPessoa> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		list = dao.getEntityManager().createQuery(sql, UsuarioPessoa.class).
				setFirstResult(first).
				setMaxResults(pageSize).getResultList();
		//Volta ao sql original 
		sql = "from UsuarioPessoa";
		
		setPageSize(pageSize);
		//SQL para contar registros
		Integer qtdRegistros = Integer.parseInt(dao.getEntityManager().createQuery("select count(1)" + sql).getSingleResult().toString());
		setRowCount(qtdRegistros);
		
		return list;
	}
	
	public void pesquisar(String campoPesquisa){
		sql += " where nome like '%"+campoPesquisa+"%'" ;
	}
	
	public List<UsuarioPessoa> getList() {
		return list;
	}
	
	
}
