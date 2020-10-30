package dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import jpautil.JPAUtil;

@Named
public class DaoGeneric<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager entityManager;

	@Inject
	private JPAUtil jpaUtil;

	public T salvar(T entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		T ent = entityManager.merge(entidade);

		transaction.commit();
		return ent;
	}

	public void deletarPorId(T entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		Object id = jpaUtil.getPrimaryKey(entidade);

		entityManager.createQuery("delete from " + entidade.getClass().getCanonicalName() + " where id = " + id)
				.executeUpdate();

		transaction.commit();
	}

	@SuppressWarnings("unchecked")
	public List<T> listarTodos(T entidade) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		List<T> lista = entityManager.createQuery("from " + entidade.getClass().getCanonicalName()).getResultList();

		transaction.commit();
		return lista;
	}

	public T consultar(Class<T> entidade, String codigo) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		T objeto = (T) entityManager.find(entidade, Long.parseLong(codigo));

		transaction.commit();
		return objeto;
	}

}
