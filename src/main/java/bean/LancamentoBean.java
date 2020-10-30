package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dao.DaoGeneric;
import entidade.Lancamento;
import entidade.Pessoa;
import repository.IDaoLancamento;

@ViewScoped
@Named(value = "lancamentoBean")
public class LancamentoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DaoGeneric<Lancamento> daoGeneric;
	
	private Lancamento lancamento = new Lancamento();
	private List<Lancamento> lancamentos = new ArrayList<Lancamento>();
	
	@Inject
	private IDaoLancamento iDaoLancamento;

	public String salvar() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("loginUsuario");

		lancamento.setUsuario(pessoaUser);
		daoGeneric.salvar(lancamento);
		carregarLancamentos();
		lancamento = new Lancamento();
		return "";
	}
	
	public String novo() {
		lancamento = new Lancamento();
		return "";
	}
	
	@PostConstruct
	public void carregarLancamentos() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext externalContext = context.getExternalContext();
		Pessoa pessoaUser = (Pessoa) externalContext.getSessionMap().get("loginUsuario");
		
		lancamentos = iDaoLancamento.consultar(pessoaUser.getId());
	}
	
	public String remover() {		
		daoGeneric.deletarPorId(lancamento);
		lancamento = new Lancamento();
		carregarLancamentos();
		return "";
		
	}

	public DaoGeneric<Lancamento> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<Lancamento> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public List<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}

}
