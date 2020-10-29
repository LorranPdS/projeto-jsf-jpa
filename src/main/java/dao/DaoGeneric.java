package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import jpautil.JPAUtil;

public class DaoGeneric<T> {

	public T salvar(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		T ent = entityManager.merge(entidade);

		transaction.commit();
		entityManager.close();
		return ent;
	}

	public void deletarPorId(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		Object id = JPAUtil.getPrimaryKey(entidade);

		entityManager.createQuery("delete from " + entidade.getClass().getCanonicalName() + " where id = " + id)
				.executeUpdate();

		transaction.commit();
		entityManager.close();
	}

	@SuppressWarnings("unchecked")
	public List<T> listarTodos(T entidade) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		List<T> lista = entityManager.createQuery("from " + entidade.getClass().getCanonicalName()).getResultList();

		transaction.commit();
		entityManager.close();
		return lista;
	}

	public T consultar(Class<T> entidade, String codigo) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		T objeto = (T) entityManager.find(entidade, Long.parseLong(codigo));

		transaction.commit();
		entityManager.close();
		return objeto;
	}

}
