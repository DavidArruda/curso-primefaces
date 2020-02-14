package managedbean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dao.DaoTelefone;
import dao.DaoUsuarioPessoa;
import model.TelefoneUser;
import model.UsuarioPessoa;

@ManagedBean(name = "telefoneManegedBean")
@ViewScoped
public class TelefoneManegedBean {

	private UsuarioPessoa user = new UsuarioPessoa();
	private TelefoneUser telefone = new TelefoneUser();
	private DaoUsuarioPessoa<UsuarioPessoa> daoUser = new DaoUsuarioPessoa<UsuarioPessoa>();
	private DaoTelefone<TelefoneUser> daoTelefone = new DaoTelefone<TelefoneUser>();

	@PostConstruct
	public void init() {
		String codUser = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("codigoUser");
		user = daoUser.pesquisar2(Long.parseLong(codUser), UsuarioPessoa.class);
	}
	
	public String salvar() {
		telefone.setUsuarioPessoa(user);
		daoTelefone.salvar(telefone);
		telefone = new TelefoneUser();
		user = daoUser.pesquisar2(user.getId(), UsuarioPessoa.class);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação", "Salvo com sucesso"));
		return "";
	}
	
	public String remover() {
		daoTelefone.deletePorId(telefone);
		user = daoUser.pesquisar2(user.getId(), UsuarioPessoa.class);
		telefone = new TelefoneUser();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação", "Removido com sucesso"));
		return "";
	}

	public UsuarioPessoa getUser() {
		return user;
	}

	public void setUser(UsuarioPessoa user) {
		this.user = user;
	}

	public TelefoneUser getTelefone() {
		return telefone;
	}

	public void setTelefone(TelefoneUser telefone) {
		this.telefone = telefone;
	}
}
