package pacote.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import pacote.dao.DaoGeneric;
import pacote.model.PessoaModel;

@ViewScoped
@ManagedBean(name = "pessoaBean")
public class PessoaBean {

	private PessoaModel pessoa = new PessoaModel();
	private DaoGeneric<PessoaModel> daoGeneric = new DaoGeneric<PessoaModel>();
	private List<PessoaModel> pessoas = new ArrayList<PessoaModel>();

	public String salvar() {
		pessoa = daoGeneric.merge(pessoa);
		carregarPessoas();
		return "";
	}

	public String novo() {
		pessoa = new PessoaModel();
		return "";
	}

	public String remover() {
		daoGeneric.deletarPorId(pessoa);
		pessoa = new PessoaModel();
		carregarPessoas();
		return "";
	}

	@PostConstruct
	public void carregarPessoas() {
		pessoas = daoGeneric.listarTodos(PessoaModel.class);
	}

	public PessoaModel getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaModel pessoa) {
		this.pessoa = pessoa;
	}

	public DaoGeneric<PessoaModel> getDaoGeneric() {
		return daoGeneric;
	}

	public void setDaoGeneric(DaoGeneric<PessoaModel> daoGeneric) {
		this.daoGeneric = daoGeneric;
	}

	public List<PessoaModel> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<PessoaModel> pessoas) {
		this.pessoas = pessoas;
	}

}
