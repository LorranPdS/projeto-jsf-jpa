package bean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import dao.DaoGeneric;
import entidade.Cidades;
import entidade.Estados;
import entidade.Pessoa;
import jpautil.JPAUtil;
import repository.IDaoPessoa;
import repository.IDaoPessoaImpl;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private Pessoa pessoa = new Pessoa();
	private DaoGeneric<Pessoa> daoGeneric = new DaoGeneric<Pessoa>();
	private List<Pessoa> pessoas = new ArrayList<Pessoa>();
	private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();

	private List<SelectItem> estados;
	private List<SelectItem> cidades;

	public String salvar() {
		pessoa = daoGeneric.salvar(pessoa);
		carregarPessoas();
		mostrarMsg("Salvo com sucesso!");
		return "";
	}

	private void mostrarMsg(String msg) {
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage message = new FacesMessage(msg);
		context.addMessage(null, message);
	}

	public String novo() {
		pessoa = new Pessoa();
		return "";
	}

	public String remover() {
		daoGeneric.deletarPorId(pessoa);
		pessoa = new Pessoa();
		carregarPessoas();
		mostrarMsg("Removido com sucesso!");
		return "";
	}

	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.listarTodos(pessoa);
	}

	public String logar() {

		Pessoa pessoaUser = iDaoPessoa.consultarUsuario(pessoa.getLogin(), pessoa.getSenha());

		if (pessoaUser != null) {
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext externalContext = context.getExternalContext();
			externalContext.getSessionMap().put("loginUsuario", pessoaUser);

			return "cadastro.jsf";
		}
		return "index.jsf";
	}

	public boolean permissaoAcesso(char acesso) {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("loginUsuario");

		return pessoaUser.getPerfil().equals(acesso);
	}

	@SuppressWarnings("unchecked")
	public void carregarCidades(AjaxBehaviorEvent event) {
		Estados estado = (Estados) ((HtmlSelectOneMenu) event.getSource()).getValue();

		if (estado != null) {
			pessoa.setEstados(estado);

			List<Cidades> cidades = JPAUtil.getEntityManager()
					.createQuery("from Cidades where estados.id = " + estado.getId())
					.getResultList();

			List<SelectItem> selectItemCidade = new ArrayList<SelectItem>();

			for (Cidades c : cidades) {
				selectItemCidade.add(new SelectItem(c, c.getNome()));
			}
			setCidades(selectItemCidade);
		}
	}

	public String deslogar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		externalContext.getSessionMap().remove("loginUsuario");

		HttpServletRequest servRequest = (HttpServletRequest) externalContext.getRequest();
		servRequest.getSession().invalidate();
		return "index.jsf";
	}

	public void pesquisarCep(AjaxBehaviorEvent event) {
		try {
			URL url = new URL("https://viacep.com.br/ws/" + pessoa.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder jsonCep = new StringBuilder();

			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}

			Pessoa gsonAux = new Gson().fromJson(jsonCep.toString(), Pessoa.class);
			pessoa.setCep(gsonAux.getCep());
			pessoa.setLogradouro(gsonAux.getLogradouro());
			pessoa.setBairro(gsonAux.getBairro());
			pessoa.setLocalidade(gsonAux.getLocalidade());
			pessoa.setUf(gsonAux.getUf());

		} catch (Exception e) {
			e.printStackTrace();
			mostrarMsg("CEP não encontrado!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void editar() {
		if(pessoa.getCidades() != null) {
			Estados estado = pessoa.getCidades().getEstados();
			pessoa.setEstados(estado);
			
			List<Cidades> cidades = JPAUtil.getEntityManager().createQuery(
					"from Cidades where estados.id = " + estado.getId()).getResultList();
			
			List<SelectItem> selectItemsCidade = new ArrayList<SelectItem>();
			
			for(Cidades c : cidades) {
				selectItemsCidade.add(new SelectItem(c, c.getNome()));
			}
			setCidades(selectItemsCidade);
		}
	}

	public List<SelectItem> getEstados() {
		estados = iDaoPessoa.listarEstados();
		return estados;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<Pessoa> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Pessoa> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

	public List<SelectItem> getCidades() {
		return cidades;
	}

	public void setCidades(List<SelectItem> cidades) {
		this.cidades = cidades;
	}

}
