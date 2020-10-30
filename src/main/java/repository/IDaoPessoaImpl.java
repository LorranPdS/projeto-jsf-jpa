package repository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import entidade.Pessoa;

@Named
public class IDaoPessoaImpl implements IDaoPessoa, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager entityManager;
	
	@Override
	public Pessoa consultarUsuario(String login, String senha) {
		try {
			EntityTransaction transaction = entityManager.getTransaction();
			transaction.begin();
			
			Pessoa pessoa = (Pessoa) entityManager.createQuery(
					"select p from Pessoa p where p.login = '" + login 
					+ "' and p.senha = '" + senha + "'").getSingleResult();
			
			transaction.commit();
			return pessoa;
			
		}catch (NoResultException e) {
			return null;
		}
	}

}
