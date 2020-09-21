package repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import entidade.Pessoa;
import jpautil.JPAUtil;

public class IDaoPessoaImpl implements IDaoPessoa {

	@Override
	public Pessoa consultarUsuario(String login, String senha) {
		try {
			
		Pessoa pessoa = null;
		
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		pessoa = (Pessoa) entityManager.createQuery("select p from Pessoa p where p.login = '"
				+ login + "' and p.senha = '" + senha + "'").getSingleResult();
		
		transaction.commit();
		entityManager.close();
		
		return pessoa;
	}
		catch (NoResultException e) {
			return null;
		}
		
	}
	
}
