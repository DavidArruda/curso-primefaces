package managedbean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.tomcat.util.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.google.gson.Gson;

import dao.DaoEmail;
import dao.DaoUsuarioPessoa;
import datatablelazy.LazyDataTableModelUserPessoa;
import model.EmailUser;
import model.UsuarioPessoa;

@ManagedBean(name = "usuarioPessoaManagedBean")
@ViewScoped
public class UsuarioPessoaManagedBean {

	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	private LazyDataTableModelUserPessoa<UsuarioPessoa> list = new LazyDataTableModelUserPessoa<UsuarioPessoa>();
	private DaoUsuarioPessoa<UsuarioPessoa> daoGenerico = new DaoUsuarioPessoa<UsuarioPessoa>();
	
	private BarChartModel barChartModel = new BarChartModel();
	
	private EmailUser emailUser = new EmailUser();
	private DaoEmail<EmailUser> daoEmail = new DaoEmail<EmailUser>();
	
	String campoPesquisa;
	
	public EmailUser getEmailUser() {
		return emailUser;
	}
	
	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}
	
	public UsuarioPessoa getUsuarioPessoa() {
		return usuarioPessoa;
	}

	public void setUsuarioPessoa(UsuarioPessoa usuarioPessoa) {
		this.usuarioPessoa = usuarioPessoa;
	}

	public BarChartModel getBarChartModel() {
		return barChartModel;
	}
	
	public String getCampoPesquisa() {
		return campoPesquisa;
	}
	
	public void setCampoPesquisa(String campoPesquisa) {
		this.campoPesquisa = campoPesquisa;
	}

	@PostConstruct
	public void init() {
		list.load(0, 5, null, null, null);
		montarGrafico();
	}

	private void montarGrafico() {
		barChartModel = new BarChartModel();
		
		ChartSeries userSalario = new ChartSeries(); // grupo de usuarios

		for (UsuarioPessoa usuarioPessoa : list.list) {
			userSalario.set(usuarioPessoa.getNome(), usuarioPessoa.getSalario()); // adiciona salarios
		}

		barChartModel.addSeries(userSalario); // adiciona o grupo ao bar model
		barChartModel.setTitle("Salário dos usuários");
	}
	
	public void addEmail() {
		emailUser.setUsuarioPessoa(usuarioPessoa);
		emailUser = daoEmail.updateMerge(emailUser); // retorna com a primarykey
		usuarioPessoa.getEmails().add(emailUser);
		emailUser = new EmailUser();
		
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Salvo com sucesso"));
	}
	
	public void removerEmail() {
		String codigoEmail = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("codigoEmail");
		
		EmailUser remover = new EmailUser();
		remover.setId(Long.parseLong(codigoEmail));
		
		daoEmail.deletePorId(remover);
		usuarioPessoa.getEmails().remove(remover);
		
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Removido com sucesso"));
	}
	
	public void pesquisar() {
		list.pesquisar(campoPesquisa);
		montarGrafico();
	}

	public String salvar() {
		usuarioPessoa = daoGenerico.updateMerge(usuarioPessoa);
		list.list.add(usuarioPessoa);
		usuarioPessoa = new UsuarioPessoa();
		init();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Salvo com sucesso"));
		return "";
	}

	public String remover() {
		try {
			daoGenerico.removerUsuarioCascade(usuarioPessoa);
			list.list.remove(usuarioPessoa);
			usuarioPessoa = new UsuarioPessoa();
			init();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Removido com sucesso"));
		} catch (Exception e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Informação", "Existe telefone(s) ou E-mail(s) para esse usuário"));
			} else {
				e.printStackTrace();
			}
		}
		return "";
	}

	public void pesquisaCep(AjaxBehaviorEvent event) {

		try {
			URL url = new URL("https://viacep.com.br/ws/" + usuarioPessoa.getCep() + "/json/");

			URLConnection connection = url.openConnection();

			InputStream is = connection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";

			StringBuilder json = new StringBuilder();

			while ((cep = br.readLine()) != null) {
				json.append(cep);
			}

			UsuarioPessoa gson = new Gson().fromJson(json.toString(), UsuarioPessoa.class);

			usuarioPessoa.setCep(gson.getCep());
			usuarioPessoa.setLogradouro(gson.getLogradouro());
			usuarioPessoa.setComplemento(gson.getComplemento());
			usuarioPessoa.setBairro(gson.getBairro());
			usuarioPessoa.setLocalidade(gson.getLocalidade());
			usuarioPessoa.setUf(gson.getUf());
			usuarioPessoa.setUnidade(gson.getUnidade());
			usuarioPessoa.setIbge(gson.getIbge());
			usuarioPessoa.setGia(gson.getGia());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void download() throws IOException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		
		String fileDownloadId = params.get("fileUploadId");
		
		UsuarioPessoa pessoa = daoGenerico.pesquisar2(Long.parseLong(fileDownloadId), UsuarioPessoa.class);
		
		byte[] imagem = new Base64().decodeBase64(usuarioPessoa.getImagem().split("\\,")[1]);
		
		HttpServletResponse response = (HttpServletResponse) 
				FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response.addHeader("Content-Disposition", "attachment; filename=download.png");
		response.setContentType("application/octect-stream");
		response.setContentLength(imagem.length);
		response.getOutputStream().write(imagem);
		response.getOutputStream().flush();
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void upload(FileUploadEvent image) {
		String imagem = "data:image/png;base64," + DatatypeConverter.printBase64Binary(image.getFile().getContents());
		usuarioPessoa.setImagem(imagem);
	}

	public String novo() {
		usuarioPessoa = new UsuarioPessoa();
		return "";
	}

	public LazyDataTableModelUserPessoa<UsuarioPessoa> getList() {
		return list;
	}

}
