package pacote.testes;

import java.util.List;

import org.junit.Test;

import pacote.dao.DaoGeneric;
import pacote.hibernate.HibernateUtil;
import pacote.model.Telefone;
import pacote.model.UsuarioPessoa;

public class JUnitTestes {

	@Test
	public void testeHibernateUtil() {
		HibernateUtil.getEntityManager();
	}
	
	@Test
	public void initSalvarUsuario() {
		UsuarioPessoa pessoa = new UsuarioPessoa();
		pessoa.setNome("Lorran");
		pessoa.setSobrenome("Pereira");
		pessoa.setIdade(29);
		pessoa.setLogin("lorran");
		pessoa.setSenha("123");

		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		daoGeneric.salvar(pessoa);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void initSalvarTelefone() {
		DaoGeneric daoGeneric = new DaoGeneric();
		
		UsuarioPessoa pessoa = 
				(UsuarioPessoa) daoGeneric.pesquisar2(2L, UsuarioPessoa.class);
		
		Telefone telefone = new Telefone();
		telefone.setNumero("543096549803");
		telefone.setTipo("Residencial");
		telefone.setPessoa(pessoa);
		
		daoGeneric.salvar(telefone);
	}	
	
	@Test
	public void initPesquisarUsuario1() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		UsuarioPessoa pessoa = new UsuarioPessoa();

		pessoa.setId(2L);
		pessoa = daoGeneric.pesquisar1(pessoa);

		System.out.println(pessoa);
	}

	@Test
	public void initPesquisarUsuario2() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		UsuarioPessoa pessoa = daoGeneric.pesquisar2(2L, UsuarioPessoa.class);

		System.out.println(pessoa);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void initConsultarTelefones() {
		DaoGeneric daoGeneric = new DaoGeneric();
		
		UsuarioPessoa pessoa = 
				(UsuarioPessoa) daoGeneric.pesquisar2(2L, UsuarioPessoa.class);
		
		int contador = 1;
		
		System.out.println("Nome: " + pessoa.getNome());
		for(Telefone fone : pessoa.getTelefones()) {
			System.out.println("Telefone " + contador + ":");
			System.out.println("Número: " + fone.getNumero());
			System.out.println("Tipo: " + fone.getTipo());
			System.out.println("___________________________");
			contador ++;
		}
		
	}
		
	@Test
	public void initAtualizar() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		UsuarioPessoa pessoa = daoGeneric.pesquisar2(2L, UsuarioPessoa.class);
		
		pessoa.setNome("Zélia");
		pessoa.setSobrenome("Duncan");
		
		pessoa = daoGeneric.atualizar(pessoa);
		
		System.out.printf("Atualizado para: %n%s", pessoa);
	}
		
	@Test
	public void initDeletarPorId() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		UsuarioPessoa pessoa = daoGeneric.pesquisar2(2L, UsuarioPessoa.class);
		
		daoGeneric.deletarPorId(pessoa);		
	}
	
	@Test
	public void initListar() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		List<UsuarioPessoa> pessoas = daoGeneric.listar(UsuarioPessoa.class);
		
		for (UsuarioPessoa usuarioPessoa : pessoas) {
			System.out.println(usuarioPessoa);
			System.out.println("___________________________________________________________");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void initListar2() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		
		List<UsuarioPessoa> list = 
				daoGeneric.getEntityManager()
				.createQuery("from UsuarioPessoa where nome = 'Lorran'")
				.getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void initListar3MaxResults() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		
		List<UsuarioPessoa> list = 
				daoGeneric.getEntityManager()
				.createQuery("from UsuarioPessoa order by nome")
				.setMaxResults(3).getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void initListar4PorParametro() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		
		List<UsuarioPessoa> list = daoGeneric
				.getEntityManager()
				.createQuery("from UsuarioPessoa where nome = :nomex or sobrenome = :sobrenomex")
				.setParameter("nomex", "Daniela")
				.setParameter("sobrenomex", "Pereira")
				.getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
		
		/* Na QUERY:
		 * a condição "OR", pode fazer com que o sistema retorne o nome de um,
		 * o sobrenome de outro e vir dois usuários, um usuário, ou mais usuários,
		 * PORÉM,
		 * se a condição for "AND", esse usuário terá que trazer o nome e o sobrenome
		 * da maneira que foi especificada na Query
		 *  */
	}
	
	@Test
	public void initListar5SomaIdades() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		Long somaIdade = (Long) daoGeneric.getEntityManager()
				.createQuery("select sum(u.idade) from UsuarioPessoa u ")
				.getSingleResult();
		
		System.out.println("Soma das idades: " + somaIdade);
		
		// Se eu queira a média das idades, trocar o sum por avg e o Long por Double
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void initNamedQuery1() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		
		List<UsuarioPessoa> list = daoGeneric.getEntityManager().createNamedQuery("UsuarioPessoa.todos")
		.getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void initNamedQuery2() {
		DaoGeneric<UsuarioPessoa> daoGeneric = new DaoGeneric<UsuarioPessoa>();
		
		List<UsuarioPessoa> nomes = daoGeneric.getEntityManager()
				.createNamedQuery("UsuarioPessoa.buscarPorNome")
				.setParameter("nomex", "Daniela")
				.getResultList();
		
		for (UsuarioPessoa usuarioPessoa : nomes) {
			System.out.println(usuarioPessoa);
		}
	}

} 