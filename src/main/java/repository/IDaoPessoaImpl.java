package repository;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import entidade.Estados;
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

			pessoa = (Pessoa) entityManager
					.createQuery("select p from Pessoa p where p.login = '" + login + "' and p.senha = '" + senha + "'")
					.getSingleResult();

			transaction.commit();
			entityManager.close();

			return pessoa;
		} catch (NoResultException e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SelectItem> listarEstados() {

		List<SelectItem> selectItems = new ArrayList<SelectItem>();

		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		List<Estados> estados = entityManager.createQuery("from Estados")
				.getResultList();

		for (Estados e : estados) {
			selectItems.add(new SelectItem(e, e.getNome()));
		}

		return selectItems;

	}

}
